package com.teamwss.websoso.ui.activityDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityActivityDetailBinding
import com.teamwss.websoso.ui.activityDetail.adapter.ActivityDetailAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityDetailActivity :
    BaseActivity<ActivityActivityDetailBinding>(R.layout.activity_activity_detail) {
    private val activityDetailViewModel: ActivityDetailViewModel by viewModels()
    private val activityDetailAdapter: ActivityDetailAdapter by lazy {
        ActivityDetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityTitle()
        setUpMyActivitiesDetailAdapter()
        setUpObserve()
        onBackButtonClick()
    }

    private fun setActivityTitle() {
        val source = intent.getStringExtra("source")

        val title = when (source) {
            "myActivity" -> getString(R.string.my_activity_detail_title)
            "otherUserActivity" -> getString(R.string.other_user_page_activity)
            else -> ""
        }
        binding.tvActivityDetailTitle.text = title
    }

    private fun setUpMyActivitiesDetailAdapter() {
        binding.rvActivityDetail.apply {
            adapter = activityDetailAdapter
        }
    }

    private fun setUpObserve() {
        activityDetailViewModel.userActivity.observe(this) { activities ->
            activityDetailAdapter.submitList(activities)
        }
    }

    private fun onBackButtonClick() {
        binding.ivActivityDetailBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun createIntentForMyActivityDetail(context: Context): Intent {
            return Intent(context, ActivityDetailActivity::class.java)
        }
    }
}