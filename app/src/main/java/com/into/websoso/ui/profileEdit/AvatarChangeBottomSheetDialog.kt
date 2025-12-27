package com.into.websoso.ui.profileEdit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseBottomSheetDialog
import com.into.websoso.databinding.DialogAvatarChangeBinding
import com.into.websoso.ui.profileEdit.adapter.AvatarPagerAdapter
import com.into.websoso.ui.profileEdit.model.AvatarChangeUiState

class AvatarChangeBottomSheetDialog : BaseBottomSheetDialog<DialogAvatarChangeBinding>(R.layout.dialog_avatar_change) {
    private val profileEditViewModel: ProfileEditViewModel by activityViewModels()

    private val avatarPagerAdapter: AvatarPagerAdapter by lazy {
        AvatarPagerAdapter { avatarModel -> profileEditViewModel.updateSelectedAvatar(avatarModel) }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupDialogBehavior()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.viewModel = profileEditViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.onCancelClick = ::onCancelClick
        binding.onSaveClick = ::onSaveClick
    }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun setupObserver() {
        profileEditViewModel.avatarChangeUiState.observe(viewLifecycleOwner) { uiState ->
            handleAvatarChangeUiState(uiState)
        }
    }

    private fun handleAvatarChangeUiState(uiState: AvatarChangeUiState) {
        when {
            uiState.loading -> Unit

            uiState.error -> Unit

            uiState.avatars.isNotEmpty() -> {
                setupViewPager()
                avatarPagerAdapter.submitList(uiState.avatars.chunked(10))
            }
        }
    }

    private fun setupViewPager() {
        binding.vpProfileEditAvatar.apply {
            adapter = avatarPagerAdapter
            offscreenPageLimit = 1
        }

        binding.dotsIndicatorProfileEdit.attachTo(binding.vpProfileEditAvatar)
    }

    private fun onSaveClick() {
        profileEditViewModel.updateRepresentativeAvatar()
        dismiss()
    }

    fun onCancelClick() {
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        profileEditViewModel.updateSelectedAvatar(profileEditViewModel.getRepresentativeAvatar())
    }
}
