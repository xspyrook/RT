package com.example.model

data class BrandPresetInfo(
    val brandName: String,
    val deviceType: DeviceType,
    val protocol: IrProtocol,
    val carrierFreq: Int,
    val address: Int,
    val keyCodes: Map<RemoteButtonType, Int>,
    val defaultModel: String = "Preset 1 (Standard)"
)

object BrandCatalog {

    val supportedBrands = listOf(
        "Samsung",
        "LG",
        "Sony",
        "TCL",
        "Hisense",
        "Philips",
        "Panasonic",
        "Toshiba",
        "Sharp",
        "Xiaomi"
    )

    private val samsungTvCodes = mapOf(
        RemoteButtonType.POWER to 0x02,
        RemoteButtonType.MUTE to 0x0F,
        RemoteButtonType.VOL_UP to 0x07,
        RemoteButtonType.VOL_DOWN to 0x0B,
        RemoteButtonType.CH_UP to 0x12,
        RemoteButtonType.CH_DOWN to 0x10,
        RemoteButtonType.NAV_UP to 0x60,
        RemoteButtonType.NAV_DOWN to 0x61,
        RemoteButtonType.NAV_LEFT to 0x65,
        RemoteButtonType.NAV_RIGHT to 0x62,
        RemoteButtonType.OK to 0x68,
        RemoteButtonType.BACK to 0x58,
        RemoteButtonType.HOME to 0x79,
        RemoteButtonType.MENU to 0x1A,
        RemoteButtonType.SOURCE to 0x01,
        RemoteButtonType.EXIT to 0x2D,
        RemoteButtonType.INFO to 0x1F,
        RemoteButtonType.PREV_CH to 0x13,
        RemoteButtonType.PLAY to 0x47,
        RemoteButtonType.PAUSE to 0x4A,
        RemoteButtonType.REWIND to 0x45,
        RemoteButtonType.FAST_FORWARD to 0x48,
        RemoteButtonType.NUM_0 to 0x11,
        RemoteButtonType.NUM_1 to 0x04,
        RemoteButtonType.NUM_2 to 0x05,
        RemoteButtonType.NUM_3 to 0x06,
        RemoteButtonType.NUM_4 to 0x08,
        RemoteButtonType.NUM_5 to 0x09,
        RemoteButtonType.NUM_6 to 0x0A,
        RemoteButtonType.NUM_7 to 0x0C,
        RemoteButtonType.NUM_8 to 0x0D,
        RemoteButtonType.NUM_9 to 0x0E,
        RemoteButtonType.COLOR_RED to 0x6C,
        RemoteButtonType.COLOR_GREEN to 0x14,
        RemoteButtonType.COLOR_YELLOW to 0x15,
        RemoteButtonType.COLOR_BLUE to 0x16
    )

    private val lgTvCodes = mapOf(
        RemoteButtonType.POWER to 0x08,
        RemoteButtonType.MUTE to 0x09,
        RemoteButtonType.VOL_UP to 0x02,
        RemoteButtonType.VOL_DOWN to 0x03,
        RemoteButtonType.CH_UP to 0x00,
        RemoteButtonType.CH_DOWN to 0x01,
        RemoteButtonType.NAV_UP to 0x40,
        RemoteButtonType.NAV_DOWN to 0x41,
        RemoteButtonType.NAV_LEFT to 0x07,
        RemoteButtonType.NAV_RIGHT to 0x06,
        RemoteButtonType.OK to 0x44,
        RemoteButtonType.BACK to 0x28,
        RemoteButtonType.HOME to 0x7C,
        RemoteButtonType.MENU to 0x43,
        RemoteButtonType.SOURCE to 0x0B,
        RemoteButtonType.EXIT to 0x5B,
        RemoteButtonType.INFO to 0xAA,
        RemoteButtonType.PREV_CH to 0x1A,
        RemoteButtonType.PLAY to 0xB0,
        RemoteButtonType.PAUSE to 0xBA,
        RemoteButtonType.REWIND to 0x8E,
        RemoteButtonType.FAST_FORWARD to 0x8F,
        RemoteButtonType.NUM_0 to 0x10,
        RemoteButtonType.NUM_1 to 0x11,
        RemoteButtonType.NUM_2 to 0x12,
        RemoteButtonType.NUM_3 to 0x13,
        RemoteButtonType.NUM_4 to 0x14,
        RemoteButtonType.NUM_5 to 0x15,
        RemoteButtonType.NUM_6 to 0x16,
        RemoteButtonType.NUM_7 to 0x17,
        RemoteButtonType.NUM_8 to 0x18,
        RemoteButtonType.NUM_9 to 0x19
    )

    private val sonyTvCodes = mapOf(
        RemoteButtonType.POWER to 0x15,
        RemoteButtonType.MUTE to 0x14,
        RemoteButtonType.VOL_UP to 0x12,
        RemoteButtonType.VOL_DOWN to 0x13,
        RemoteButtonType.CH_UP to 0x10,
        RemoteButtonType.CH_DOWN to 0x11,
        RemoteButtonType.NAV_UP to 0x74,
        RemoteButtonType.NAV_DOWN to 0x75,
        RemoteButtonType.NAV_LEFT to 0x34,
        RemoteButtonType.NAV_RIGHT to 0x33,
        RemoteButtonType.OK to 0x65,
        RemoteButtonType.BACK to 0x23,
        RemoteButtonType.HOME to 0x60,
        RemoteButtonType.MENU to 0x60,
        RemoteButtonType.SOURCE to 0x25,
        RemoteButtonType.EXIT to 0x0C,
        RemoteButtonType.INFO to 0x3A,
        RemoteButtonType.PREV_CH to 0x3B,
        RemoteButtonType.PLAY to 0x32,
        RemoteButtonType.PAUSE to 0x39,
        RemoteButtonType.REWIND to 0x34,
        RemoteButtonType.FAST_FORWARD to 0x33,
        RemoteButtonType.NUM_0 to 0x09,
        RemoteButtonType.NUM_1 to 0x00,
        RemoteButtonType.NUM_2 to 0x01,
        RemoteButtonType.NUM_3 to 0x02,
        RemoteButtonType.NUM_4 to 0x03,
        RemoteButtonType.NUM_5 to 0x04,
        RemoteButtonType.NUM_6 to 0x05,
        RemoteButtonType.NUM_7 to 0x06,
        RemoteButtonType.NUM_8 to 0x07,
        RemoteButtonType.NUM_9 to 0x08
    )

    private val genericNecCodes = mapOf(
        RemoteButtonType.POWER to 0x12,
        RemoteButtonType.MUTE to 0x10,
        RemoteButtonType.VOL_UP to 0x1E,
        RemoteButtonType.VOL_DOWN to 0x1F,
        RemoteButtonType.CH_UP to 0x1A,
        RemoteButtonType.CH_DOWN to 0x1B,
        RemoteButtonType.NAV_UP to 0x06,
        RemoteButtonType.NAV_DOWN to 0x07,
        RemoteButtonType.NAV_LEFT to 0x08,
        RemoteButtonType.NAV_RIGHT to 0x09,
        RemoteButtonType.OK to 0x0D,
        RemoteButtonType.BACK to 0x14,
        RemoteButtonType.HOME to 0x02,
        RemoteButtonType.MENU to 0x05,
        RemoteButtonType.SOURCE to 0x19,
        RemoteButtonType.EXIT to 0x15,
        RemoteButtonType.INFO to 0x0E,
        RemoteButtonType.PREV_CH to 0x1C,
        RemoteButtonType.PLAY to 0x20,
        RemoteButtonType.PAUSE to 0x21,
        RemoteButtonType.REWIND to 0x22,
        RemoteButtonType.FAST_FORWARD to 0x23,
        RemoteButtonType.NUM_0 to 0x00,
        RemoteButtonType.NUM_1 to 0x01,
        RemoteButtonType.NUM_2 to 0x02,
        RemoteButtonType.NUM_3 to 0x03,
        RemoteButtonType.NUM_4 to 0x04,
        RemoteButtonType.NUM_5 to 0x05,
        RemoteButtonType.NUM_6 to 0x06,
        RemoteButtonType.NUM_7 to 0x07,
        RemoteButtonType.NUM_8 to 0x08,
        RemoteButtonType.NUM_9 to 0x09
    )

    fun getPreset(brand: String, deviceType: DeviceType): BrandPresetInfo {
        return when (brand.lowercase()) {
            "samsung" -> BrandPresetInfo(
                brandName = "Samsung",
                deviceType = deviceType,
                protocol = IrProtocol.SAMSUNG,
                carrierFreq = 38000,
                address = 0x07,
                keyCodes = samsungTvCodes,
                defaultModel = "Samsung Smart & Crystal UHD"
            )
            "lg" -> BrandPresetInfo(
                brandName = "LG",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x04,
                keyCodes = lgTvCodes,
                defaultModel = "LG webOS & OLED Series"
            )
            "sony" -> BrandPresetInfo(
                brandName = "Sony",
                deviceType = deviceType,
                protocol = IrProtocol.SONY,
                carrierFreq = 40000,
                address = 0x01,
                keyCodes = sonyTvCodes,
                defaultModel = "Sony Bravia & Google TV"
            )
            "tcl" -> BrandPresetInfo(
                brandName = "TCL",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x40,
                keyCodes = genericNecCodes,
                defaultModel = "TCL Roku / Android TV"
            )
            "hisense" -> BrandPresetInfo(
                brandName = "Hisense",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0xBF,
                keyCodes = genericNecCodes,
                defaultModel = "Hisense VIDAA & Android"
            )
            "philips" -> BrandPresetInfo(
                brandName = "Philips",
                deviceType = deviceType,
                protocol = IrProtocol.RC5,
                carrierFreq = 36000,
                address = 0x00,
                keyCodes = genericNecCodes,
                defaultModel = "Philips Ambilight & Smart TV"
            )
            "panasonic" -> BrandPresetInfo(
                brandName = "Panasonic",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x08,
                keyCodes = genericNecCodes,
                defaultModel = "Panasonic Viera"
            )
            "toshiba" -> BrandPresetInfo(
                brandName = "Toshiba",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x02,
                keyCodes = genericNecCodes,
                defaultModel = "Toshiba Smart Series"
            )
            "sharp" -> BrandPresetInfo(
                brandName = "Sharp",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x55,
                keyCodes = genericNecCodes,
                defaultModel = "Sharp Aquos"
            )
            "xiaomi" -> BrandPresetInfo(
                brandName = "Xiaomi",
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x08,
                keyCodes = genericNecCodes,
                defaultModel = "Xiaomi Mi TV & Box"
            )
            else -> BrandPresetInfo(
                brandName = brand,
                deviceType = deviceType,
                protocol = IrProtocol.NEC,
                carrierFreq = 38000,
                address = 0x00,
                keyCodes = genericNecCodes,
                defaultModel = "Generic IR Device"
            )
        }
    }
}
