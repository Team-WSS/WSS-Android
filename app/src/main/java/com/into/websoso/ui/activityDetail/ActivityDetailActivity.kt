package com.into.websoso.ui.activityDetail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.into.websoso.R.id.tv_my_activity_thumb_up_count
import com.into.websoso.R.layout.activity_activity_detail
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.core.common.ui.model.ResultFrom.BlockUser
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.FeedDetailRemoved
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.resource.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.core.resource.R.string.block_user_success_message
import com.into.websoso.core.resource.R.string.feed_removed_feed_snackbar
import com.into.websoso.core.resource.R.string.my_activity_detail_title
import com.into.websoso.core.resource.R.string.other_user_page_activity
import com.into.websoso.databinding.ActivityActivityDetailBinding
import com.into.websoso.databinding.MenuMyActivityPopupBinding
import com.into.websoso.databinding.MenuOtherUserActivityPopupBinding
import com.into.websoso.ui.activityDetail.adapter.ActivityDetailAdapter
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDoneDialogFragment
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.IMPERTINENCE_FEED
import com.into.websoso.ui.main.feed.dialog.ReportMenuType.SPOILER_FEED
import com.into.websoso.ui.main.myPage.MyPageViewModel
import com.into.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.into.websoso.ui.main.myPage.myActivity.MyActivityFragment
import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.into.websoso.ui.mapper.toUserProfileModel
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.otherUserPage.BlockUserDialogFragment.Companion.USER_NICKNAME
import com.into.websoso.ui.otherUserPage.OtherUserPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityDetailActivity : BaseActivity<ActivityActivityDetailBinding>(activity_activity_detail) {
    private val activityDetailViewModel: ActivityDetailViewModel by viewModels()
    private val activityDetailAdapter: ActivityDetailAdapter by lazy {
        ActivityDetailAdapter(
            onClickFeedItem(),
        )
    }
    private val myPageViewModel: MyPageViewModel by viewModels()
    private val otherUserPageViewModel: OtherUserPageViewModel by viewModels()
    private val source: String by lazy {
        intent.getStringExtra(MyActivityFragment.EXTRA_SOURCE) ?: ""
    }
    private val userId: Long by lazy { intent.getLongExtra(USER_ID_KEY, DEFAULT_USER_ID) }
    private var _popupWindow: PopupWindow? = null
    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityResultCallback()
        setupUserIDAndSource()
        setActivityTitle()
        setupMyActivitiesDetailAdapter()
        setupObserver()
        onBackButtonClick()
    }

    private fun setupActivityResultCallback() {
        activityResultCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    FeedDetailBack.RESULT_OK, ResultFrom.CreateFeed.RESULT_OK, Activity.RESULT_OK -> {
                        activityDetailViewModel.updateRefreshedActivities()
                    }

                    FeedDetailRemoved.RESULT_OK -> {
                        activityDetailViewModel.updateRefreshedActivities()
                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(feed_removed_feed_snackbar),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }

                    BlockUser.RESULT_OK -> {
                        activityDetailViewModel.updateRefreshedActivities()
                        val nickname = result.data?.getStringExtra(USER_NICKNAME).orEmpty()
                        showWebsosoSnackBar(
                            view = binding.root,
                            message = getString(block_user_success_message, nickname),
                            icon = ic_blocked_user_snack_bar,
                        )
                    }
                }
            }
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

    private fun setupObserver() {
        activityDetailViewModel.uiState.observe(this) { uiState ->
            updateAdapterWithActivitiesAndProfile(uiState.activities, getUserProfile())
        }

        when (activityDetailViewModel.source) {
            SOURCE_MY_ACTIVITY -> {
                myPageViewModel.uiState.observe(this) { uiState ->
                    uiState.myProfile?.let { myProfile ->
                        updateAdapterWithActivitiesAndProfile(
                            activityDetailViewModel.uiState.value?.activities,
                            myProfile.toUserProfileModel(),
                        )
                    }
                }
            }

            SOURCE_OTHER_USER_ACTIVITY -> {
                otherUserPageViewModel.uiState.observe(this) { uiState ->
                    uiState.otherUserProfile?.let {
                        updateAdapterWithActivitiesAndProfile(
                            activityDetailViewModel.uiState.value?.activities,
                            uiState.otherUserProfile.toUserProfileModel(),
                        )
                    }
                }
            }
        }
    }

    private fun updateAdapterWithActivitiesAndProfile(
        activities: List<ActivitiesModel.ActivityModel>?,
        userProfile: UserProfileModel?,
    ) {
        if (activities != null && userProfile != null) {
            val userActivityModels = activities.map { activity ->
                UserActivityModel(activity, userProfile)
            }
            activityDetailAdapter.submitList(userActivityModels)
        } else {
            activityDetailAdapter.submitList(emptyList())
        }
    }

    private fun getUserProfile(): UserProfileModel? =
        when (activityDetailViewModel.source) {
            SOURCE_MY_ACTIVITY -> {
                myPageViewModel.uiState.value
                    ?.myProfile
                    ?.toUserProfileModel()
            }

            SOURCE_OTHER_USER_ACTIVITY -> {
                otherUserPageViewModel.uiState.value
                    ?.otherUserProfile
                    ?.toUserProfileModel()
            }

            else -> null
        }

    private fun onBackButtonClick() {
        binding.ivActivityDetailBackButton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun onClickFeedItem() =
        object : ActivityItemClickListener {
            override fun onContentClick(feedId: Long) {
                activityResultCallback.launch(
                    FeedDetailActivity.getIntent(this@ActivityDetailActivity, feedId),
                )
            }

            override fun onNovelInfoClick(novelId: Long) {
                startActivity(NovelDetailActivity.getIntent(this@ActivityDetailActivity, novelId))
            }

            override fun onLikeButtonClick(
                view: View,
                feedId: Long,
            ) {
                val likeCountTextView: TextView = view.findViewById(tv_my_activity_thumb_up_count)
                val currentLikeCount = likeCountTextView.text.toString().toInt()

                val updatedLikeCount: Int = if (view.isSelected) {
                    if (currentLikeCount > 0) currentLikeCount - 1 else 0
                } else {
                    currentLikeCount + 1
                }

                likeCountTextView.text = updatedLikeCount.toString()
                view.isSelected = !view.isSelected

                activityDetailViewModel.updateActivityLike(
                    view.isSelected,
                    feedId,
                    updatedLikeCount,
                )
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
        val inflater = LayoutInflater.from(this)
        val binding = when (source) {
            SOURCE_MY_ACTIVITY -> MenuMyActivityPopupBinding.inflate(inflater)
            SOURCE_OTHER_USER_ACTIVITY -> MenuOtherUserActivityPopupBinding.inflate(inflater)
            else -> return
        }

        _popupWindow?.dismiss()
        _popupWindow = PopupWindow(binding.root, WRAP_CONTENT, WRAP_CONTENT, true).apply {
            elevation = 2f
            showAsDropDown(view)
        }
        setupPopupMenuClickListeners(binding, feedId)
    }

    private fun setupPopupMenuClickListeners(
        binding: ViewDataBinding,
        feedId: Long,
    ) {
        when (binding) {
            is MenuMyActivityPopupBinding -> {
                binding.tvMyActivityModification.setOnClickListener {
                    navigateToFeedEdit(feedId)
                    _popupWindow?.dismiss()
                }
                binding.tvMyActivityPopupDeletion.setOnClickListener {
                    showRemoveDialog(feedId)
                    _popupWindow?.dismiss()
                }
            }

            is MenuOtherUserActivityPopupBinding -> {
                binding.tvOtherUserActivityReportSpoiler.setOnClickListener {
                    showReportDialog(feedId, SPOILER_FEED.name)
                    _popupWindow?.dismiss()
                }
                binding.tvOtherUserActivityReportExpression.setOnClickListener {
                    showReportDialog(feedId, IMPERTINENCE_FEED.name)
                    _popupWindow?.dismiss()
                }
            }
        }
    }

    private fun navigateToFeedEdit(feedId: Long) {
        val activityModel =
            activityDetailViewModel.uiState.value
                ?.activities
                ?.find { it.feedId == feedId }
        activityModel?.let { feed ->
            val editFeedModel = EditFeedModel(
                feedId = feed.feedId,
                novelId = feed.novelId,
                novelTitle = feed.title,
                isSpoiler = feed.isSpoiler,
                isPublic = feed.isPublic,
                feedContent = feed.feedContent,
                feedCategory = feed.relevantCategories?.split(", ") ?: emptyList(),
            )
            activityResultCallback.launch(CreateFeedActivity.getIntent(this, editFeedModel))
        } ?: throw IllegalArgumentException("Feed not found")
    }

    private fun showRemoveDialog(feedId: Long) {
        val dialogFragment = FeedRemoveDialogFragment.newInstance(
            menuType = RemoveMenuType.REMOVE_FEED.name,
            event = {
                activityDetailViewModel.updateRemovedFeed(feedId)
            },
        )
        dialogFragment.show(supportFragmentManager, FeedRemoveDialogFragment.TAG)
    }

    private fun showReportDialog(
        feedId: Long,
        menuType: String,
    ) {
        val dialogFragment = FeedReportDialogFragment.newInstance(
            menuType = menuType,
            event = {
                when (menuType) {
                    SPOILER_FEED.name ->
                        activityDetailViewModel.updateReportedSpoilerFeed(feedId)

                    IMPERTINENCE_FEED.name ->
                        activityDetailViewModel.updateReportedImpertinenceFeed(feedId)
                }
                showReportDoneDialog(menuType)
            },
        )
        dialogFragment.show(supportFragmentManager, FeedReportDialogFragment.TAG)
    }

    private fun showReportDoneDialog(menuType: String) {
        val doneDialogFragment = FeedReportDoneDialogFragment.newInstance(
            menuType = menuType,
            event = {},
        )
        doneDialogFragment.show(supportFragmentManager, FeedReportDoneDialogFragment.TAG)
    }

    companion object {
        const val USER_ID_KEY = "userId"
        const val DEFAULT_USER_ID = -1L
        const val SOURCE_MY_ACTIVITY = "myActivity"
        const val SOURCE_OTHER_USER_ACTIVITY = "otherUserActivity"

        fun getIntent(context: Context): Intent = Intent(context, ActivityDetailActivity::class.java)
    }
}
