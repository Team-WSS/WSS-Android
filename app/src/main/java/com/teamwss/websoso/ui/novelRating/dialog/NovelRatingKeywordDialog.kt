package com.teamwss.websoso.ui.novelRating.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelRating.NovelRatingViewModel
import com.teamwss.websoso.ui.novelRating.adapter.NovelRatingKeywordAdapter
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState

class NovelRatingKeywordDialog : BottomSheetDialogFragment() {
    private var _binding: DialogNovelRatingKeywordBinding? = null
    private val binding: DialogNovelRatingKeywordBinding get() = requireNotNull(_binding)
    private val viewModel: NovelRatingViewModel by activityViewModels()
    private lateinit var novelRatingKeywordAdapter: NovelRatingKeywordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.WebsosoBottomSheetTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogNovelRatingKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBinding()
        setupDialogBehavior()
        setupRecyclerView()
        observeUiState()
        viewModel.initCurrentSelectedKeywords()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.dialog = this
        binding.lifecycleOwner = this
    }

    private fun setupDialogBehavior() {
        (dialog as BottomSheetDialog).behavior.state = BottomSheetBehavior.STATE_EXPANDED
        (dialog as BottomSheetDialog).behavior.skipCollapsed = true
    }

    private fun setupRecyclerView() {
        novelRatingKeywordAdapter =
            NovelRatingKeywordAdapter(
                onKeywordClick = { keyword, isSelected ->
                    viewModel.updateCurrentSelectedKeywords(keyword, isSelected)
                },
            )
        binding.rvRatingKeywordList.apply {
            adapter = novelRatingKeywordAdapter
            itemAnimator = null
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            setupCurrentSelectedChips(it)
            novelRatingKeywordAdapter.submitList(it.ratingKeywordModel.categories)
        }
    }

    private fun setupCurrentSelectedChips(uiState: NovelRatingUiState) {
        val currentSelectedKeywords = uiState.ratingKeywordModel.currentSelectedKeywords
        val keywordChipGroup = binding.wcgNovelRatingKeywordSelectedKeyword
        keywordChipGroup.removeAllViews()
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
                    viewModel.updateCurrentSelectedKeywords(keyword, isSelected = false)
                }
                closeIcon =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_novel_rating_keword_remove,
                        null,
                    )
                closeIconSize = 20f
                closeIconEndPadding = 18f
                isCloseIconVisible = true
                setCloseIconTintResource(R.color.primary_100_6A5DFD)
            }.also { websosoChip -> keywordChipGroup.addChip(websosoChip) }
        }
    }

    fun saveKeywordEdit() {
        viewModel.saveSelectedKeywords()
        dismiss()
    }

    fun clearSelectedKeywords() {
        viewModel.clearEditingKeyword()
        dismiss()
    }

    fun cancelKeywordEdit() {
        viewModel.cancelEditingKeyword()
        dismiss()
    }

    override fun onDestroyView() {
        cancelKeywordEdit()
        _binding = null
        super.onDestroyView()
    }
}
