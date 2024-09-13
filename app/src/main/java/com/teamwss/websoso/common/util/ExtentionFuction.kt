package com.teamwss.websoso.common.util

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import com.teamwss.websoso.BuildConfig
import com.teamwss.websoso.common.ui.custom.WebsosoCustomSnackBar
import com.teamwss.websoso.common.ui.custom.WebsosoCustomToast
import java.io.Serializable

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

fun View.getS3ImageUrl(imageName: String): String {
    val baseUrl = BuildConfig.S3_BASE_URL
    val scale = when {
        context.resources.displayMetrics.density >= 4.0 -> 4
        context.resources.displayMetrics.density >= 3.0 -> 3
        context.resources.displayMetrics.density >= 2.0 -> 2
        else -> 1
    }

    return "$baseUrl$imageName@${scale}x.png"
}

fun View.hideKeyboard() {
    val inputMethodManager : InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun showWebsosoSnackBar(view: View, message: String, icon: Int) {
    WebsosoCustomSnackBar.make(view)
        .setText(message)
        .setIcon(icon)
        .show()
}

fun showWebsosoToast(context: Context, message: String, icon: Int) {
    WebsosoCustomToast.make(context)
        .setText(message)
        .setIcon(icon)
        .show()
}

inline fun <reified T : Serializable> Intent.getAdaptedSerializableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
}

inline fun <reified T : Parcelable> Intent.getAdaptedParcelableExtra(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(key) as? T
    }
}

inline fun <reified T : Parcelable> Bundle.getAdaptedParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelable(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelable(key) as? T
    }
}
