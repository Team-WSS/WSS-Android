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
        setupDialog()
        onTermsAgreementPopupMenuUpdateClick()
    }

    private fun setupDialog() {
        isCancelable = false
        setCancelable(false)
    }

    private fun onTermsAgreementPopupMenuUpdateClick() {
        binding.tvTermsAgreementPopupMenuUpdate.setOnClickListener {
            showTermsAgreementBottomSheet()
            dismiss()
        }
    }

    private fun showTermsAgreementBottomSheet() {
        val isBottomSheetShown =
            parentFragmentManager.findFragmentByTag(TERMS_AGREEMENT_BOTTOM_SHEET_TAG)

        if (isBottomSheetShown == null) {
            TermsAgreementDialogBottomSheet
                .newInstance(isFromHome = true)
                .show(parentFragmentManager, TERMS_AGREEMENT_BOTTOM_SHEET_TAG)
        }
        dismiss()
    }

    companion object {
        const val TERMS_AGREEMENT_TAG = "TermsAgreementDialog"
        const val TERMS_AGREEMENT_BOTTOM_SHEET_TAG = "TermsAgreementDialogBottomSheet"

        fun newInstance(): TermsAgreementDialogFragment = TermsAgreementDialogFragment()
    }
}
