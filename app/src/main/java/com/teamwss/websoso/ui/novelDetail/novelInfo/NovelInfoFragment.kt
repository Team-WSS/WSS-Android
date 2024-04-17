package com.teamwss.websoso.ui.novelDetail.novelInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment

class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {
    private val novelInfoViewModel by viewModels<NovelInfoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupOnClickNovelInfoItem()
        initViewMoreTextVisibility()
    }

    private fun setupViewModel() {
        binding.novelInfoViewModel = novelInfoViewModel
        binding.lifecycleOwner = this
    }

    private fun setupOnClickNovelInfoItem() {
        binding.novelInfoClickListener = setupOnClickNovelInfoClickListener()
    }

    private fun setupOnClickNovelInfoClickListener() = object : NovelInfoClickListener {
        override fun onNovelInfoViewMoreClick() {
            novelInfoViewModel.onViewMoreClicked()
        }
    }

    private fun initViewMoreTextVisibility() {
        val bodyTextView = binding.tvNovelInfoIntroBody
        bodyTextView.post {
            val lineCount = bodyTextView.layout.lineCount
            val ellipsisCount = bodyTextView.layout.getEllipsisCount(lineCount - 1)
            novelInfoViewModel.initViewMoreTextVisibility(lineCount, ellipsisCount)
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
            SCROLL_TO_TOP_DURATION
        )
    }

    companion object {
        private const val PRIMATE_SCROLL_POSITION = 0
        private const val SCROLL_TO_TOP_DURATION = 1700
    }
}