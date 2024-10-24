package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentOtherUserActivityBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import com.teamwss.websoso.ui.otherUserPage.OtherUserPageViewModel
import com.teamwss.websoso.ui.otherUserPage.otherUserActivity.adapter.OtherUserActivityAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserActivityFragment :
    BaseFragment<FragmentOtherUserActivityBinding>(R.layout.fragment_other_user_activity),
    ActivityItemClickListener {
    private val otherUserActivityViewModel: OtherUserActivityViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by activityViewModels()
    private val otherUserActivityAdapter: OtherUserActivityAdapter by lazy {
        OtherUserActivityAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserId()
        setUpMyActivitiesAdapter()
        setupObserver()
        onActivityDetailButtonClick()
    }

    private fun setupUserId() {
        val userId = arguments?.getLong(USER_ID_KEY) ?: 0L
        otherUserActivityViewModel.updateUserId(userId)
    }

    private fun setUpMyActivitiesAdapter() {
        binding.rvOtherUserActivity.adapter = otherUserActivityAdapter
    }

    private fun setupObserver() {
        otherUserActivityViewModel.otherUserActivity.observe(viewLifecycleOwner) { activities ->
            val userProfile = getUserProfile()
            updateAdapterWithActivitiesAndProfile(activities, userProfile)
        }

        otherUserPageViewModel.otherUserProfile.observe(viewLifecycleOwner) { otherUserProfile ->
            otherUserProfile?.let {
                val userProfile = UserProfileModel(
                    nickname = it.nickname,
                    avatarImage = it.avatarImage
                )
                updateAdapterWithActivitiesAndProfile(
                    otherUserActivityViewModel.otherUserActivity.value,
                    userProfile
                )
            }
        }
    }

    private fun updateAdapterWithActivitiesAndProfile(
        activities: List<ActivityModel>?,
        userProfile: UserProfileModel?
    ) {
        if (activities != null && userProfile != null) {
            val userActivityModels = activities.map { activity ->
                UserActivityModel(activity, userProfile)
            }
            otherUserActivityAdapter.submitList(userActivityModels)
        } else {
            otherUserActivityAdapter.submitList(emptyList())
        }
    }

    private fun getUserProfile(): UserProfileModel? {
        return otherUserPageViewModel.otherUserProfile.value?.let {
            UserProfileModel(
                nickname = it.nickname,
                avatarImage = it.avatarImage
            )
        }
    }

    private fun onActivityDetailButtonClick() {
        binding.btnOtherUserActivityMore.setOnClickListener {
            val intent = ActivityDetailActivity.getIntent(requireContext()).apply {
                putExtra(EXTRA_SOURCE, SOURCE_OTHER_USER_ACTIVITY)
                putExtra(USER_ID_KEY, otherUserActivityViewModel.userId.value)
            }
            startActivity(intent)
        }
    }

    override fun onContentClick(feedId: Long) {
        startActivity(FeedDetailActivity.getIntent(requireContext(), feedId))
    }

    override fun onNovelInfoClick(novelId: Long) {
        startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
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

        otherUserActivityViewModel.updateActivityLike(view.isSelected, feedId, updatedLikeCount)
    }

    override fun onMoreButtonClick(view: View, feedId: Long) {
        // TODO 팝업메뉴 수정 and 차단
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_OTHER_USER_ACTIVITY = "otherUserActivity"
        const val USER_ID_KEY = "userId"

        fun newInstance(userId: Long) = OtherUserActivityFragment().apply {
            arguments = Bundle().apply {
                putLong(USER_ID_KEY, userId)
            }
        }
    }
}