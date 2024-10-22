package com.teamwss.websoso.ui.activityDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.R.string.my_activity_detail_title
import com.teamwss.websoso.R.string.other_user_page_activity
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityActivityDetailBinding
import com.teamwss.websoso.ui.activityDetail.adapter.ActivityDetailAdapter
import com.teamwss.websoso.ui.main.myPage.MyPageViewModel
import com.teamwss.websoso.ui.main.myPage.myActivity.MyActivityFragment
import com.teamwss.websoso.ui.mapper.toUserProfileModel
import com.teamwss.websoso.ui.otherUserPage.OtherUserPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityDetailActivity :
    BaseActivity<ActivityActivityDetailBinding>(R.layout.activity_activity_detail) {
    private val activityDetailViewModel: ActivityDetailViewModel by viewModels()
    private val activityDetailAdapter: ActivityDetailAdapter by lazy {
        ActivityDetailAdapter()
    }
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by viewModels()

    private val source: String by lazy {
        intent.getStringExtra(MyActivityFragment.EXTRA_SOURCE) ?: ""
    }
    private val userId: Long by lazy { intent.getLongExtra(USER_ID_KEY, DEFAULT_USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUserIDAndSource()
        setActivityTitle()
        setupMyActivitiesDetailAdapter()
        setUpObserver()
        onBackButtonClick()
    }

    private fun setupUserIDAndSource() {
        activityDetailViewModel.source = source
        activityDetailViewModel.userId = userId

        if (source == SOURCE_OTHER_USER_ACTIVITY) {
            otherUserPageViewModel.updateUserId(userId)
        }

        activityDetailViewModel.updateUserActivities(userId)
    }

    private fun setActivityTitle() {
        binding.tvActivityDetailTitle.text = when (source) {
            SOURCE_MY_ACTIVITY -> getString(my_activity_detail_title)
            SOURCE_OTHER_USER_ACTIVITY -> getString(other_user_page_activity)
            else -> ""
        }
    }

    private fun setupMyActivitiesDetailAdapter() {
        binding.rvActivityDetail.apply {
            adapter = activityDetailAdapter
        }
    }

    private fun setUpObserver() {
        activityDetailViewModel.userActivity.observe(this) { activities ->
            activityDetailAdapter.submitList(activities)
        }

        when (activityDetailViewModel.source) {
            SOURCE_MY_ACTIVITY -> {
                myPageViewModel.myPageUiState.observe(this) { uiState ->
                    uiState.myProfile?.let { myProfile ->
                        activityDetailAdapter.userProfile = myProfile.toUserProfileModel()
                        activityDetailAdapter.submitList(activityDetailAdapter.currentList)
                    }
                }
            }

            SOURCE_OTHER_USER_ACTIVITY -> {
                otherUserPageViewModel.otherUserProfile.observe(this) { otherUserProfile ->
                    otherUserProfile?.let {
                        activityDetailAdapter.userProfile = otherUserProfile.toUserProfileModel()
                        activityDetailAdapter.submitList(activityDetailAdapter.currentList)
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
        const val DEFAULT_USER_ID = -1L
        const val SOURCE_MY_ACTIVITY = "myActivity"
        const val SOURCE_OTHER_USER_ACTIVITY = "otherUserActivity"

        fun getIntent(context: Context): Intent {
            return Intent(context, ActivityDetailActivity::class.java)
        }
    }
}