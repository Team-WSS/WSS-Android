package com.into.websoso.ui.notice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseActivity
import com.into.websoso.databinding.ActivityNoticeBinding
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.notice.adapter.NoticeAdapter
import com.into.websoso.ui.notice.model.NoticeModel
import com.into.websoso.ui.noticeDetail.NoticeDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BaseActivity<ActivityNoticeBinding>(R.layout.activity_notice) {

    private val noticeViewModel: NoticeViewModel by viewModels()

    private val noticeAdapter: NoticeAdapter by lazy {
        NoticeAdapter(::navigateToNoticeDetail)
    }

    private fun navigateToNoticeDetail(notice: NoticeModel) =
        startActivity(NoticeDetailActivity.getIntent(this, notice))

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
        startActivity(MainActivity.getIntent(context = this, isLogin = true))
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
