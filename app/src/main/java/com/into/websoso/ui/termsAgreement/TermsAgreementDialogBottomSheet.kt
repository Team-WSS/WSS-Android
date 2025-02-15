package com.into.websoso.ui.termsAgreement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.into.websoso.R
import com.into.websoso.R.drawable.bg_novel_rating_date_primary_100_radius_12dp
import com.into.websoso.R.drawable.bg_profile_edit_gray_70_radius_12dp
import com.into.websoso.R.drawable.ic_terms_agreement_selected
import com.into.websoso.R.drawable.ic_terms_agreement_unselected
import com.into.websoso.R.string.string_terms_agreement_complete
import com.into.websoso.R.string.string_terms_agreement_next
import com.into.websoso.core.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.databinding.DialogTermsAgreementBinding
import com.into.websoso.ui.termsAgreement.model.AgreementType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TermsAgreementDialogBottomSheet :
    BaseBottomSheetDialog<DialogTermsAgreementBinding>(R.layout.dialog_terms_agreement) {

    private val termsAgreementViewModel: TermsAgreementViewModel by viewModels()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomSheet()
        onRequireTermsAgreementClick()
        onTermsAgreementToggleClick()
        onTermsAgreementCompleteButtonClick()
        setupViewModel()
        updateCompleteButtonText()
    }

    private fun setupBottomSheet() {
        (dialog as BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.isHideable = false
            setCancelable(false)
        }
    }

    private fun onRequireTermsAgreementClick() {
        binding.tvTermsAgreementService.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.notion.so/websoso/143600bd74688050be18f4da31d9403e?pvs=4"),
                ),
            )
        }

        binding.tvTermsAgreementPrivacy.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.notion.so/kimmjabc/18e9e64a45328048842ed4dd7b17e5b3?pvs=25"),
                ),
            )
        }
    }

    private fun onTermsAgreementToggleClick() {
        binding.ivTermsAgreementAll.setOnClickListener { termsAgreementViewModel.updateTermsAgreementsAll() }

        binding.ivTermsAgreementService.setOnClickListener {
            termsAgreementViewModel.updateTermsAgreements(
                AgreementType.SERVICE,
            )
        }

        binding.ivTermsAgreementPrivacy.setOnClickListener {
            termsAgreementViewModel.updateTermsAgreements(
                AgreementType.PRIVACY,
            )
        }

        binding.ivTermsAgreementMarketing.setOnClickListener {
            termsAgreementViewModel.updateTermsAgreements(
                AgreementType.MARKETING,
            )
        }

    }

    private fun onTermsAgreementCompleteButtonClick() {
        binding.btnTermsAgreementComplete.setOnClickListener { dismissIfAgreementsCompleted() }
    }

    private fun dismissIfAgreementsCompleted() {
        if (termsAgreementViewModel.isRequiredAgreementsChecked.value) {
            dismiss()
        }
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch { termsAgreementViewModel.agreementStatus.collect { updateAgreementIcons(it) } }
            launch { termsAgreementViewModel.isAllChecked.collect { updateAllAgreementIcon(it) } }
            launch { termsAgreementViewModel.isRequiredAgreementsChecked.collect { updateCompleteButtonState(it) }
            }
        }
    }

    private fun updateAgreementIcons(status: Map<AgreementType, Boolean>) {
        fun getIcon(isChecked: Boolean) = if (isChecked)
            ic_terms_agreement_selected else ic_terms_agreement_unselected

        binding.apply {
            ivTermsAgreementService.setImageResource(getIcon(status[AgreementType.SERVICE] == true))
            ivTermsAgreementPrivacy.setImageResource(getIcon(status[AgreementType.PRIVACY] == true))
            ivTermsAgreementMarketing.setImageResource(getIcon(status[AgreementType.MARKETING] == true))
        }
    }

    private fun updateAllAgreementIcon(isChecked: Boolean) {
        binding.ivTermsAgreementAll.setImageResource(
            if (isChecked) ic_terms_agreement_selected else ic_terms_agreement_unselected,
        )
    }

    private fun updateCompleteButtonState(isEnabled: Boolean) {
        binding.btnTermsAgreementComplete.setBackgroundResource(
            if (isEnabled) {
                bg_novel_rating_date_primary_100_radius_12dp
            } else {
                (bg_profile_edit_gray_70_radius_12dp)
            },
        )
    }

    private fun updateCompleteButtonText() {
        val isFromHome = arguments?.getBoolean(IS_FROM_HOME_TAG, false) ?: false

        binding.btnTermsAgreementComplete.text =
            if (isFromHome) getString(string_terms_agreement_complete)
            else getString(string_terms_agreement_next)
    }

    companion object {
        private const val IS_FROM_HOME_TAG = "IS_FROM_HOME"

        fun newInstance(isFromHome: Boolean = false): TermsAgreementDialogBottomSheet {
            return TermsAgreementDialogBottomSheet().apply {
                arguments = bundleOf(IS_FROM_HOME_TAG to isFromHome)
            }
        }
    }
}
