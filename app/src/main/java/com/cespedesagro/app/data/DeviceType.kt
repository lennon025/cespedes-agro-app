package com.cespedesagro.app.data

enum class DeviceType {
    AGRO, METEO, UNKNOWN;

    companion object {
        fun fromName(name: String?): DeviceType {
            if (name.isNullOrBlank()) return UNKNOWN
            val u = name.uppercase().trim()
            return when {
                u.startsWith("AGRO") -> AGRO
                u == "METEO" || u.startsWith("METEO") -> METEO
                else -> UNKNOWN
            }
        }
    }
}
