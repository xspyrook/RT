package com.example.model

enum class DeviceType(val labelEn: String, val labelAr: String) {
    TV("Smart / IR TV", "تلفزيون ذكي / عادي"),
    AC("Air Conditioner", "مكيف هواء"),
    RECEIVER("Receiver / Set-Top Box", "ريسيفر / مستقبل فضائي"),
    AUDIO("Audio / Soundbar", "مسرح منزلي / ساوند بار")
}

enum class ConnectionType(val labelEn: String, val labelAr: String) {
    IR("Infrared (IR)", "أشعة تحت الحمراء (IR)"),
    WIFI("Wi-Fi LAN Control", "شبكة Wi-Fi المحلية")
}

enum class IrProtocol {
    NEC,
    SAMSUNG,
    SONY,
    RC5,
    RAW
}

enum class RemoteButtonType(
    val codeKey: String,
    val defaultLabelEn: String,
    val defaultLabelAr: String
) {
    POWER("POWER", "Power", "تشغيل"),
    MUTE("MUTE", "Mute", "كتم الصوت"),
    VOL_UP("VOL_UP", "Vol +", "الصوت +"),
    VOL_DOWN("VOL_DOWN", "Vol -", "الصوت -"),
    CH_UP("CH_UP", "CH +", "القناة +"),
    CH_DOWN("CH_DOWN", "CH -", "القناة -"),
    
    // D-Pad Navigation
    NAV_UP("NAV_UP", "Up", "أعلى"),
    NAV_DOWN("NAV_DOWN", "Down", "أسفل"),
    NAV_LEFT("NAV_LEFT", "Left", "يسار"),
    NAV_RIGHT("NAV_RIGHT", "Right", "يمين"),
    OK("OK", "OK", "موافق"),
    
    // Core Actions
    BACK("BACK", "Back", "رجوع"),
    HOME("HOME", "Home", "الرئيسية"),
    MENU("MENU", "Menu", "القائمة"),
    SOURCE("SOURCE", "Source", "المصدر"),
    EXIT("EXIT", "Exit", "خروج"),
    INFO("INFO", "Info", "معلومات"),
    PREV_CH("PREV_CH", "Prev CH", "السابقة"),
    
    // Media Playback
    PLAY("PLAY", "Play", "تشغيل"),
    PAUSE("PAUSE", "Pause", "إيقاف مؤقت"),
    REWIND("REWIND", "Rewind", "ترجيع"),
    FAST_FORWARD("FAST_FORWARD", "Forward", "تقديم"),
    
    // Numbers
    NUM_0("NUM_0", "0", "0"),
    NUM_1("NUM_1", "1", "1"),
    NUM_2("NUM_2", "2", "2"),
    NUM_3("NUM_3", "3", "3"),
    NUM_4("NUM_4", "4", "4"),
    NUM_5("NUM_5", "5", "5"),
    NUM_6("NUM_6", "6", "6"),
    NUM_7("NUM_7", "7", "7"),
    NUM_8("NUM_8", "8", "8"),
    NUM_9("NUM_9", "9", "9"),
    
    // Colored function keys
    COLOR_RED("COLOR_RED", "Red", "أحمر"),
    COLOR_GREEN("COLOR_GREEN", "Green", "أخضر"),
    COLOR_YELLOW("COLOR_YELLOW", "Yellow", "أصفر"),
    COLOR_BLUE("COLOR_BLUE", "Blue", "أزرق")
}

data class DiscoveredTv(
    val id: String,
    val name: String,
    val ipAddress: String,
    val port: Int = 8060,
    val brand: String = "Generic Smart TV",
    val model: String = "Smart TV"
)
