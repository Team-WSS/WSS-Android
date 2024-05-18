package com.teamwss.websoso.ui.onBoarding.first

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentOnBoardingFirstBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.onBoarding.OnBoardingViewModel
import com.teamwss.websoso.ui.onBoarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onBoarding.first.model.onBoardingFirstUiModelMap

class OnBoardingFirstFragment :
    BindingFragment<FragmentOnBoardingFirstBinding>(R.layout.fragment_on_boarding_first) {
    private val viewModel by activityViewModels<OnBoardingViewModel>()

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
        onBoardingFirstUiModelMap[type]?.let { res ->
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