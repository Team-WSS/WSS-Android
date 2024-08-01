package com.teamwss.websoso.ui.detailExplore.keyword

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.FragmentDetailExploreKeywordBinding
import com.teamwss.websoso.ui.common.base.BindingFragment
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.detailExplore.keyword.adapter.DetailExploreKeywordAdapter
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel.CategoryModel.KeywordModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreKeywordFragment :
    BindingFragment<FragmentDetailExploreKeywordBinding>(R.layout.fragment_detail_explore_keyword) {
    private val detailExploreKeywordViewModel: DetailExploreKeywordViewModel by viewModels()
    private val detailExploreKeywordAdapter: DetailExploreKeywordAdapter by lazy {
        DetailExploreKeywordAdapter(detailExploreKeywordViewModel::updateCurrentSelectedKeywords)
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
            detailExploreKeywordAdapter.submitList(it.keywordModel.categories)
        }

        detailExploreKeywordViewModel.selectedKeywords.observe(viewLifecycleOwner) { keywords ->
            if (keywords.isEmpty()) {
                binding.wcgDetailExploreKeywordSelectedKeyword.removeAllViews()
                return@observe
            }

            val currentChipKeywords = binding.wcgDetailExploreKeywordSelectedKeyword.children
                .filterIsInstance<WebsosoChip>()
                .map { it.text.toString() }
                .toList()

            val newKeywords = keywords.map { it.keywordName }

            currentChipKeywords.filter { chipKeyword ->
                !newKeywords.contains(chipKeyword)
            }.forEach { keywordName ->
                removeSelectedChip(keywordName)
            }

            newKeywords.filter { newKeyword ->
                !currentChipKeywords.any { it == newKeyword }
            }.forEach { keywordName ->
                val keywordModel = keywords.find { it.keywordName == keywordName }
                keywordModel?.let { setupSelectedChip(it) }
            }
        }
    }

    private fun removeSelectedChip(keywordName: String) {
        val chipToRemove = binding.wcgDetailExploreKeywordSelectedKeyword.children.find {
            (it as WebsosoChip).text == keywordName
        }
        chipToRemove?.let {
            binding.wcgDetailExploreKeywordSelectedKeyword.removeView(it)
        }
    }

    private fun setupSelectedChip(selectedKeyword: KeywordModel) {
        WebsosoChip(requireContext()).apply {
            setWebsosoChipText(selectedKeyword.keywordName)
            setWebsosoChipTextAppearance(R.style.body2)
            setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
            setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
            setWebsosoChipBackgroundColor(R.color.white)
            setWebsosoChipPaddingVertical(20f)
            setWebsosoChipPaddingHorizontal(12f)
            setWebsosoChipRadius(40f)
            setOnCloseIconClickListener {
                detailExploreKeywordViewModel.updateCurrentSelectedKeywords(
                    selectedKeyword
                )
            }
            setWebsosoChipCloseIconVisibility(true)
            setWebsosoChipCloseIconDrawable(R.drawable.ic_novel_rating_keword_remove)
            setWebsosoChipCloseIconSize(20f)
            setWebsosoChipCloseIconEndPadding(18f)
            setCloseIconTintResource(R.color.primary_100_6A5DFD)
        }.also { websosoChip ->
            binding.wcgDetailExploreKeywordSelectedKeyword.addChip(websosoChip)
        }
    }
}