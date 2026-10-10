package com.into.websoso.domain.library.model

data class SeriesStatuses(
    val value: Map<SeriesStatus, Boolean> = SeriesStatus.entries.associateWith { false },
) {
    val isSelected: Boolean
        get() = value.any { it.value }

    val selectedStatuses: List<SeriesStatus>
        get() = value.filterValues { it }.keys.toList()

    val selectedLabels: List<String>
        get() = selectedStatuses.map { it.label }

    val isComplete: Boolean?
        get() = when {
            value[SeriesStatus.COMPLETED] == true -> true
            value[SeriesStatus.IN_SERIES] == true -> false
            else -> null
        }

    operator fun get(seriesStatus: SeriesStatus): Boolean = value[seriesStatus] ?: false

    fun set(seriesStatus: SeriesStatus): SeriesStatuses {
        if (!value.containsKey(seriesStatus)) return this
        val currentlySelected = value[seriesStatus] ?: false
        val updated = SeriesStatus.entries.associateWith { false }.toMutableMap()
        if (!currentlySelected) updated[seriesStatus] = true
        return copy(value = updated)
    }

    companion object {
        fun fromIsComplete(isComplete: Boolean?): SeriesStatuses {
            val selected = when (isComplete) {
                true -> SeriesStatus.COMPLETED
                false -> SeriesStatus.IN_SERIES
                null -> null
            }
            return SeriesStatuses(SeriesStatus.entries.associateWith { it == selected })
        }
    }
}
