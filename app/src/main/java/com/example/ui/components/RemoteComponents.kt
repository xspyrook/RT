package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RemoteButtonType

enum class ButtonStyleVariant {
    POWER,
    PRIMARY_CYAN,
    NEUTRAL,
    NUMERIC,
    ACTION_PILL
}

@Composable
fun RemoteButton(
    icon: ImageVector? = null,
    label: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonStyleVariant = ButtonStyleVariant.NEUTRAL,
    size: Dp = 56.dp,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
    testTag: String = "remote_btn"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = if (isPressed) 0.92f else 1.0f

    val backgroundColor = when (variant) {
        ButtonStyleVariant.POWER -> if (isPressed) Color(0xFFCC1144) else Color(0xFFFF2B56)
        ButtonStyleVariant.PRIMARY_CYAN -> if (isPressed) Color(0xFF00B0FF) else Color(0xFF00E5FF)
        ButtonStyleVariant.ACTION_PILL -> if (isPressed) Color(0xFF283244) else Color(0xFF1E2636)
        ButtonStyleVariant.NUMERIC -> if (isPressed) Color(0xFF222B3D) else Color(0xFF181F2C)
        ButtonStyleVariant.NEUTRAL -> if (isPressed) Color(0xFF273142) else Color(0xFF1A2230)
    }

    val contentColor = when (variant) {
        ButtonStyleVariant.POWER -> Color.White
        ButtonStyleVariant.PRIMARY_CYAN -> Color(0xFF0A0F1A)
        else -> Color(0xFFE2E8F0)
    }

    val borderColor = when (variant) {
        ButtonStyleVariant.POWER -> Color(0xFFFF5E80).copy(alpha = 0.5f)
        ButtonStyleVariant.PRIMARY_CYAN -> Color(0xFF80EAFF).copy(alpha = 0.6f)
        else -> Color(0xFF2E3A4E)
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .shadow(if (isPressed) 2.dp else 6.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = label ?: "Button",
                tint = contentColor,
                modifier = Modifier.size((size.value * 0.44f).dp)
            )
        } else if (label != null) {
            Text(
                text = label,
                color = contentColor,
                fontSize = if (label.length > 3) 11.sp else 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Ergonomic Circular Directional Pad (D-Pad) with Center OK button
 */
@Composable
fun DirectionalPad(
    onUp: () -> Unit,
    onDown: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onOk: () -> Unit,
    modifier: Modifier = Modifier
) {
    val padSize = 196.dp
    val buttonSize = 48.dp

    Box(
        modifier = modifier
            .size(padSize)
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF222B3B),
                        Color(0xFF141A25),
                        Color(0xFF0F131C)
                    )
                )
            )
            .border(2.dp, Color(0xFF2C394F), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Up
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .size(buttonSize)
                .clip(CircleShape)
                .clickable { onUp() }
                .testTag("dpad_up"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Up",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(32.dp)
            )
        }

        // Down
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .size(buttonSize)
                .clip(CircleShape)
                .clickable { onDown() }
                .testTag("dpad_down"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Down",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(32.dp)
            )
        }

        // Left
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(buttonSize)
                .clip(CircleShape)
                .clickable { onLeft() }
                .testTag("dpad_left"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Left",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(32.dp)
            )
        }

        // Right
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
                .size(buttonSize)
                .clip(CircleShape)
                .clickable { onRight() }
                .testTag("dpad_right"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Right",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(32.dp)
            )
        }

        // Center OK button
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFF0091EA))
                    )
                )
                .border(2.dp, Color(0xFF80D8FF), CircleShape)
                .clickable { onOk() }
                .testTag("dpad_ok"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OK",
                color = Color(0xFF0A0F1A),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        }
    }
}

/**
 * Vertical Rocker Switch for Volume (Vol+/Vol-) or Channel (CH+/CH-)
 */
@Composable
fun RockerSwitch(
    title: String,
    onUp: () -> Unit,
    onDown: () -> Unit,
    upIcon: ImageVector,
    downIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(58.dp)
            .height(132.dp)
            .shadow(8.dp, RoundedCornerShape(29.dp))
            .clip(RoundedCornerShape(29.dp))
            .background(Color(0xFF171E2B))
            .border(1.dp, Color(0xFF2C384D), RoundedCornerShape(29.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Up Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onUp() }
                .testTag("rocker_${title}_up"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = upIcon,
                contentDescription = "$title +",
                tint = Color(0xFFE2E8F0),
                modifier = Modifier.size(24.dp)
            )
        }

        // Center Label
        Text(
            text = title,
            color = Color(0xFF00E5FF),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        // Down Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onDown() }
                .testTag("rocker_${title}_down"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = downIcon,
                contentDescription = "$title -",
                tint = Color(0xFFE2E8F0),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Glowing infrared LED indicator at the top of the remote
 */
@Composable
fun IrEmitterIndicator(
    isTransmitting: Boolean,
    deviceName: String,
    hasHardwareIr: Boolean,
    connectionType: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing LED diode
        Box(
            modifier = Modifier
                .size(if (isTransmitting) 18.dp else 12.dp)
                .shadow(
                    elevation = if (isTransmitting) 16.dp else 2.dp,
                    shape = CircleShape,
                    spotColor = if (isTransmitting) Color(0xFFFF2B56) else Color(0xFF00E5FF)
                )
                .clip(CircleShape)
                .background(
                    if (isTransmitting) Color(0xFFFF2B56)
                    else if (hasHardwareIr) Color(0xFF00E5FF).copy(alpha = 0.5f)
                    else Color(0xFF475569)
                )
                .border(
                    width = 1.dp,
                    color = if (isTransmitting) Color.White else Color(0xFF334155),
                    shape = CircleShape
                )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (connectionType == "WIFI") Icons.Default.Wifi else Icons.Default.Tv,
                contentDescription = null,
                tint = if (connectionType == "WIFI") Color(0xFF38BDF8) else Color(0xFF00E5FF),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = deviceName,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (connectionType == "WIFI") "• Wi-Fi" else if (hasHardwareIr) "• IR 38kHz" else "• IR (Sim)",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )
        }
    }
}
