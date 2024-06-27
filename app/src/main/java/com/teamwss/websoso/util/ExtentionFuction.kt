package com.teamwss.websoso.util

import android.content.res.Resources

fun Float.floatToDp(): Float = this / Resources.getSystem().displayMetrics.density

fun Float.floatToPx(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.intToPx(): Int = this * Resources.getSystem().displayMetrics.density.toInt()