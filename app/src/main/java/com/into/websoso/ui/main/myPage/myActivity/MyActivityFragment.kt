package com.into.websoso.ui.main.myPage.myActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.R.id.tv_my_activity_thumb_up_count
import com.into.websoso.R.layout.fragment_my_activity
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.model.ResultFrom.CreateFeed
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailRemoved
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.resource.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.core.resource.R.string.feed_removed_feed_snackbar
import com.into.websoso.databinding.FragmentMyActivityBinding
import com.into.websoso.databinding.MenuMyActivityPopupBinding
import com.into.websoso.ui.activityDetail.ActivityDetailActivity
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.into.websoso.ui.main.myPage.MyPageViewModel
import com.into.websoso.ui.main.myPage.myActivity.adapter.MyActivityAdapter
import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.into.websoso.ui.mapper.toUserProfileModel
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityFragment : BaseFragment<FragmentMyActivityBinding>(fragment_my_activity) {
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val myActivityAdapter: MyActivityAdapter by lazy { MyActivityAdapter(onClickFeedItem()) }
    private var _popupWindow: PopupWindow? = null
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupActivityResultCallback()
        setupMyActivitiesAdapter()
        setupObserver()
        onMyActivityDetailButtonClick()
    }

    private fun setupActivityResultCallback() {
        activityResultCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    FeedDetailBack.RESULT_OK, CreateFeed.RESULT_OK, Activity.RESULT_OK -> {
                        myActivityViewModel.updateRefreshedActivities()
                    }

                    FeedDetailRemoved.RESULT_OK -> {
                        myActivityViewModel.updateRefreshedActivities()
                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(feed_removed_feed_snackbar),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }
                }
            }
    }

    private fun setupMyActivitiesAdapter() {
        binding.rvMyActivity.adapter = myActivityAdapter
    }

    private fun setupObserver() {
        myActivityViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            val userProfile = getUserProfile()
            updateAdapterWithActivitiesAndProfile(uiState.activities, userProfile)
            setupVisibility(uiState.activities)
            setupActivityMoreButtonVisibility(uiState.activities.count())
        }

        myPageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.myProfile?.let { myProfile ->
                val userProfile = myProfile.toUserProfileModel()
                updateAdapterWithActivitiesAndProfile(
                    myActivityViewModel.uiState.value?.activities,
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
            myActivityAdapter.submitList(userActivityModels)
        } else {
            myActivityAdapter.submitList(emptyList())
        }
    }

    private fun getUserProfile(): UserProfileModel {
        val myProfile = myPageViewModel.uiState.value?.myProfile
        return UserProfileModel(
            nickname = myProfile?.nickname.orEmpty(),
            avatarImage = myProfile?.avatarImage.orEmpty(),
        )
    }

    private fun setupVisibility(activities: List<ActivityModel>?) {
        when (activities.isNullOrEmpty()) {
            true -> {
                binding.clMyActivityExistsNull.visibility = VISIBLE
                binding.nsMyActivityExists.visibility = GONE
            }

            false -> {
                binding.clMyActivityExistsNull.visibility = GONE
                binding.nsMyActivityExists.visibility = VISIBLE
            }
        }
    }

    private fun setupActivityMoreButtonVisibility(activityCount: Int) {
        if (activityCount >= 5) {
            binding.btnMyActivityMore.visibility = VISIBLE
        } else {
            binding.btnMyActivityMore.visibility = GONE
        }
    }

    private fun onMyActivityDetailButtonClick() {
        binding.btnMyActivityMore.setOnClickListener {
            val intent = ActivityDetailActivity.getIntent(requireContext()).apply {
                putExtra(EXTRA_SOURCE, SOURCE_MY_ACTIVITY)
            }
            activityResultCallback.launch(intent)
        }
    }

    private fun onClickFeedItem() =
        object : ActivityItemClickListener {
            override fun onContentClick(feedId: Long) {
                activityResultCallback.launch(
                    FeedDetailActivity.getIntent(
                        requireContext(),
                        feedId,
                    ),
                )
            }

            override fun onNovelInfoClick(novelId: Long) {
                startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
            }

            override fun onLikeButtonClick(
                view: View,
                feedId: Long,
            ) {
                val likeCount: Int = view.findViewById<TextView>(tv_my_activity_thumb_up_count)
                    .text
                    .toString()
                    .toInt()
                val updatedLikeCount: Int = when (view.isSelected) {
                    true -> if (likeCount > 0) likeCount - 1 else 0
                    false -> likeCount + 1
                }

                view.findViewById<TextView>(tv_my_activity_thumb_up_count).text =
                    updatedLikeCount.toString()
                view.isSelected = !view.isSelected

                singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                    myActivityViewModel.updateLike(feedId, view.isSelected, updatedLikeCount)
                }
            }

            override fun onMoreButtonClick(
                view: View,
                feedId: Long,
            ) {
                showPopupMenu(view, feedId)
            }
        }

    private fun showPopupMenu(
        view: View,
        feedId: Long,
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val binding = MenuMyActivityPopupBinding.inflate(inflater)

        _popupWindow?.dismiss()
        _popupWindow = PopupWindow(binding.root, WRAP_CONTENT, WRAP_CONTENT, true).apply {
            elevation = POPUP_ELEVATION
            showAsDropDown(view)
        }

        binding.tvMyActivityModification.setOnClickListener {
            navigateToFeedEdit(feedId)
            _popupWindow?.dismiss()
        }

        binding.tvMyActivityPopupDeletion.setOnClickListener {
            showRemovedDialog(feedId)
            _popupWindow?.dismiss()
        }
    }

    private fun navigateToFeedEdit(feedId: Long) {
        val activityModel =
            myActivityViewModel.uiState.value
                ?.activities
                ?.find { it.feedId == feedId }
        activityModel?.let { feed ->
            val editFeedModel = EditFeedModel(
                feedId = feed.feedId,
                novelId = feed.novelId,
                novelTitle = feed.title,
                isSpoiler = feed.isSpoiler,
                feedContent = feed.feedContent,
                feedCategory = feed.relevantCategories?.split(", ") ?: emptyList(),
            )
            activityResultCallback.launch(
                CreateFeedActivity.getIntent(
                    requireContext(),
                    editFeedModel,
                ),
            )
        } ?: throw IllegalArgumentException("Feed not found")
    }

    private fun showRemovedDialog(feedId: Long) {
        val dialogFragment = FeedRemoveDialogFragment.newInstance(
            menuType = REMOVE_FEED.name,
            event = {
                myActivityViewModel.updateRemovedFeed(feedId)
            },
        )
        dialogFragment.show(parentFragmentManager, FeedRemoveDialogFragment.TAG)
    }

    override fun onResume() {
        super.onResume()

        view?.requestLayout()
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_MY_ACTIVITY = "myActivity"
        const val POPUP_ELEVATION = 2f
    }
}
