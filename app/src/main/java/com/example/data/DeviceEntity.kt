package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ConnectionType
import com.example.model.DeviceType
import com.example.model.IrProtocol

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val model: String,
    val deviceType: String = DeviceType.TV.name,
    val connectionType: String = ConnectionType.IR.name,
    val ipAddress: String? = null,
    val port: Int = 8060,
    val carrierFreq: Int = 38000,
    val irProtocol: String = IrProtocol.SAMSUNG.name,
    val isFavorite: Boolean = false,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)
