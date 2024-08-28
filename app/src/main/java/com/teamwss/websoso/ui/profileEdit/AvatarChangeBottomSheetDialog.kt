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
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState.Error
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState.Loading
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState.Success

class AvatarChangeBottomSheetDialog : BaseBottomSheetDialog<DialogAvatarChangeBinding>(R.layout.dialog_avatar_change) {
    private val profileEditViewModel: ProfileEditViewModel by activityViewModels()
    private val avatarChangeAdapter: AvatarChangeAdapter by lazy {
        AvatarChangeAdapter { avatarModel -> profileEditViewModel.updateSelectedAvatar(avatarModel) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onCancelClick = ::onCancelClick
        binding.onSaveClick = ::onSaveClick

        setupObserver()
        setupDialogBehavior()
    }

    private fun setupObserver() {
        profileEditViewModel.avatarChangeUiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is Success -> {
                    setupRecyclerView()
                    avatarChangeAdapter.submitList(uiState.avatars)
                }

                Loading -> {
                }

                Error -> {
                }
            }
        }
    }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
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

    fun onSaveClick() {
        profileEditViewModel.updateRepresentativeAvatar()
        dismiss()
    }

    fun onCancelClick() {
        dismiss()
    }
}