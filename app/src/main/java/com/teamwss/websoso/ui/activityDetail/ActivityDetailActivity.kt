package com.teamwss.websoso.ui.activityDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.model.ResultFrom
import com.teamwss.websoso.databinding.ActivityActivityDetailBinding
import com.teamwss.websoso.databinding.MenuMyActivityPopupBinding
import com.teamwss.websoso.databinding.MenuOtherUserActivityPopupBinding
import com.teamwss.websoso.ui.activityDetail.adapter.ActivityDetailAdapter
import com.teamwss.websoso.ui.createFeed.CreateFeedActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.feedDetail.model.EditFeedModel
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.FeedReportDoneDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType
import com.teamwss.websoso.ui.main.feed.dialog.ReportMenuType
import com.teamwss.websoso.ui.main.myPage.MyPageViewModel
import com.teamwss.websoso.ui.main.myPage.myActivity.ActivityItemClickListener
import com.teamwss.websoso.ui.main.myPage.myActivity.MyActivityFragment
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.mapper.toUserProfileModel
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import com.teamwss.websoso.ui.otherUserPage.BlockUserDialogFragment.Companion.USER_NICKNAME
import com.teamwss.websoso.ui.otherUserPage.OtherUserPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityDetailActivity :
    BaseActivity<ActivityActivityDetailBinding>(R.layout.activity_activity_detail) {
    private val activityDetailViewModel: ActivityDetailViewModel by viewModels()
    private val activityDetailAdapter: ActivityDetailAdapter by lazy { ActivityDetailAdapter(onClickFeedItem()) }
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
        activityResultCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ResultFrom.FeedDetailBack.RESULT_OK, ResultFrom.CreateFeed.RESULT_OK -> {
                    activityDetailViewModel.updateRefreshedActivities()
                }
                ResultFrom.FeedDetailRemoved.RESULT_OK -> {
                    activityDetailViewModel.updateRefreshedActivities()
                    showSnackBar(getString(R.string.feed_removed_feed_snackbar))
                }
                ResultFrom.BlockUser.RESULT_OK -> {
                    activityDetailViewModel.updateRefreshedActivities()
                    val nickname = result.data?.getStringExtra(USER_NICKNAME).orEmpty()
                    showSnackBar(getString(R.string.block_user_success_message, nickname))
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
            SOURCE_MY_ACTIVITY -> getString(R.string.my_activity_detail_title)
            SOURCE_OTHER_USER_ACTIVITY -> getString(R.string.other_user_page_activity)
            else -> ""
        }
    }

    private fun setupMyActivitiesDetailAdapter() {
        binding.rvActivityDetail.apply {
            adapter = activityDetailAdapter
        }
    }

    private fun setupObserver() {
        activityDetailViewModel.activityDetailUiState.observe(this) { uiState ->
            updateAdapterWithActivitiesAndProfile(uiState.activities, getUserProfile())
        }

        when (activityDetailViewModel.source) {
            SOURCE_MY_ACTIVITY -> {
                myPageViewModel.myPageUiState.observe(this) { uiState ->
                    uiState.myProfile?.let { myProfile ->
                        updateAdapterWithActivitiesAndProfile(
                            activityDetailViewModel.activityDetailUiState.value?.activities,
                            myProfile.toUserProfileModel(),
                        )
                    }
                }
            }

            SOURCE_OTHER_USER_ACTIVITY -> {
                otherUserPageViewModel.otherUserProfile.observe(this) { otherUserProfile ->
                    otherUserProfile?.let {
                        updateAdapterWithActivitiesAndProfile(
                            activityDetailViewModel.activityDetailUiState.value?.activities,
                            otherUserProfile.toUserProfileModel(),
                        )
                    }
                }
            }
        }
    }

    private fun updateAdapterWithActivitiesAndProfile(
        activities: List<ActivitiesModel.ActivityModel>?,
        userProfile: UserProfileModel?
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

    private fun getUserProfile(): UserProfileModel? {
        return when (activityDetailViewModel.source) {
            SOURCE_MY_ACTIVITY -> {
                myPageViewModel.myPageUiState.value?.myProfile?.toUserProfileModel()
            }
            SOURCE_OTHER_USER_ACTIVITY -> {
                otherUserPageViewModel.otherUserProfile.value?.toUserProfileModel()
            }
            else -> null
        }
    }

    private fun onBackButtonClick() {
        binding.ivActivityDetailBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onClickFeedItem() = object : ActivityItemClickListener {
        override fun onContentClick(feedId: Long) {
            activityResultCallback.launch(FeedDetailActivity.getIntent(this@ActivityDetailActivity, feedId))
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
            showPopupMenu(view, feedId)
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showPopupMenu(view: View, feedId: Long) {
        val inflater = LayoutInflater.from(this)
        val binding = when (source) {
            SOURCE_MY_ACTIVITY -> MenuMyActivityPopupBinding.inflate(inflater)
            SOURCE_OTHER_USER_ACTIVITY -> MenuOtherUserActivityPopupBinding.inflate(inflater)
            else -> return
        }

        _popupWindow?.dismiss()
        _popupWindow = PopupWindow(
            binding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            elevation = 2f
            showAsDropDown(view)
        }
        setupPopupMenuClickListeners(binding, feedId)
    }

    private fun setupPopupMenuClickListeners(binding: ViewDataBinding, feedId: Long) {
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
                    showReportDialog(feedId, ReportMenuType.SPOILER_FEED.name)
                    _popupWindow?.dismiss()
                }
                binding.tvOtherUserActivityReportExpression.setOnClickListener {
                    showReportDialog(feedId, ReportMenuType.IMPERTINENCE_FEED.name)
                    _popupWindow?.dismiss()
                }
            }
        }
    }

    private fun navigateToFeedEdit(feedId: Long) {
        val activityModel =
            activityDetailViewModel.activityDetailUiState.value?.activities?.find { it.feedId == feedId }
        activityModel?.let { feed ->
            val editFeedModel = EditFeedModel(
                feedId = feed.feedId,
                novelId = feed.novelId ?: 0L,
                novelTitle = feed.title ?: "",
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
            }
        )
        dialogFragment.show(supportFragmentManager, FeedRemoveDialogFragment.TAG)
    }

    private fun showReportDialog(feedId: Long, menuType: String) {
        val dialogFragment = FeedReportDialogFragment.newInstance(
            menuType = menuType,
            event = {
                when (menuType) {
                    ReportMenuType.SPOILER_FEED.name -> activityDetailViewModel.updateReportedSpoilerFeed(
                        feedId,
                    )

                    ReportMenuType.IMPERTINENCE_FEED.name -> activityDetailViewModel.updateReportedImpertinenceFeed(
                        feedId,
                    )
                }
                showReportDoneDialog(menuType)
            }
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

        fun getIntent(context: Context): Intent {
            return Intent(context, ActivityDetailActivity::class.java)
        }
    }
}