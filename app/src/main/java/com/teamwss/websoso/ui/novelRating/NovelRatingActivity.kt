package com.teamwss.websoso.ui.novelRating

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
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
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelRatingActivity : BindingActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {
    private val novelRatingViewModel: NovelRatingViewModel by viewModels()
    private val charmPoints: List<CharmPoint> = CharmPoint.entries.toList()
    private val novelId: Long by lazy { intent.getLongExtra(NOVEL_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        novelRatingViewModel.updateNovelRating(novelId)
        bindViewModel()
        observeUiState()
        setupCharmPointChips()
        setupWebsosoLoadingLayout()
    }

    private fun bindViewModel() {
        binding.viewModel = novelRatingViewModel
        binding.lifecycleOwner = this
    }

    private fun onNovelRatingButtonClick() =
        object : NovelRatingClickListener {
            override fun onDateEditClick() {
                showDatePickerBottomSheetDialog()
            }

            override fun onKeywordEditClick() {
                showRatingKeywordBottomSheetDialog()
            }

            override fun onNavigateBackClick() {
                finish()
            }

            override fun onSaveClick() {
                novelRatingViewModel.updateNovelRating(novelId, intent.getBooleanExtra(IS_ALREADY_RATED, false))
            }

            override fun onCancelClick() {}

            override fun onClearClick() {}
        }

    private fun observeUiState() {
        var isInitialUpdate = true

        novelRatingViewModel.uiState.observe(this) { uiState ->
            when {
                isInitialUpdate && !uiState.error && !uiState.loading -> {
                    isInitialUpdate = false
                    binding.wllNovelRating.setWebsosoLoadingVisibility(false)
                    updateInitialReadStatus()
                }
                !uiState.error && !uiState.loading -> {
                    updateSelectedDate(uiState.novelRatingModel.ratingDateModel)
                    updateCharmPointChips(uiState.novelRatingModel.charmPoints)
                    updateKeywordChips(uiState.keywordsModel.currentSelectedKeywords)
                }
                uiState.loading -> binding.wllNovelRating.setWebsosoLoadingVisibility(true)
                else -> binding.wllNovelRating.setErrorLayoutVisibility(true)
            }
        }
    }

    private fun updateInitialReadStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val readStatus = intent.getSerializableExtra(READ_STATUS, ReadStatus::class.java)
            if (readStatus != null) {
                novelRatingViewModel.updateReadStatus(readStatus)
            }
        } else {
            val readStatus = intent.getSerializableExtra(READ_STATUS) as? ReadStatus
            if (readStatus != null) {
                novelRatingViewModel.updateReadStatus(readStatus)
            }
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
                        novelRatingViewModel.updateSelectedKeywords(keyword, false)
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
        novelRatingViewModel.updateCharmPoints(charmPoints.find { it == charmPoint } ?: return)
    }

    private fun showDatePickerBottomSheetDialog() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingDateDialog")
        if (existingDialog == null) {
            NovelRatingDateBottomSheetDialog().show(supportFragmentManager, "RatingDateDialog")
        }
    }

    private fun showRatingKeywordBottomSheetDialog() {
        val existingDialog = supportFragmentManager.findFragmentByTag("RatingKeywordDialog")
        if (existingDialog == null) {
            NovelRatingKeywordBottomSheetDialog().show(supportFragmentManager, "RatingKeywordDialog")
        }
    }

    private fun setupWebsosoLoadingLayout() {
        binding.wllNovelRating.setReloadButtonClickListener {
            novelRatingViewModel.updateNovelRating(novelId)
        }
    }

    companion object {
        private const val NOVEL_ID = "NOVEL_ID"
        private const val IS_ALREADY_RATED = "IS_ALREADY_RATED"
        private const val READ_STATUS = "READ_STATUS"

        fun getIntent(context: Context, novelId: Long, isAlreadyRated: Boolean, readStatus: ReadStatus?): Intent {
            return Intent(context, NovelRatingActivity::class.java).apply {
                putExtra(NOVEL_ID, novelId)
                putExtra(IS_ALREADY_RATED, isAlreadyRated)
                putExtra(READ_STATUS, readStatus)
            }
        }
    }
}
