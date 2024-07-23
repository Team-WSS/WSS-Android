package com.teamwss.websoso.ui.common.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.teamwss.websoso.databinding.LayoutLoadBinding

class WebsosoLoad @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutLoadBinding = LayoutLoadBinding.inflate(LayoutInflater.from(context), this, true)

    fun setErrorMessage(title: String, description: String) {
        binding.tvLoadFailTitle.text = title
        binding.tvLoadFailDescription.text = description
    }

    fun setReloadButtonClickListener(listener: OnClickListener) {
        binding.tvLoadFailReload.setOnClickListener(listener)
    }

    fun setErrorLayoutVisibility(isVisible: Boolean) {
        binding.clLoadFail.visibility = if (isVisible) VISIBLE else GONE
    }
}
