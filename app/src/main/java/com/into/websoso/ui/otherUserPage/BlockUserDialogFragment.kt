package com.into.websoso.ui.otherUserPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseDialogFragment
import com.into.websoso.common.ui.model.ResultFrom.BlockUser
import com.into.websoso.common.util.SingleEventHandler
import com.into.websoso.common.util.tracker.Tracker
import com.into.websoso.databinding.DialogBlockUserBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BlockUserDialogFragment :
    BaseDialogFragment<DialogBlockUserBinding>(R.layout.dialog_block_user) {
    @Inject
    lateinit var tracker: Tracker

    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val otherUserPageViewModel: OtherUserPageViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCancelButtonClick()
        onBlockButtonClick()
        setupObserver()
    }

    private fun onCancelButtonClick() {
        binding.tvBlockUserCancelButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                dismiss()
            }
        }
    }

    private fun onBlockButtonClick() {
        binding.tvBlockUserButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                tracker.trackEvent("other_block")
                otherUserPageViewModel.updateBlockedUser()
            }
        }
    }

    private fun setupObserver() {
        otherUserPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            if (uiState.isBlockedCompleted) {
                val intent = Intent().apply {
                    putExtra(
                        USER_NICKNAME,
                        otherUserPageViewModel.uiState.value?.otherUserProfile?.nickname
                    )
                }
                activity?.setResult(BlockUser.RESULT_OK, intent)
                dismiss()
                requireActivity().finish()
            }
        }
    }

    companion object {
        const val USER_NICKNAME = "UserNickname"
        const val TAG = "BlockUserDialog"

        fun newInstance(): BlockUserDialogFragment = BlockUserDialogFragment()
    }
}