package com.teamwss.websoso.ui.main.myPage.myActivity

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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.model.ResultFrom
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.showWebsosoSnackBar
import com.teamwss.websoso.databinding.FragmentMyActivityBinding
import com.teamwss.websoso.databinding.MenuMyActivityPopupBinding
import com.teamwss.websoso.ui.activityDetail.ActivityDetailActivity
import com.teamwss.websoso.ui.createFeed.CreateFeedActivity
import com.teamwss.websoso.ui.feedDetail.FeedDetailActivity
import com.teamwss.websoso.ui.feedDetail.model.EditFeedModel
import com.teamwss.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.teamwss.websoso.ui.main.feed.dialog.RemoveMenuType
import com.teamwss.websoso.ui.main.myPage.MyPageViewModel
import com.teamwss.websoso.ui.main.myPage.myActivity.adapter.MyActivityAdapter
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.mapper.toUserProfileModel
import com.teamwss.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyActivityFragment :
    BaseFragment<FragmentMyActivityBinding>(R.layout.fragment_my_activity) {
    private val myActivityViewModel: MyActivityViewModel by viewModels()
    private val myPageViewModel: MyPageViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val myActivityAdapter: MyActivityAdapter by lazy { MyActivityAdapter(onClickFeedItem()) }
    private var _popupWindow: PopupWindow? = null

    private lateinit var activityResultCallback: ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActivityResultCallback()
        setupMyActivitiesAdapter()
        setupObserver()
        onMyActivityDetailButtonClick()
    }

    private fun setupActivityResultCallback() {
        activityResultCallback = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                ResultFrom.FeedDetailBack.RESULT_OK, ResultFrom.CreateFeed.RESULT_OK -> {
                    myActivityViewModel.updateRefreshedActivities()
                }
                ResultFrom.FeedDetailRemoved.RESULT_OK -> {
                    myActivityViewModel.updateRefreshedActivities()
                    showWebsosoSnackBar(
                        view = binding.root,
                        message = getString(R.string.feed_removed_feed_snackbar),
                        icon = R.drawable.ic_blocked_user_snack_bar,
                    )
                }
            }
        }
    }

    private fun setupMyActivitiesAdapter() {
        binding.rvMyActivity.adapter = myActivityAdapter
    }

    private fun setupObserver() {
        myActivityViewModel.myActivityUiState.observe(viewLifecycleOwner) { uiState ->
            val userProfile = getUserProfile()
            updateAdapterWithActivitiesAndProfile(uiState.activities, userProfile)
            setupVisibility(uiState.activities)
        }

        myPageViewModel.myPageUiState.observe(viewLifecycleOwner) { uiState ->
            uiState.myProfile?.let { myProfile ->
                val userProfile = myProfile.toUserProfileModel()
                updateAdapterWithActivitiesAndProfile(
                    myActivityViewModel.myActivityUiState.value?.activities,
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
        val myProfile = myPageViewModel.myPageUiState.value?.myProfile
        return UserProfileModel(
            nickname = myProfile?.nickname.orEmpty(),
            avatarImage = myProfile?.avatarImage.orEmpty(),
        )
    }

    private fun setupVisibility(activities: List<ActivityModel>?) {
        when (activities.isNullOrEmpty()) {
            true -> {
                binding.clMyActivityExistsNull.visibility = View.VISIBLE
                binding.nsMyActivityExists.visibility = View.GONE
            }
            false -> {
                binding.clMyActivityExistsNull.visibility = View.GONE
                binding.nsMyActivityExists.visibility = View.VISIBLE
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
            activityResultCallback.launch(FeedDetailActivity.getIntent(requireContext(), feedId))
        }

        override fun onNovelInfoClick(novelId: Long) {
            startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
        }

        override fun onLikeButtonClick(view: View, feedId: Long) {
            val likeCount: Int =
                view.findViewById<TextView>(R.id.tv_my_activity_thumb_up_count).text.toString().toInt()
            val updatedLikeCount: Int = when (view.isSelected) {
                true -> if (likeCount > 0) likeCount - 1 else 0
                false -> likeCount + 1
            }

            view.findViewById<TextView>(R.id.tv_my_activity_thumb_up_count).text = updatedLikeCount.toString()
            view.isSelected = !view.isSelected

            singleEventHandler.debounce(coroutineScope = lifecycleScope) {
                myActivityViewModel.updateLike(feedId, view.isSelected, updatedLikeCount)
            }
        }

        override fun onMoreButtonClick(view: View, feedId: Long) {
            showPopupMenu(view, feedId)
        }
    }

    private fun showPopupMenu(view: View, feedId: Long) {
        val inflater = LayoutInflater.from(requireContext())
        val binding = MenuMyActivityPopupBinding.inflate(inflater)

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
            myActivityViewModel.myActivityUiState.value?.activities?.find { it.feedId == feedId }
        activityModel?.let { feed ->
            val editFeedModel = EditFeedModel(
                feedId = feed.feedId,
                novelId = feed.novelId ?: 0L,
                novelTitle = feed.title ?: "",
                feedContent = feed.feedContent,
                feedCategory = feed.relevantCategories?.split(", ") ?: emptyList(),
            )
            activityResultCallback.launch(CreateFeedActivity.getIntent(requireContext(), editFeedModel))
        } ?: throw IllegalArgumentException("Feed not found")
    }

    private fun showRemovedDialog(feedId: Long) {
        val dialogFragment = FeedRemoveDialogFragment.newInstance(
            menuType = RemoveMenuType.REMOVE_FEED.name,
            event = {
                myActivityViewModel.updateRemovedFeed(feedId)
            }
        )
        dialogFragment.show(parentFragmentManager, FeedRemoveDialogFragment.TAG)
    }

    companion object {
        const val EXTRA_SOURCE = "source"
        const val SOURCE_MY_ACTIVITY = "myActivity"
        const val POPUP_ELEVATION = 2f
    }
}