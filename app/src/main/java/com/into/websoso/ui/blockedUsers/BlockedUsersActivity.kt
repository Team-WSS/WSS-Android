package com.into.websoso.ui.blockedUsers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.into.websoso.R.layout.activity_blocked_users
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.resource.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.core.resource.R.string.blocked_users_unblocked
import com.into.websoso.databinding.ActivityBlockedUsersBinding
import com.into.websoso.ui.blockedUsers.adapter.BlockedUsersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockedUsersActivity : BaseActivity<ActivityBlockedUsersBinding>(activity_blocked_users) {
    private val blockedUsersAdapter: BlockedUsersAdapter by lazy {
        BlockedUsersAdapter(::onUnblockedUserButtonClick)
    }
    private val blockedUsersViewModel: BlockedUsersViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupAdapter()
        setupObserver()
        onBackButtonClick()
    }

    private fun bindViewModel() {
        binding.blockedUserViewModel = blockedUsersViewModel
        binding.lifecycleOwner = this
    }

    private fun setupAdapter() {
        binding.rvBlockedUsers.adapter = blockedUsersAdapter
    }

    private fun setupObserver() {
        blockedUsersViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> Unit
                uiState.error -> Unit
                !uiState.loading -> blockedUsersAdapter.submitList(uiState.blockedUsers)
            }
        }

        blockedUsersViewModel.unblockedUserNickname.observe(this) { userNickname ->
            showSnackBar(userNickname)
        }
    }

    private fun showSnackBar(nickName: String) {
        showWebsosoSnackBar(
            view = binding.root,
            message = getString(blocked_users_unblocked, nickName),
            icon = ic_blocked_user_snack_bar,
        )
    }

    private fun onBackButtonClick() {
        binding.ivBlockedUsersBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onUnblockedUserButtonClick(blockId: Long) {
        singleEventHandler.throttleFirst { blockedUsersViewModel.deleteBlockedUser(blockId) }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, BlockedUsersActivity::class.java)
    }
}
