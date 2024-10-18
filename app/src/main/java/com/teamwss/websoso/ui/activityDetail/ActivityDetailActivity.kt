package com.teamwss.websoso.ui.activityDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.R.string.my_activity_detail_title
import com.teamwss.websoso.R.string.other_user_page_activity
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.databinding.ActivityActivityDetailBinding
import com.teamwss.websoso.ui.activityDetail.adapter.ActivityDetailAdapter
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.myPage.MyPageViewModel
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.teamwss.websoso.ui.main.myPage.myActivity.MyActivityFragment
import com.teamwss.websoso.ui.mapper.toUserProfileModel
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import com.teamwss.websoso.ui.otherUserPage.OtherUserPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityDetailActivity :
    BaseActivity<ActivityActivityDetailBinding>(R.layout.activity_activity_detail) {
    private val activityDetailViewModel: ActivityDetailViewModel by viewModels()
    private val activityDetailAdapter: ActivityDetailAdapter by lazy {
        ActivityDetailAdapter(onClickFeedItem())
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

    private fun onClickFeedItem() = object : ActivityItemClickListener {
        override fun onContentClick(feedId: Long) {
            startActivity(FeedDetailActivity.getIntent(this@ActivityDetailActivity, feedId))
        }

        override fun onNovelInfoClick(novelId: Long) {
            startActivity(NovelDetailActivity.getIntent(this@ActivityDetailActivity, novelId))
        }

        override fun onLikeButtonClick(view: View, feedId: Long) {
            val likeCountTextView: TextView = view.findViewById(R.id.tv_my_activity_thumb_up_count)
            val currentLikeCount = likeCountTextView.text.toString().toInt()

            val updatedLikeCount: Int = if (view.isSelected) {
                if (currentLikeCount > 0) currentLikeCount - 1 else 0
            } else {
                currentLikeCount + 1
            }

            likeCountTextView.text = updatedLikeCount.toString()
            view.isSelected = !view.isSelected

            activityDetailViewModel.updateActivityLike(view.isSelected, feedId, updatedLikeCount)
        }

        override fun onMoreButtonClick(view: View, feedId: Long) {
            // TODO 팝업메뉴 수정 및 차단
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