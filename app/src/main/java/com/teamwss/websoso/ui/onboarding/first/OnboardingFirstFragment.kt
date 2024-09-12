package com.teamwss.websoso.ui.onboarding.first

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.hideKeyboard
import com.teamwss.websoso.databinding.FragmentOnboardingFirstBinding
import com.teamwss.websoso.ui.onboarding.OnboardingViewModel
import com.teamwss.websoso.ui.onboarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onboarding.first.model.onboardingFirstUiModelMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFirstFragment :
    BaseFragment<FragmentOnboardingFirstBinding>(R.layout.fragment_onboarding_first) {
    private val viewModel by activityViewModels<OnboardingViewModel>()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        hideKeyboard()
        setupFocusChangeListener()
        observeInputTypeChanges()
        observeInputNicknameChanges()
        onNicknameDuplicationCheckButtonClick()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun hideKeyboard() {
        view?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                v.hideKeyboard()
                v.clearFocus()
            }
            false
        }
    }

    private fun setupFocusChangeListener() {
        binding.etOnboardingFirstNickname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.updateNicknameInputType(NicknameInputType.TYPING)
            }
        }
    }

    private fun observeInputNicknameChanges() {
        viewModel.currentNicknameInput.observe(viewLifecycleOwner) {
            viewModel.validateNickname()
        }
    }

    private fun observeInputTypeChanges() {
        viewModel.onboardingFirstUiState.observe(viewLifecycleOwner) {
            updateUI(it.nicknameInputType)
        }
    }

    private fun onNicknameDuplicationCheckButtonClick() {
        binding.btnOnboardingFirstNicknameDuplicateCheck.setOnClickListener {
            singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                viewModel.dispatchNicknameValidity()
            }
        }
    }

    private fun updateUI(type: NicknameInputType) {
        onboardingFirstUiModelMap[type]?.let { res ->
            with(binding) {
                clOnBoardingFirstNicknameInput.background =
                    requireContext().getDrawable(res.editTextBackgroundRes)
                ivOnboardingFirstNicknameInputClear.setImageDrawable(
                    requireContext().getDrawable(res.clearIconRes)
                )
                tvOnBoardingFirstMessage.setTextColor(
                    requireContext().getColor(res.messageColorRes)
                )
            }
        }
    }
}
