package com.teamwss.websoso.common.ui.custom

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.teamwss.websoso.common.util.toIntScaledByPx
import com.teamwss.websoso.databinding.CustomSnackBarBinding

class WebsosoCustomToast(
    context: Context,
) {
    private val toast: Toast = Toast(context)
    private val binding: CustomSnackBarBinding

    init {
        val inflater = LayoutInflater.from(context)
        binding = CustomSnackBarBinding.inflate(inflater)

        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 16.toIntScaledByPx())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = binding.root
    }

    fun setText(text: String): WebsosoCustomToast {
        binding.tvCustomSnackBar.text = text
        return this
    }

    fun setIcon(resourceId: Int): WebsosoCustomToast {
        binding.ivCustomSnackBar.setImageResource(resourceId)
        return this
    }

    fun setDuration(duration: Int): WebsosoCustomToast {
        toast.duration = duration
        return this
    }

    fun show() {
        toast.show()
    }

    companion object {

        fun make(context: Context): WebsosoCustomToast {
            return WebsosoCustomToast(context)
        }
    }
}
