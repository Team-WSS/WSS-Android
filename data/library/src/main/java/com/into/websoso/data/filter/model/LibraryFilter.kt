package com.into.websoso.data.filter.model

data class LibraryFilter(
    val sortCriteria: String = DEFAULT_SORT_CRITERIA,
    val isInterested: Boolean = DEFAULT_IS_INTERESTED,
    val novelRating: Float = DEFAULT_NOVEL_RATING,
    val readStatuses: Map<String, Boolean> = emptyMap(),
    val attractivePoints: Map<String, Boolean> = emptyMap(),
) {
    val readStatusKeys: List<String> = readStatuses.toSelectedKeyList { it }
    val attractivePointKeys: List<String> = attractivePoints.toSelectedKeyList { it }

    private fun <K> Map<K, Boolean>.toSelectedKeyList(toSelectedKeyName: (K) -> String): List<String> =
        this
            .filterValues { it }
            .keys
            .map { toSelectedKeyName(it) }

    companion object {
        private const val DEFAULT_SORT_CRITERIA = "RECENT"
        private const val DEFAULT_IS_INTERESTED = false
        private const val DEFAULT_NOVEL_RATING = 0.0f
    }
}
