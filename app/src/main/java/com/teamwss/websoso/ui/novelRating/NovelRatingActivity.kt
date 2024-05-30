package com.teamwss.websoso.ui.novelRating

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelRatingBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.common.customView.WebsosoChip
import com.teamwss.websoso.ui.novelRating.model.CharmPoint.Companion.toWrappedCharmPoint

class NovelRatingActivity :
    BindingActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {
    private val viewModel: NovelRatingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataBinding()
        viewModel.getUserNovelInfo() // 테스트용 함수~ 지울예정
        observeDisplayDate()
        setupCharmPointChips()
    }

    private fun setupDataBinding() {
        binding.viewModel = viewModel
        binding.activity = this
        binding.lifecycleOwner = this
    }

    private fun observeDisplayDate() {
        viewModel.uiState.observe(this) {
            val (startDate, endDate) = with(it.novelRatingModel) { currentStartDate to currentEndDate }

            val text = when {
                startDate == null && endDate == null -> getString(R.string.rating_add_date)
                startDate != null && endDate != null -> formatRangeDate(startDate, endDate)
                startDate != null -> formatSingleDate(startDate)
                endDate != null -> formatSingleDate(endDate)
                else -> ""
            }.toUnderlinedSpan()

            binding.tvNovelRatingDisplayDate.text = text
        }
    }

    private fun formatRangeDate(
        startDate: Triple<Int, Int, Int>,
        endDate: Triple<Int, Int, Int>
    ): String =
        getString(
            R.string.rating_display_date_with_tilde,
            startDate.first, startDate.second, startDate.third,
            endDate.first, endDate.second, endDate.third
        )

    private fun formatSingleDate(date: Triple<Int, Int, Int>): String =
        getString(
            R.string.rating_display_date,
            date.first, date.second, date.third
        )

    private fun String.toUnderlinedSpan(): SpannableString =
        SpannableString(this).apply {
            setSpan(UnderlineSpan(), 0, this.length, 0)
        }

    private fun setupCharmPointChips() {
        getString(R.string.rating_charm_point).toWrappedCharmPoint().forEach { charmPoint ->
            WebsosoChip(this@NovelRatingActivity).apply {
                setWebsosoChipText(charmPoint.title)
                setWebsosoChipTextAppearance(R.style.body2)
                setWebsosoChipTextColor(R.color.bg_novel_rating_chip_text_selector)
                setWebsosoChipStrokeColor(R.color.bg_novel_rating_chip_stroke_selector)
                setWebsosoChipBackgroundColor(R.color.bg_novel_rating_chip_background_selector)
                setWebsosoChipPaddingVertical(20f)
                setWebsosoChipPaddingHorizontal(12f)
                setWebsosoChipRadius(30f)
                setOnWebsosoChipClick { handleCharmPointChipClick(this) }
            }.also { websosoChip -> binding.wcgNovelRatingCharmPoints.addChip(websosoChip) }
        }
    }

    private fun handleCharmPointChipClick(websosoChip: WebsosoChip) {
        if (binding.wcgNovelRatingCharmPoints.getSelectedChipCount() > 3) {
            websosoChip.isSelected = false
            Snackbar.make(binding.root, "최대 3개 커스텀 스낵바 추가 예정", Snackbar.LENGTH_SHORT).show()
        }
    }

    fun showDatePickerBottomSheet() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingDateDialog")
        if (existingDialog == null) {
            RatingDateDialog().show(supportFragmentManager, "RatingDateDialog")
        }
    }

    fun navigateToNovelDetail() {
        finish()
    }
}
