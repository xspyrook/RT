package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DeviceEntity
import com.example.model.RemoteButtonType
import com.example.ui.components.ButtonStyleVariant
import com.example.ui.components.DirectionalPad
import com.example.ui.components.IrEmitterIndicator
import com.example.ui.components.RemoteButton
import com.example.ui.components.RockerSwitch

@Composable
fun ClassicRemoteScreen(
    activeDevice: DeviceEntity?,
    isTransmitting: Boolean,
    hasHardwareIr: Boolean,
    numericPadOpen: Boolean,
    onPressButton: (RemoteButtonType) -> Unit,
    onToggleNumericPad: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Infrared LED status indicator
        IrEmitterIndicator(
            isTransmitting = isTransmitting,
            deviceName = activeDevice?.name ?: stringResource(R.string.tv_not_connected),
            hasHardwareIr = hasHardwareIr,
            connectionType = activeDevice?.connectionType ?: "IR"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Remote Ergonomic Chassis
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF111622)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222C3E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1: Power, Source, Mute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteButton(
                        icon = Icons.Default.PowerSettingsNew,
                        label = stringResource(R.string.btn_power),
                        variant = ButtonStyleVariant.POWER,
                        size = 58.dp,
                        onClick = { onPressButton(RemoteButtonType.POWER) },
                        testTag = "btn_power"
                    )

                    RemoteButton(
                        icon = Icons.AutoMirrored.Filled.Input,
                        label = stringResource(R.string.btn_source),
                        variant = ButtonStyleVariant.NEUTRAL,
                        size = 50.dp,
                        onClick = { onPressButton(RemoteButtonType.SOURCE) },
                        testTag = "btn_source"
                    )

                    RemoteButton(
                        icon = Icons.Default.VolumeMute,
                        label = stringResource(R.string.btn_mute),
                        variant = ButtonStyleVariant.NEUTRAL,
                        size = 50.dp,
                        onClick = { onPressButton(RemoteButtonType.MUTE) },
                        testTag = "btn_mute"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Row 2: Media playback controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF171E2B))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteButton(
                        icon = Icons.Default.FastRewind,
                        label = stringResource(R.string.btn_rewind),
                        size = 42.dp,
                        onClick = { onPressButton(RemoteButtonType.REWIND) },
                        testTag = "btn_rewind"
                    )
                    RemoteButton(
                        icon = Icons.Default.PlayArrow,
                        label = stringResource(R.string.btn_play),
                        size = 42.dp,
                        onClick = { onPressButton(RemoteButtonType.PLAY) },
                        testTag = "btn_play"
                    )
                    RemoteButton(
                        icon = Icons.Default.Pause,
                        label = stringResource(R.string.btn_pause),
                        size = 42.dp,
                        onClick = { onPressButton(RemoteButtonType.PAUSE) },
                        testTag = "btn_pause"
                    )
                    RemoteButton(
                        icon = Icons.Default.FastForward,
                        label = stringResource(R.string.btn_fast_forward),
                        size = 42.dp,
                        onClick = { onPressButton(RemoteButtonType.FAST_FORWARD) },
                        testTag = "btn_fast_forward"
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Directional Pad (D-Pad) Navigation
                DirectionalPad(
                    onUp = { onPressButton(RemoteButtonType.NAV_UP) },
                    onDown = { onPressButton(RemoteButtonType.NAV_DOWN) },
                    onLeft = { onPressButton(RemoteButtonType.NAV_LEFT) },
                    onRight = { onPressButton(RemoteButtonType.NAV_RIGHT) },
                    onOk = { onPressButton(RemoteButtonType.OK) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Row 3: Back, Home, Menu, Exit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RemoteButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        label = stringResource(R.string.btn_back),
                        size = 48.dp,
                        onClick = { onPressButton(RemoteButtonType.BACK) },
                        testTag = "btn_back"
                    )
                    RemoteButton(
                        icon = Icons.Default.Home,
                        label = stringResource(R.string.btn_home),
                        variant = ButtonStyleVariant.PRIMARY_CYAN,
                        size = 52.dp,
                        onClick = { onPressButton(RemoteButtonType.HOME) },
                        testTag = "btn_home"
                    )
                    RemoteButton(
                        icon = Icons.Default.Menu,
                        label = stringResource(R.string.btn_menu),
                        size = 48.dp,
                        onClick = { onPressButton(RemoteButtonType.MENU) },
                        testTag = "btn_menu"
                    )
                    RemoteButton(
                        icon = Icons.Default.Close,
                        label = stringResource(R.string.btn_exit),
                        size = 48.dp,
                        onClick = { onPressButton(RemoteButtonType.EXIT) },
                        testTag = "btn_exit"
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Central Control Section: Rockers (Vol & CH) + Center Utility Keys
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Volume Rocker
                    RockerSwitch(
                        title = "VOL",
                        onUp = { onPressButton(RemoteButtonType.VOL_UP) },
                        onDown = { onPressButton(RemoteButtonType.VOL_DOWN) },
                        upIcon = Icons.Default.Add,
                        downIcon = Icons.Default.Remove
                    )

                    // Center column: Numbers keypad toggle, Info, Prev CH
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        RemoteButton(
                            icon = Icons.Default.Keyboard,
                            label = "123",
                            size = 46.dp,
                            variant = if (numericPadOpen) ButtonStyleVariant.PRIMARY_CYAN else ButtonStyleVariant.NEUTRAL,
                            onClick = onToggleNumericPad,
                            testTag = "btn_toggle_numpad"
                        )
                        RemoteButton(
                            icon = Icons.Default.Info,
                            label = stringResource(R.string.btn_info),
                            size = 44.dp,
                            onClick = { onPressButton(RemoteButtonType.INFO) },
                            testTag = "btn_info"
                        )
                        RemoteButton(
                            icon = Icons.Default.Replay,
                            label = stringResource(R.string.btn_prev_channel),
                            size = 44.dp,
                            onClick = { onPressButton(RemoteButtonType.PREV_CH) },
                            testTag = "btn_prev_ch"
                        )
                    }

                    // Channel Rocker
                    RockerSwitch(
                        title = "CH",
                        onUp = { onPressButton(RemoteButtonType.CH_UP) },
                        onDown = { onPressButton(RemoteButtonType.CH_DOWN) },
                        upIcon = Icons.Default.KeyboardArrowUp,
                        downIcon = Icons.Default.KeyboardArrowDown
                    )
                }

                // Expandable Numeric Keypad (0-9)
                AnimatedVisibility(
                    visible = numericPadOpen,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F141F))
                            .border(1.dp, Color(0xFF263246), RoundedCornerShape(20.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val numRows = listOf(
                            listOf(RemoteButtonType.NUM_1, RemoteButtonType.NUM_2, RemoteButtonType.NUM_3),
                            listOf(RemoteButtonType.NUM_4, RemoteButtonType.NUM_5, RemoteButtonType.NUM_6),
                            listOf(RemoteButtonType.NUM_7, RemoteButtonType.NUM_8, RemoteButtonType.NUM_9)
                        )

                        numRows.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                row.forEach { btn ->
                                    RemoteButton(
                                        label = btn.defaultLabelEn,
                                        size = 48.dp,
                                        variant = ButtonStyleVariant.NUMERIC,
                                        onClick = { onPressButton(btn) },
                                        testTag = "num_${btn.codeKey}"
                                    )
                                }
                            }
                        }

                        // Last row: Prev CH, 0, Clear
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RemoteButton(
                                label = "CH-",
                                size = 48.dp,
                                variant = ButtonStyleVariant.NUMERIC,
                                onClick = { onPressButton(RemoteButtonType.PREV_CH) },
                                testTag = "num_prev"
                            )
                            RemoteButton(
                                label = "0",
                                size = 48.dp,
                                variant = ButtonStyleVariant.NUMERIC,
                                onClick = { onPressButton(RemoteButtonType.NUM_0) },
                                testTag = "num_0"
                            )
                            RemoteButton(
                                label = "OK",
                                size = 48.dp,
                                variant = ButtonStyleVariant.PRIMARY_CYAN,
                                onClick = { onPressButton(RemoteButtonType.OK) },
                                testTag = "num_ok"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Colored Function Keys (Red, Green, Yellow, Blue)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEF4444))
                            .shadow(4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF22C55E))
                            .shadow(4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFBBF24))
                            .shadow(4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF3B82F6))
                            .shadow(4.dp)
                    )
                }
            }
        }
    }
}
