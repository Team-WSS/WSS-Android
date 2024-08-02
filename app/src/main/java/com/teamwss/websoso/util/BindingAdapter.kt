package com.teamwss.websoso.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.decode.SvgDecoder
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.teamwss.websoso.R
import jp.wasabeef.transformers.coil.BlurTransformation

object BindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["loadImageUrl", "cornerRadius", "blurRadius", "isVectorImage"], requireAll = false)
    fun loadImageWithOptions(
        view: ImageView,
        imageUrl: String?,
        cornerRadius: Float?,
        blurRadius: Int?,
        isVectorImage: Boolean?,
    ) {
        view.load(imageUrl) {
            crossfade(true)
            if (isVectorImage == true) decoderFactory(SvgDecoder.Factory())
            if (cornerRadius != null) transformations(RoundedCornersTransformation(cornerRadius))
            if (blurRadius != null) transformations(BlurTransformation(view.context, blurRadius))
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
}
