package com.teamwss.websoso.ui.notice

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNoticeBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : BindingActivity<ActivityNoticeBinding>(R.layout.activity_notice) {

    private val viewModel: NoticeViewModel by viewModels()

    private val adapter: NoticeAdapter by lazy {
        NoticeAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupUi() {
        binding.ivNoticeBack.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = MainActivity.getIntent(this)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        binding.rvNotice.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.notices.observe(this, Observer {
            adapter.submitList(it)
        })
    }
}
