package com.into.websoso.ui.main.home

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.into.websoso.common.util.toIntPxFromDp

class HomeCustomItemDecoration(private val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == 0) {
            outRect.left = margin.toIntPxFromDp()
        }

        if (position == itemCount - 1) {
            outRect.right = margin.toIntPxFromDp()
        }
    }
}