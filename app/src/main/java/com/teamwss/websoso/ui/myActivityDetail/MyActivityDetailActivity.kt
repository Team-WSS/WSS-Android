package com.teamwss.websoso.ui.myActivityDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityMyActivityDetailBinding
import com.teamwss.websoso.ui.myActivityDetail.adapter.MyActivityDetailAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityDetailActivity :
    BaseActivity<ActivityMyActivityDetailBinding>(R.layout.activity_my_activity_detail) {
    private val myActivityDetailViewModel: MyActivityDetailViewModel by viewModels()
    private val myActivityDetailAdapter: MyActivityDetailAdapter by lazy {
        MyActivityDetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpMyActivitiesDetailAdapter()
        setUpObserve()
        onBackButtonClick()
    }

    private fun setUpMyActivitiesDetailAdapter() {
        binding.rvMyActivityDetail.apply {
            adapter = myActivityDetailAdapter
        }
    }

    private fun setUpObserve() {
        myActivityDetailViewModel.myActivity.observe(this) { activities ->
            myActivityDetailAdapter.submitList(activities)
        }
    }

    private fun onBackButtonClick() {
        binding.ivMyActivityDetailBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun createIntentForMyActivityDetail(context: Context): Intent {
            return Intent(context, MyActivityDetailActivity::class.java)
        }
    }
}