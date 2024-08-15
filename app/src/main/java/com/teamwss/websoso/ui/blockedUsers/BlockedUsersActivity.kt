package com.teamwss.websoso.ui.blockedUsers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityBlockedUsersBinding
import com.teamwss.websoso.ui.blockedUsers.adapter.BlockedUsersAdapter
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockedUsersActivity :
    BindingActivity<ActivityBlockedUsersBinding>(R.layout.activity_blocked_users) {
    private val blockedUsersAdapter: BlockedUsersAdapter by lazy {
        BlockedUsersAdapter(
            blockedUsersViewModel::deleteBlockedUser,
        )
    }
    private val blockedUsersViewModel: BlockedUsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupTranslucentOnStatusBar()
        setupAdapter()
        setupObserver()
        onBackButtonClick()
    }

    private fun bindViewModel() {
        binding.blockedUserViewModel = blockedUsersViewModel
        binding.lifecycleOwner = this
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
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