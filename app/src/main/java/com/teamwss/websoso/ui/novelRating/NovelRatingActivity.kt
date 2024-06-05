package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import com.google.android.material.snackbar.Snackbar
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelRatingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelRating.dialog.NovelRatingKeywordDialog
import com.teamwss.websoso.ui.novelRating.model.CharmPoint.Companion.toWrappedCharmPoint
import com.teamwss.websoso.ui.novelRating.model.NovelRatingUiState

class NovelRatingActivity :
    BindingActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {
    private val viewModel: NovelRatingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()
        viewModel.getDummy()
        observeUiState()
        setupCharmPointChips()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
    }

    private fun observeUiState() {
        viewModel.uiState.observe(this) { uiState ->
            updateSelectedDateDisplay(uiState)
            updateKeywordChips(uiState)
        }
    }

    private fun updateSelectedDateDisplay(it: NovelRatingUiState) {
        val (startDate, endDate) = with(it.novelRatingModel.ratingDateModel) { currentStartDate to currentEndDate }

        val underLinedText = SpannableString(
            when {
                startDate == null && endDate == null -> getString(R.string.novel_rating_add_date)
                startDate != null && endDate != null -> formatRangeDateText(startDate, endDate)
                startDate != null -> formatSingleDateText(startDate)
                endDate != null -> formatSingleDateText(endDate)
                else -> ""
            }
        ).apply { setSpan(UnderlineSpan(), 0, this.length, 0) }

        binding.tvNovelRatingDisplayDate.text = underLinedText
    }

    private fun formatRangeDateText(
        startDate: Triple<Int, Int, Int>,
        endDate: Triple<Int, Int, Int>
    ): String =
        getString(
            R.string.novel_rating_display_date_with_tilde,
            startDate.first, startDate.second, startDate.third,
            endDate.first, endDate.second, endDate.third
        )

    private fun formatSingleDateText(date: Triple<Int, Int, Int>): String =
        getString(
            R.string.novel_rating_display_date,
            date.first, date.second, date.third
        )

    private fun updateKeywordChips(uiState: NovelRatingUiState) {
        val pastSelectedKeywords = uiState.keywordModel.pastSelectedKeywords
        val keywordChipGroup = binding.wcgNovelRatingKeywords
        keywordChipGroup.removeAllViews()
        pastSelectedKeywords.forEach { keyword ->
            WebsosoChip(binding.root.context).apply {
                setWebsosoChipText(keyword.keywordName)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.primary_100_6A5DFD)
                setWebsosoChipStrokeColor(R.color.primary_100_6A5DFD)
                setWebsosoChipBackgroundColor(R.color.primary_50_F1EFFF)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(40f)
                setOnWebsosoChipClick {
                }
                setOnCloseIconClickListener {
                    viewModel.updatePastSelectedKeywords(keyword)
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
            }.also { websosoChip -> keywordChipGroup.addChip(websosoChip) }
        }
    }

    private fun setupCharmPointChips() {
        getString(R.string.novel_rating_charm_point).toWrappedCharmPoint().forEach { charmPoint ->
            WebsosoChip(this@NovelRatingActivity).apply {
                setWebsosoChipText(charmPoint.title)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_novel_rating_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_novel_rating_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_novel_rating_chip_background_selector)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(40f)
                setOnWebsosoChipClick { handleCharmPointChipClick(this) }
            }.also { websosoChip -> binding.wcgNovelRatingCharmPoints.addChip(websosoChip) }
        }
    }

    private fun handleCharmPointChipClick(websosoChip: WebsosoChip) {
        var count = 0
        binding.wcgNovelRatingCharmPoints.forEach {
            if (it.isSelected) count++
        }
        if (count > 3) {
            websosoChip.isSelected = false
            Snackbar.make(binding.root, "최대 3개 커스텀 스낵바 추가 예정", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun showDatePickerBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingDateDialog")
        if (existingDialog == null) {
            NovelRatingDateDialog().show(supportFragmentManager, "RatingDateDialog")
        }
    }

    fun showRatingKeywordBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingKeywordDialog")
        if (existingDialog == null) {
            NovelRatingKeywordDialog().show(supportFragmentManager, "RatingKeywordDialog")
        }
    }

    fun navigateToNovelDetail() {
        finish()
    }
}
