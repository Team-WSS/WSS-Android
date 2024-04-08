package com.teamwss.websoso.ui.explore

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SosoPickItemDecoration(private val rightOffsetDp: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val density = parent.context.resources.displayMetrics.density
        val rightOffsetPx = (rightOffsetDp * density).toInt()

        val position = parent.getChildAdapterPosition(view)

        if (position != state.itemCount - 1) {
            outRect.right = rightOffsetPx
        }
    }
}
