package com.example.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import android.util.Log
import com.example.model.IrProtocol

data class IrHardwareInfo(
    val hasIrEmitter: Boolean,
    val supportedFrequencies: List<Pair<Int, Int>> = emptyList(),
    val statusDescription: String
)

data class TransmissionResult(
    val success: Boolean,
    val message: String,
    val frequencyHz: Int,
    val patternLength: Int,
    val isPhysicalTransmission: Boolean
)

class ConsumerIrController(private val context: Context) {

    private val consumerIrManager: ConsumerIrManager? by lazy {
        try {
            context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager
        } catch (e: Exception) {
            Log.e("ConsumerIrController", "Error getting ConsumerIrManager", e)
            null
        }
    }

    fun getHardwareInfo(): IrHardwareInfo {
        val manager = consumerIrManager
        if (manager == null || !manager.hasIrEmitter()) {
            return IrHardwareInfo(
                hasIrEmitter = false,
                supportedFrequencies = emptyList(),
                statusDescription = "No IR Blaster detected on this device. You can use Wi-Fi Smart TV control."
            )
        }

        val freqs = mutableListOf<Pair<Int, Int>>()
        try {
            manager.carrierFrequencies?.forEach { range ->
                freqs.add(range.minFrequency to range.maxFrequency)
            }
        } catch (e: Exception) {
            Log.w("ConsumerIrController", "Failed to query carrier frequencies", e)
        }

        val freqSummary = if (freqs.isNotEmpty()) {
            freqs.joinToString { "${it.first / 1000}kHz - ${it.second / 1000}kHz" }
        } else {
            "Standard 38kHz - 56kHz"
        }

        return IrHardwareInfo(
            hasIrEmitter = true,
            supportedFrequencies = freqs,
            statusDescription = "IR Blaster Ready ($freqSummary)"
        )
    }

    /**
     * Transmits raw pulse/space timing array in microseconds.
     */
    fun transmit(frequency: Int, pattern: IntArray, buttonLabel: String): TransmissionResult {
        val manager = consumerIrManager
        val hasHardware = manager?.hasIrEmitter() == true

        if (hasHardware && manager != null) {
            try {
                manager.transmit(frequency, pattern)
                return TransmissionResult(
                    success = true,
                    message = "Transmitted $buttonLabel via IR (${frequency / 1000} kHz)",
                    frequencyHz = frequency,
                    patternLength = pattern.size,
                    isPhysicalTransmission = true
                )
            } catch (e: Exception) {
                Log.e("ConsumerIrController", "IR transmission failed", e)
                return TransmissionResult(
                    success = false,
                    message = "IR Transmission error: ${e.localizedMessage ?: "Unknown hardware error"}",
                    frequencyHz = frequency,
                    patternLength = pattern.size,
                    isPhysicalTransmission = true
                )
            }
        } else {
            // Hardware absent: inform user accurately per prompt guidelines
            return TransmissionResult(
                success = false,
                message = "IR hardware missing: simulated $buttonLabel (${frequency / 1000} kHz, ${pattern.size} pulses)",
                frequencyHz = frequency,
                patternLength = pattern.size,
                isPhysicalTransmission = false
            )
        }
    }

    /**
     * Helper to encode protocol and send.
     */
    fun sendCommand(
        protocol: IrProtocol,
        carrierFrequency: Int,
        address: Int,
        commandCode: Int,
        label: String
    ): TransmissionResult {
        val (freq, pattern) = IrSignalBuilder.build(protocol, address, commandCode)
        val finalFreq = if (carrierFrequency > 0) carrierFrequency else freq
        return transmit(finalFreq, pattern, label)
    }
}
