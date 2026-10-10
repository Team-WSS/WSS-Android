package com.into.websoso.domain.library.model

data class Keywords(
    val registered: List<Keyword> = emptyList(),
    val selectedNames: Set<String> = emptySet(),
) {
    val isSelected: Boolean
        get() = selectedNames.isNotEmpty()

    val selectedLabels: List<String>
        get() = selectedNames.toList()

    fun isSelected(name: String): Boolean = name in selectedNames

    fun set(name: String): Keywords {
        val updated = if (name in selectedNames) selectedNames - name else selectedNames + name
        return copy(selectedNames = updated)
    }

    fun clearSelection(): Keywords = copy(selectedNames = emptySet())

    companion object {
        const val MAX_SELECTION = 20
    }
}
