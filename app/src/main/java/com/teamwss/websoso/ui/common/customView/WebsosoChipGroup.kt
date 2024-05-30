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

    /*
    Adds a child view. If no layout parameters are already set on the child, the default parameters for this ViewGroup are set on the child.
    Note: do not invoke this method from draw(Canvas), onDraw(Canvas), dispatchDraw(Canvas) or any related method.
    Params:
    chip – the websosoChip to add
    */
    fun addChip(chip: WebsosoChip) {
        addView(chip)
    }

    fun updateSelectedChip(view: View) {
        previousChip = view
    }

    fun removePreviousChipSelected() {
        previousChip?.isSelected = false
    }

    fun getSelectedChipCount(): Int {
        var count = 0
        for (i in 0 until childCount) {
            if (getChildAt(i).isSelected) {
                count++
            }
        }
        return count
    }

    override fun setSingleSelection(isSingleSelection: Boolean) {
        this.isSingleSelectionMode = isSingleSelection
    }
}
