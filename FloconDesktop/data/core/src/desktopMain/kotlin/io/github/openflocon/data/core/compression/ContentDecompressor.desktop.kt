package io.github.openflocon.data.core.compression

import io.github.openflocon.domain.logs.Logger
import io.github.openflocon.domain.logs.models.LogCategory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

object CompressionLogger : KoinComponent {
    val logger by inject<Logger>()
}

actual fun decompressGzipBytes(compressedData: ByteArray): String? {
    return try {
        val inputStream = GZIPInputStream(ByteArrayInputStream(compressedData))
        val outputStream = ByteArrayOutputStream()
        
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        outputStream.toString("UTF-8")
    } catch (e: Exception) {
        CompressionLogger.logger.error(LogCategory.NETWORK, "Error decompressing GZIP content", exception = e)
        null
    }
}

actual fun decompressDeflateBytes(compressedData: ByteArray): String? {
    return try {
        val inputStream = InflaterInputStream(ByteArrayInputStream(compressedData))
        val outputStream = ByteArrayOutputStream()
        
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        
        outputStream.toString("UTF-8")
    } catch (e: Exception) {
        CompressionLogger.logger.error(LogCategory.NETWORK, "Error decompressing Deflate content", exception = e)
        null
    }
}

