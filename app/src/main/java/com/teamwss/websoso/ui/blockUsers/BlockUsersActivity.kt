package com.teamwss.websoso.ui.blockUsers

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityBlockUsersBinding
import com.teamwss.websoso.ui.blockUsers.adapter.BlockUsersAdapter
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BlockUsersActivity :
    BindingActivity<ActivityBlockUsersBinding>(R.layout.activity_block_users) {
    private val blockUsersAdapter: BlockUsersAdapter by lazy { BlockUsersAdapter(blockUsersViewModel::deleteBlockUser) }
    private val blockUsersViewModel: BlockUsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupTranslucentOnStatusBar()
        setupAdapter()
        setupObserver()
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun setupAdapter() {
        binding.rvBlockUsers.adapter = blockUsersAdapter
    }

    private fun setupObserver() {
        blockUsersViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> Unit
                uiState.error -> Unit
                !uiState.loading -> blockUsersAdapter.submitList(uiState.blockUsers)
            }
        }
    }
}