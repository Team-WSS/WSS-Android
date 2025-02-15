package com.into.websoso.ui.termsAgreement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

        setupBottomSheetDialog()
        onRequiredTermsAgreementClick()
        onTermsAgreementToggleClick()
        onTermsAgreementCompleteButtonClick()
        setupViewModel()
        updateCompleteButtonText()
    }

    private fun setupBottomSheetDialog() {
        (dialog as BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            behavior.isHideable = false
            setCancelable(false)
        }
    }

    private fun onRequiredTermsAgreementClick() {
        binding.tvTermsAgreementService.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.terms_agreement_service)),
                ),
            )
        }

        binding.tvTermsAgreementPrivacy.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.terms_agreement_privacy)),
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    termsAgreementViewModel.agreementStatus.collect { status ->
                        updateAgreementIcons(status)
                        updateAllAgreementIcon(status.values.all { it })
                    }
                }

                launch {
                    termsAgreementViewModel.isRequiredAgreementsChecked.collect {
                        updateCompleteButtonState(it)
                    }
                }
            }
        }
    }

    private fun updateAgreementIcons(status: Map<AgreementType, Boolean>) {
        binding.apply {
            ivTermsAgreementService.setImageResource(getToggleIcon(status[AgreementType.SERVICE] == true))
            ivTermsAgreementPrivacy.setImageResource(getToggleIcon(status[AgreementType.PRIVACY] == true))
            ivTermsAgreementMarketing.setImageResource(getToggleIcon(status[AgreementType.MARKETING] == true))
        }
    }

    private fun getToggleIcon(isChecked: Boolean): Int {
        return if (isChecked) ic_terms_agreement_selected else ic_terms_agreement_unselected
    }

    private fun updateAllAgreementIcon(isChecked: Boolean) {
        binding.ivTermsAgreementAll.setImageResource(
            if (isChecked) ic_terms_agreement_selected else ic_terms_agreement_unselected,
        )
    }

    private fun updateCompleteButtonState(isEnabled: Boolean) {
        binding.btnTermsAgreementComplete.setBackgroundResource(
            when (isEnabled) {
                true -> bg_novel_rating_date_primary_100_radius_12dp
                false -> bg_profile_edit_gray_70_radius_12dp
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
