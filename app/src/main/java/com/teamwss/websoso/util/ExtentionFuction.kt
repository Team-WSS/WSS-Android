package com.teamwss.websoso.util

import android.content.res.Resources

fun Float.toFloatScaledByDp(): Float = this / Resources.getSystem().displayMetrics.density

fun Int.toIntScaledByDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Float.toFloatScaledByPx(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.toIntScaledByPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
