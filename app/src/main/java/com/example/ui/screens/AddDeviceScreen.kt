package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ir.TransmissionResult
import com.example.model.BrandCatalog
import com.example.model.ConnectionType
import com.example.model.DeviceType
import com.example.model.DiscoveredTv
import com.example.model.IrProtocol

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddDeviceScreen(
    hasHardwareIr: Boolean,
    discoveredTvs: List<DiscoveredTv>,
    isScanningWifi: Boolean,
    onTestPower: (brand: String, protocol: IrProtocol, carrierFreq: Int, code: Int) -> TransmissionResult,
    onSaveDevice: (name: String, brand: String, model: String, deviceType: DeviceType, connectionType: ConnectionType, ipAddress: String?, port: Int) -> Unit,
    onScanWifi: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: IR Preset, 1: Wi-Fi Discovery

    var selectedDeviceType by remember { mutableStateOf(DeviceType.TV) }
    var selectedBrand by remember { mutableStateOf("Samsung") }
    var selectedConnectionType by remember { mutableStateOf(if (hasHardwareIr) ConnectionType.IR else ConnectionType.WIFI) }
    var customName by remember { mutableStateOf("Living Room Samsung TV") }
    var ipAddressInput by remember { mutableStateOf("192.168.1.100") }
    var testFeedback by remember { mutableStateOf<String?>(null) }
    var testSucceeded by remember { mutableStateOf(false) }

    val presetInfo = remember(selectedBrand, selectedDeviceType) {
        BrandCatalog.getPreset(selectedBrand, selectedDeviceType)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("btn_add_device_back")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.add_device_title),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: IR Brand Catalog vs Wi-Fi Network Discovery
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF131A26),
            contentColor = Color(0xFF00E5FF),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color(0xFF00E5FF)
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF243044), RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Brand Presets (IR/Smart)", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Tv, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    onScanWifi()
                },
                text = { Text(stringResource(R.string.scan_network), fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                icon = { Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            // 1. Device Type Selector
            Text(
                text = "1. " + stringResource(R.string.device_type_tv),
                color = Color(0xFF00E5FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val types = listOf(
                    DeviceType.TV to Icons.Default.Tv,
                    DeviceType.AC to Icons.Default.DeviceThermostat,
                    DeviceType.RECEIVER to Icons.Default.VpnKey,
                    DeviceType.AUDIO to Icons.Default.Speaker
                )
                types.forEach { (type, icon) ->
                    val isSelected = selectedDeviceType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) Color(0xFF00E5FF) else Color(0xFF141C28))
                            .border(1.dp, if (isSelected) Color(0xFF80EAFF) else Color(0xFF263346), RoundedCornerShape(14.dp))
                            .clickable {
                                selectedDeviceType = type
                                customName = "$selectedBrand ${type.labelEn}"
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFF0A0F1A) else Color(0xFFE2E8F0),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = type.name,
                                color = if (isSelected) Color(0xFF0A0F1A) else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Brand Selector
            Text(
                text = "2. " + stringResource(R.string.brand_select),
                color = Color(0xFF00E5FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BrandCatalog.supportedBrands.forEach { brand ->
                    val isSelected = selectedBrand == brand
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF00E5FF) else Color(0xFF151D2A))
                            .border(1.dp, if (isSelected) Color(0xFF00E5FF) else Color(0xFF263346), RoundedCornerShape(12.dp))
                            .clickable {
                                selectedBrand = brand
                                customName = "$brand ${selectedDeviceType.labelEn}"
                                testFeedback = null
                                testSucceeded = false
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = brand,
                            color = if (isSelected) Color(0xFF0A0F1A) else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Connection Type (IR vs Wi-Fi LAN)
            Text(
                text = "3. " + stringResource(R.string.connection_type),
                color = Color(0xFF00E5FF),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // IR Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selectedConnectionType == ConnectionType.IR) Color(0xFF1E2838) else Color(0xFF121824))
                        .border(
                            1.5.dp,
                            if (selectedConnectionType == ConnectionType.IR) Color(0xFF00E5FF) else Color(0xFF222B3B),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedConnectionType = ConnectionType.IR }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Infrared (IR)", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = if (hasHardwareIr) "Hardware IR ready" else "IR blaster missing (simulated)",
                            color = if (hasHardwareIr) Color(0xFF22C55E) else Color(0xFFF59E0B),
                            fontSize = 10.sp
                        )
                    }
                }

                // Wi-Fi Option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selectedConnectionType == ConnectionType.WIFI) Color(0xFF1E2838) else Color(0xFF121824))
                        .border(
                            1.5.dp,
                            if (selectedConnectionType == ConnectionType.WIFI) Color(0xFF00E5FF) else Color(0xFF222B3B),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedConnectionType = ConnectionType.WIFI }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wi-Fi LAN", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Local network control", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }

            if (selectedConnectionType == ConnectionType.WIFI) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = ipAddressInput,
                    onValueChange = { ipAddressInput = it },
                    label = { Text(stringResource(R.string.ip_address)) },
                    placeholder = { Text("192.168.1.100") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00E5FF),
                        unfocusedBorderColor = Color(0xFF263346),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Test Power Button Step (MANDATORY per user prompt)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263448))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "4. " + stringResource(R.string.test_power_button),
                        color = Color(0xFF00E5FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.test_power_hint),
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val res = onTestPower(
                                selectedBrand,
                                presetInfo.protocol,
                                presetInfo.carrierFreq,
                                0x02
                            )
                            testFeedback = res.message
                            testSucceeded = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF2B56),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_test_power")
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TEST POWER SIGNAL", fontWeight = FontWeight.Bold)
                    }

                    if (testFeedback != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = testFeedback ?: "",
                            color = if (testSucceeded) Color(0xFF22C55E) else Color(0xFFEF4444),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Custom Device Name & Save
            OutlinedTextField(
                value = customName,
                onValueChange = { customName = it },
                label = { Text(stringResource(R.string.device_name_label)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00E5FF),
                    unfocusedBorderColor = Color(0xFF263346),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSaveDevice(
                        customName,
                        selectedBrand,
                        presetInfo.defaultModel,
                        selectedDeviceType,
                        selectedConnectionType,
                        if (selectedConnectionType == ConnectionType.WIFI) ipAddressInput else null,
                        8060
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_save_remote"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF0A0F1A)
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.save_device),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        } else {
            // Wi-Fi Local Discovery View
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.found_tvs),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onScanWifi, modifier = Modifier.testTag("btn_rescan_wifi")) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                }

                if (isScanningWifi) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.scanning),
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                } else if (discoveredTvs.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131A26)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Smart TVs automatically replied to discovery yet.",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ensure your phone is on the same 2.4/5GHz Wi-Fi network as the TV, or enter its IP address directly.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    discoveredTvs.forEach { tv ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    onSaveDevice(
                                        tv.name,
                                        tv.brand,
                                        tv.model,
                                        DeviceType.TV,
                                        ConnectionType.WIFI,
                                        tv.ipAddress,
                                        tv.port
                                    )
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF161E2C)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263346))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(tv.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${tv.ipAddress}:${tv.port}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    }
                                }
                                Text("Connect", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
