package com.teamwss.websoso.util

import android.content.res.Resources

fun Float.toDp(): Float {
    return this * Resources.getSystem().displayMetrics.density
}
