package com.into.websoso.ui.splash.dialog

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.databinding.DialogMinimumVersionPopupMenuBinding

class MinimumVersionDialogFragment :
    BaseDialogFragment<DialogMinimumVersionPopupMenuBinding>(R.layout.dialog_minimum_version_popup_menu) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onMinimumVersionUpdateButtonClick()
    }

    private fun onMinimumVersionUpdateButtonClick() {
        binding.tvMinimumVersionPopupMenuUpdate.setOnClickListener {
            navigateToPlayStore()
        }
    }

    private fun navigateToPlayStore() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(getString(R.string.minimum_version_popup_menu_url)),
        )
        startActivity(intent)
    }

    companion object {
        fun newInstance(): MinimumVersionDialogFragment {
            return MinimumVersionDialogFragment()
        }
    }
}
