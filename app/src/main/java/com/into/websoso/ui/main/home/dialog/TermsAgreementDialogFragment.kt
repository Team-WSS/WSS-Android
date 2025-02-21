package com.into.websoso.ui.main.home.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseDialogFragment
import com.into.websoso.databinding.DialogTermsAgreementPopupMenuBinding
import com.into.websoso.ui.termsAgreement.TermsAgreementDialogBottomSheet

class TermsAgreementDialogFragment :
    BaseDialogFragment<DialogTermsAgreementPopupMenuBinding>(R.layout.dialog_terms_agreement_popup_menu) {
    private var isBottomSheetShown = false

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
        isBottomSheetShown = true

        val bottomSheet = TermsAgreementDialogBottomSheet.newInstance(isFromHome = true)
        bottomSheet.show(parentFragmentManager, "TermsAgreementDialogBottomSheet")

        bottomSheet.onDismissListener = {
            isBottomSheetShown = false
        }
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isBottomSheetShown = false
    }

    companion object {
        const val TERMS_AGREEMENT_TAG = "TermsAgreementDialog"

        fun newInstance(): TermsAgreementDialogFragment = TermsAgreementDialogFragment()
    }
}
