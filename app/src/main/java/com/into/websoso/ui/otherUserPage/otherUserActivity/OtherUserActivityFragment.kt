package com.into.websoso.ui.otherUserPage.otherUserActivity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.into.websoso.R
import com.into.websoso.common.ui.base.BaseFragment
import com.into.websoso.common.ui.model.ResultFrom
import com.into.websoso.databinding.FragmentOtherUserActivityBinding
import com.into.websoso.databinding.MenuOtherUserActivityPopupBinding
import com.into.websoso.ui.activityDetail.ActivityDetailActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDoneDialogFragment
import com.into.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.otherUserPage.OtherUserPageViewModel
import com.into.websoso.ui.otherUserPage.otherUserActivity.adapter.OtherUserActivityAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserActivityFragment :
    BaseFragment<FragmentOtherUserActivityBinding>(R.layout.fragment_other_user_activity) {

    private val otherUserActivityViewModel: OtherUserActivityViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by activityViewModels()
    private val otherUserActivityAdapter: OtherUserActivityAdapter by lazy { OtherUserActivityAdapter(onClickFeedItem()) }
    private var _popupWindow: PopupWindow? = null
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivityResultCallback()
        setupUserId()
        setupMyActivitiesAdapter()
        setupObserver()
        onActivityDetailButtonClick()
    }

    private fun setupActivityResultCallback() {
        activityResultCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ResultFrom.FeedDetailBack.RESULT_OK,
                ResultFrom.CreateFeed.RESULT_OK -> {
                    otherUserActivityViewModel.updateRefreshedActivities()
                }
            }
        }
    }

    private fun setupUserId() {
        val userId = arguments?.getLong(USER_ID_KEY) ?: 0L
        otherUserActivityViewModel.updateUserId(userId)
    }

    private fun setupMyActivitiesAdapter() {
        binding.rvOtherUserActivity.adapter = otherUserActivityAdapter
    }

    private fun setupObserver() {
        otherUserPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.isLoading -> binding.wllOtherUserActivity.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllOtherUserActivity.setLoadingLayoutVisibility(false)
                !uiState.isLoading -> {
                    binding.wllOtherUserActivity.setWebsosoLoadingVisibility(false)
                }
            }

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

        otherUserActivityViewModel.otherUserActivityUiState.observe(viewLifecycleOwner) { uiState ->
            updateAdapterWithActivitiesAndProfile(uiState.activities, getUserProfile())

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
            setupActivityMoreButtonVisibility(uiState.activities.count())
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

    private fun setupActivityMoreButtonVisibility(activityCount: Int) {
        if (activityCount >= 5) {
            binding.btnOtherUserActivityMore.visibility = View.VISIBLE
        } else {
            binding.btnOtherUserActivityMore.visibility = View.GONE
        }
    }

    private fun onClickFeedItem() = object : ActivityItemClickListener {
        override fun onContentClick(feedId: Long) {
            activityResultCallback.launch(FeedDetailActivity.getIntent(requireContext(), feedId))
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
                        feedId,
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
            event = {},
        )
        doneDialogFragment.show(parentFragmentManager, FeedReportDoneDialogFragment.TAG)
    }

    override fun onResume() {
        super.onResume()

        view?.requestLayout()
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