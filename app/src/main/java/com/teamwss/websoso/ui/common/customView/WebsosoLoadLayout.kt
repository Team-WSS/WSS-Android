package com.teamwss.websoso.ui.common.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.load
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.LayoutLoadBinding

class WebsosoLoadLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutLoadBinding = LayoutLoadBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setupLoadAnimation(context)
    }

    /**
     * Initializes and sets up the loading animation using Coil for loading GIFs.
     * @param context The context to be used for the ImageLoader.
     */
    private fun setupLoadAnimation(context: Context) {
        val imageLoader = ImageLoader.Builder(context)
            .components {
                add(ImageDecoderDecoder.Factory())
            }
            .build()
        binding.ivLoadLoad.load(R.drawable.ic_load_load, imageLoader)
    }

    /**
     * Sets the error message displayed when loading fails.
     * @param title The title of the error message.
     * @param description The description of the error message.
     */
    fun setErrorMessage(title: String, description: String) {
        binding.tvLoadFailTitle.text = title
        binding.tvLoadFailDescription.text = description
    }

    /**
     * Sets a click listener for the reload button.
     * @param listener The OnClickListener to be set for the reload button.
     */
    fun setReloadButtonClickListener(listener: OnClickListener) {
        binding.tvLoadFailReload.setOnClickListener(listener)
    }

    /**
     * Sets the visibility of the loading layout.
     * @param isVisible Boolean indicating whether the loading layout should be visible.
     */
    fun setLoadLayoutVisibility(isVisible: Boolean) {
        binding.clLoadLoad.visibility = if (isVisible) VISIBLE else GONE
        binding.clLoadFail.visibility = if (isVisible) GONE else VISIBLE
    }

    /**
     * Sets the visibility of the error layout.
     * @param isVisible Boolean indicating whether the error layout should be visible.
     */
    fun setErrorLayoutVisibility(isVisible: Boolean) {
        binding.clLoadLoad.visibility = if (isVisible) GONE else VISIBLE
        binding.clLoadFail.visibility = if (isVisible) VISIBLE else GONE
    }

    /**
     * Sets the overall visibility of the WebsosoLoadLayout.
     * @param isVisible Boolean indicating whether the WebsosoLoadLayout should be visible.
     */
    fun setWebsosoLoadVisibility(isVisible: Boolean) {
        this.visibility = if (isVisible) VISIBLE else GONE
    }
}
