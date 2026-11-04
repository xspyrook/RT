package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.DeviceEntity
import com.example.model.RemoteButtonType
import com.example.ui.components.ButtonStyleVariant
import com.example.ui.components.DirectionalPad
import com.example.ui.components.RemoteButton

@Composable
fun SmartHubScreen(
    activeDevice: DeviceEntity?,
    onPressButton: (RemoteButtonType) -> Unit,
    onOpenAddDevice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Active Device Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A26)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243044))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (activeDevice?.connectionType == "WIFI") Icons.Default.Wifi else Icons.Default.Tv,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = activeDevice?.name ?: "No Remote Selected",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activeDevice?.brand ?: "Brand"} • ${activeDevice?.model ?: "Model"} (${activeDevice?.connectionType ?: "IR"})",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                // Master Power Button
                RemoteButton(
                    icon = Icons.Default.PowerSettingsNew,
                    label = "Power",
                    variant = ButtonStyleVariant.POWER,
                    size = 54.dp,
                    onClick = { onPressButton(RemoteButtonType.POWER) },
                    testTag = "smart_hub_power"
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Input Sources Row
        Text(
            text = "QUICK INPUT & APPS",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 4.dp, bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val inputs = listOf(
                "HDMI 1" to RemoteButtonType.SOURCE,
                "TV / Ant" to RemoteButtonType.PREV_CH,
                "Home Hub" to RemoteButtonType.HOME,
                "Settings" to RemoteButtonType.MENU
            )
            inputs.forEach { (label, btn) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF161E2C))
                        .border(1.dp, Color(0xFF273448), RoundedCornerShape(12.dp))
                        .clickable { onPressButton(btn) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Volume & Navigation Matrix
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick Volume Pill
            Column(
                modifier = Modifier
                    .width(72.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFF131A26))
                    .border(1.dp, Color(0xFF243044), RoundedCornerShape(36.dp)),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { onPressButton(RemoteButtonType.VOL_UP) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Vol +",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E2838))
                        .clickable { onPressButton(RemoteButtonType.MUTE) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeMute,
                        contentDescription = "Mute",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable { onPressButton(RemoteButtonType.VOL_DOWN) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = "Vol -",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Central Navigation D-Pad
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                DirectionalPad(
                    onUp = { onPressButton(RemoteButtonType.NAV_UP) },
                    onDown = { onPressButton(RemoteButtonType.NAV_DOWN) },
                    onLeft = { onPressButton(RemoteButtonType.NAV_LEFT) },
                    onRight = { onPressButton(RemoteButtonType.NAV_RIGHT) },
                    onOk = { onPressButton(RemoteButtonType.OK) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Media Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF141B26))
                .border(1.dp, Color(0xFF232E40), RoundedCornerShape(16.dp))
                .padding(vertical = 10.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RemoteButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                label = stringResource(R.string.btn_back),
                size = 46.dp,
                onClick = { onPressButton(RemoteButtonType.BACK) }
            )
            RemoteButton(
                icon = Icons.Default.PlayArrow,
                label = stringResource(R.string.btn_play),
                variant = ButtonStyleVariant.PRIMARY_CYAN,
                size = 50.dp,
                onClick = { onPressButton(RemoteButtonType.PLAY) }
            )
            RemoteButton(
                icon = Icons.Default.Pause,
                label = stringResource(R.string.btn_pause),
                size = 46.dp,
                onClick = { onPressButton(RemoteButtonType.PAUSE) }
            )
            RemoteButton(
                icon = Icons.Default.Home,
                label = stringResource(R.string.btn_home),
                size = 46.dp,
                onClick = { onPressButton(RemoteButtonType.HOME) }
            )
        }
    }
}
