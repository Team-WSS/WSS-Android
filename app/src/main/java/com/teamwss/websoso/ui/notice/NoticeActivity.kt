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
        setupRecyclerView()
        setupObservers()
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

    private fun setupRecyclerView() {
        binding.rvNotice.adapter = noticeAdapter
    }

    private fun setupObservers() {
        noticeViewModel.notices.observe(this) {
            noticeAdapter.submitList(it)
        }
    }
}
