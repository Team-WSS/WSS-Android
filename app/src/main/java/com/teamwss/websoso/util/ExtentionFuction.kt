package com.teamwss.websoso.util

import android.content.res.Resources

fun Float.toDpFloat(): Float = this / Resources.getSystem().displayMetrics.density

fun Float.toPxFloat(): Float = this * Resources.getSystem().displayMetrics.density

fun Int.toPxInt(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
