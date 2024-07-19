package com.teamwss.websoso.ui.detailExplore.keyword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentDetailExploreKeywordBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.detailExplore.keyword.adapter.DetailExploreKeywordAdapter
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreKeywordFragment :
    BindingFragment<FragmentDetailExploreKeywordBinding>(R.layout.fragment_detail_explore_keyword) {
    private val detailExploreKeywordViewModel: DetailExploreKeywordViewModel by viewModels()
    private val detailExploreKeywordAdapter: DetailExploreKeywordAdapter by lazy {
        DetailExploreKeywordAdapter(onKeywordClick = { keyword, isSelected ->
            detailExploreKeywordViewModel.updateCurrentSelectedKeywords(keyword, isSelected)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailExploreKeywordViewModel.updateKeywords()
        bindViewModel()
        setupAdapter()
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

    private fun setupAdapter() {
        binding.rvDetailExploreKeywordList.apply {
            adapter = detailExploreKeywordAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        detailExploreKeywordViewModel.searchWord.observe(viewLifecycleOwner) {
            detailExploreKeywordViewModel.updateSearchCancelButtonVisibility()
        }

        detailExploreKeywordViewModel.uiState.observe(viewLifecycleOwner) {
            setupCurrentSelectedChips(it)
            detailExploreKeywordAdapter.submitList(it.keywordModel.categories)
        }
    }

    private fun setupCurrentSelectedChips(uiState: DetailExploreKeywordUiState) {
        val currentSelectedKeywords = uiState.keywordModel.currentSelectedKeywords
        val keywordChipGroup = binding.wcgDetailExploreKeywordSelectedKeyword
        keywordChipGroup.removeAllViews()
        currentSelectedKeywords.forEach { keyword ->
            WebsosoChip(requireContext()).apply {
                setWebsosoChipText(keyword.keywordName)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
                setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
                setWebsosoChipBackgroundColor(R.color.white)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(40f)
                setOnCloseIconClickListener {
                    detailExploreKeywordViewModel.updateCurrentSelectedKeywords(
                        keyword, isSelected = false
                    )
                }
                setWebsosoChipCloseIconVisibility(true)
                setWebsosoChipCloseIconDrawable(R.drawable.ic_novel_rating_keword_remove)
                setWebsosoChipCloseIconSize(20f)
                setWebsosoChipCloseIconEndPadding(18f)
                setCloseIconTintResource(R.color.primary_100_6A5DFD)
            }.also { websosoChip -> keywordChipGroup.addChip(websosoChip) }
        }
    }
}