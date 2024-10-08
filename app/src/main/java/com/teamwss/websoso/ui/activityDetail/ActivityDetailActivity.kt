package com.teamwss.websoso.ui.activityDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityActivityDetailBinding
import com.teamwss.websoso.ui.activityDetail.adapter.ActivityDetailAdapter
import com.teamwss.websoso.ui.main.myPage.MyPageViewModel
import com.teamwss.websoso.ui.main.myPage.myActivity.MyActivityFragment
import com.teamwss.websoso.ui.mapper.toUserProfileModel
import com.teamwss.websoso.ui.otherUserPage.OtherUserPageViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.teamwss.websoso.R.string.my_activity_detail_title
import com.teamwss.websoso.R.string.other_user_page_activity

@AndroidEntryPoint
class ActivityDetailActivity :
    BaseActivity<ActivityActivityDetailBinding>(R.layout.activity_activity_detail) {
    private val activityDetailViewModel: ActivityDetailViewModel by viewModels()
    private val activityDetailAdapter: ActivityDetailAdapter by lazy {
        ActivityDetailAdapter()
    }
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by viewModels()

    private lateinit var source: String
    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUserIDAndSource()
        setActivityTitle()
        setupMyActivitiesDetailAdapter()
        setUpObserve(source)
        onBackButtonClick()
    }

    private fun setupUserIDAndSource() {
        userId = intent.getLongExtra(USER_ID_KEY, -1L)
        source = intent.getStringExtra(MyActivityFragment.EXTRA_SOURCE) ?: ""

        if (source == SOURCE_OTHER_USER_ACTIVITY) {
            otherUserPageViewModel.updateUserId(userId)
        }

        activityDetailViewModel.updateUserId(userId)
    }

    private fun setActivityTitle() {
        val title = when (source) {
            SOURCE_MY_ACTIVITY -> getString(my_activity_detail_title)
            SOURCE_OTHER_USER_ACTIVITY -> getString(other_user_page_activity)
            else -> ""
        }
        binding.tvActivityDetailTitle.text = title
    }

    private fun setupMyActivitiesDetailAdapter() {
        binding.rvActivityDetail.apply {
            adapter = activityDetailAdapter
        }
    }

    private fun setUpObserve(source: String?) {
        activityDetailViewModel.userActivity.observe(this) { activities ->
            activityDetailAdapter.submitList(activities)
        }

        when (source) {
            SOURCE_MY_ACTIVITY -> {
                myPageViewModel.myPageUiState.observe(this) { uiState ->
                    uiState.myProfile?.let { myProfile ->
                        activityDetailAdapter.setUserProfile(myProfile.toUserProfileModel())
                    }
                }
            }

            SOURCE_OTHER_USER_ACTIVITY -> {
                otherUserPageViewModel.otherUserProfile.observe(this) { otherUserProfile ->
                    otherUserProfile?.let {
                        activityDetailAdapter.setUserProfile(otherUserProfile.toUserProfileModel())
                    }
                }
            }
        }
    }

    private fun onBackButtonClick() {
        binding.ivActivityDetailBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val USER_ID_KEY = "userId"
        const val SOURCE_MY_ACTIVITY = "myActivity"
        const val SOURCE_OTHER_USER_ACTIVITY = "otherUserActivity"

        fun getIntent(context: Context): Intent {
            return Intent(context, ActivityDetailActivity::class.java)
        }
    }
}