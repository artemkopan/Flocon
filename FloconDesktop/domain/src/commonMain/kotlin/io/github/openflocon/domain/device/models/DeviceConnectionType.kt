package io.github.openflocon.domain.device.models

enum class DeviceConnectionType {
    USB,
    WIFI,
    UNKNOWN;
    
    companion object {
        fun fromSerial(serial: String): DeviceConnectionType {
            return when {
                // WiFi connections typically show IP:port format like "192.168.1.100:5555"
                serial.contains(":") && serial.contains(".") -> WIFI
                // USB connections show hardware serials without colons or IPs
                serial.isNotBlank() && !serial.contains(":") -> USB
                else -> UNKNOWN
            }
        }
    }
}

/**
 * Creates a composite device ID that includes connection type to differentiate
 * the same physical device connected via different methods (USB vs WiFi)
 */
fun createCompositeDeviceId(originalDeviceId: String, connectionType: DeviceConnectionType): String {
    return "${originalDeviceId}_${connectionType.name.lowercase()}"
}

/**
 * Extracts the original device ID from a composite device ID
 */
fun extractOriginalDeviceId(compositeDeviceId: String): String {
    return if (compositeDeviceId.contains("_usb") || compositeDeviceId.contains("_wifi")) {
        compositeDeviceId.substringBeforeLast("_")
    } else {
        compositeDeviceId
    }
}
