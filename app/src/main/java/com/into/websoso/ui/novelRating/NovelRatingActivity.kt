package com.into.websoso.ui.novelRating

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.forEach
import com.into.websoso.R.color.bg_novel_rating_chip_background_selector
import com.into.websoso.R.color.bg_novel_rating_chip_stroke_selector
import com.into.websoso.R.color.bg_novel_rating_chip_text_selector
import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.primary_50_F1EFFF
import com.into.websoso.R.drawable.bg_novel_detail_primary_100_radius_8dp
import com.into.websoso.R.layout.activity_novel_rating
import com.into.websoso.R.style.body2
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.ui.model.CategoriesModel.CategoryModel.KeywordModel
import com.into.websoso.core.common.ui.model.ResultFrom.NovelRating
import com.into.websoso.core.common.util.getAdaptedSerializableExtra
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.common.util.showWebsosoToast
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.ic_novel_detail_check
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_alert
import com.into.websoso.core.resource.R.drawable.ic_novel_rating_keword_remove
import com.into.websoso.core.resource.R.string.novel_rating_cancel_alert_accept
import com.into.websoso.core.resource.R.string.novel_rating_cancel_alert_cancel
import com.into.websoso.core.resource.R.string.novel_rating_cancel_alert_title
import com.into.websoso.core.resource.R.string.novel_rating_charm_point_exceed
import com.into.websoso.core.resource.R.string.novel_rating_charm_points
import com.into.websoso.core.resource.R.string.novel_rating_complete
import com.into.websoso.core.resource.R.string.novel_rating_save_error
import com.into.websoso.databinding.ActivityNovelRatingBinding
import com.into.websoso.ui.novelDetail.NovelAlertDialogFragment
import com.into.websoso.ui.novelDetail.model.NovelAlertModel
import com.into.websoso.ui.novelDetail.model.NovelDetailModel
import com.into.websoso.ui.novelRating.model.CharmPoint
import com.into.websoso.ui.novelRating.model.CharmPoint.Companion.toWrappedCharmPoint
import com.into.websoso.ui.novelRating.model.NovelRatingUiState
import com.into.websoso.ui.novelRating.model.RatingDateModel
import com.into.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NovelRatingActivity : BaseActivity<ActivityNovelRatingBinding>(activity_novel_rating) {
    @Inject
    lateinit var tracker: Tracker

    private val novelRatingViewModel: NovelRatingViewModel by viewModels()
    private val charmPoints: List<CharmPoint> = CharmPoint.entries.toList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView()
        setupNovelRating()
        setupObserver()
        setupCharmPointChips()
        setupWebsosoLoadingLayout()
        setupBackPressCallback()
        tracker.trackEvent("rate")
    }

    private fun bindView() {
        binding.viewModel = novelRatingViewModel
        binding.lifecycleOwner = this
        binding.onClick = onNovelRatingButtonClick()
    }

    private fun setupNovelRating() {
        val isInterest = intent.getBooleanExtra(IS_INTEREST, false)
        novelRatingViewModel.updateNovelRating(isInterest)
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
                tracker.trackEvent("rate_novel")
                novelRatingViewModel.updateUserNovelRating(binding.rbNovelRating.rating)
            }

            override fun onCancelClick() {}

            override fun onClearClick() {}

            override fun onReportKeywordClick() {}
        }

    private fun showCancelNovelRatingAlertDialog() {
        val novelAlertModel = NovelAlertModel(
            title = getString(novel_rating_cancel_alert_title),
            acceptButtonText = getString(novel_rating_cancel_alert_cancel),
            cancelButtonText = getString(novel_rating_cancel_alert_accept),
            acceptButtonColor = bg_novel_detail_primary_100_radius_8dp,
            onCancelClick = { finish() },
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

                uiState.novelRatingModel.isCharmPointExceed -> handleCharmPointError(uiState)

                uiState.isFetchError -> binding.wllNovelRating.setErrorLayoutVisibility(true)

                uiState.isSaveSuccess -> handleRatingSuccess()

                uiState.isSaveError -> handleRatingError()

                isInitialUpdate -> {
                    isInitialUpdate = false
                    initView()
                }

                else -> updateView(uiState)
            }
        }
    }

    private fun handleCharmPointError(uiState: NovelRatingUiState) {
        showWebsosoSnackBar(
            view = binding.root,
            message = getString(novel_rating_charm_point_exceed),
            icon = ic_novel_rating_alert,
        )
        novelRatingViewModel.updateCharmPoints(uiState.novelRatingModel.charmPoints.last())
    }

    private fun handleRatingSuccess() {
        showWebsosoToast(
            context = this@NovelRatingActivity,
            message = getString(novel_rating_complete),
            icon = ic_novel_detail_check,
        )

        setResult(NovelRating.RESULT_OK)
        finish()
    }

    private fun handleRatingError() {
        showWebsosoSnackBar(
            view = binding.root,
            message = getString(novel_rating_save_error),
            icon = ic_novel_rating_alert,
        )
    }

    private fun initView() {
        binding.wllNovelRating.setWebsosoLoadingVisibility(false)
        updateInitialReadStatus()
    }

    private fun updateView(uiState: NovelRatingUiState) {
        updateSelectedDate(uiState.novelRatingModel.ratingDateModel)
        updateCharmPointChips(uiState.novelRatingModel.charmPoints)
        updateKeywordChips(uiState.keywordsModel.currentSelectedKeywords)
    }

    private fun updateInitialReadStatus() {
        val readStatus = intent.getAdaptedSerializableExtra<ReadStatus>(READ_STATUS)
        readStatus.let {
            novelRatingViewModel.updateReadStatus(it ?: return)
        }
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

    private fun updateKeywordChips(selectedKeywords: Set<KeywordModel>) {
        val keywordChipGroup = binding.wcgNovelRatingKeywords
        keywordChipGroup.removeAllViews()
        selectedKeywords.forEach { keyword ->
            WebsosoChip(this@NovelRatingActivity)
                .apply {
                    setWebsosoChipText(keyword.keywordName)
                    setWebsosoChipTextAppearance(body2)
                    setWebsosoChipTextColor(primary_100_6A5DFD)
                    setWebsosoChipStrokeColor(primary_100_6A5DFD)
                    setWebsosoChipBackgroundColor(primary_50_F1EFFF)
                    setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                    setWebsosoChipPaddingHorizontal(6f.toFloatPxFromDp())
                    setWebsosoChipRadius(20f.toFloatPxFromDp())
                    setOnCloseIconClickListener {
                        novelRatingViewModel.updateSelectedKeywords(keyword, false)
                        novelRatingViewModel.saveSelectedKeywords()
                    }
                    setWebsosoChipCloseIconVisibility(true)
                    setWebsosoChipCloseIconDrawable(ic_novel_rating_keword_remove)
                    setWebsosoChipCloseIconSize(10f.toFloatPxFromDp())
                    setWebsosoChipCloseIconEndPadding(12f.toFloatPxFromDp())
                    setCloseIconTintResource(primary_100_6A5DFD)
                }.also { websosoChip -> keywordChipGroup.addChip(websosoChip) }
        }
    }

    private fun setupCharmPointChips() {
        getString(novel_rating_charm_points).toWrappedCharmPoint().forEach { charmPoint ->
            WebsosoChip(this@NovelRatingActivity)
                .apply {
                    setWebsosoChipText(charmPoint.title)
                    setWebsosoChipTextAppearance(body2)
                    setWebsosoChipTextColor(bg_novel_rating_chip_text_selector)
                    setWebsosoChipStrokeColor(bg_novel_rating_chip_stroke_selector)
                    setWebsosoChipBackgroundColor(bg_novel_rating_chip_background_selector)
                    setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                    setWebsosoChipPaddingHorizontal(6f.toFloatPxFromDp())
                    setWebsosoChipRadius(20f.toFloatPxFromDp())
                    setOnWebsosoChipClick { handleCharmPointClick(charmPoint) }
                }.also { websosoChip -> binding.wcgNovelRatingCharmPoints.addChip(websosoChip) }
        }
    }

    private fun handleCharmPointClick(charmPoint: CharmPoint) {
        novelRatingViewModel.updateCharmPoints(charmPoints.find { it == charmPoint } ?: return)
    }

    private fun showDatePickerBottomSheetDialog() {
        val existingDialog =
            supportFragmentManager.findFragmentByTag(NovelRatingDateBottomSheetDialog.TAG)
        if (existingDialog == null) {
            NovelRatingDateBottomSheetDialog().show(
                supportFragmentManager,
                NovelRatingDateBottomSheetDialog.TAG,
            )
        }
    }

    private fun showRatingKeywordBottomSheetDialog() {
        val existingDialog =
            supportFragmentManager.findFragmentByTag(NovelRatingKeywordBottomSheetDialog.TAG)
        if (existingDialog == null) {
            NovelRatingKeywordBottomSheetDialog().show(
                supportFragmentManager,
                NovelRatingKeywordBottomSheetDialog.TAG,
            )
        }
    }

    private fun setupWebsosoLoadingLayout() {
        binding.wllNovelRating.setReloadButtonClickListener {
            val isInterest = intent.getBooleanExtra(IS_INTEREST, false)
            novelRatingViewModel.updateNovelRating(isInterest)
        }
    }

    private fun setupBackPressCallback() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showCancelNovelRatingAlertDialog()
                }
            },
        )
    }

    companion object {
        private const val NOVEL = "NOVEL"
        private const val FEEDS = "FEEDS"
        private const val READ_STATUS = "READ_STATUS"
        private const val IS_INTEREST = "IS_INTEREST"

        fun getIntent(
            context: Context,
            novel: NovelDetailModel?,
            feeds: List<String>,
            readStatus: ReadStatus,
            isInterest: Boolean,
        ): Intent =
            Intent(context, NovelRatingActivity::class.java).apply {
                putExtra(READ_STATUS, readStatus)
                putExtra(IS_INTEREST, isInterest)
                putExtra(FEEDS, ArrayList(feeds))
                putExtra(NOVEL, novel)
            }
    }
}
