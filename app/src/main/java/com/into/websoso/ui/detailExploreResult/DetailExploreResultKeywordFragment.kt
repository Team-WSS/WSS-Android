package com.into.websoso.ui.detailExploreResult

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.into.websoso.R
import com.into.websoso.R.color.bg_novel_rating_chip_background_selector
import com.into.websoso.R.color.bg_novel_rating_chip_stroke_selector
import com.into.websoso.R.color.bg_novel_rating_chip_text_selector
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.white
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseFragment
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.into.websoso.core.common.ui.model.CategoriesModel.Companion.findKeywordByName
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_keword_remove
import com.into.websoso.core.resource.R.string.detail_explore_search_hint
import com.into.websoso.core.resource.R.string.inquire_link
import com.into.websoso.databinding.FragmentDetailExploreResultKeywordBinding
import com.into.websoso.ui.detailExplore.keyword.DetailExploreClickListener
import com.into.websoso.ui.detailExplore.keyword.adapter.DetailExploreKeywordAdapter
import com.into.websoso.ui.detailExploreResult.model.DetailExploreResultUiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailExploreResultKeywordFragment :
    BaseFragment<FragmentDetailExploreResultKeywordBinding>(R.layout.fragment_detail_explore_result_keyword) {
    @Inject
    lateinit var tracker: Tracker

    private val detailExploreResultViewModel: DetailExploreResultViewModel by activityViewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val detailExploreKeywordAdapter: DetailExploreKeywordAdapter by lazy {
        DetailExploreKeywordAdapter(detailExploreResultViewModel::updateClickedChipState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        bindViewModel()
        setupAdapter()
        setupObserver()
        setupSearchKeyword()
        setupWebsosoSearchEditListener()
        setupBackButtonListener()
        tracker.trackEvent("seek_result")
    }

    private fun bindViewModel() {
        binding.detailExploreResultViewModel = detailExploreResultViewModel
        binding.onClick = onDetailExploreKeywordButtonClick()
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun onDetailExploreKeywordButtonClick() =
        object : DetailExploreClickListener {
            override fun onNovelInquireButtonClick() {
                val inquireUrl = getString(inquire_link)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
                startActivity(intent)
            }

            override fun onDetailSearchNovelButtonClick() {
                singleEventHandler.throttleFirst {
                    detailExploreResultViewModel.updateSearchResult(true)

                    (parentFragment as? DetailExploreResultDialogBottomSheet)?.dismiss()

                    detailExploreResultViewModel.updateIsBottomSheetOpen(false)
                }
            }

            override fun onKeywordResetButtonClick() {
                detailExploreResultViewModel.updateSelectedKeywordValueClear()
            }
        }

    private fun setupAdapter() {
        binding.rvDetailExploreKeywordList.apply {
            adapter = detailExploreKeywordAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        detailExploreResultViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            detailExploreKeywordAdapter.submitList(uiState.categories)
            setupSelectedScrollViewVisibility(uiState.categories)
            setupSelectedChips(uiState.categories)
            updateSearchKeywordResult(uiState)
        }
    }

    private fun setupSelectedScrollViewVisibility(categories: List<CategoryModel>) {
        val hasSelectedKeywords = categories.flatMap { it.keywords }.any { it.isSelected }

        binding.hsvRatingKeywordSelectedKeyword.isVisible = hasSelectedKeywords
    }

    private fun setupSelectedChips(categories: List<CategoryModel>) {
        val currentChipKeywords =
            binding.wcgDetailExploreKeywordSelectedKeyword.children
                .filterIsInstance<WebsosoChip>()
                .map { it.text.toString() }
                .toList()

        val selectedKeywords =
            categories
                .asSequence()
                .flatMap { it.keywords.asSequence() }
                .filter { it.isSelected }
                .map { it.keywordName }
                .toList()

        val chipsToRemove = currentChipKeywords - selectedKeywords.toSet()
        val chipsToAdd = selectedKeywords - currentChipKeywords.toSet()

        chipsToRemove.forEach { keywordName ->
            removeSelectedChip(keywordName)
        }

        chipsToAdd.forEach { keywordName ->
            createSelectedChip(
                categories.findKeywordByName(keywordName)
                    ?: throw IllegalArgumentException("Keyword not found: $keywordName"),
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
        WebsosoChip(requireContext())
            .apply {
                setWebsosoChipText(selectedKeyword.keywordName)
                setWebsosoChipTextAppearance(body2)
                setWebsosoChipTextColor(primary_100_6A5DFD)
                setWebsosoChipStrokeColor(primary_100_6A5DFD)
                setWebsosoChipBackgroundColor(white)
                setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                setWebsosoChipPaddingHorizontal(4f.toFloatPxFromDp())
                setWebsosoChipRadius(20f.toFloatPxFromDp())
                setOnCloseIconClickListener {
                    detailExploreResultViewModel.updateClickedChipState(
                        selectedKeyword.keywordId,
                    )
                }
                setWebsosoChipCloseIconVisibility(true)
                setWebsosoChipCloseIconDrawable(ic_novel_rating_keword_remove)
                setWebsosoChipCloseIconSize(20f)
                setWebsosoChipCloseIconEndPadding(18f)
                setCloseIconTintResource(primary_100_6A5DFD)
            }.also { websosoChip ->
                binding.wcgDetailExploreKeywordSelectedKeyword.addChip(websosoChip)
            }
    }

    private fun updateSearchKeywordResult(uiState: DetailExploreResultUiState) {
        val previousSearchResultKeywords =
            binding.wcgDetailExploreKeywordResult.children
                .toList()
                .map { it as WebsosoChip }

        when {
            !uiState.isSearchKeywordProceeding -> return

            uiState.isSearchResultKeywordsEmpty -> return

            uiState.searchResultKeywords.map { it.keywordName } == previousSearchResultKeywords.map { it.text.toString() } -> {
                updateSearchKeywordResultIsSelected(uiState)
                return
            }

            else -> {
                binding.wcgDetailExploreKeywordResult.removeAllViews()
                updateSearchKeywordResultWebsosoChips(uiState)
            }
        }
    }

    private fun updateSearchKeywordResultIsSelected(uiState: DetailExploreResultUiState) {
        binding.wcgDetailExploreKeywordResult.forEach { view ->
            val chip = view as? WebsosoChip ?: return@forEach

            val isSelected =
                uiState.categories
                    .asSequence()
                    .flatMap { it.keywords }
                    .filter { it.isSelected }
                    .any { it.keywordName == chip.text.toString() }

            chip.isSelected = isSelected
        }
    }

    private fun updateSearchKeywordResultWebsosoChips(uiState: DetailExploreResultUiState) {
        uiState.searchResultKeywords.forEach { keyword ->
            val isSelected = uiState.categories
                .asSequence()
                .flatMap { it.keywords }
                .any { it.keywordId == keyword.keywordId && it.isSelected }

            WebsosoChip(binding.root.context)
                .apply {
                    setWebsosoChipText(keyword.keywordName)
                    setWebsosoChipTextAppearance(body2)
                    setWebsosoChipTextColor(bg_novel_rating_chip_text_selector)
                    setWebsosoChipStrokeColor(bg_novel_rating_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(bg_novel_rating_chip_background_selector)
                    setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                    setWebsosoChipPaddingHorizontal(6f.toFloatPxFromDp())
                    setWebsosoChipRadius(20f.toFloatPxFromDp())
                    setOnWebsosoChipClick {
                        detailExploreResultViewModel.updateClickedChipState(keyword.keywordId)
                    }
                    this.isSelected = isSelected
                }.also { websosoChip ->
                    binding.wcgDetailExploreKeywordResult.addChip(websosoChip)
                }
        }
    }

    private fun setupSearchKeyword() {
        binding.wsetDetailExploreKeywordSearch.apply {
            setWebsosoSearchHint(getString(detail_explore_search_hint))
        }
    }

    private fun setupWebsosoSearchEditListener() {
        binding.wsetDetailExploreKeywordSearch.setOnWebsosoSearchActionListener { _, _, _ ->
            performSearch()
            true
        }

        binding.wsetDetailExploreKeywordSearch.setOnWebsosoSearchFocusChangeListener { _, isFocused ->
            if (isFocused) detailExploreResultViewModel.updateIsSearchKeywordProceeding(true)
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
        detailExploreResultViewModel.updateKeyword(input)
    }

    private fun initSearchKeyword() {
        binding.wsetDetailExploreKeywordSearch.clearWebsosoSearchFocus()
        detailExploreResultViewModel.initSearchKeyword()
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
            },
        )
    }

    override fun onDestroyView() {
        initSearchKeyword()
        super.onDestroyView()
    }
}
