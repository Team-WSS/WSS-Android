package com.teamwss.websoso.ui.novelDetail.novelInfo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment

class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewMoreListener(binding.tvNovelInfoIntroBody, binding.tvNovelInfoIntroMore)
    }

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
        scrollToTop()
    }

    private fun setupViewMoreListener(contentTextView: TextView, viewMoreTextView: TextView) {
        contentTextView.post {
            val lineCount = contentTextView.layout.lineCount
            Log.e("NovelInfoFragmentTest", "linCount $lineCount")
            Log.e(
                "NovelInfoFragmentTest",
                "ellipsize ${contentTextView.layout.getEllipsisCount(lineCount - 1)}"
            )
            if (lineCount >= 3) {
                if (contentTextView.layout.getEllipsisCount(lineCount - 1) > 0) {
                    viewMoreTextView.visibility = View.VISIBLE
                    contentTextView.ellipsize = null

                    binding.llNovelInfoIntro.setOnClickListener {
                        contentTextView.maxLines = Int.MAX_VALUE
                        viewMoreTextView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun scrollToTop() {
        binding.nsvNovelInfo.smoothScrollTo(0, 0, SCROLL_TO_TOP_DURATION)
    }

    companion object {
        const val SCROLL_TO_TOP_DURATION = 1700
    }
}