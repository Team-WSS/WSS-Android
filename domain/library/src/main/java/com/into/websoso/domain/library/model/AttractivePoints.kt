package com.into.websoso.domain.library.model

data class AttractivePoints(
    val value: Map<AttractivePoint, Boolean> = AttractivePoint.entries.associateWith { false },
) {
    val isSelected: Boolean
        get() = value.any { it.value }

    val selectedAttractivePoints: List<AttractivePoint>
        get() = value.filterValues { it }.keys.toList()

    val selectedLabels: List<String>
        get() = selectedAttractivePoints.map { it.label }

    val selectedKeys: List<String>
        get() = selectedAttractivePoints.map { it.key }

    operator fun get(attractivePoint: AttractivePoint): Boolean = value[attractivePoint] ?: false

    fun set(attractivePoint: AttractivePoint): AttractivePoints {
        if (!value.containsKey(attractivePoint)) return this
        val updatedAttractivePoint = value.toMutableMap().apply {
            this[attractivePoint] = !(this[attractivePoint] ?: false)
        }
        return this.copy(value = updatedAttractivePoint)
    }

    companion object {
        fun List<String>.toAttractivePoints(): AttractivePoints {
            val enabledSet = mapNotNull(AttractivePoint::from).toSet()
            val mapped = AttractivePoint.entries.associateWith { it in enabledSet }
            return AttractivePoints(mapped)
        }
    }
}
