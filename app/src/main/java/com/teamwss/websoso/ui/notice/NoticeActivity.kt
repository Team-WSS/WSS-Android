package com.teamwss.websoso.ui.notice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityNoticeBinding
import com.teamwss.websoso.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BaseActivity<ActivityNoticeBinding>(R.layout.activity_notice) {

    private val noticeViewModel: NoticeViewModel by viewModels()

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupClickListeners()
        setupAdapter()
        setupObserver()
    }

    private fun setupClickListeners() {
        binding.ivNoticeBack.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(MainActivity.getIntent(this))
    }

    private fun setupAdapter() {
        binding.rvNotice.adapter = noticeAdapter
    }

    private fun setupObserver() {
        noticeViewModel.noticeUiState.observe(this) { noticeUiState ->
            when {
                noticeUiState.loading -> binding.wllNotice.setLoadingLayoutVisibility(true)
                noticeUiState.error -> binding.wllNotice.setLoadingLayoutVisibility(false)
                !noticeUiState.loading -> {
                    binding.wllNotice.setWebsosoLoadingVisibility(false)
                    noticeAdapter.submitList(noticeUiState.notices)
                }
            }
        }
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, NoticeActivity::class.java)
        }
    }
}
