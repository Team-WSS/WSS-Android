package com.teamwss.websoso.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.teamwss.websoso.R
import jp.wasabeef.transformers.coil.BlurTransformation

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

    @JvmStatic
    @BindingAdapter(value = ["loadImageUrl", "blurRadius"], requireAll = true)
    fun loadBlurredImage(view: ImageView, imageUrl: String?, blurRadius: Int) {
        view.load(imageUrl) {
            crossfade(true)
            transformations(BlurTransformation(view.context, blurRadius))
            error(R.drawable.img_loading_thumbnail)
        }
    }

    @JvmStatic
    @BindingAdapter("loadImageUrlCircular")
    fun loadCircularImage(view: ImageView, imageUrl: String?) {
        view.load(imageUrl) {
            crossfade(true)
            transformations(CircleCropTransformation())
            error(R.drawable.img_loading_thumbnail)
        }
    }

    @JvmStatic
    @BindingAdapter("isVisible")
    fun setVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("android:text")
    fun setIntToString(view: TextView, value: Int) {
        view.text = value.toString()
    }
}