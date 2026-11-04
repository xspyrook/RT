package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DeviceEntity
import com.example.data.DeviceRepository
import com.example.ir.ConsumerIrController
import com.example.ir.IrHardwareInfo
import com.example.ir.IrSignalBuilder
import com.example.ir.TransmissionResult
import com.example.model.BrandCatalog
import com.example.model.ConnectionType
import com.example.model.DeviceType
import com.example.model.DiscoveredTv
import com.example.model.IrProtocol
import com.example.model.RemoteButtonType
import com.example.mouse.AirMouseController
import com.example.mouse.MouseState
import com.example.network.TvNetworkController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppNavMode {
    CLASSIC,
    TOUCH_MOUSE,
    SMART_HUB,
    DEVICES,
    SETTINGS
}

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeviceRepository
    val irController: ConsumerIrController
    val networkController: TvNetworkController
    val mouseController: AirMouseController

    val irHardwareInfo: IrHardwareInfo

    val allDevices: StateFlow<List<DeviceEntity>>

    private val _activeDevice = MutableStateFlow<DeviceEntity?>(null)
    val activeDevice: StateFlow<DeviceEntity?> = _activeDevice.asStateFlow()

    private val _navMode = MutableStateFlow(AppNavMode.CLASSIC)
    val navMode: StateFlow<AppNavMode> = _navMode.asStateFlow()

    private val _isTransmittingPulse = MutableStateFlow(false)
    val isTransmittingPulse: StateFlow<Boolean> = _isTransmittingPulse.asStateFlow()

    private val _lastTransmission = MutableStateFlow<TransmissionResult?>(null)
    val lastTransmission: StateFlow<TransmissionResult?> = _lastTransmission.asStateFlow()

    private val _isHapticEnabled = MutableStateFlow(true)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _discoveredTvs = MutableStateFlow<List<DiscoveredTv>>(emptyList())
    val discoveredTvs: StateFlow<List<DiscoveredTv>> = _discoveredTvs.asStateFlow()

    private val _isScanningWifi = MutableStateFlow(false)
    val isScanningWifi: StateFlow<Boolean> = _isScanningWifi.asStateFlow()

    private val _numericPadOpen = MutableStateFlow(false)
    val numericPadOpen: StateFlow<Boolean> = _numericPadOpen.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>("Ready")
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    val mouseState: StateFlow<MouseState>

    private var pulseJob: Job? = null
    private var scanJob: Job? = null

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DeviceRepository(database.deviceDao())
        irController = ConsumerIrController(application)
        networkController = TvNetworkController(application)
        mouseController = AirMouseController(application)
        mouseState = mouseController.mouseState

        irHardwareInfo = irController.getHardwareInfo()

        allDevices = repository.allDevices.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            allDevices.collect { list ->
                if (list.isEmpty()) {
                    repository.seedDefaultsIfEmpty()
                } else if (_activeDevice.value == null) {
                    _activeDevice.value = list.firstOrNull { it.isFavorite } ?: list.first()
                }
            }
        }
    }

    fun setNavMode(mode: AppNavMode) {
        _navMode.value = mode
    }

    fun toggleNumericPad() {
        _numericPadOpen.update { !it }
    }

    fun toggleHaptics() {
        _isHapticEnabled.update { !it }
    }

    fun toggleTheme() {
        _isDarkTheme.update { !it }
    }

    fun selectDevice(device: DeviceEntity) {
        _activeDevice.value = device
        viewModelScope.launch {
            repository.updateLastUsed(device.id)
        }
        _statusBanner.value = "Switched to ${device.name}"
    }

    private fun triggerHaptic() {
        if (!_isHapticEnabled.value) return
        try {
            val app = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (e: Exception) {
            // Ignore haptic failures
        }
    }

    private fun triggerPulseAnimation() {
        pulseJob?.cancel()
        pulseJob = viewModelScope.launch {
            _isTransmittingPulse.value = true
            delay(350)
            _isTransmittingPulse.value = false
        }
    }

    fun pressButton(button: RemoteButtonType) {
        triggerHaptic()
        triggerPulseAnimation()

        val device = _activeDevice.value
        val brand = device?.brand ?: "Samsung"
        val deviceType = DeviceType.values().firstOrNull { it.name == device?.deviceType } ?: DeviceType.TV
        val preset = BrandCatalog.getPreset(brand, deviceType)

        if (device != null && device.connectionType == ConnectionType.WIFI.name && device.ipAddress != null) {
            // Wi-Fi LAN dispatch
            viewModelScope.launch {
                _statusBanner.value = "Sending ${button.defaultLabelEn} via Wi-Fi..."
                val result = networkController.sendCommand(device.ipAddress, device.port, button)
                _statusBanner.value = result.message
            }
        } else {
            // IR Dispatch
            val protocol = try {
                IrProtocol.valueOf(device?.irProtocol ?: preset.protocol.name)
            } catch (e: Exception) {
                IrProtocol.NEC
            }
            val commandCode = preset.keyCodes[button] ?: 0x02
            val address = preset.address
            val freq = device?.carrierFreq ?: preset.carrierFreq

            val result = irController.sendCommand(
                protocol = protocol,
                carrierFrequency = freq,
                address = address,
                commandCode = commandCode,
                label = button.defaultLabelEn
            )
            _lastTransmission.value = result
            _statusBanner.value = result.message
        }
    }

    fun testPowerCode(brand: String, protocol: IrProtocol, carrierFreq: Int, code: Int = 0x02): TransmissionResult {
        triggerHaptic()
        triggerPulseAnimation()
        val preset = BrandCatalog.getPreset(brand, DeviceType.TV)
        val result = irController.sendCommand(
            protocol = protocol,
            carrierFrequency = carrierFreq,
            address = preset.address,
            commandCode = code,
            label = "Test Power ($brand)"
        )
        _lastTransmission.value = result
        _statusBanner.value = result.message
        return result
    }

    fun createDevice(
        name: String,
        brand: String,
        model: String,
        deviceType: DeviceType,
        connectionType: ConnectionType,
        ipAddress: String? = null,
        port: Int = 8060
    ) {
        viewModelScope.launch {
            val preset = BrandCatalog.getPreset(brand, deviceType)
            val entity = DeviceEntity(
                name = name.ifBlank { "$brand ${deviceType.labelEn}" },
                brand = brand,
                model = model.ifBlank { preset.defaultModel },
                deviceType = deviceType.name,
                connectionType = connectionType.name,
                ipAddress = ipAddress?.trim(),
                port = port,
                carrierFreq = preset.carrierFreq,
                irProtocol = preset.protocol.name,
                isFavorite = false
            )
            val id = repository.insert(entity)
            _activeDevice.value = entity.copy(id = id)
            _statusBanner.value = "Saved remote: ${entity.name}"
            _navMode.value = AppNavMode.CLASSIC
        }
    }

    fun deleteDevice(device: DeviceEntity) {
        viewModelScope.launch {
            repository.delete(device)
            val currentList = allDevices.value.filter { it.id != device.id }
            if (_activeDevice.value?.id == device.id) {
                _activeDevice.value = currentList.firstOrNull()
            }
            _statusBanner.value = "Removed ${device.name}"
        }
    }

    fun scanWifiNetworks() {
        if (_isScanningWifi.value) return
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _isScanningWifi.value = true
            _statusBanner.value = "Scanning local Wi-Fi for Smart TVs..."
            val tvs = networkController.discoverDevices()
            _discoveredTvs.value = tvs
            _isScanningWifi.value = false
            _statusBanner.value = if (tvs.isNotEmpty()) "Found ${tvs.size} Smart TV(s)" else "No Smart TVs responded to discovery"
        }
    }

    fun addDiscoveredTvAsDevice(tv: DiscoveredTv) {
        createDevice(
            name = tv.name,
            brand = tv.brand,
            model = tv.model,
            deviceType = DeviceType.TV,
            connectionType = ConnectionType.WIFI,
            ipAddress = tv.ipAddress,
            port = tv.port
        )
    }

    // Touch Mouse actions
    fun onMouseMove(dx: Float, dy: Float) {
        mouseController.onMove(dx, dy)
        val step = mouseController.consumeDirectionalStep()
        if (step != null) {
            triggerHaptic()
            val button = when (step) {
                "UP" -> RemoteButtonType.NAV_UP
                "DOWN" -> RemoteButtonType.NAV_DOWN
                "LEFT" -> RemoteButtonType.NAV_LEFT
                else -> RemoteButtonType.NAV_RIGHT
            }
            pressButton(button)
        }
    }

    fun onMouseClick() {
        triggerHaptic()
        mouseController.onSingleClick()
        pressButton(RemoteButtonType.OK)
    }

    fun onMouseDoubleClick() {
        triggerHaptic()
        mouseController.onDoubleClick()
        pressButton(RemoteButtonType.OK)
    }

    fun onMouseRightClick() {
        triggerHaptic()
        mouseController.onRightClick()
        pressButton(RemoteButtonType.BACK)
    }

    fun onMouseScroll(deltaY: Float) {
        triggerHaptic()
        val dir = mouseController.onScroll(deltaY)
        if (dir == "SCROLL_UP") {
            pressButton(RemoteButtonType.NAV_UP)
        } else {
            pressButton(RemoteButtonType.NAV_DOWN)
        }
    }

    fun setMouseSensitivity(sens: Float) {
        mouseController.setSensitivity(sens)
    }

    fun toggleMouseActive(active: Boolean) {
        mouseController.setEnabled(active)
    }
}
