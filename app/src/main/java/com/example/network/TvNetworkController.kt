package com.example.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import com.example.model.DiscoveredTv
import com.example.model.RemoteButtonType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit

data class NetworkCommandResult(
    val success: Boolean,
    val message: String,
    val httpCode: Int = 0
)

class TvNetworkController(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(2, TimeUnit.SECONDS)
        .build()

    fun isWifiConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun getLocalIpAddress(): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val ipInt = wifiManager?.connectionInfo?.ipAddress ?: return null
        if (ipInt == 0) return null
        return String.format(
            "%d.%d.%d.%d",
            ipInt and 0xff,
            (ipInt shr 8) and 0xff,
            (ipInt shr 16) and 0xff,
            (ipInt shr 24) and 0xff
        )
    }

    /**
     * Discovers Smart TVs on the local network using SSDP multicast and rapid subnet inspection.
     */
    suspend fun discoverDevices(): List<DiscoveredTv> = withContext(Dispatchers.IO) {
        val discovered = mutableMapOf<String, DiscoveredTv>()
        val localIp = getLocalIpAddress()

        // 1. SSDP Multicast Search
        try {
            val ssdpRequest = "M-SEARCH * HTTP/1.1\r\n" +
                    "HOST: 239.255.255.250:1900\r\n" +
                    "MAN: \"ssdp:discover\"\r\n" +
                    "MX: 2\r\n" +
                    "ST: ssdp:all\r\n\r\n"

            val socket = DatagramSocket()
            socket.soTimeout = 1500
            val group = InetAddress.getByName("239.255.255.250")
            val sendPacket = DatagramPacket(
                ssdpRequest.toByteArray(),
                ssdpRequest.length,
                group,
                1900
            )
            socket.send(sendPacket)

            val buffer = ByteArray(2048)
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 1500) {
                try {
                    val recvPacket = DatagramPacket(buffer, buffer.size)
                    socket.receive(recvPacket)
                    val response = String(recvPacket.data, 0, recvPacket.length)
                    val ip = recvPacket.address.hostAddress ?: continue
                    
                    var brand = "Smart TV"
                    if (response.contains("roku", ignoreCase = true)) brand = "Roku TV"
                    else if (response.contains("samsung", ignoreCase = true)) brand = "Samsung Smart TV"
                    else if (response.contains("lg", ignoreCase = true) || response.contains("webos", ignoreCase = true)) brand = "LG webOS TV"
                    else if (response.contains("sony", ignoreCase = true) || response.contains("bravia", ignoreCase = true)) brand = "Sony Bravia"
                    else if (response.contains("dial", ignoreCase = true)) brand = "Smart TV (DIAL/Cast)"

                    val name = "$brand (${ip.substringAfterLast('.')})"
                    discovered[ip] = DiscoveredTv(
                        id = ip,
                        name = name,
                        ipAddress = ip,
                        port = if (brand.contains("Roku")) 8060 else 8001,
                        brand = brand
                    )
                } catch (e: Exception) {
                    break
                }
            }
            socket.close()
        } catch (e: Exception) {
            Log.w("TvNetworkController", "SSDP discovery error", e)
        }

        // 2. Local subnet fast scan for standard TV ports (Roku 8060, Samsung 8001, Android TV 8080)
        if (localIp != null && localIp.contains(".")) {
            val prefix = localIp.substringBeforeLast(".")
            val testIps = listOf(
                "$prefix.100", "$prefix.101", "$prefix.102", "$prefix.105",
                "$prefix.110", "$prefix.120", "$prefix.150"
            )
            for (ip in testIps) {
                if (discovered.containsKey(ip)) continue
                if (isPortOpen(ip, 8060, 200)) {
                    discovered[ip] = DiscoveredTv(
                        id = ip,
                        name = "Roku Smart TV ($ip)",
                        ipAddress = ip,
                        port = 8060,
                        brand = "Roku TV"
                    )
                } else if (isPortOpen(ip, 8001, 200)) {
                    discovered[ip] = DiscoveredTv(
                        id = ip,
                        name = "Samsung Smart TV ($ip)",
                        ipAddress = ip,
                        port = 8001,
                        brand = "Samsung TV"
                    )
                }
            }
        }

        discovered.values.toList()
    }

    private fun isPortOpen(ip: String, port: Int, timeoutMs: Int): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeoutMs)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Checks if TV is reachable at the given IP.
     */
    suspend fun verifyConnection(ipAddress: String, port: Int = 8060): Boolean = withContext(Dispatchers.IO) {
        try {
            isPortOpen(ipAddress, port, 1500)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Sends network command to Smart TV.
     * Implements Roku ECP protocol (REST POST) which is supported on Roku OS TVs,
     * with graceful fallback and status reporting.
     */
    suspend fun sendCommand(
        ipAddress: String,
        port: Int,
        button: RemoteButtonType
    ): NetworkCommandResult = withContext(Dispatchers.IO) {
        val rokuKey = mapToRokuKey(button)
        if (rokuKey != null) {
            try {
                val url = "http://$ipAddress:$port/keypress/$rokuKey"
                val request = Request.Builder()
                    .url(url)
                    .post("".toRequestBody("text/plain".toMediaTypeOrNull()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    return@withContext NetworkCommandResult(
                        success = true,
                        message = "Command ${button.name} executed on Smart TV",
                        httpCode = response.code
                    )
                } else {
                    return@withContext NetworkCommandResult(
                        success = false,
                        message = "TV returned HTTP ${response.code}",
                        httpCode = response.code
                    )
                }
            } catch (e: Exception) {
                Log.w("TvNetworkController", "Failed to send to $ipAddress:$port", e)
                return@withContext NetworkCommandResult(
                    success = false,
                    message = "Network error: ${e.localizedMessage ?: "Could not reach TV at $ipAddress"}"
                )
            }
        }

        NetworkCommandResult(
            success = false,
            message = "Command not mapped for network protocol"
        )
    }

    private fun mapToRokuKey(button: RemoteButtonType): String? {
        return when (button) {
            RemoteButtonType.POWER -> "Power"
            RemoteButtonType.MUTE -> "VolumeMute"
            RemoteButtonType.VOL_UP -> "VolumeUp"
            RemoteButtonType.VOL_DOWN -> "VolumeDown"
            RemoteButtonType.CH_UP -> "ChannelUp"
            RemoteButtonType.CH_DOWN -> "ChannelDown"
            RemoteButtonType.NAV_UP -> "Up"
            RemoteButtonType.NAV_DOWN -> "Down"
            RemoteButtonType.NAV_LEFT -> "Left"
            RemoteButtonType.NAV_RIGHT -> "Right"
            RemoteButtonType.OK -> "Select"
            RemoteButtonType.BACK -> "Back"
            RemoteButtonType.HOME -> "Home"
            RemoteButtonType.MENU -> "Info"
            RemoteButtonType.SOURCE -> "InputTuner"
            RemoteButtonType.PLAY -> "Play"
            RemoteButtonType.PAUSE -> "Play"
            RemoteButtonType.REWIND -> "Rev"
            RemoteButtonType.FAST_FORWARD -> "Fwd"
            RemoteButtonType.INFO -> "Info"
            RemoteButtonType.PREV_CH -> "InstantReplay"
            RemoteButtonType.NUM_0 -> "Lit_0"
            RemoteButtonType.NUM_1 -> "Lit_1"
            RemoteButtonType.NUM_2 -> "Lit_2"
            RemoteButtonType.NUM_3 -> "Lit_3"
            RemoteButtonType.NUM_4 -> "Lit_4"
            RemoteButtonType.NUM_5 -> "Lit_5"
            RemoteButtonType.NUM_6 -> "Lit_6"
            RemoteButtonType.NUM_7 -> "Lit_7"
            RemoteButtonType.NUM_8 -> "Lit_8"
            RemoteButtonType.NUM_9 -> "Lit_9"
            else -> null
        }
    }
}
