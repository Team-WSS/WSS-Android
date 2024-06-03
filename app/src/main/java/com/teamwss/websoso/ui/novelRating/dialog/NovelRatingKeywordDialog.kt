package com.teamwss.websoso.ui.novelRating.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.DialogNovelRatingKeywordBinding
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelRating.NovelRatingViewModel
import com.teamwss.websoso.ui.novelRating.adapter.NovelRatingKeywordAdapter

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
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogNovelRatingKeywordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBinding()
        setupDialogBehavior()
        setupRecyclerView()
        observeUiState()
        viewModel.getPastSelectedKeywords()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.dialog = this
        binding.lifecycleOwner = this
    }

    private fun setupDialogBehavior() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

        bottomSheet.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    private fun setupRecyclerView() {
        novelRatingKeywordAdapter = NovelRatingKeywordAdapter(
            onKeywordClick = { keyword, isSelected ->
                viewModel.updateSelectedKeywords(keyword, isSelected)
            }
        )
        binding.rvRatingKeywordList.apply {
            adapter = novelRatingKeywordAdapter
            itemAnimator = null
        }
    }

    private fun observeUiState() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            novelRatingKeywordAdapter.submitList(it.keywordModel.categories)
            binding.wcgNovelRatingKeywordSelectedKeyword.removeAllViews()
            it.keywordModel.currentSelectedKeywords.forEach { keyword ->
                WebsosoChip(binding.root.context).apply {
                    setWebsosoChipText(keyword.keywordName)
                    setWebsosoChipTextAppearance(R.style.body2)
                    setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
                    setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
                    setWebsosoChipBackgroundColor(R.color.white)
                    setWebsosoChipPaddingVertical(20f)
                    setWebsosoChipPaddingHorizontal(12f)
                    setWebsosoChipRadius(40f)
                    setOnWebsosoChipClick {
                    }
                    setOnCloseIconClickListener {
                        viewModel.updateSelectedKeywords(keyword, false)
                    }
                    closeIcon = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_novel_rating_keword_remove,
                        null
                    )
                    closeIconSize = 20f
                    closeIconEndPadding = 18f
                    isCloseIconVisible = true
                    setCloseIconTintResource(R.color.primary_100_6A5DFD)
                }.also { websosoChip ->
                    binding.wcgNovelRatingKeywordSelectedKeyword.addChip(websosoChip)
                }
            }
        }
    }

    fun saveKeywordEdit() {
        viewModel.saveSelectedKeywords()
        dismiss()
    }

    fun clearSelectedKeywords() {
        viewModel.clearKeywordEdit()
        dismiss()
    }

    fun cancelKeywordEdit() {
        viewModel.cancelKeywordEdit()
        dismiss()
    }

    override fun onDestroyView() {
        cancelKeywordEdit()
        _binding = null
        super.onDestroyView()
    }
}