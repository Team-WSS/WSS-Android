package com.teamwss.websoso.ui.withdraw.first

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityWithdrawFirstBinding
import com.teamwss.websoso.common.ui.base.BindingActivity
import com.teamwss.websoso.ui.withdraw.first.model.UserNovelStatsModel
import com.teamwss.websoso.ui.withdraw.second.WithdrawSecondActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WithdrawFirstActivity :
    BindingActivity<ActivityWithdrawFirstBinding>(R.layout.activity_withdraw_first) {
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
                    binding.wlWithdrawFirst.setWebsosoLoadingVisibility(true)
                    binding.wlWithdrawFirst.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wlWithdrawFirst.setWebsosoLoadingVisibility(false)
                    binding.wlWithdrawFirst.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wlWithdrawFirst.setWebsosoLoadingVisibility(false)
                    binding.wlWithdrawFirst.setErrorLayoutVisibility(false)
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

        fun getIntent(context: Context): Intent {
            return Intent(context, WithdrawFirstActivity::class.java)
        }
    }
}