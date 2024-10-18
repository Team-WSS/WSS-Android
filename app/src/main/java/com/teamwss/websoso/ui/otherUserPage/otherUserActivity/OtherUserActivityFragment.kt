package com.teamwss.websoso.ui.otherUserPage.otherUserActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.teamwss.websoso.ui.main.feed.FeedFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
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
    private var _popupBinding: MenuOtherUserActivityPopupBinding? = null
    private val popupBinding: MenuOtherUserActivityPopupBinding
        get() = _popupBinding ?: error("Popup binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        _popupBinding = MenuOtherUserActivityPopupBinding.inflate(inflater, container, false)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _popupBinding = null
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
        otherUserActivityViewModel.otherUserActivityUiState.observe(viewLifecycleOwner) { uiState ->
            otherUserActivityAdapter.submitList(uiState.activities)
        }

        otherUserPageViewModel.otherUserProfile.observe(viewLifecycleOwner) { otherUserProfile ->
            otherUserProfile?.let { otherUserProfileEntity ->
                val userProfile = UserProfileModel(
                    nickname = otherUserProfileEntity.nickname,
                    avatarImage = otherUserProfileEntity.avatarImage,
                )
                otherUserActivityAdapter.setUserProfile(userProfile)
            }
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

            otherUserActivityViewModel.updateActivityLike(view.isSelected, feedId, updatedLikeCount)
        }

        override fun onMoreButtonClick(view: View, feedId: Long) {
            showPopupMenu(view, feedId)
        }
    }

    private fun showPopupMenu(view: View, feedId: Long) {
        val popupWindow: PopupWindow = PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = POPUP_ELEVATION
            showAsDropDown(view)
        }

        bindMenuItems(popupWindow, feedId)
    }

    private fun bindMenuItems(popup: PopupWindow, feedId: Long) {
        with(popupBinding) {
            tvOtherUserActivityReportSpoiler.setOnClickListener {
                showReportDialog(ReportMenuType.SPOILER_FEED.name) {
                    otherUserActivityViewModel.updateReportedSpoilerFeed(feedId)
                }
                popup.dismiss()
            }

            tvOtherUserActivityReportExpression.setOnClickListener {
                showReportDialog(ReportMenuType.IMPERTINENCE_FEED.name) {
                    otherUserActivityViewModel.updateReportedImpertinenceFeed(feedId)
                }
                popup.dismiss()
            }
        }
    }

    private fun showReportDialog(menuType: String, event: FeedFragment.FeedDialogClickListener) {
        val dialogFragment = FeedReportDialogFragment.newInstance(
            menuType = menuType,
            event = event,
        )
        dialogFragment.show(parentFragmentManager, FeedRemoveDialogFragment.TAG)
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
