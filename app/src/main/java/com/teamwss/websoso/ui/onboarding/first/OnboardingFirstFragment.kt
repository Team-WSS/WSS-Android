package com.teamwss.websoso.ui.onboarding.first

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentOnboardingFirstBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.onboarding.OnboardingViewModel
import com.teamwss.websoso.ui.onboarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onboarding.first.model.onboardingFirstUiModelMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFirstFragment :
    BindingFragment<FragmentOnboardingFirstBinding>(R.layout.fragment_onboarding_first) {
    private val viewModel by activityViewModels<OnboardingViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        observeInputNicknameChanges()
        observeInputTypeChanges()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeInputNicknameChanges() {
        viewModel.currentNicknameInput.observe(viewLifecycleOwner) {
            viewModel.validateNickname()
        }
    }

    private fun observeInputTypeChanges() {
        viewModel.onBoardingFirstUiState.observe(viewLifecycleOwner) {
            updateUI(it.nicknameInputType)
        }
    }

    private fun updateUI(type: NicknameInputType) {
        onboardingFirstUiModelMap[type]?.let { res ->
            with(binding) {
                clOnBoardingFirstNicknameInput.background =
                    requireContext().getDrawable(res.editTextBackgroundRes)
                ivOnBoardingFirstNicknameInputClear.setImageDrawable(
                    requireContext().getDrawable(res.clearIconRes)
                )
                tvOnBoardingFirstMessage.setTextColor(
                    requireContext().getColor(res.messageColorRes)
                )
            }
        }
    }
}