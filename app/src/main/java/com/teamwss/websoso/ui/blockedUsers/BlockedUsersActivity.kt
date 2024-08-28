package com.teamwss.websoso.ui.blockedUsers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoCustomSnackBar
import com.teamwss.websoso.databinding.ActivityBlockedUsersBinding
import com.teamwss.websoso.ui.blockedUsers.adapter.BlockedUsersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockedUsersActivity :
    BaseActivity<ActivityBlockedUsersBinding>(R.layout.activity_blocked_users) {
    private val blockedUsersAdapter: BlockedUsersAdapter by lazy {
        BlockedUsersAdapter(
            blockedUsersViewModel::deleteBlockedUser,
        )
    }
    private val blockedUsersViewModel: BlockedUsersViewModel by viewModels()

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
        WebsosoCustomSnackBar.make(binding.root)
            .setText(getString(R.string.blocked_users_unblocked, nickName))
            .setIcon(R.drawable.ic_blocked_user_snack_bar)
            .show()
    }

    private fun onBackButtonClick() {
        binding.ivBlockedUsersBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, BlockedUsersActivity::class.java)
        }
    }
}
