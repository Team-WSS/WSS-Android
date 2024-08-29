package com.teamwss.websoso.ui.detailExplore.keyword

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.forEach
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
import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordUiState
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

        bindViewModel()
        setupAdapter()
        setupObserver()
        setupSearchKeyword()
        setupWebsosoSearchEditListener()
        setupBackButtonListener()
    }

    private fun bindViewModel() {
        binding.detailExploreViewModel = detailExploreViewModel
        binding.onClick = onDetailExploreKeywordButtonClick()
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun onDetailExploreKeywordButtonClick() = object : DetailExploreClickListener {

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

        val keywordIds = detailExploreViewModel.uiState.value?.categories?.asSequence()
            ?.flatMap { it.keywords.asSequence() }
            ?.filter { it.isSelected }
            ?.map { it.keywordId }
            ?.toList() ?: emptyList()

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
        detailExploreViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            detailExploreKeywordAdapter.submitList(uiState.categories)
            setupSelectedChips(uiState.categories)
            updateSearchKeywordResult(uiState)
        }
    }

    private fun setupSelectedChips(categories: List<CategoryModel>) {
        val currentChipKeywords =
            binding.wcgDetailExploreKeywordSelectedKeyword.children
                .filterIsInstance<WebsosoChip>()
                .map { it.text.toString() }.toList()

        val selectedKeywords =
            categories
                .asSequence()
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
            setWebsosoChipPaddingHorizontal(4f.toFloatScaledByPx())
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

    private fun updateSearchKeywordResult(uiState: DetailExploreKeywordUiState) {
        val previousSearchResultKeywords =
            binding.wcgDetailExploreKeywordResult.children.toList().map { it as WebsosoChip }
        if (!uiState.isSearchKeywordProceeding) return
        if (uiState.isSearchResultKeywordsEmpty) return
        if (uiState.searchResultKeywords.map { it.keywordName } == previousSearchResultKeywords.map { it.text.toString() }) {
            updateSearchKeywordResultIsSelected(uiState)
            return
        }
        binding.wcgDetailExploreKeywordResult.removeAllViews()
        updateSearchKeywordResultWebsosoChips(uiState)
    }

    private fun updateSearchKeywordResultIsSelected(uiState: DetailExploreKeywordUiState) {
        binding.wcgDetailExploreKeywordResult.forEach { view ->
            val chip = view as? WebsosoChip ?: return@forEach

            val isSelected = uiState.categories
                .asSequence()
                .flatMap { it.keywords }
                .filter { it.isSelected }
                .any { it.keywordName == chip.text.toString() }

            chip.isSelected = isSelected
        }
    }

    private fun updateSearchKeywordResultWebsosoChips(uiState: DetailExploreKeywordUiState) {
        uiState.searchResultKeywords.forEach { keyword ->
            val isSelected = uiState.categories.asSequence().flatMap { it.keywords }
                .any { it.keywordId == keyword.keywordId && it.isSelected }

            WebsosoChip(binding.root.context).apply {
                setWebsosoChipText(keyword.keywordName)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_novel_rating_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_novel_rating_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_novel_rating_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatScaledByPx())
                setWebsosoChipPaddingHorizontal(6f.toFloatScaledByPx())
                setWebsosoChipRadius(20f.toFloatScaledByPx())
                setOnWebsosoChipClick {
                    detailExploreViewModel.updateClickedChipState(keyword.keywordId)
                }
                this.isSelected = isSelected
            }.also { websosoChip ->
                binding.wcgDetailExploreKeywordResult.addChip(websosoChip)
            }
        }
    }

    private fun setupSearchKeyword() {
        binding.wsetDetailExploreKeywordSearch.apply {
            setWebsosoSearchHint(getString(R.string.detail_explore_search_hint))
        }
    }

    private fun setupWebsosoSearchEditListener() {
        binding.wsetDetailExploreKeywordSearch.setOnWebsosoSearchActionListener { _, _, _ ->
            performSearch()
            true
        }

        binding.wsetDetailExploreKeywordSearch.setOnWebsosoSearchFocusChangeListener { _, isFocused ->
            if (isFocused) detailExploreViewModel.updateIsSearchKeywordProceeding(true)
        }

        binding.wsetDetailExploreKeywordSearch.setOnWebsosoSearchClearClickListener {
            initSearchKeyword()
        }
    }

    private fun performSearch() {
        val input = binding.wsetDetailExploreKeywordSearch.getWebsosoSearchText()
        if (input.isEmpty()) {
            initSearchKeyword()
            return
        }
        detailExploreViewModel.updateKeyword(input)
    }

    private fun initSearchKeyword() {
        binding.wsetDetailExploreKeywordSearch.clearWebsosoSearchFocus()
        detailExploreViewModel.initSearchKeyword()
    }

    private fun setupBackButtonListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.wsetDetailExploreKeywordSearch.hasFocus()) {
                        initSearchKeyword()
                    } else {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    override fun onDestroyView() {
        initSearchKeyword()
        super.onDestroyView()
    }
}
