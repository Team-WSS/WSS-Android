package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.base.BindingBottomSheetDialog
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.common.model.KeywordsModel
import com.teamwss.websoso.ui.novelRating.adapter.NovelRatingKeywordAdapter
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState

class NovelRatingKeywordBottomSheetDialog :
    BindingBottomSheetDialog<DialogNovelRatingKeywordBinding>(R.layout.dialog_novel_rating_keyword) {
    private val novelRatingViewModel: NovelRatingViewModel by activityViewModels()
    private val novelRatingKeywordAdapter by lazy {
        NovelRatingKeywordAdapter(
            onKeywordClick = { keyword, isSelected ->
                novelRatingViewModel.updateSelectedKeywords(keyword, isSelected)
            },
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        bindViewModel()
        setupDialogBehavior()
        setupRecyclerViewAnimation()
        setupObserver()
        setupSearchEditorAction()
    }

    private fun bindViewModel() {
        binding.viewModel = novelRatingViewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun onNovelRatingButtonClick() =
        object : NovelRatingClickListener {
            override fun onDateEditClick() {}

            override fun onKeywordEditClick() {}

            override fun onNavigateBackClick() {}

            override fun onSaveClick() {
                novelRatingViewModel.saveSelectedKeywords()
                dismiss()
            }

            override fun onCancelClick() {
                novelRatingViewModel.cancelEditingKeyword()
                dismiss()
            }

            override fun onClearClick() {
                novelRatingViewModel.clearEditingKeyword()
                dismiss()
            }
        }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun setupRecyclerViewAnimation() {
        binding.rvRatingKeywordList.apply {
            adapter = novelRatingKeywordAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        novelRatingViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            updateCurrentSelectedKeywordsHeader(uiState.keywordsModel.currentSelectedKeywords)
            updateKeywordRecyclerView(uiState)
        }
    }

    private fun updateCurrentSelectedKeywordsHeader(currentSelectedKeywords: List<KeywordsModel.CategoryModel.KeywordModel>) {
        val existingKeywords = mutableListOf<String>()

        for (i in 0 until binding.wcgNovelRatingKeywordSelectedKeyword.childCount) {
            val chip = binding.wcgNovelRatingKeywordSelectedKeyword.getChildAt(i) as WebsosoChip
            existingKeywords.add(chip.text.toString())
        }

        val newKeywords = currentSelectedKeywords.map { it.keywordName }

        existingKeywords.filterNot { it in newKeywords }.forEach { keyword ->
            removeCurrentSelectedKeywordChip(keyword)
        }

        currentSelectedKeywords.filterNot { it.keywordName in existingKeywords }.forEach { keyword ->
            addCurrentSelectedKeywordChip(keyword)
        }
    }

    private fun removeCurrentSelectedKeywordChip(keyword: String) {
        val chip = binding.wcgNovelRatingKeywordSelectedKeyword.findViewWithTag<WebsosoChip>(keyword)
        if (chip != null) binding.wcgNovelRatingKeywordSelectedKeyword.removeView(chip)
    }

    private fun addCurrentSelectedKeywordChip(keyword: KeywordsModel.CategoryModel.KeywordModel) {
        WebsosoChip(binding.root.context).apply {
            setWebsosoChipText(keyword.keywordName)
            setWebsosoChipTextAppearance(R.style.body2)
            setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
            setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
            setWebsosoChipBackgroundColor(R.color.white)
            setWebsosoChipPaddingVertical(20f)
            setWebsosoChipPaddingHorizontal(12f)
            setWebsosoChipRadius(40f)
            setOnCloseIconClickListener {
                novelRatingViewModel.updateSelectedKeywords(keyword = keyword, isSelected = false)
            }
            setWebsosoChipCloseIconVisibility(true)
            setWebsosoChipCloseIconDrawable(R.drawable.ic_novel_rating_keword_remove)
            setWebsosoChipCloseIconSize(20f)
            setWebsosoChipCloseIconEndPadding(18f)
            setCloseIconTintResource(R.color.primary_100_6A5DFD)
            tag = keyword.keywordName
        }.also { websosoChip ->
            binding.wcgNovelRatingKeywordSelectedKeyword.addView(websosoChip)
        }
    }

    private fun updateKeywordRecyclerView(uiState: NovelRatingUiState) {
        novelRatingKeywordAdapter.submitList(uiState.keywordsModel.categories)
    }

    private fun setupSearchEditorAction() {
        binding.etRatingKeywordSearch.setOnEditorActionListener { _, _, _ ->
            performSearch(binding.etRatingKeywordSearch.text.toString())
            true
        }
    }

    private fun performSearch(input: String) {
        if (input.isEmpty()) return
        novelRatingViewModel.updateKeywordCategories(input)
        binding.etRatingKeywordSearch.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        novelRatingViewModel.updateKeywordCategories()
        binding.etRatingKeywordSearch.requestFocus()
    }

    override fun onDestroyView() {
        novelRatingViewModel.cancelEditingKeyword()
        super.onDestroyView()
    }
}
