package com.into.websoso.ui.novelInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.primary_50_F1EFFF
import com.into.websoso.R.color.transparent
import com.into.websoso.R.layout.fragment_novel_info
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.databinding.FragmentNovelInfoBinding
import com.into.websoso.resource.R.string.novel_info_charm_points_body
import com.into.websoso.resource.R.string.novel_info_keyword_chip_text
import com.into.websoso.resource.R.string.novel_info_read_status_quit_count
import com.into.websoso.resource.R.string.novel_info_read_status_watched_count
import com.into.websoso.resource.R.string.novel_info_read_status_watching_count
import com.into.websoso.resource.R.string.novel_info_user_unit
import com.into.websoso.ui.novelInfo.component.NovelInfoPlatformsContainer
import com.into.websoso.ui.novelInfo.model.ExpandTextUiModel
import com.into.websoso.ui.novelInfo.model.KeywordModel
import com.into.websoso.ui.novelInfo.model.PlatformModel
import com.into.websoso.ui.novelInfo.model.UnifiedReviewCountModel
import com.into.websoso.ui.novelRating.model.ReadStatus
import com.into.websoso.ui.novelRating.model.ReadStatus.QUIT
import com.into.websoso.ui.novelRating.model.ReadStatus.WATCHED
import com.into.websoso.ui.novelRating.model.ReadStatus.WATCHING
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NovelInfoFragment : BaseFragment<FragmentNovelInfoBinding>(fragment_novel_info) {
    @Inject
    lateinit var tracker: Tracker

    private val novelInfoViewModel by activityViewModels<NovelInfoViewModel>()
    private val novelId: Long by lazy { arguments?.getLong(NOVEL_ID) ?: 0L }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        novelInfoViewModel.updateNovelInfo(novelId)
        bindViewModel()
        setupObserver()
        setupWebsosoLoadingLayout()
    }

    private fun bindViewModel() {
        binding.navigateToReadNovel = ::navigateToWebView
        binding.novelInfoViewModel = novelInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun navigateToWebView(url: String) {
        tracker.trackEvent("direct_novel")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun setupObserver() {
        novelInfoViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.novelInfoModel.novelDescription.isNotBlank() -> {
                    setupKeywordChip(uiState.keywords)
                    setupPlatforms(uiState.platforms)
                    updateExpandTextToggle(uiState.expandTextModel)
                    updateExpandTextToggleVisibility(uiState.expandTextModel)
                    updateGraphHeightValue(uiState.novelInfoModel.unifiedReviewCount)
                    updateGraphUi(uiState.novelInfoModel.unifiedReviewCount)
                    updateUsersReadStatusText(uiState.novelInfoModel.unifiedReviewCount)
                    updateUsersCharmPointBody(uiState.novelInfoModel.formatAttractivePoints())
                    binding.wllNovelInfo.setWebsosoLoadingVisibility(false)
                }

                uiState.loading -> {
                    novelInfoViewModel.updateNovelInfo(novelId)
                    binding.wllNovelInfo.setLoadingLayoutVisibility(true)
                }

                uiState.error -> {
                    binding.wllNovelInfo.setErrorLayoutVisibility(true)
                }
            }
        }
    }

    private fun setupKeywordChip(keywords: List<KeywordModel>) {
        if (binding.wcgNovelInfoKeyword.childCount > 0) return
        keywords.forEach { keyword ->
            WebsosoChip(requireContext())
                .apply {
                    setWebsosoChipText(
                        getString(
                            novel_info_keyword_chip_text,
                            keyword.keywordName,
                            keyword.keywordCount,
                        ),
                    )
                    setWebsosoChipTextAppearance(body2)
                    setWebsosoChipTextColor(primary_100_6A5DFD)
                    setWebsosoChipStrokeColor(transparent)
                    setWebsosoChipBackgroundColor(primary_50_F1EFFF)
                    setWebsosoChipPaddingVertical(20f)
                    setWebsosoChipPaddingHorizontal(12f)
                    setWebsosoChipRadius(40f)
                    isEnabled = false
                }.also { websosoChip -> binding.wcgNovelInfoKeyword.addChip(websosoChip) }
        }
    }

    private fun setupPlatforms(platforms: List<PlatformModel>) {
        binding.cvNovelInfoPlatforms.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NovelInfoPlatformsContainer(platforms, ::navigateToWebView)
            }
        }
    }

    private fun updateExpandTextToggle(expandTextModel: ExpandTextUiModel) {
        binding.ivNovelInfoDescriptionToggle.isSelected = expandTextModel.isExpandTextToggleSelected
    }

    private fun updateExpandTextToggleVisibility(expandTextModel: ExpandTextUiModel) {
        if (expandTextModel.expandTextToggleVisibility) return
        val bodyTextView = binding.tvNovelInfoDescriptionBody
        bodyTextView.post {
            val lineCount = bodyTextView.layout.lineCount
            val ellipsisCount = bodyTextView.layout.getEllipsisCount(lineCount - 1)
            novelInfoViewModel.updateExpandTextToggleVisibility(lineCount, ellipsisCount)
        }
    }

    private fun updateGraphHeightValue(unifiedReviewCountModel: UnifiedReviewCountModel) {
        val counts = listOf(
            unifiedReviewCountModel.watchingCount.count,
            unifiedReviewCountModel.watchedCount.count,
            unifiedReviewCountModel.quitCount.count,
        )

        val graphHeights = listOf(
            unifiedReviewCountModel.watchingCount.graphHeight,
            unifiedReviewCountModel.watchedCount.graphHeight,
            unifiedReviewCountModel.quitCount.graphHeight,
        )

        when {
            counts.sumOf { it } == 0 || graphHeights.sumOf { it } != 0 -> return
            else -> {
                val graphHeight = binding.cvNovelInfoReadStatusWatching.layoutParams.height
                novelInfoViewModel.updateGraphHeight(graphHeight)
            }
        }
    }

    private fun updateGraphUi(unifiedReviewCountModel: UnifiedReviewCountModel) {
        when (unifiedReviewCountModel.maxCountReadStatus()) {
            WATCHING -> {
                updateGraphHeight(
                    binding.viewNovelInfoReadStatusWatching,
                    unifiedReviewCountModel.watchingCount.graphHeight,
                )
                updateGraphSelection(
                    binding.viewNovelInfoReadStatusWatching,
                    binding.tvNovelInfoReadStatusWatchingCount,
                    binding.tvNovelInfoReadStatusWatching,
                )
            }

            WATCHED -> {
                updateGraphHeight(
                    binding.viewNovelInfoReadStatusWatched,
                    unifiedReviewCountModel.watchedCount.graphHeight,
                )
                updateGraphSelection(
                    binding.viewNovelInfoReadStatusWatched,
                    binding.tvNovelInfoReadStatusWatchedCount,
                    binding.tvNovelInfoReadStatusWatched,
                )
            }

            QUIT -> {
                updateGraphHeight(
                    binding.viewNovelInfoReadStatusQuit,
                    unifiedReviewCountModel.quitCount.graphHeight,
                )
                updateGraphSelection(
                    binding.viewNovelInfoReadStatusQuit,
                    binding.tvNovelInfoReadStatusQuitCount,
                    binding.tvNovelInfoReadStatusQuit,
                )
            }

            else -> Unit
        }
    }

    private fun updateGraphHeight(
        view: View,
        height: Int,
    ) {
        view.layoutParams.height = height
        view.requestLayout()
    }

    private fun updateGraphSelection(
        graphView: View,
        countTextView: TextView,
        statusTextView: TextView,
    ) {
        graphView.isSelected = true
        countTextView.isSelected = true
        statusTextView.isSelected = true
    }

    private fun updateUsersReadStatusText(unifiedReviewCountModel: UnifiedReviewCountModel) {
        val color = AppCompatResources
            .getColorStateList(
                requireContext(),
                primary_100_6A5DFD,
            ).defaultColor
        when (unifiedReviewCountModel.maxCountReadStatus()) {
            WATCHING -> {
                val watchingCountText = getString(
                    novel_info_read_status_watching_count,
                    unifiedReviewCountModel.watchingCount.count,
                )
                val coloredWatchingCountText =
                    unifiedReviewCountModel.watchingCount.count.toString() + getString(
                        novel_info_user_unit,
                    )
                binding.tvNovelInfoReadStatusTitle.text =
                    getColoredText(watchingCountText, listOf(coloredWatchingCountText), color)
            }

            WATCHED -> {
                val watchedCountText = getString(
                    novel_info_read_status_watched_count,
                    unifiedReviewCountModel.watchedCount.count,
                )
                val coloredWatchedText =
                    unifiedReviewCountModel.watchedCount.count.toString() + getString(
                        novel_info_user_unit,
                    )
                binding.tvNovelInfoReadStatusTitle.text =
                    getColoredText(watchedCountText, listOf(coloredWatchedText), color)
            }

            QUIT -> {
                val quitCountText = getString(
                    novel_info_read_status_quit_count,
                    unifiedReviewCountModel.quitCount.count,
                )
                val coloredQuitText =
                    unifiedReviewCountModel.quitCount.count.toString() + getString(
                        novel_info_user_unit,
                    )
                binding.tvNovelInfoReadStatusTitle.text =
                    getColoredText(quitCountText, listOf(coloredQuitText), color)
            }

            ReadStatus.NONE -> Unit
        }
    }

    private fun getColoredText(
        text: String,
        wordsToColor: List<String>,
        color: Int,
    ): SpannableString {
        val spannableString = SpannableString(text)
        wordsToColor.forEach { word ->
            val start = text.indexOf(word)
            if (start >= 0) {
                val end = start + word.length
                val colorSpan = ForegroundColorSpan(color)
                spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return spannableString
    }

    private fun updateUsersCharmPointBody(charmPoints: String) {
        binding.tvNovelInfoCharmPointsBody.text = getColoredText(
            getString(novel_info_charm_points_body, charmPoints),
            listOf(charmPoints),
            AppCompatResources
                .getColorStateList(
                    requireContext(),
                    primary_100_6A5DFD,
                ).defaultColor,
        )
    }

    private fun setupWebsosoLoadingLayout() {
        binding.wllNovelInfo.setReloadButtonClickListener {
            novelInfoViewModel.updateNovelInfo(novelId)
            binding.wllNovelInfo.setErrorLayoutVisibility(false)
        }
    }

    override fun onResume() {
        super.onResume()
        scrollToTop()
    }

    private fun scrollToTop() {
        binding.nsvNovelInfo.smoothScrollTo(
            PRIMATE_SCROLL_POSITION,
            PRIMATE_SCROLL_POSITION,
            SCROLL_TO_TOP_DURATION,
        )
    }

    companion object {
        private const val PRIMATE_SCROLL_POSITION = 0
        private const val SCROLL_TO_TOP_DURATION = 1700

        private const val NOVEL_ID = "NOVEL_ID"

        fun newInstance(novelId: Long): NovelInfoFragment =
            NovelInfoFragment().also {
                it.arguments = Bundle().apply { putLong(NOVEL_ID, novelId) }
            }
    }
}
