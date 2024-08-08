package com.teamwss.websoso.ui.notice

import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNoticeBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BindingActivity<ActivityNoticeBinding>(R.layout.activity_notice) {

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
        val intent = MainActivity.getIntent(this)
        startActivity(intent)
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
}
