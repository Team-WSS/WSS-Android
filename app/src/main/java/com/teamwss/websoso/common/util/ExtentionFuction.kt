package com.teamwss.websoso.common.util

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.ListView
import java.io.Serializable

fun Float.toFloatScaledByPx(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.toIntScaledByPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

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

inline fun <reified T : Serializable> Intent.getAdaptedSerializableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
}
