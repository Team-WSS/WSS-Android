package com.teamwss.websoso.ui.otherUserPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseDialogFragment
import com.teamwss.websoso.common.ui.model.ResultFrom.BlockUser
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.databinding.DialogBlockUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockUserDialogFragment :
    BaseDialogFragment<DialogBlockUserBinding>(R.layout.dialog_block_user) {
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
                otherUserPageViewModel.updateBlockedUser()
            }
        }
    }

    private fun setupObserver() {
        otherUserPageViewModel.isBlockedCompleted.observe(viewLifecycleOwner) { isBlockedCompleted ->
            if (isBlockedCompleted) {
                val intent = Intent().apply {
                    putExtra(USER_NICKNAME, otherUserPageViewModel.otherUserProfile.value?.nickname)
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