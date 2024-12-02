package com.into.websoso.common.util

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.decode.SvgDecoder
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.into.websoso.R
import jp.wasabeef.transformers.coil.BlurTransformation

object BindingAdapter {

    @JvmStatic
    @BindingAdapter(
        value = ["loadImageUrl", "cornerRadius", "blurRadius", "isVectorImage", "isCircularImage"],
        requireAll = false
    )
    fun loadImageWithOptions(
        view: ImageView,
        imageUrl: String?,
        cornerRadius: Float?,
        blurRadius: Int?,
        isVectorImage: Boolean?,
        isCircularImage: Boolean?,
    ) {
        view.load(imageUrl) {
            crossfade(true)
            if (isVectorImage == true) decoderFactory(SvgDecoder.Factory())
            if (cornerRadius != null) transformations(RoundedCornersTransformation(cornerRadius.toFloatPxFromDp()))
            if (blurRadius != null) transformations(BlurTransformation(view.context, blurRadius))
            if (isCircularImage == true) transformations(CircleCropTransformation())
            error(R.drawable.img_loading_thumbnail)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["isVisible", "isInvisibleMode"], requireAll = false)
    fun setVisibility(view: View, isVisible: Boolean, isInvisibleMode: Boolean = false) {
        view.visibility =
            if (isVisible) View.VISIBLE else if (isInvisibleMode) View.INVISIBLE else View.GONE
    }
}
