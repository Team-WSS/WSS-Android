package com.teamwss.websoso.ui.detailExplore.keyword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentDetailExploreKeywordBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreKeywordFragment :
    BindingFragment<FragmentDetailExploreKeywordBinding>(R.layout.fragment_detail_explore_keyword) {
    private val detailExploreKeywordViewModel: DetailExploreKeywordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.detailExploreKeywordViewModel = detailExploreKeywordViewModel
        binding.onClick = onDetailExploreKeywordButtonClick()
        binding.lifecycleOwner = this
    }

    private fun onDetailExploreKeywordButtonClick() = object : DetailExploreClickListener {

        override fun onSearchButtonClick() {
            // TODO 검색하는 버튼
        }

        override fun onSearchCancelButtonClick() {
            detailExploreKeywordViewModel.updateSearchWordEmpty()
        }

        override fun onNovelInquireButtonClick() {
            // TODO 문의하기로 이동
        }
    }

    private fun setupObserver() {

        detailExploreKeywordViewModel.searchWord.observe(viewLifecycleOwner) {
            detailExploreKeywordViewModel.validateSearchWordClearButton()
        }
    }
}