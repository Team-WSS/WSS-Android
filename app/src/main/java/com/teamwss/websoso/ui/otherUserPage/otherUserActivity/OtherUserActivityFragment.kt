package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentOtherUserActivityBinding
import com.teamwss.websoso.databinding.MenuOtherUserActivityPopupBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDoneDialogFragment
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
    BaseFragment<FragmentOtherUserActivityBinding>(R.layout.fragment_other_user_activity) {
    private val otherUserActivityViewModel: OtherUserActivityViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by activityViewModels()
    private val otherUserActivityAdapter: OtherUserActivityAdapter by lazy {
        OtherUserActivityAdapter(onClickFeedItem())
    }
    private var _popupWindow: PopupWindow? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserId()
        setupMyActivitiesAdapter()
        setupObserver()
        onActivityDetailButtonClick()
    }

    private fun setupUserId() {
        val userId = arguments?.getLong(USER_ID_KEY) ?: 0L
        otherUserActivityViewModel.updateUserId(userId)
    }

    private fun setupMyActivitiesAdapter() {
        binding.rvOtherUserActivity.adapter = otherUserActivityAdapter
    }

    private fun setupObserver() {
        otherUserActivityViewModel.otherUserActivityUiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.isLoading -> binding.wllOtherUserActivity.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllOtherUserActivity.setLoadingLayoutVisibility(false)
                !uiState.isLoading -> {
                    binding.wllOtherUserActivity.setWebsosoLoadingVisibility(false)
                }
            }
        }

        otherUserActivityViewModel.otherUserActivityUiState.observe(viewLifecycleOwner) { uiState ->
            val userProfile = getUserProfile()
            updateAdapterWithActivitiesAndProfile(uiState.activities, userProfile)

            when (uiState.activities.isNullOrEmpty()) {
                true -> {
                    binding.clOtherUserActivityExistsNull.visibility = View.VISIBLE
                    binding.nsOtherUserActivityExists.visibility = View.GONE
                }
                false -> {
                    binding.clOtherUserActivityExistsNull.visibility = View.GONE
                    binding.nsOtherUserActivityExists.visibility = View.VISIBLE
                }
            }
        }

        otherUserPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.otherUserProfile?.let {
                val userProfile = UserProfileModel(
                    nickname = it.nickname,
                    avatarImage = it.avatarImage,
                )
                updateAdapterWithActivitiesAndProfile(
                    otherUserActivityViewModel.otherUserActivityUiState.value?.activities,
                    userProfile,
                )
            }
        }
    }

    private fun updateAdapterWithActivitiesAndProfile(
        activities: List<ActivityModel>?,
        userProfile: UserProfileModel,
    ) {
        if (activities != null) {
            val userActivityModels = activities.map { activity ->
                UserActivityModel(activity, userProfile)
            }
            otherUserActivityAdapter.submitList(userActivityModels)
        } else {
            otherUserActivityAdapter.submitList(emptyList())
        }
    }

    private fun getUserProfile(): UserProfileModel {
        val profile = otherUserPageViewModel.uiState.value?.otherUserProfile
        return UserProfileModel(
            nickname = profile?.nickname.orEmpty(),
            avatarImage = profile?.avatarImage.orEmpty(),
        )
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

            otherUserActivityViewModel.updateActivityLike(
                view.isSelected,
                feedId,
                updatedLikeCount,
            )
        }

        override fun onMoreButtonClick(view: View, feedId: Long) {
            showPopupMenu(view, feedId)
        }
    }

    private fun showPopupMenu(view: View, feedId: Long) {
        val inflater = LayoutInflater.from(requireContext())
        val binding = MenuOtherUserActivityPopupBinding.inflate(inflater)

        _popupWindow?.dismiss()
        _popupWindow = PopupWindow(
            binding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = POPUP_ELEVATION
            showAsDropDown(view)
        }

        binding.tvOtherUserActivityReportSpoiler.setOnClickListener {
            showReportDialog(feedId, "SPOILER_FEED")
            _popupWindow?.dismiss()
        }

        binding.tvOtherUserActivityReportExpression.setOnClickListener {
            showReportDialog(feedId, "IMPERTINENCE_FEED")
            _popupWindow?.dismiss()
        }
    }

    fun showReportDialog(feedId: Long, menuType: String) {
        _popupWindow?.dismiss()
        _popupWindow = null

        val dialogFragment = FeedReportDialogFragment.newInstance(
            menuType = menuType,
            event = {
                when (menuType) {
                    "SPOILER_FEED" -> otherUserActivityViewModel.updateReportedSpoilerFeed(feedId)
                    "IMPERTINENCE_FEED" -> otherUserActivityViewModel.updateReportedImpertinenceFeed(
                        feedId
                    )
                }

                parentFragmentManager.findFragmentByTag(FeedReportDialogFragment.TAG)?.let {
                    (it as? FeedReportDialogFragment)?.dismiss()
                }

                showReportDoneDialog(menuType)
            }
        )
        dialogFragment.show(parentFragmentManager, FeedReportDialogFragment.TAG)
    }

    private fun showReportDoneDialog(menuType: String) {
        val doneDialogFragment = FeedReportDoneDialogFragment.newInstance(
            menuType = menuType,
            event = {}
        )
        doneDialogFragment.show(parentFragmentManager, FeedReportDoneDialogFragment.TAG)
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_OTHER_USER_ACTIVITY = "otherUserActivity"
        const val USER_ID_KEY = "userId"
        const val POPUP_ELEVATION = 2f

        fun newInstance(userId: Long) = OtherUserActivityFragment().apply {
            arguments = Bundle().apply {
                putLong(USER_ID_KEY, userId)
            }
        }
    }
}