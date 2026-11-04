package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.screens.AddDeviceScreen
import com.example.ui.screens.ClassicRemoteScreen
import com.example.ui.screens.DevicesListScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartHubScreen
import com.example.ui.screens.TouchMouseScreen
import com.example.viewmodel.AppNavMode
import com.example.viewmodel.RemoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: RemoteViewModel,
    modifier: Modifier = Modifier
) {
    val navMode by viewModel.navMode.collectAsStateWithLifecycle()
    val activeDevice by viewModel.activeDevice.collectAsStateWithLifecycle()
    val allDevices by viewModel.allDevices.collectAsStateWithLifecycle()
    val isTransmitting by viewModel.isTransmittingPulse.collectAsStateWithLifecycle()
    val numericPadOpen by viewModel.numericPadOpen.collectAsStateWithLifecycle()
    val mouseState by viewModel.mouseState.collectAsStateWithLifecycle()
    val discoveredTvs by viewModel.discoveredTvs.collectAsStateWithLifecycle()
    val isScanningWifi by viewModel.isScanningWifi.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isHapticEnabled.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()

    var deviceDropdownExpanded by remember { mutableStateOf(false) }
    var isAddingDevice by remember { mutableStateOf(false) }

    // Android physical Back button handler
    BackHandler(enabled = isAddingDevice || navMode != AppNavMode.CLASSIC) {
        if (isAddingDevice) {
            isAddingDevice = false
        } else {
            viewModel.setNavMode(AppNavMode.CLASSIC)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { deviceDropdownExpanded = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("device_selector_top")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E5FF).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (activeDevice?.connectionType == "WIFI") Icons.Default.Wifi else Icons.Default.Tv,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = activeDevice?.name ?: stringResource(R.string.app_name),
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${activeDevice?.brand ?: "TV"} • Tap to switch",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch",
                            tint = Color(0xFF00E5FF)
                        )

                        // Dropdown Menu for fast switching between saved remotes
                        DropdownMenu(
                            expanded = deviceDropdownExpanded,
                            onDismissRequest = { deviceDropdownExpanded = false },
                            modifier = Modifier
                                .background(Color(0xFF131A26))
                                .border(1.dp, Color(0xFF263346), RoundedCornerShape(12.dp))
                        ) {
                            allDevices.forEach { dev ->
                                val isSelected = dev.id == activeDevice?.id
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = dev.name,
                                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = "${dev.brand} (${dev.connectionType})",
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 11.sp
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00E5FF),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.selectDevice(dev)
                                        deviceDropdownExpanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("+ Add New Remote", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                                    }
                                },
                                onClick = {
                                    deviceDropdownExpanded = false
                                    isAddingDevice = true
                                }
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isAddingDevice = true },
                        modifier = Modifier.testTag("action_add_device")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Remote",
                            tint = Color(0xFF00E5FF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D121B)
                )
            )
        },
        bottomBar = {
            if (!isAddingDevice) {
                NavigationBar(
                    containerColor = Color(0xFF0D121B),
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF1B2433),
                            shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                        )
                ) {
                    val navItems = listOf(
                        AppNavMode.CLASSIC to (stringResource(R.string.mode_classic) to Icons.Default.Tv),
                        AppNavMode.TOUCH_MOUSE to (stringResource(R.string.mode_mouse) to Icons.Default.Mouse),
                        AppNavMode.SMART_HUB to (stringResource(R.string.mode_smart) to Icons.Default.FlashOn),
                        AppNavMode.DEVICES to (stringResource(R.string.tab_devices) to Icons.Default.GridView),
                        AppNavMode.SETTINGS to (stringResource(R.string.tab_settings) to Icons.Default.Settings)
                    )

                    navItems.forEach { (mode, pair) ->
                        val (label, icon) = pair
                        val selected = navMode == mode
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.setNavMode(mode) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0A0F1A),
                                selectedTextColor = Color(0xFF00E5FF),
                                indicatorColor = Color(0xFF00E5FF),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier.testTag("nav_${mode.name.lowercase()}")
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF0A0D14)
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isAddingDevice) {
                AddDeviceScreen(
                    hasHardwareIr = viewModel.irHardwareInfo.hasIrEmitter,
                    discoveredTvs = discoveredTvs,
                    isScanningWifi = isScanningWifi,
                    onTestPower = { brand, proto, freq, code ->
                        viewModel.testPowerCode(brand, proto, freq, code)
                    },
                    onSaveDevice = { name, brand, model, type, conn, ip, port ->
                        viewModel.createDevice(name, brand, model, type, conn, ip, port)
                        isAddingDevice = false
                    },
                    onScanWifi = { viewModel.scanWifiNetworks() },
                    onBack = { isAddingDevice = false }
                )
            } else {
                when (navMode) {
                    AppNavMode.CLASSIC -> {
                        ClassicRemoteScreen(
                            activeDevice = activeDevice,
                            isTransmitting = isTransmitting,
                            hasHardwareIr = viewModel.irHardwareInfo.hasIrEmitter,
                            numericPadOpen = numericPadOpen,
                            onPressButton = { viewModel.pressButton(it) },
                            onToggleNumericPad = { viewModel.toggleNumericPad() }
                        )
                    }
                    AppNavMode.TOUCH_MOUSE -> {
                        TouchMouseScreen(
                            mouseState = mouseState,
                            onMove = { dx, dy -> viewModel.onMouseMove(dx, dy) },
                            onClick = { viewModel.onMouseClick() },
                            onDoubleClick = { viewModel.onMouseDoubleClick() },
                            onRightClick = { viewModel.onMouseRightClick() },
                            onScroll = { delta -> viewModel.onMouseScroll(delta) },
                            onSensitivityChange = { viewModel.setMouseSensitivity(it) },
                            onToggleActive = { viewModel.toggleMouseActive(it) }
                        )
                    }
                    AppNavMode.SMART_HUB -> {
                        SmartHubScreen(
                            activeDevice = activeDevice,
                            onPressButton = { viewModel.pressButton(it) },
                            onOpenAddDevice = { isAddingDevice = true }
                        )
                    }
                    AppNavMode.DEVICES -> {
                        DevicesListScreen(
                            devices = allDevices,
                            activeDevice = activeDevice,
                            onSelectDevice = {
                                viewModel.selectDevice(it)
                                viewModel.setNavMode(AppNavMode.CLASSIC)
                            },
                            onDeleteDevice = { viewModel.deleteDevice(it) },
                            onOpenAddDevice = { isAddingDevice = true }
                        )
                    }
                    AppNavMode.SETTINGS -> {
                        SettingsScreen(
                            irHardwareInfo = viewModel.irHardwareInfo,
                            isHapticEnabled = isHapticEnabled,
                            isDarkTheme = isDarkTheme,
                            localIpAddress = viewModel.networkController.getLocalIpAddress(),
                            onToggleHaptic = { viewModel.toggleHaptics() },
                            onToggleTheme = { viewModel.toggleTheme() }
                        )
                    }
                }
            }

            // Real-time status toast pill at bottom
            if (statusBanner != null && !isAddingDevice) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF161F2E).copy(alpha = 0.95f))
                        .border(1.dp, Color(0xFF2C394F), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusBanner ?: "",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
