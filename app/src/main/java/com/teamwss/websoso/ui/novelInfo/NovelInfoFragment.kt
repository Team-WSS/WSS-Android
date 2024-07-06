package com.teamwss.websoso.ui.novelInfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelInfo.model.ExpandTextUiModel
import com.teamwss.websoso.ui.novelInfo.model.KeywordModel
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
        updateGraphHeights(unifiedReviewCountModel)
        updateGraphSelectionStates(unifiedReviewCountModel)
        updateTextViewSelectionStates(unifiedReviewCountModel)
    }

    private fun updateGraphHeights(unifiedReviewCountModel: UnifiedReviewCountModel) {
        binding.viewNovelInfoReadStatusWatching.layoutParams.height = unifiedReviewCountModel.watchingCount.graphHeight
        binding.viewNovelInfoReadStatusWatched.layoutParams.height = unifiedReviewCountModel.watchedCount.graphHeight
        binding.viewNovelInfoReadStatusQuit.layoutParams.height = unifiedReviewCountModel.quitCount.graphHeight

        binding.viewNovelInfoReadStatusWatching.requestLayout()
        binding.viewNovelInfoReadStatusWatched.requestLayout()
        binding.viewNovelInfoReadStatusQuit.requestLayout()
    }

    private fun updateGraphSelectionStates(unifiedReviewCountModel: UnifiedReviewCountModel) {
        binding.viewNovelInfoReadStatusWatching.isSelected = unifiedReviewCountModel.watchingCount.isMaxValue
        binding.viewNovelInfoReadStatusWatched.isSelected = unifiedReviewCountModel.watchedCount.isMaxValue
        binding.viewNovelInfoReadStatusQuit.isSelected = unifiedReviewCountModel.quitCount.isMaxValue
    }

    private fun updateTextViewSelectionStates(unifiedReviewCountModel: UnifiedReviewCountModel) {
        binding.tvNovelInfoReadStatusWatchingCount.isSelected = unifiedReviewCountModel.watchingCount.isMaxValue
        binding.tvNovelInfoReadStatusWatchedCount.isSelected = unifiedReviewCountModel.watchedCount.isMaxValue
        binding.tvNovelInfoReadStatusQuitCount.isSelected = unifiedReviewCountModel.quitCount.isMaxValue

        binding.tvNovelInfoReadStatusWatching.isSelected = unifiedReviewCountModel.watchingCount.isMaxValue
        binding.tvNovelInfoReadStatusWatched.isSelected = unifiedReviewCountModel.watchedCount.isMaxValue
        binding.tvNovelInfoReadStatusQuit.isSelected = unifiedReviewCountModel.quitCount.isMaxValue
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
