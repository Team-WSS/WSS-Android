package com.teamwss.websoso.ui.main.myPage.myActivity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentMyActivityBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.myPage.MyPageViewModel
import com.teamwss.websoso.ui.main.myPage.myActivity.adapter.MyActivityAdapter
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityFragment :
    BaseFragment<FragmentMyActivityBinding>(R.layout.fragment_my_activity) {
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val myActivityAdapter: MyActivityAdapter by lazy {
        MyActivityAdapter(onClickFeedItem())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMyActivitiesAdapter()
        setupObserver()
        onMyActivityDetailButtonClick()
    }

    private fun setupMyActivitiesAdapter() {
        binding.rvMyActivity.adapter = myActivityAdapter
    }

    private fun setupObserver() {
        myActivityViewModel.myActivity.observe(viewLifecycleOwner) { activities ->
            myActivityAdapter.submitList(activities)
        }

        myPageViewModel.myPageUiState.observe(viewLifecycleOwner) { uiState ->
            uiState.myProfile?.let { myProfileEntity ->
                val userProfile = UserProfileModel(
                    nickname = myProfileEntity.nickname,
                    avatarImage = myProfileEntity.avatarImage,
                )
                myActivityAdapter.setUserProfile(userProfile)
            }
        }
    }

    private fun onMyActivityDetailButtonClick() {
        binding.btnMyActivityMore.setOnClickListener {
            val intent = ActivityDetailActivity.getIntent(requireContext()).apply {
                putExtra(EXTRA_SOURCE, SOURCE_MY_ACTIVITY)
            }
            startActivity(intent)
        }
    }

    private fun onClickFeedItem() = object : ActivityItemClickListener {

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

            myActivityViewModel.updateActivityLike(view.isSelected, feedId, updatedLikeCount)
        }

        override fun onMoreButtonClick(view: View, feedId: Long) {
            // TODO 팝업메뉴 수정 and 차단
        }
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_MY_ACTIVITY = "myActivity"
    }
}