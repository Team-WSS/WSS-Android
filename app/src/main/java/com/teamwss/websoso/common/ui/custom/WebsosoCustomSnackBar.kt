package com.teamwss.websoso.common.ui.custom

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.teamwss.websoso.databinding.CustomSnackBarBinding

class WebsosoCustomSnackBar(
    private val view: View,
) {
    private val snackBar: Snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT)
    private val binding: CustomSnackBarBinding

    init {
        val inflater = LayoutInflater.from(view.context)
        binding = CustomSnackBarBinding.inflate(inflater)

        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent,
            )
        )
        snackBarLayout.addView(binding.root, 0)
    }

    fun setText(text: String): WebsosoCustomSnackBar {
        binding.tvCustomSnackBar.text = text
        return this
    }

    fun setIcon(resourceId: Int): WebsosoCustomSnackBar {
        binding.ivCustomSnackBar.setImageResource(resourceId)
        return this
    }

    fun show() {
        snackBar.show()
    }

    companion object {

        fun make(view: View): WebsosoCustomSnackBar {
            return WebsosoCustomSnackBar(view)
        }
    }
}