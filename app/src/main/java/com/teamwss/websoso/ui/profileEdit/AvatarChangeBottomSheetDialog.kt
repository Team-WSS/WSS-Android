package com.teamwss.websoso.ui.profileEdit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseBottomSheetDialog
import com.teamwss.websoso.databinding.DialogAvatarChangeBinding
import com.teamwss.websoso.ui.profileEdit.adapter.AvatarChangeAdapter
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState

class AvatarChangeBottomSheetDialog : BaseBottomSheetDialog<DialogAvatarChangeBinding>(R.layout.dialog_avatar_change) {
    private val profileEditViewModel: ProfileEditViewModel by activityViewModels()
    private val avatarChangeAdapter: AvatarChangeAdapter by lazy {
        AvatarChangeAdapter { avatarModel -> profileEditViewModel.updateSelectedAvatar(avatarModel) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            val selectedAvatarId = profileEditViewModel.getSelectedAvatar().avatarId
            handleAvatarChangeUiState(uiState)
            updateAvatarAnimation(profileEditViewModel.getAvatarAnimation(selectedAvatarId))
        }
    }

    private fun handleAvatarChangeUiState(uiState: AvatarChangeUiState) {
        when {
            uiState.loading -> {

            }

            uiState.error -> {

            }

            else -> {
                setupRecyclerView()
                avatarChangeAdapter.submitList(uiState.avatars)
            }
        }
    }

    private fun setupRecyclerView() {
        // TODO: 갯수가 일정 갯수 초과시 그리드 리사이클러뷰 가운데 정렬
        if (binding.rvProfileEditAvatar.childCount > 0) return
        binding.rvProfileEditAvatar.apply {
            adapter = avatarChangeAdapter
            layoutManager = GridLayoutManager(context, profileEditViewModel.getFormattedSpanCount())
            itemAnimator = null
        }
    }

    private fun updateAvatarAnimation(lottieId: Int) {
        binding.lavProfileEditAvatar.apply {
            setAnimation(lottieId)
            playAnimation()
        }
    }

    fun onSaveClick() {
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