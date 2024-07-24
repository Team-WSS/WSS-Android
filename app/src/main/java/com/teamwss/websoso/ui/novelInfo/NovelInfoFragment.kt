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
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelInfo.model.ExpandTextUiModel
import com.teamwss.websoso.ui.novelInfo.model.KeywordModel
import com.teamwss.websoso.ui.novelInfo.model.ReadStatus
import com.teamwss.websoso.ui.novelInfo.model.UnifiedReviewCountModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {
    private val viewModel by viewModels<NovelInfoViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupObserver()
        viewModel.updateNovelInfo(1)
    }

    private fun bindViewModel() {
        binding.navigateToReadNovel = ::navigateToReadNovel
        binding.novelInfoViewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun navigateToReadNovel(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            setupKeywordChip(uiState.keywords)
            updateExpandTextToggle(uiState.expandTextModel)
            updateExpandTextToggleVisibility(uiState.expandTextModel)
            updateGraphHeightValue(uiState.novelInfoModel.unifiedReviewCount)
            updateGraphUi(uiState.novelInfoModel.unifiedReviewCount)
            updateUsersReadStatusText(uiState.novelInfoModel.unifiedReviewCount)
            updateUsersCharmPointBody(uiState.novelInfoModel.formatAttractivePoints())
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
            viewModel.updateExpandTextToggleVisibility(lineCount, ellipsisCount)
        }
    }

    private fun updateGraphHeightValue(unifiedReviewCountModel: UnifiedReviewCountModel) {
        if (unifiedReviewCountModel.watchingCount.graphHeight != 0) return
        val graphHeight = binding.cvNovelInfoReadStatusWatching.layoutParams.height
        viewModel.updateGraphHeight(graphHeight)
    }

    private fun updateGraphUi(unifiedReviewCountModel: UnifiedReviewCountModel) {
        when (unifiedReviewCountModel.maxCountReadStatus()) {
            ReadStatus.WATCHING -> {
                updateGraphHeight(binding.viewNovelInfoReadStatusWatching, unifiedReviewCountModel.watchingCount.graphHeight)
                updateGraphSelection(binding.viewNovelInfoReadStatusWatching, binding.tvNovelInfoReadStatusWatchingCount, binding.tvNovelInfoReadStatusWatching)
            }

            ReadStatus.WATCHED -> {
                updateGraphHeight(binding.viewNovelInfoReadStatusWatched, unifiedReviewCountModel.watchedCount.graphHeight)
                updateGraphSelection(binding.viewNovelInfoReadStatusWatched, binding.tvNovelInfoReadStatusWatchedCount, binding.tvNovelInfoReadStatusWatched)
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
            AppCompatResources.getColorStateList(requireContext(),R.color.primary_100_6A5DFD).defaultColor,
        )
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
    }
}
