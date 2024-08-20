package com.teamwss.websoso.common.util

import android.view.View
import android.widget.ImageView
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
    @BindingAdapter(value = ["isVisible", "isInvisibleMode"], requireAll = false)
    fun setVisibility(view: View, isVisible: Boolean, isInvisibleMode: Boolean = false) {
        view.visibility = if (isVisible) View.VISIBLE else if (isInvisibleMode) View.INVISIBLE else View.GONE
    }
}
