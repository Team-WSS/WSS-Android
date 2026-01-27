package com.into.websoso.ui

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("app:imageTint")
fun ImageView.setImageTint(@ColorRes colorRes: Int?) {
    if (colorRes == null) return
    setColorFilter(ContextCompat.getColor(context, colorRes))
}

@BindingAdapter("app:backgroundTint")
fun View.setBackgroundTint(@ColorRes colorRes: Int?) {
    if (colorRes == null) return
    val background = this.background
    if (background is GradientDrawable) {
        background.setColor(ContextCompat.getColor(context, colorRes))
    }
}
