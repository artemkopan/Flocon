package com.flocon.data.remote.sharedpreference.mapper

import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceValuesResponseDomainModel
import kotlinx.serialization.Serializable

@Serializable
internal data class SharedPreferenceValuesResponse(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<Row>,
) {
    @Serializable
    data class Row(
        val key: String,
        val stringValue: String? = null,
        val intValue: Int? = null,
        val floatValue: Float? = null,
        val booleanValue: Boolean? = null,
        val longValue: Long? = null,
        val setStringValue: List<String>? = null,
    )
}

internal fun toSharedPreferenceValuesResponseDomain(data: SharedPreferenceValuesResponse): SharedPreferenceValuesResponseDomainModel = SharedPreferenceValuesResponseDomainModel(
    requestId = data.requestId,
    sharedPreferenceName = data.sharedPreferenceName,
    rows = data.rows.mapNotNull { row ->
        row.stringValue?.let { value ->
            return@mapNotNull SharedPreferenceRowDomainModel(
                key = row.key,
                value = SharedPreferenceRowDomainModel.Value.StringValue(value = value),
            )
        }
        row.intValue?.let { value ->
            return@mapNotNull SharedPreferenceRowDomainModel(
                key = row.key,
                value = SharedPreferenceRowDomainModel.Value.IntValue(value),
            )
        }
        row.floatValue?.let { value ->
            return@mapNotNull SharedPreferenceRowDomainModel(
                key = row.key,
                value = SharedPreferenceRowDomainModel.Value.FloatValue(value),
            )
        }
        row.booleanValue?.let { value ->
            return@mapNotNull SharedPreferenceRowDomainModel(
                key = row.key,
                value = SharedPreferenceRowDomainModel.Value.BooleanValue(value),
            )
        }
        row.longValue?.let { value ->
            return@mapNotNull SharedPreferenceRowDomainModel(
                key = row.key,
                value = SharedPreferenceRowDomainModel.Value.LongValue(value),
            )
        }
        row.setStringValue?.let { value ->
            return@mapNotNull SharedPreferenceRowDomainModel(
                key = row.key,
                value = SharedPreferenceRowDomainModel.Value.StringSetValue(value.toSet()),
            )
        }
    },
)

private fun decodeStringToSet(encodedString: String): Set<String> {
    // If the string is empty, it means the original set was empty.
    if (encodedString.isEmpty()) {
        return emptySet()
    }
    // Use split to divide the string by the delimiter,
    // then toSet() to convert the resulting list into a Set.
    return encodedString.split("[_*+*_]").toSet()
}
