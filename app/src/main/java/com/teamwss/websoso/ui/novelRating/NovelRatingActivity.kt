package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.viewModels
import androidx.core.view.forEach
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelRatingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelRating.model.CharmPoint
import com.teamwss.websoso.ui.novelRating.model.CharmPoint.Companion.toWrappedCharmPoint
import com.teamwss.websoso.ui.novelRating.model.NovelRatingKeywordModel
import com.teamwss.websoso.ui.novelRating.model.RatingDateModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelRatingActivity : BindingActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {
    private val viewModel: NovelRatingViewModel by viewModels()
    private val charmPoints: List<CharmPoint> = CharmPoint.entries.toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        viewModel.updateNovelRating(1)
        bindViewModel()
        observeUiState()
        setupCharmPointChips()
    }

    private fun bindViewModel() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun onNovelRatingButtonClick() =
        object : NovelRatingClickListener {
            override fun onDateEditClick() {
                showDatePickerBottomSheet()
            }

            override fun onKeywordEditClick() {
                showRatingKeywordBottomSheet()
            }

            override fun onNavigateBackClick() {
                finish()
            }

            override fun onSaveClick() {}

            override fun onCancelClick() {}

            override fun onClearClick() {}
        }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { uiState ->
            updateSelectedDate(uiState.novelRatingModel.ratingDateModel)
            updateCharmPointChips(uiState.novelRatingModel.charmPoints)
            updateKeywordChips(uiState.keywordsModel.currentSelectedKeywords)
        }
    }

    private fun updateSelectedDate(ratingDateModel: RatingDateModel) {
        val (resId, params) = ratingDateModel.formatDisplayDate(ratingDateModel)

        val underlinedText =
            SpannableString(getString(resId, *params)).apply {
                setSpan(UnderlineSpan(), 0, this.length, 0)
            }

        binding.tvNovelRatingDisplayDate.text = underlinedText
    }

    private fun updateCharmPointChips(previousSelectedCharmPoints: List<CharmPoint>) {
        binding.wcgNovelRatingCharmPoints.forEach { view ->
            val chip = view as WebsosoChip
            chip.isSelected =
                previousSelectedCharmPoints.contains(
                    charmPoints.find { charmPoint -> charmPoint.title == chip.text.toString() },
                )
        }
    }

    private fun updateKeywordChips(previousSelectedKeywords: List<NovelRatingKeywordModel>) {
        val keywordChipGroup = binding.wcgNovelRatingKeywords
        keywordChipGroup.removeAllViews()
        previousSelectedKeywords.forEach { keyword ->
            WebsosoChip(binding.root.context)
                .apply {
                    setWebsosoChipText(keyword.keywordName)
                    setWebsosoChipTextAppearance(R.style.body2)
                    setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
                    setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
                    setWebsosoChipBackgroundColor(R.color.primary_50_F1EFFF)
                    setWebsosoChipPaddingVertical(20f)
                    setWebsosoChipPaddingHorizontal(12f)
                    setWebsosoChipRadius(40f)
                    setOnCloseIconClickListener {
                        viewModel.updateSelectedKeywords(keyword, false)
                    }
                    setWebsosoChipCloseIconVisibility(true)
                    setWebsosoChipCloseIconDrawable(R.drawable.ic_novel_rating_keword_remove)
                    setWebsosoChipCloseIconSize(20f)
                    setWebsosoChipCloseIconEndPadding(18f)
                    setCloseIconTintResource(R.color.primary_100_6A5DFD)
                }.also { websosoChip -> keywordChipGroup.addChip(websosoChip) }
        }
    }

    private fun setupCharmPointChips() {
        getString(R.string.novel_rating_charm_points).toWrappedCharmPoint().forEach { charmPoint ->
            WebsosoChip(this@NovelRatingActivity)
                .apply {
                    setWebsosoChipText(charmPoint.title)
                    setWebsosoChipTextAppearance(R.style.body2)
                    setWebsosoChipTextColor(R.color.bg_novel_rating_chip_text_selector)
                    setWebsosoChipStrokeColor(R.color.bg_novel_rating_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(R.color.bg_novel_rating_chip_background_selector)
                    setWebsosoChipPaddingVertical(20f)
                    setWebsosoChipPaddingHorizontal(12f)
                    setWebsosoChipRadius(40f)
                    setOnWebsosoChipClick { handleCharmPointClick(charmPoint) }
                }.also { websosoChip -> binding.wcgNovelRatingCharmPoints.addChip(websosoChip) }
        }
    }

    private fun handleCharmPointClick(charmPoint: CharmPoint) {
        viewModel.updateCharmPoints(charmPoints.find { it == charmPoint } ?: return)
    }

    private fun showDatePickerBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingDateDialog")
        if (existingDialog == null) {
            NovelRatingDateBottomSheetDialog().show(supportFragmentManager, "RatingDateDialog")
        }
    }

    private fun showRatingKeywordBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingKeywordDialog")
        if (existingDialog == null) {
            NovelRatingKeywordBottomSheetDialog().show(supportFragmentManager, "RatingKeywordDialog")
        }
    }
}
