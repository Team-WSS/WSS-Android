package com.into.websoso.ui.withdraw.first

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.databinding.ActivityWithdrawFirstBinding
import com.into.websoso.ui.withdraw.first.model.UserNovelStatsModel
import com.into.websoso.ui.withdraw.second.WithdrawSecondActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawFirstActivity : BaseActivity<ActivityWithdrawFirstBinding>(R.layout.activity_withdraw_first) {
    private val withdrawFirstViewModel: WithdrawFirstViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onWithdrawCheckButtonClick()
        onBackButtonClick()
        setupObserver()
    }

    private fun onWithdrawCheckButtonClick() {
        binding.tvWithdrawCheckButton.setOnClickListener {
            val intent = WithdrawSecondActivity.getIntent(this)
            startActivity(intent)
        }
    }

    private fun onBackButtonClick() {
        binding.ivWithdrawBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setupObserver() {
        withdrawFirstViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> {
                    binding.wllWithdrawFirst.setWebsosoLoadingVisibility(true)
                    binding.wllWithdrawFirst.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wllWithdrawFirst.setWebsosoLoadingVisibility(false)
                    binding.wllWithdrawFirst.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wllWithdrawFirst.setWebsosoLoadingVisibility(false)
                    binding.wllWithdrawFirst.setErrorLayoutVisibility(false)
                    updateUserNovelStats(uiState.userNovelStats)
                }
            }
        }
    }

    private fun updateUserNovelStats(userNovelStats: UserNovelStatsModel) {
        binding.apply {
            tvWithdrawInterestedCount.text = userNovelStats.interestNovelCount.toString()
            tvWithdrawWatchingCount.text = userNovelStats.watchingNovelCount.toString()
            tvWithdrawWatchedCount.text = userNovelStats.watchedNovelCount.toString()
            tvWithdrawQuitCount.text = userNovelStats.quitNovelCount.toString()
        }
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, WithdrawFirstActivity::class.java)
    }
}
