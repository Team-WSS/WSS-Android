package com.teamwss.websoso.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.teamwss.websoso.R

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        view.load(imageUrl)
    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageUrl", "cornerRadius"], requireAll = true)
    fun loadRoundedCornerImage(view: ImageView, imageUrl: String?, cornerRadius: Float) {
        view.load(imageUrl) {
            crossfade(true)
            transformations(RoundedCornersTransformation(cornerRadius))
            error(R.drawable.img_loading_thumbnail)
        }
    }
}