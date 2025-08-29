package io.github.openflocon.flocondesktop.features.deeplinks.mapper

import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState

internal fun mapToUi(deepLinks: List<DeeplinkDomainModel>): List<DeeplinkViewState> = buildList {
    addAll(
        deepLinks.map {
            mapToUi(it)
        },
    )
}

internal fun mapToUi(deepLink: DeeplinkDomainModel): DeeplinkViewState = DeeplinkViewState(
    label = deepLink.label,
    description = deepLink.description,
    parts = parseDeeplinkString(deepLink.link),
)

internal fun parseDeeplinkString(input: String): List<DeeplinkPart> {
    val regex = "\\[([^\\[\\]]*)\\]".toRegex() // Regex to find [something]
    val result = mutableListOf<DeeplinkPart>()
    var lastIndex = 0

    regex.findAll(input).forEach { matchResult ->
        val range = matchResult.range
        val value = matchResult.groupValues[1] // The content between brackets

        // 1. Add the "Text" part before the [value]
        if (range.first > lastIndex) {
            val textContent = input.substring(lastIndex, range.first)
            if (textContent.isNotEmpty()) {
                result.add(DeeplinkPart.Text(textContent))
            }
        }

        // 2. Add the "TextField" part
        result.add(DeeplinkPart.TextField(value))

        lastIndex = range.last + 1
    }

    // 3. Add the last "Text" part after the last [value] (if there is one)
    if (lastIndex < input.length) {
        val remainingText = input.substring(lastIndex)
        if (remainingText.isNotEmpty()) {
            result.add(DeeplinkPart.Text(remainingText))
        }
    }

    return result
}
