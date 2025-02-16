package com.into.websoso.ui.main.home.dialog

import android.os.Bundle
import android.view.View
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.databinding.DialogTermsAgreementPopupMenuBinding
import com.into.websoso.ui.termsAgreement.TermsAgreementDialogBottomSheet

class TermsAgreementDialogFragment :
    BaseDialogFragment<DialogTermsAgreementPopupMenuBinding>(R.layout.dialog_terms_agreement_popup_menu) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        onTermsAgreementPopupMenuUpdateClick()
    }

    private fun onTermsAgreementPopupMenuUpdateClick() {
        binding.tvTermsAgreementPopupMenuUpdate.setOnClickListener {
            showTermsAgreementBottomSheet()
            dismiss()
        }
    }

    private fun showTermsAgreementBottomSheet() {
        TermsAgreementDialogBottomSheet.newInstance(isFromHome = true)
            .show(parentFragmentManager, "TermsAgreementDialogBottomSheet")
    }

    companion object {
        const val TERMS_AGREEMENT_TAG = "TermsAgreementDialog"

        fun newInstance(): TermsAgreementDialogFragment {
            return TermsAgreementDialogFragment()
        }
    }
}
