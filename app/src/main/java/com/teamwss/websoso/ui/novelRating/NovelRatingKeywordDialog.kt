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
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel

class NovelRatingKeywordDialog :
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
        onSearchEditorAction()
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
        novelRatingKeywordAdapter =
            NovelRatingKeywordAdapter(
                onKeywordClick = { keyword, isSelected ->
                    viewModel.updateSelectedKeywords(keyword, isSelected)
                },
            )
        binding.rvRatingKeywordList.apply {
            adapter = novelRatingKeywordAdapter
            itemAnimator = null
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            setupCurrentSelectedChips(uiState.keywordsModel.currentSelectedKeywords)
            novelRatingKeywordAdapter.submitList(uiState.keywordsModel.categories)
        }
    }

    private fun setupCurrentSelectedChips(currentSelectedKeywords: List<NovelRatingKeywordModel>) {
        binding.wcgNovelRatingKeywordSelectedKeyword.removeAllViews()
        currentSelectedKeywords.forEach { keyword ->
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
                    viewModel.updateSelectedKeywords(keyword, isSelected = false)
                }
                setWebsosoChipCloseIconVisibility(true)
                setWebsosoChipCloseIconDrawable(R.drawable.ic_novel_rating_keword_remove)
                setWebsosoChipCloseIconSize(20f)
                setWebsosoChipCloseIconEndPadding(18f)
                setCloseIconTintResource(R.color.primary_100_6A5DFD)
            }.also { websosoChip -> binding.wcgNovelRatingKeywordSelectedKeyword.addChip(websosoChip) }
        }
    }

    private fun onSearchEditorAction() {
        binding.etRatingKeywordSearch.setOnEditorActionListener { _, _, _ ->
            performSearch(binding.etRatingKeywordSearch.text.toString())
            true
        }
    }

    private fun performSearch(input: String?) {
        viewModel.updateKeywordCategories(input.orEmpty())
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
