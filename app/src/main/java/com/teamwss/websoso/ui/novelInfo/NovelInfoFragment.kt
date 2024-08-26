package com.teamwss.websoso.ui.novelInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.ui.novelInfo.model.ExpandTextUiModel
import com.teamwss.websoso.ui.novelInfo.model.KeywordModel
import com.teamwss.websoso.ui.novelInfo.model.UnifiedReviewCountModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelInfoFragment : BaseFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {
    private val novelInfoViewModel by viewModels<NovelInfoViewModel>()
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun setupObserver() {
        novelInfoViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when {
                uiState.novelInfoModel.novelDescription.isNotBlank() -> {
                    setupKeywordChip(uiState.keywords)
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
        keywords.forEach { keyword ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(
                    getString(
                        R.string.novel_info_keyword_chip_text,
                        keyword.keywordName,
                        keyword.keywordCount
                    )
                )
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
                setWebsosoChipStrokeColor(R.color.transparent)
                setWebsosoChipBackgroundColor(R.color.primary_50_F1EFFF)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(40f)
                isEnabled = false
            }.also { websosoChip -> binding.wcgNovelInfoKeyword.addChip(websosoChip) }
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
            ReadStatus.WATCHING -> {
                updateGraphHeight(binding.viewNovelInfoReadStatusWatching, unifiedReviewCountModel.watchingCount.graphHeight)
                updateGraphSelection(
                    binding.viewNovelInfoReadStatusWatching,
                    binding.tvNovelInfoReadStatusWatchingCount,
                    binding.tvNovelInfoReadStatusWatching
                )
            }

            ReadStatus.WATCHED -> {
                updateGraphHeight(binding.viewNovelInfoReadStatusWatched, unifiedReviewCountModel.watchedCount.graphHeight)
                updateGraphSelection(
                    binding.viewNovelInfoReadStatusWatched,
                    binding.tvNovelInfoReadStatusWatchedCount,
                    binding.tvNovelInfoReadStatusWatched
                )
            }

            ReadStatus.QUIT -> {
                updateGraphHeight(binding.viewNovelInfoReadStatusQuit, unifiedReviewCountModel.quitCount.graphHeight)
                updateGraphSelection(binding.viewNovelInfoReadStatusQuit, binding.tvNovelInfoReadStatusQuitCount, binding.tvNovelInfoReadStatusQuit)
            }
        }
    }

    private fun updateGraphHeight(view: View, height: Int) {
        view.layoutParams.height = height
        view.requestLayout()
    }

    private fun updateGraphSelection(graphView: View, countTextView: TextView, statusTextView: TextView) {
        graphView.isSelected = true
        countTextView.isSelected = true
        statusTextView.isSelected = true
    }

    private fun updateUsersReadStatusText(unifiedReviewCountModel: UnifiedReviewCountModel) {
        val color = AppCompatResources.getColorStateList(requireContext(), R.color.primary_100_6A5DFD).defaultColor
        when (unifiedReviewCountModel.maxCountReadStatus()) {
            ReadStatus.WATCHING -> {
                val watchingCountText = getString(R.string.novel_info_read_status_watching_count, unifiedReviewCountModel.watchingCount.count)
                val coloredWatchingCountText = unifiedReviewCountModel.watchingCount.count.toString() + getString(R.string.novel_info_user_unit)
                binding.tvNovelInfoReadStatusTitle.text = getColoredText(watchingCountText, listOf(coloredWatchingCountText), color)
            }

            ReadStatus.WATCHED -> {
                val watchedCountText = getString(R.string.novel_info_read_status_watched_count, unifiedReviewCountModel.watchedCount.count)
                val coloredWatchedText = unifiedReviewCountModel.watchedCount.count.toString() + getString(R.string.novel_info_user_unit)
                binding.tvNovelInfoReadStatusTitle.text = getColoredText(watchedCountText, listOf(coloredWatchedText), color)
            }

            ReadStatus.QUIT -> {
                val quitCountText = getString(R.string.novel_info_read_status_quit_count, unifiedReviewCountModel.quitCount.count)
                val coloredQuitText = unifiedReviewCountModel.quitCount.count.toString() + getString(R.string.novel_info_user_unit)
                binding.tvNovelInfoReadStatusTitle.text = getColoredText(quitCountText, listOf(coloredQuitText), color)
            }
        }
    }

    private fun getColoredText(text: String, wordsToColor: List<String>, color: Int): SpannableString {
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
            getString(R.string.novel_info_charm_points_body, charmPoints),
            listOf(charmPoints),
            AppCompatResources.getColorStateList(requireContext(), R.color.primary_100_6A5DFD).defaultColor,
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

        fun newInstance(novelId: Long): NovelInfoFragment {
            return NovelInfoFragment().also {
                it.arguments = Bundle().apply { putLong(NOVEL_ID, novelId) }
            }
        }
    }
}
