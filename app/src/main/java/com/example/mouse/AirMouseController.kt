package com.example.mouse

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MouseState(
    val isEnabled: Boolean = true,
    val cursorX: Float = 0.5f, // Normalized 0f..1f
    val cursorY: Float = 0.5f,
    val sensitivity: Float = 1.2f,
    val lastAction: String = "Ready",
    val clickCount: Int = 0,
    val isBluetoothHidSupported: Boolean = false,
    val connectionModeName: String = "Smart Network / Virtual HID"
)

class AirMouseController(private val context: Context) {

    private val _mouseState = MutableStateFlow(MouseState())
    val mouseState: StateFlow<MouseState> = _mouseState.asStateFlow()

    private var accumulatedDx = 0f
    private var accumulatedDy = 0f
    private val swipeThreshold = 45f // Threshold to fire discrete TV directional step

    init {
        checkBluetoothHidCapability()
    }

    private fun checkBluetoothHidCapability() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val hasBt = bluetoothAdapter != null && bluetoothAdapter.isEnabled
        val isHidSupported = hasBt && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
        _mouseState.update {
            it.copy(
                isBluetoothHidSupported = isHidSupported,
                connectionModeName = if (isHidSupported) "Bluetooth HID + Network" else "Smart TV Network Mode"
            )
        }
    }

    fun setEnabled(enabled: Boolean) {
        _mouseState.update { it.copy(isEnabled = enabled, lastAction = if (enabled) "Mouse Active" else "Mouse Standby") }
    }

    fun setSensitivity(sensitivity: Float) {
        _mouseState.update { it.copy(sensitivity = sensitivity.coerceIn(0.4f, 3.0f)) }
    }

    /**
     * Handles touch delta from touchpad.
     * Returns a directional step (Up, Down, Left, Right) if threshold exceeded,
     * allowing directional TV navigation for smart TVs without native mouse pointers.
     */
    fun onMove(deltaX: Float, deltaY: Float): Pair<Float, Float> {
        val current = _mouseState.value
        if (!current.isEnabled) return 0f to 0f

        val sens = current.sensitivity
        val scaledDx = deltaX * sens
        val scaledDy = deltaY * sens

        // Update normalized screen cursor
        val newX = (current.cursorX + (scaledDx / 800f)).coerceIn(0.02f, 0.98f)
        val newY = (current.cursorY + (scaledDy / 800f)).coerceIn(0.02f, 0.98f)

        accumulatedDx += scaledDx
        accumulatedDy += scaledDy

        _mouseState.update {
            it.copy(
                cursorX = newX,
                cursorY = newY,
                lastAction = "Moving (dx=${scaledDx.toInt()}, dy=${scaledDy.toInt()})"
            )
        }

        return scaledDx to scaledDy
    }

    /**
     * Checks if accumulated delta crossed the threshold to trigger TV navigation tick.
     */
    fun consumeDirectionalStep(): String? {
        val dx = accumulatedDx
        val dy = accumulatedDy

        if (Math.abs(dx) > swipeThreshold && Math.abs(dx) > Math.abs(dy)) {
            accumulatedDx = 0f
            return if (dx > 0) "RIGHT" else "LEFT"
        } else if (Math.abs(dy) > swipeThreshold) {
            accumulatedDy = 0f
            return if (dy > 0) "DOWN" else "UP"
        }
        return null
    }

    fun onSingleClick(): String {
        _mouseState.update {
            it.copy(
                lastAction = "Left Click (Select)",
                clickCount = it.clickCount + 1
            )
        }
        return "CLICK"
    }

    fun onDoubleClick(): String {
        _mouseState.update {
            it.copy(
                lastAction = "Double Click (Open)",
                clickCount = it.clickCount + 2
            )
        }
        return "DOUBLE_CLICK"
    }

    fun onRightClick(): String {
        _mouseState.update {
            it.copy(lastAction = "Right Click (Back)")
        }
        return "BACK"
    }

    fun onScroll(deltaY: Float): String {
        val direction = if (deltaY < 0) "SCROLL_UP" else "SCROLL_DOWN"
        _mouseState.update {
            it.copy(lastAction = if (deltaY < 0) "Scroll Up" else "Scroll Down")
        }
        return direction
    }
}
