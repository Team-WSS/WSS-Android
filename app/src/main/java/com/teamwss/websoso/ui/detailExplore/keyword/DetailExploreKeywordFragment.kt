package com.teamwss.websoso.ui.detailExplore.keyword

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseFragment
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel
import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.teamwss.websoso.common.ui.model.CategoriesModel.Companion.findKeywordByName
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.databinding.FragmentDetailExploreKeywordBinding
import com.teamwss.websoso.ui.detailExplore.DetailExploreViewModel
import com.teamwss.websoso.ui.detailExplore.keyword.adapter.DetailExploreKeywordAdapter
import com.teamwss.websoso.ui.detailExploreResult.DetailExploreResultActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailExploreKeywordFragment :
    BaseFragment<FragmentDetailExploreKeywordBinding>(R.layout.fragment_detail_explore_keyword) {
    private val detailExploreViewModel: DetailExploreViewModel by activityViewModels()
    private val detailExploreKeywordAdapter: DetailExploreKeywordAdapter by lazy {
        DetailExploreKeywordAdapter(detailExploreViewModel::updateClickedChipState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailExploreViewModel.updateKeyword()
        bindViewModel()
        setupAdapter()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.detailExploreViewModel = detailExploreViewModel
        binding.onClick = onDetailExploreKeywordButtonClick()
        binding.lifecycleOwner = this
    }

    private fun onDetailExploreKeywordButtonClick() = object : DetailExploreClickListener {

        override fun onSearchButtonClick() {
            // TODO 검색하는 버튼
        }

        override fun onSearchCancelButtonClick() {
            detailExploreViewModel.updateSearchWordEmpty()
        }

        override fun onNovelInquireButtonClick() {
            // TODO 문의하기로 이동
        }

        override fun onDetailSearchNovelButtonClick() {
            navigateToDetailSearchResult()
        }

        override fun onKeywordResetButtonClick() {
            detailExploreViewModel.updateSelectedKeywordValueClear()
        }
    }

    private fun navigateToDetailSearchResult() {
        val selectedGenres = detailExploreViewModel.selectedGenres.value ?: emptyList()
        val isCompleted = detailExploreViewModel.selectedStatus.value?.isCompleted
        val novelRating = detailExploreViewModel.selectedRating.value

        val keywordIds = detailExploreViewModel.uiState.value?.categories
            ?.asSequence()
            ?.flatMap { it.keywords.asSequence() }
            ?.filter { it.isSelected }
            ?.map { it.keywordId }
            ?.toList()
            ?: emptyList()

        val intent = DetailExploreResultActivity.getIntent(
            context = requireContext(),
            genres = selectedGenres,
            isCompleted = isCompleted,
            novelRating = novelRating,
            keywordIds = keywordIds,
        )

        startActivity(intent)
    }

    private fun setupAdapter() {
        binding.rvDetailExploreKeywordList.apply {
            adapter = detailExploreKeywordAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        detailExploreViewModel.searchWord.observe(viewLifecycleOwner) {
            detailExploreViewModel.updateSearchCancelButtonVisibility()
        }

        detailExploreViewModel.uiState.observe(viewLifecycleOwner) {
            detailExploreKeywordAdapter.submitList(it.categories)
            setupSelectedChips(it.categories)
        }
    }

    private fun setupSelectedChips(categories: List<CategoryModel>) {
        val currentChipKeywords =
            binding.wcgDetailExploreKeywordSelectedKeyword.children
                .filterIsInstance<WebsosoChip>()
                .map { it.text.toString() }.toList()

        val selectedKeywords =
            categories.asSequence()
                .flatMap { it.keywords.asSequence() }
                .filter { it.isSelected }
                .map { it.keywordName }.toList()

        val chipsToRemove = currentChipKeywords - selectedKeywords.toSet()
        val chipsToAdd = selectedKeywords - currentChipKeywords.toSet()

        chipsToRemove.forEach { keywordName ->
            removeSelectedChip(keywordName)
        }

        chipsToAdd.forEach { keywordName ->
            createSelectedChip(
                categories.findKeywordByName(keywordName)
                    ?: throw IllegalArgumentException("Keyword not found: $keywordName")
            )
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

    private fun createSelectedChip(selectedKeyword: KeywordModel) {
        WebsosoChip(requireContext()).apply {
            setWebsosoChipText(selectedKeyword.keywordName)
            setWebsosoChipTextAppearance(R.style.body2)
            setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
            setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
            setWebsosoChipBackgroundColor(R.color.white)
            setWebsosoChipPaddingVertical(12f.toFloatScaledByPx())
            setWebsosoChipPaddingHorizontal(6f.toFloatScaledByPx())
            setWebsosoChipRadius(20f.toFloatScaledByPx())
            setOnCloseIconClickListener {
                detailExploreViewModel.updateClickedChipState(
                    selectedKeyword.keywordId
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
