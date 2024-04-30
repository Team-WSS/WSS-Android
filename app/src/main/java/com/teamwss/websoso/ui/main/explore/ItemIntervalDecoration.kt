package com.teamwss.websoso.ui.main.explore

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemIntervalDecoration(
    private val leftOffsetDp: Int = 0,
    private val topOffsetDp: Int = 0,
    private val rightOffsetDp: Int = 0,
    private val bottomOffsetDp: Int = 0
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        val density = parent.context.resources.displayMetrics.density
        val leftOffsetPx = (leftOffsetDp * density).toInt()
        val topOffsetPx = (topOffsetDp * density).toInt()
        val rightOffsetPx = (rightOffsetDp * density).toInt()
        val bottomOffsetPx = (bottomOffsetDp * density).toInt()
        val position = parent.getChildAdapterPosition(view)

        if (position != state.itemCount - 1) {
            outRect.set(leftOffsetPx, topOffsetPx, rightOffsetPx, bottomOffsetPx)
        }
    }
}