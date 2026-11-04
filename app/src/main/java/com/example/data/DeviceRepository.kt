package com.example.data

import com.example.model.ConnectionType
import com.example.model.DeviceType
import com.example.model.IrProtocol
import kotlinx.coroutines.flow.Flow

class DeviceRepository(private val deviceDao: DeviceDao) {

    val allDevices: Flow<List<DeviceEntity>> = deviceDao.getAllDevices()

    suspend fun insert(device: DeviceEntity): Long {
        return deviceDao.insertDevice(device)
    }

    suspend fun update(device: DeviceEntity) {
        deviceDao.updateDevice(device)
    }

    suspend fun delete(device: DeviceEntity) {
        deviceDao.deleteDevice(device)
    }

    suspend fun updateLastUsed(id: Long) {
        deviceDao.updateLastUsed(id)
    }

    suspend fun seedDefaultsIfEmpty() {
        // Will check in ViewModel when list is empty
        val default1 = DeviceEntity(
            name = "Living Room Samsung TV",
            brand = "Samsung",
            model = "Samsung Crystal UHD 4K",
            deviceType = DeviceType.TV.name,
            connectionType = ConnectionType.IR.name,
            carrierFreq = 38000,
            irProtocol = IrProtocol.SAMSUNG.name,
            isFavorite = true
        )
        val default2 = DeviceEntity(
            name = "Bedroom LG OLED TV",
            brand = "LG",
            model = "LG webOS OLED",
            deviceType = DeviceType.TV.name,
            connectionType = ConnectionType.IR.name,
            carrierFreq = 38000,
            irProtocol = IrProtocol.NEC.name,
            isFavorite = false
        )
        val default3 = DeviceEntity(
            name = "Smart TV (Wi-Fi LAN)",
            brand = "TCL",
            model = "Roku / Android Smart TV",
            deviceType = DeviceType.TV.name,
            connectionType = ConnectionType.WIFI.name,
            ipAddress = "192.168.1.100",
            port = 8060,
            carrierFreq = 38000,
            irProtocol = IrProtocol.NEC.name,
            isFavorite = false
        )
        deviceDao.insertDevice(default1)
        deviceDao.insertDevice(default2)
        deviceDao.insertDevice(default3)
    }
}
