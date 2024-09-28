package com.teamwss.websoso.ui.createFeed

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.R.color.bg_detail_explore_chip_background_selector
import com.teamwss.websoso.R.color.bg_detail_explore_chip_stroke_selector
import com.teamwss.websoso.R.color.bg_detail_explore_chip_text_selector
import com.teamwss.websoso.R.color.gray_200_AEADB3
import com.teamwss.websoso.R.layout.activity_create_feed
import com.teamwss.websoso.R.string.wset_create_feed_search_novel
import com.teamwss.websoso.R.style.body2
import com.teamwss.websoso.R.style.body4
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.custom.WebsosoChip
import com.teamwss.websoso.common.ui.model.ResultFrom.CreateFeed
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.getAdaptedParcelableExtra
import com.teamwss.websoso.common.util.hideKeyboard
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.databinding.ActivityCreateFeedBinding
import com.teamwss.websoso.ui.createFeed.model.CreatedFeedCategoryModel
import com.teamwss.websoso.ui.feedDetail.model.EditFeedModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFeedActivity : BaseActivity<ActivityCreateFeedBinding>(activity_create_feed) {
    private val createFeedViewModel: CreateFeedViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        onCreateFeedClick()
        bindViewModel()
        setupObserver()
        createFeedViewModel.categories.setupCategoryChips()
    }

    private fun setupView() {
        binding.wsetCreateFeedSearchNovel.apply {
            setWebsosoFocusableInTouchMode(false)
            setWebsosoSearchHint(getString(wset_create_feed_search_novel))
            setWebsosoSearchHintTextColor(gray_200_AEADB3)
            setWebsosoSearchTextAppearance(body4)
            setWebsosoOnClickListener {
                singleEventHandler.throttleFirst { showSearchNovelDialog() }
            }
        }
    }

    private fun showSearchNovelDialog() {
        CreateFeedSearchNovelBottomSheetDialog.apply {
            newInstance().show(supportFragmentManager, CREATE_FEED_SEARCH_NOVEL_TAG)
        }
    }

    private fun onCreateFeedClick() {
        onBackPressedDispatcher.addCallback(this) {
            singleEventHandler.throttleFirst {
                val isEmptyCategory = createFeedViewModel.categories.any { it.isSelected }
                val isBlankContent = createFeedViewModel.content.value.isNullOrBlank()
                val isSelectedNovel = createFeedViewModel.selectedNovelTitle.value.isNullOrBlank()

                if (isEmptyCategory || !isBlankContent || !isSelectedNovel) {
                    CreatingFeedDialogFragment.newInstance(event = ::finish)
                        .show(supportFragmentManager, CreatingFeedDialogFragment.TAG)
                    return@throttleFirst
                }
                if (!isFinishing) finish()
            }
        }

        binding.root.setOnClickListener { it.hideKeyboard() }

        binding.ivCreateFeedRemoveButton.setOnClickListener {
            binding.clCreateFeedNovelInfo.visibility = View.INVISIBLE
            createFeedViewModel.updateSelectedNovelClear()
        }

        binding.tvCreateFeedDoneButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                val editFeedModel = intent.getAdaptedParcelableExtra<EditFeedModel>(FEED)

                when {
                    editFeedModel == null -> createFeedViewModel.createFeed()
                    editFeedModel.feedCategory.isEmpty() -> createFeedViewModel.createFeed()
                    else -> createFeedViewModel.editFeed(editFeedModel.feedId)
                }

                setResult(CreateFeed.RESULT_OK)
                if (!isFinishing) finish()
            }
        }

        binding.ivCreateFeedBackButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                val isEmptyCategory = createFeedViewModel.categories.any { it.isSelected }
                val isBlankContent = createFeedViewModel.content.value.isNullOrBlank()
                val isSelectedNovel = createFeedViewModel.selectedNovelTitle.value.isNullOrBlank()

                if (isEmptyCategory || !isBlankContent || !isSelectedNovel) {
                    CreatingFeedDialogFragment.newInstance(event = ::finish)
                        .show(supportFragmentManager, CreatingFeedDialogFragment.TAG)
                    return@throttleFirst
                }
                if (!isFinishing) finish()
            }
        }
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = createFeedViewModel
    }

    private fun setupObserver() {
        createFeedViewModel.isActivated.observe(this) { isSelected ->
            binding.tvCreateFeedDoneButton.isSelected = isSelected
            binding.tvCreateFeedDoneButton.isEnabled = isSelected
        }
        createFeedViewModel.selectedNovelTitle.observe(this) { novelTitle ->
            if (novelTitle.isNullOrBlank().not()) {
                binding.clCreateFeedNovelInfo.visibility = View.VISIBLE
                binding.tvCreateFeedNovelName.text = novelTitle
            }
        }
        createFeedViewModel.content.observe(this) {
            binding.tvCreateFeedCharactersCount.text =
                getString(R.string.tv_create_feed_characters_count, it.length)
        }
    }

    private fun List<CreatedFeedCategoryModel>.setupCategoryChips() {
        forEach { category ->
            WebsosoChip(this@CreateFeedActivity).apply {
                setWebsosoChipText(category.category.krTitle)
                setWebsosoChipTextAppearance(body2)
                setWebsosoChipTextColor(bg_detail_explore_chip_text_selector)
                setWebsosoChipStrokeColor(bg_detail_explore_chip_stroke_selector)
                setWebsosoChipBackgroundColor(bg_detail_explore_chip_background_selector)
                setWebsosoChipPaddingVertical(12f.toFloatPxFromDp())
                setWebsosoChipPaddingHorizontal(6.7f.toFloatPxFromDp())
                setWebsosoChipSelected(category.isSelected)
                setWebsosoChipRadius(20f.toFloatPxFromDp())
                setOnWebsosoChipClick { createFeedViewModel.updateSelectedCategory(category.category.enTitle) }
            }.also { websosoChip -> binding.wcgDetailExploreInfoGenre.addChip(websosoChip) }
        }
    }

    companion object {
        private const val FEED = "FEED"

        fun getIntent(context: Context): Intent = Intent(context, CreateFeedActivity::class.java)

        fun getIntent(context: Context, editFeedModel: EditFeedModel): Intent =
            Intent(context, CreateFeedActivity::class.java).apply {
                putExtra(FEED, editFeedModel)
            }
    }
}
