package com.teamwss.websoso.common.util

import android.content.res.Resources
import android.view.View
import android.widget.ListView

fun Float.toFloatPxFromDp(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.toIntPxFromDp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun ListView.setListViewHeightBasedOnChildren() {
    val listAdapter = adapter ?: return
    var totalHeight = 0

    for (i in 0 until listAdapter.count) {
        val listItem = listAdapter.getView(i, null, this)
        listItem.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        totalHeight += listItem.measuredHeight
    }

    val params = layoutParams
    params.height = totalHeight + (dividerHeight * (listAdapter.count - 1))
    layoutParams = params
    requestLayout()
}
