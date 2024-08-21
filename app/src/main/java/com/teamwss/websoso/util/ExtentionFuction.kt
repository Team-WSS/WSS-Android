package com.teamwss.websoso.util

import android.content.res.Resources

fun Float.toFloatPxFromDp(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.toIntPxFromDp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
