package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mouse.MouseState
import com.example.ui.components.ButtonStyleVariant
import com.example.ui.components.RemoteButton

@Composable
fun TouchMouseScreen(
    mouseState: MouseState,
    onMove: (Float, Float) -> Unit,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit,
    onRightClick: () -> Unit,
    onScroll: (Float) -> Unit,
    onSensitivityChange: (Float) -> Unit,
    onToggleActive: (Boolean) -> Unit,
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
        // Mode Header & Active Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A26)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243044))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (mouseState.isEnabled) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = if (mouseState.isEnabled) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.mouse_title),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (mouseState.isEnabled) stringResource(R.string.mouse_mode_active) else stringResource(R.string.mouse_mode_inactive),
                            color = if (mouseState.isEnabled) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }

                Switch(
                    checked = mouseState.isEnabled,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E5FF),
                        checkedTrackColor = Color(0xFF004D5C),
                        uncheckedThumbColor = Color(0xFF64748B),
                        uncheckedTrackColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.testTag("switch_mouse_active")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Virtual TV Mini-Screen with Live Cursor Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D14)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E2A3C))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // TV Screen Mockup with Animated Cursor Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    val gridColor = Color(0xFF1E2838)
                    drawLine(gridColor, Offset(w * 0.33f, 0f), Offset(w * 0.33f, h), 1f)
                    drawLine(gridColor, Offset(w * 0.66f, 0f), Offset(w * 0.66f, h), 1f)
                    drawLine(gridColor, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), 1f)

                    // Cursor position
                    val cx = w * mouseState.cursorX
                    val cy = h * mouseState.cursorY

                    // Cursor glow
                    drawCircle(
                        color = Color(0xFF00E5FF).copy(alpha = 0.25f),
                        radius = 16f,
                        center = Offset(cx, cy)
                    )
                    // Cursor pointer ring
                    drawCircle(
                        color = Color(0xFF00E5FF),
                        radius = 6f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.5f,
                        center = Offset(cx, cy)
                    )
                }

                // Mini TV Label
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "TV Screen Preview (${(mouseState.cursorX * 100).toInt()}%, ${(mouseState.cursorY * 100).toInt()}%)",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = mouseState.lastAction,
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Large Trackpad Area + Side Scroll Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Main Touchpad Surface
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1B2433),
                                Color(0xFF121824),
                                Color(0xFF0D121B)
                            )
                        )
                    )
                    .border(2.dp, if (mouseState.isEnabled) Color(0xFF00E5FF).copy(alpha = 0.5f) else Color(0xFF263346), RoundedCornerShape(24.dp))
                    .pointerInput(mouseState.isEnabled) {
                        if (!mouseState.isEnabled) return@pointerInput
                        detectTapGestures(
                            onTap = { onClick() },
                            onDoubleTap = { onDoubleClick() },
                            onLongPress = { onRightClick() }
                        )
                    }
                    .pointerInput(mouseState.isEnabled) {
                        if (!mouseState.isEnabled) return@pointerInput
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            onMove(dragAmount.x, dragAmount.y)
                        }
                    }
                    .testTag("trackpad_surface"),
                contentAlignment = Alignment.Center
            ) {
                // Subtle concentric rings indicating trackpad center
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val c = Offset(size.width / 2, size.height / 2)
                    drawCircle(Color(0xFF263346).copy(alpha = 0.3f), radius = 60f, center = c, style = Stroke(1.5f))
                    drawCircle(Color(0xFF263346).copy(alpha = 0.2f), radius = 110f, center = c, style = Stroke(1.5f))
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = if (mouseState.isEnabled) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color(0xFF475569),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.mouse_trackpad_hint),
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Scroll Bar Rail (Scroll Up / Scroll Down)
            Column(
                modifier = Modifier
                    .width(56.dp)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141B26))
                    .border(1.dp, Color(0xFF243044), RoundedCornerShape(20.dp)),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .clickable { onScroll(-1f) }
                        .testTag("mouse_scroll_up"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = stringResource(R.string.mouse_scroll_up),
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                        Text("UP", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(2.dp)
                        .background(Color(0xFF334155))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                        .clickable { onScroll(1f) }
                        .testTag("mouse_scroll_down"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("DOWN", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = stringResource(R.string.mouse_scroll_down),
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Left Click & Right Click Buttons (Physical Feel)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Left Click (Select)
            Button(
                onClick = onClick,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("mouse_btn_left_click"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E5FF),
                    contentColor = Color(0xFF0A0F1A)
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.mouse_left_click),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Right Click (Back)
            Button(
                onClick = onRightClick,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("mouse_btn_right_click"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E2838),
                    contentColor = Color(0xFFE2E8F0)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.mouse_right_click),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sensitivity Control Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131A26)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243044))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.mouse_sensitivity),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = String.format("%.1fx", mouseState.sensitivity),
                        color = Color(0xFF00E5FF),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Slider(
                    value = mouseState.sensitivity,
                    onValueChange = onSensitivityChange,
                    valueRange = 0.5f..2.5f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF00E5FF),
                        activeTrackColor = Color(0xFF00E5FF),
                        inactiveTrackColor = Color(0xFF243044)
                    ),
                    modifier = Modifier.testTag("slider_mouse_sensitivity")
                )

                Text(
                    text = "Mode: ${mouseState.connectionModeName} • Swipe threshold maps to discrete TV navigation ticks",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }
        }
    }
}
