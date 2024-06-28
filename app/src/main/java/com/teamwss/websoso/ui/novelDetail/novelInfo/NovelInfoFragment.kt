package com.teamwss.websoso.ui.novelDetail.novelInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {
    private val novelInfoViewModel by viewModels<NovelInfoViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        setupObserver()
        setupViewMoreTextVisibility()
        novelInfoViewModel.getDummyNovelInfo()
    }

    private fun bindViewModel() {
        binding.novelInfoViewModel = novelInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun setupObserver() {
        novelInfoViewModel.dummyNovelInfo.observe(viewLifecycleOwner) { novelInfo ->
            setupKeywordChip(novelInfo)
        }
    }

    private fun setupKeywordChip(novelInfo: NovelInfoResponseDto) {
        novelInfo.keywords.forEach { keyword ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(keyword.keywordName + " " + keyword.keywordCount)
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

    private fun setupViewMoreTextVisibility() {
        val bodyTextView = binding.tvNovelInfoIntroBody
        bodyTextView.post {
            val lineCount = bodyTextView.layout.lineCount
            val ellipsisCount = bodyTextView.layout.getEllipsisCount(lineCount - 1)
            novelInfoViewModel.updateExpandTextToggleVisibility(lineCount, ellipsisCount)
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
    }
}
