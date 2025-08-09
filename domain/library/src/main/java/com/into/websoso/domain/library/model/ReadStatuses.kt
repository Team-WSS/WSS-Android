package com.into.websoso.domain.library.model

data class ReadStatuses(
    val value: Map<ReadStatus, Boolean> = ReadStatus.entries.associateWith { false },
) {
    val isSelected: Boolean
        get() = value.any { it.value }

    val selectedLabels: List<String>
        get() = value.filterValues { it }.keys.map { it.label }

    val selectedKeys: List<String>
        get() = value.filterValues { it }.keys.map { it.key }

    operator fun get(readStatus: ReadStatus): Boolean = value[readStatus] ?: false

    fun set(readStatus: ReadStatus): ReadStatuses {
        if (!value.containsKey(readStatus)) return this
        val updatedReadStatus = value.toMutableMap().apply {
            this[readStatus] = !(this[readStatus] ?: false)
        }
        return this.copy(value = updatedReadStatus)
    }

    companion object {
        fun List<String>.toReadStatuses(): ReadStatuses {
            val enabled = mapNotNull(ReadStatus::from).toSet()
            val mapped = ReadStatus.entries.associateWith { it in enabled }
            return ReadStatuses(mapped)
        }
    }
}
