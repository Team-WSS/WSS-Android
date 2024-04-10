package com.teamwss.websoso.ui.common.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.chip.ChipGroup

class WebsosoChipGroup(
    context: Context,
    attrs: AttributeSet? = null,
) : ChipGroup(context, attrs) {
    var previousChip: View? = null
        private set
    var isSingleSelectionMode: Boolean = false
        private set

    fun addChip(chip: WebsosoChip) {
        addView(chip)
    }

    fun updateSelectedChip(view: View) {
        previousChip = view
    }

    fun removePreviousChipSelected() {
        previousChip?.isSelected = false
    }

    override fun setSingleSelection(isSingleSelection: Boolean) {
        this.isSingleSelectionMode = isSingleSelection
    }
}
