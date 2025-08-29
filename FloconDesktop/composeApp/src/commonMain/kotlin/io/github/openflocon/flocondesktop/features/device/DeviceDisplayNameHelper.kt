package io.github.openflocon.flocondesktop.features.device

import io.github.openflocon.domain.device.models.DeviceConnectionType

/**
 * Helper to create user-friendly device display names that show connection type
 */
object DeviceDisplayNameHelper {

    fun createDisplayName(deviceId: String, originalDeviceName: String): String {
        val connectionType = when {
            deviceId.endsWith("_usb") -> DeviceConnectionType.USB
            deviceId.endsWith("_wifi") -> DeviceConnectionType.WIFI
            else -> DeviceConnectionType.UNKNOWN
        }

        return when (connectionType) {
            DeviceConnectionType.USB -> "$originalDeviceName (USB)"
            DeviceConnectionType.WIFI -> "$originalDeviceName (WiFi)"
            DeviceConnectionType.UNKNOWN -> originalDeviceName
        }
    }

    fun getConnectionIcon(deviceId: String): String = when {
        deviceId.endsWith("_usb") -> "🔌" // USB cable icon
        deviceId.endsWith("_wifi") -> "📶" // WiFi signal icon
        else -> "📱" // Generic device icon
    }
}
