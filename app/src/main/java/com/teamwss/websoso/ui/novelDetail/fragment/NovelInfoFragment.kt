package com.teamwss.websoso.ui.novelDetail.fragment

import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentNovelInfoBinding
import com.teamwss.websoso.ui.common.base.BindingFragment

class NovelInfoFragment : BindingFragment<FragmentNovelInfoBinding>(R.layout.fragment_novel_info) {

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
    }
}