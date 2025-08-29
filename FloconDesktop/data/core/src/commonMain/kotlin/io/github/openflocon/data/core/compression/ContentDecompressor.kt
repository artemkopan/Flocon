package io.github.openflocon.data.core.compression

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import io.github.openflocon.domain.logs.Logger
import io.github.openflocon.domain.logs.models.LogCategory
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Utility class for decompressing network response content based on Content-Encoding headers
 */
object ContentDecompressor : KoinComponent {
    private val logger by inject<Logger>()
    
    /**
     * Decompresses content based on the Content-Encoding header
     * 
     * @param content The raw response body content
     * @param headers The response headers map
     * @return Decompressed content as string, or original content if no compression or decompression fails
     */
    fun decompressContent(content: String?, headers: Map<String, String>): String? {
        if (content.isNullOrEmpty()) return content
        
        val contentEncoding = headers.entries
            .find { it.key.lowercase() == "content-encoding" }
            ?.value?.lowercase()
            
        return when (contentEncoding) {
            "gzip" -> decompressGzip(content) ?: content
            "deflate" -> decompressDeflate(content) ?: content
            else -> content
        }
    }
    
    /**
     * Attempts to decompress gzipped content
     * 
     * @param content Base64 encoded or raw gzipped content
     * @return Decompressed string or null if decompression fails
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun decompressGzip(content: String): String? {
        return try {
            // Try to decode as Base64 first (common case)
            val compressed = try {
                Base64.decode(content)
            } catch (e: Exception) {
                // If Base64 decoding fails, treat as raw bytes
                content.encodeToByteArray()
            }
            
            decompressGzipBytes(compressed)
        } catch (e: Exception) {
            logger.error(LogCategory.NETWORK, "Failed to decompress gzip content", exception = e)
            null
        }
    }
    
    /**
     * Attempts to decompress deflate content
     * 
     * @param content Base64 encoded or raw deflated content  
     * @return Decompressed string or null if decompression fails
     */
    @OptIn(ExperimentalEncodingApi::class)
    private fun decompressDeflate(content: String): String? {
        return try {
            // Try to decode as Base64 first (common case)
            val compressed = try {
                Base64.decode(content)
            } catch (e: Exception) {
                // If Base64 decoding fails, treat as raw bytes
                content.encodeToByteArray()
            }
            
            decompressDeflateBytes(compressed)
        } catch (e: Exception) {
            logger.error(LogCategory.NETWORK, "Failed to decompress deflate content", exception = e)
            null
        }
    }
}

expect fun decompressGzipBytes(compressedData: ByteArray): String?
expect fun decompressDeflateBytes(compressedData: ByteArray): String?

