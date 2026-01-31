package com.into.websoso.ui.main.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import com.into.websoso.R
import com.into.websoso.R.layout.fragment_feed
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.databinding.DialogRemovePopupMenuBinding
import com.into.websoso.databinding.DialogReportPopupMenuBinding
import com.into.websoso.databinding.FragmentFeedBinding
import com.into.websoso.feature.feed.FeedRoute
import com.into.websoso.feature.feed.FeedViewModel
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.main.feed.dialog.FeedRemoveDialogFragment
import com.into.websoso.ui.main.feed.dialog.FeedReportDialogFragment
import com.into.websoso.ui.main.feed.dialog.RemoveMenuType.REMOVE_FEED
import com.into.websoso.ui.main.feed.dialog.ReportMenuType
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.otherUserPage.OtherUserPageActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>(fragment_feed) {
    @Inject
    lateinit var tracker: Tracker
    private val feedViewModel: FeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(fragment_feed, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.cv_feed)

        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WebsosoTheme {
                    FeedRoute(
                        viewModel = feedViewModel,
                        onWriteClick = ::navigateToWriteFeed,
                        onProfileClick = { userId, isMyFeed ->
                            navigateToProfile(
                                userId,
                                isMyFeed,
                            )
                        },
                        onNovelClick = { novelId -> navigateToNovelDetail(novelId) },
                        onContentClick = { feedId, isLiked ->
                            navigateToFeedDetail(
                                feedId,
                                isLiked,
                            )
                        },
                        onFirstItemClick = { feedId, isMyFeed ->
                            when (isMyFeed) {
                                true -> navigateToFeedEdit(feedId)

                                false -> showDialog<DialogReportPopupMenuBinding>(
                                    menuType = ReportMenuType.SPOILER_FEED.name,
                                    event = { feedViewModel.updateReportedSpoilerFeed(feedId) },
                                )
                            }
                        },
                        onSecondItemClick = { feedId, isMyFeed ->
                            when (isMyFeed) {
                                true -> showDialog<DialogRemovePopupMenuBinding>(
                                    menuType = REMOVE_FEED.name,
                                    event = { feedViewModel.updateRemovedFeed(feedId) },
                                )

                                false -> showDialog<DialogReportPopupMenuBinding>(
                                    menuType = ReportMenuType.IMPERTINENCE_FEED.name,
                                    event = { feedViewModel.updateReportedImpertinenceFeed(feedId) },
                                )
                            }
                        },
                    )
                }
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        feedViewModel.resetFeedsToInitial()
    }

    private fun navigateToWriteFeed() {
        startActivity(CreateFeedActivity.getIntent(requireContext()))
    }

    private fun navigateToProfile(
        userId: Long,
        isMyFeed: Boolean,
    ) {
        if (isMyFeed) return

        startActivity(
            OtherUserPageActivity.getIntent(
                requireContext(),
                userId,
            ),
        )
    }

    private inline fun <reified Dialog : ViewDataBinding> showDialog(
        menuType: String,
        noinline event: () -> Unit,
    ) {
        when (Dialog::class) {
            DialogRemovePopupMenuBinding::class -> {
                FeedRemoveDialogFragment
                    .newInstance(
                        menuType = menuType,
                        event = { event() },
                    ).show(childFragmentManager, FeedRemoveDialogFragment.TAG)
            }

            DialogReportPopupMenuBinding::class -> {
                FeedReportDialogFragment
                    .newInstance(
                        menuType = menuType,
                        event = { event() },
                    ).show(childFragmentManager, FeedReportDialogFragment.TAG)
            }
        }
    }

    fun interface FeedDialogClickListener : Serializable {
        operator fun invoke()
    }

    private fun navigateToFeedEdit(feedId: Long) {
        val feedContent =
            feedViewModel.uiState.value.myFeedData.feeds.find { it.id == feedId }?.let { feed ->
                EditFeedModel(
                    feedId = feed.id,
                    novelId = feed.novel?.id,
                    novelTitle = feed.novel?.title,
                    isSpoiler = feed.isSpoiler,
                    isPublic = feed.isPublic,
                    feedContent = feed.content,
                    feedCategory = feed.relevantCategoriesByKr,
                    imageUrls = feed.imageUrls,
                )
            } ?: throw IllegalArgumentException()

        startActivity(CreateFeedActivity.getIntent(requireContext(), feedContent))
    }

    private fun navigateToFeedDetail(
        feedId: Long,
        isLiked: Boolean,
    ) {
        startActivity(
            FeedDetailActivity.getIntent(
                context = requireContext(),
                feedId = feedId,
                isLiked = isLiked,
            ),
        )
    }

    private fun navigateToNovelDetail(novelId: Long) {
        startActivity(NovelDetailActivity.getIntent(requireContext(), novelId))
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        tracker.trackEvent("feed_all")
    }

    companion object {
        const val TAG = "FeedFragment"
    }
}
