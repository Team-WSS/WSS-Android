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
import com.teamwss.websoso.ui.novelRating.adapter.NovelRatingKeywordAdapter
import com.teamwss.websoso.ui.novelRating.adapter.NovelRatingKeywordViewHolder
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState

class NovelRatingKeywordBottomSheetDialog :
    BindingBottomSheetDialog<DialogNovelRatingKeywordBinding>(R.layout.dialog_novel_rating_keyword) {
    private val viewModel: NovelRatingViewModel by activityViewModels()
    private lateinit var novelRatingKeywordAdapter: NovelRatingKeywordAdapter

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        bindViewModel()
        setupDialogBehavior()
        setupRecyclerView()
        setupObserver()
        setupSearchEditorAction()
    }

    private fun bindViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    private fun onNovelRatingButtonClick() =
        object : NovelRatingClickListener {
            override fun onDateEditClick() {}

            override fun onKeywordEditClick() {}

            override fun onNavigateBackClick() {}

            override fun onSaveClick() {
                viewModel.saveSelectedKeywords()
                dismiss()
            }

            override fun onCancelClick() {
                viewModel.cancelEditingKeyword()
                dismiss()
            }

            override fun onClearClick() {
                viewModel.clearEditingKeyword()
                dismiss()
            }
        }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun setupRecyclerView() {
        novelRatingKeywordAdapter = NovelRatingKeywordAdapter(
            onKeywordClick = { keyword, isSelected ->
                viewModel.updateSelectedKeywords(keyword, isSelected)
                updateChipSelection(keyword, isSelected)
            },
        )
        binding.rvRatingKeywordList.apply {
            adapter = novelRatingKeywordAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            updateCurrentSelectedChips(uiState.keywordsModel.currentSelectedKeywords)
            updateKeywordRecyclerView(uiState)
        }
    }

    private fun updateCurrentSelectedChips(currentSelectedKeywords: List<NovelRatingKeywordModel>) {
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

    private fun addCurrentSelectedKeywordChip(keyword: NovelRatingKeywordModel) {
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
                viewModel.updateSelectedKeywords(keyword = keyword, isSelected = false)
                updateChipSelection(keyword = keyword, isSelected = false)
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

    private fun updateChipSelection(keyword: NovelRatingKeywordModel, isSelected: Boolean) {
        for (i in 0 until binding.rvRatingKeywordList.childCount) {
            val child = binding.rvRatingKeywordList.getChildAt(i)
            val viewHolder = binding.rvRatingKeywordList.getChildViewHolder(child) as? NovelRatingKeywordViewHolder
            viewHolder?.updateChipSelection(keyword, isSelected)
        }
    }

    private fun updateKeywordRecyclerView(uiState: NovelRatingUiState) {
        if (binding.rvRatingKeywordList.childCount > 0) return
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
        viewModel.updateKeywordCategories(input)
        binding.etRatingKeywordSearch.clearFocus()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateKeywordCategories()
        binding.etRatingKeywordSearch.requestFocus()
    }

    override fun onDestroyView() {
        viewModel.cancelEditingKeyword()
        super.onDestroyView()
    }
}
