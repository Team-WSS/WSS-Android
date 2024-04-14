package com.teamwss.websoso.ui.novelDetail.novelInfo

import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment

class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
        scrollToTop()
    }

    private fun scrollToTop() {
        binding.nsvNovelInfo.smoothScrollTo(0, 0, SCROLL_BACK_DURATION)
    }

    companion object {
        const val SCROLL_BACK_DURATION = 1700
    }
}