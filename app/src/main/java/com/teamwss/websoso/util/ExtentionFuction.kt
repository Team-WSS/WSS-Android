package com.teamwss.websoso.util

import android.content.res.Resources
import android.view.Window
import android.view.WindowManager

fun Float.toFloatScaledByPx(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.toIntScaledByPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Window.setupTranslucentOnStatusBar() {
    this.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
    )
}
