package com.teamwss.websoso.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation
import com.teamwss.websoso.R

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImageUrl")
    fun loadImage(view: ImageView, imageUrl: String?) {
        loadImageWithTransformation(view, imageUrl, null)
    }

    @JvmStatic
    @BindingAdapter("loadImageUrl", "cornerRadius")
    fun loadRoundedCornerImage(view: ImageView, imageUrl: String?, cornerRadius: Float?) {
        val transformation = cornerRadius?.let { RoundedCornersTransformation(it) }
        loadImageWithTransformation(view, imageUrl, transformation)
    }

    private fun loadImageWithTransformation(
        view: ImageView,
        imageUrl: String?,
        transformation: Transformation?,
        placeholder: Int = R.drawable.img_loading_thumbnail
    ) {
        view.load(imageUrl ?: placeholder) {
            crossfade(true)
            transformation?.let { transformations(it) }
            listener(onError = { _, _ -> view.load(placeholder) })
        }
    }
}
