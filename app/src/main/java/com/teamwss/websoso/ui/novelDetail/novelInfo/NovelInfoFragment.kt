package com.teamwss.websoso.ui.novelDetail.novelInfo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment

class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewMoreListener()
    }

    private fun setupViewMoreListener() {
        val targetText = binding.tvNovelInfoIntroBody
        val viewMore = binding.tvNovelInfoIntroMore

        targetText.post {
            val lineCount = targetText.layout.lineCount
            if (lineCount < 3) return@post
            if (targetText.layout.getEllipsisCount(lineCount - 1) > 0) {
                viewMore.visibility = View.VISIBLE
                targetText.ellipsize = null

                binding.llNovelInfoIntro.setOnClickListener {
                    targetText.maxLines = Int.MAX_VALUE
                    viewMore.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
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