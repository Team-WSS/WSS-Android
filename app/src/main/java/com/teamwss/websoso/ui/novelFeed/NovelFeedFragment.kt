package com.teamwss.websoso.ui.novelFeed

import android.os.Bundle
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.databinding.FragmentNovelFeedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelFeedFragment : BaseFragment<FragmentNovelFeedBinding>(R.layout.fragment_novel_feed) {

    private val novelId: Long by lazy { requireArguments().getLong(NOVEL_ID) }

    companion object {
        private const val NOVEL_ID = "NOVEL_ID"

        fun newInstance(novelId: Long): NovelFeedFragment {
            return NovelFeedFragment().also {
                it.arguments = Bundle().apply { putLong(NOVEL_ID, novelId) }
            }
        }
    }
}
