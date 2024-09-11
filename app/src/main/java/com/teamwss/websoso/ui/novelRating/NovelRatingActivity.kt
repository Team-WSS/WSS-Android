package com.teamwss.websoso.ui.novelRating

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.forEach
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.ui.model.CategoriesModel
import com.teamwss.websoso.common.util.getAdaptedSerializableExtra
import com.teamwss.websoso.common.util.showWebsosoSnackBar
import com.teamwss.websoso.common.util.showWebsosoToast
import com.teamwss.websoso.databinding.ActivityNovelRatingBinding
import com.teamwss.websoso.ui.novelDetail.NovelAlertDialogFragment
import com.teamwss.websoso.ui.novelDetail.model.NovelAlertModel
import com.teamwss.websoso.ui.novelRating.model.CharmPoint
import com.teamwss.websoso.ui.novelRating.model.CharmPoint.Companion.toWrappedCharmPoint
import com.teamwss.websoso.ui.novelRating.model.RatingDateModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelRatingActivity :
    BaseActivity<ActivityNovelRatingBinding>(R.layout.activity_novel_rating) {
    private val novelRatingViewModel: NovelRatingViewModel by viewModels()
    private val charmPoints: List<CharmPoint> = CharmPoint.entries.toList()
    private val novelId: Long by lazy { intent.getLongExtra(NOVEL_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.onClick = onNovelRatingButtonClick()
        novelRatingViewModel.updateNovelRating(novelId)
        bindViewModel()
        setupObserver()
        setupCharmPointChips()
        setupWebsosoLoadingLayout()
        setupBackPressCallback()
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
                showCancelNovelRatingAlertDialog()
            }

            override fun onSaveClick() {
                novelRatingViewModel.updateUserNovelRating(novelId, binding.rbNovelRating.rating)
            }

            override fun onCancelClick() {}

            override fun onClearClick() {}
        }

    private fun showCancelNovelRatingAlertDialog() {
        val novelAlertModel = NovelAlertModel(
            title = getString(R.string.novel_rating_cancel_alert_title),
            acceptButtonText = getString(R.string.novel_rating_cancel_alert_accept),
            cancelButtonText = getString(R.string.novel_rating_cancel_alert_cancel),
            onAcceptClick = { finish() },
        )

        NovelAlertDialogFragment
            .newInstance(novelAlertModel)
            .show(supportFragmentManager, NovelAlertDialogFragment.TAG)
    }

    private fun setupObserver() {
        var isInitialUpdate = true

        novelRatingViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> binding.wllNovelRating.setWebsosoLoadingVisibility(true)

                uiState.novelRatingModel.isCharmPointExceed -> {
                    showWebsosoSnackBar(
                        view = binding.root,
                        message = getString(R.string.novel_rating_charm_point_exceed),
                        icon = R.drawable.ic_novel_rating_alert,
                    )
                    novelRatingViewModel.updateCharmPoints(uiState.novelRatingModel.charmPoints.last())
                }

                uiState.isFetchError -> binding.wllNovelRating.setErrorLayoutVisibility(true)

                uiState.isSaveSuccess -> {
                    showWebsosoToast(
                        context = this@NovelRatingActivity,
                        message = getString(R.string.novel_rating_complete),
                        icon = R.drawable.ic_novel_detail_check,
                    )
                    finish()
                }

                uiState.isSaveError -> {
                    showWebsosoSnackBar(
                        view = binding.root,
                        message = getString(R.string.novel_rating_save_error),
                        icon = R.drawable.ic_novel_rating_alert,
                    )
                }

                isInitialUpdate -> {
                    isInitialUpdate = false
                    binding.wllNovelRating.setWebsosoLoadingVisibility(false)
                    updateInitialReadStatus()
                }

                else -> {
                    updateSelectedDate(uiState.novelRatingModel.ratingDateModel)
                    updateCharmPointChips(uiState.novelRatingModel.charmPoints)
                    updateKeywordChips(uiState.keywordsModel.currentSelectedKeywords)
                }
            }
        }
    }

    private fun updateInitialReadStatus() {
        val readStatus = intent.getAdaptedSerializableExtra<ReadStatus>(READ_STATUS)
        if (readStatus != null) novelRatingViewModel.updateReadStatus(readStatus)
    }

    private fun updateSelectedDate(ratingDateModel: RatingDateModel) {
        val (resId, params) = ratingDateModel.formatDisplayDate(ratingDateModel)

        val underlinedText = SpannableString(getString(resId, *params)).apply {
            setSpan(UnderlineSpan(), 0, this.length, 0)
        }

        binding.tvNovelRatingDisplayDate.text = underlinedText
    }

    private fun updateCharmPointChips(previousSelectedCharmPoints: List<CharmPoint>) {
        binding.wcgNovelRatingCharmPoints.forEach { view ->
            val chip = view as WebsosoChip
            chip.isSelected = previousSelectedCharmPoints.contains(
                charmPoints.find { charmPoint -> charmPoint.title == chip.text.toString() },
            )
        }
    }

    private fun updateKeywordChips(selectedKeywords: List<CategoriesModel.CategoryModel.KeywordModel>) {
        val keywordChipGroup = binding.wcgNovelRatingKeywords
        keywordChipGroup.removeAllViews()
        selectedKeywords.forEach { keyword ->
            WebsosoChip(this@NovelRatingActivity)
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
        val existingDialog = supportFragmentManager.findFragmentByTag(NovelRatingDateBottomSheetDialog.TAG)
        if (existingDialog == null) {
            NovelRatingDateBottomSheetDialog().show(supportFragmentManager, NovelRatingDateBottomSheetDialog.TAG)
        }
    }

    private fun showRatingKeywordBottomSheetDialog() {
        val existingDialog = supportFragmentManager.findFragmentByTag(NovelRatingKeywordBottomSheetDialog.TAG)
        if (existingDialog == null) {
            NovelRatingKeywordBottomSheetDialog().show(
                supportFragmentManager,
                NovelRatingKeywordBottomSheetDialog.TAG,
            )
        }
    }

    private fun setupWebsosoLoadingLayout() {
        binding.wllNovelRating.setReloadButtonClickListener {
            novelRatingViewModel.updateNovelRating(novelId)
        }
    }

    private fun setupBackPressCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showCancelNovelRatingAlertDialog()
            }
        })
    }

    companion object {
        private const val NOVEL_ID = "NOVEL_ID"
        private const val READ_STATUS = "READ_STATUS"

        fun getIntent(context: Context, novelId: Long, readStatus: ReadStatus): Intent {
            return Intent(context, NovelRatingActivity::class.java).apply {
                putExtra(NOVEL_ID, novelId)
                putExtra(READ_STATUS, readStatus)
            }
        }
    }
}
