package com.into.websoso.ui

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.into.websoso.R

@BindingAdapter("imageTint")
fun ImageView.setImageTint(
    @ColorRes colorRes: Int?,
) {
    if (colorRes == null) {
        clearColorFilter()
    } else {
        setColorFilter(ContextCompat.getColor(context, colorRes))
    }
}

@BindingAdapter(value = ["dynamicBackgroundColor", "genreName"], requireAll = false)
fun View.setGenreBackground(
    @ColorRes colorRes: Int?,
    genreName: String?,
) {
    val drawable = background?.mutate() as? GradientDrawable ?: return

    if (colorRes != null) {
        drawable.setColor(ContextCompat.getColor(context, colorRes))
    }

    val normalized = genreName?.trim()
    val isEtc = normalized.isNullOrEmpty() || normalized == "기타"

    if (isEtc) {
        val strokeWidth = (1 * context.resources.displayMetrics.density).toInt()
        val strokeColor = ContextCompat.getColor(context, R.color.gray_70_DFDFE3)
        drawable.setStroke(strokeWidth, strokeColor)
    } else {
        drawable.setStroke(0, 0)
    }
}
