package com.into.websoso.ui.createFeed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.load
import com.into.websoso.R.color.bg_detail_explore_chip_background_selector
import com.into.websoso.R.color.bg_detail_explore_chip_stroke_selector
import com.into.websoso.R.color.bg_detail_explore_chip_text_selector
import com.into.websoso.R.color.gray_200_AEADB3
import com.into.websoso.R.layout.activity_create_feed
import com.into.websoso.R.style.body2
import com.into.websoso.R.style.body4
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.custom.WebsosoChip
import com.into.websoso.core.common.ui.model.ResultFrom.CreateFeed
import com.into.websoso.core.common.util.DynamicLimitPhotoPicker
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.core.common.util.getAdaptedParcelableExtra
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_blocked_user_snack_bar
import com.into.websoso.core.resource.R.drawable.ic_load_load
import com.into.websoso.core.resource.R.string.create_feed_image_limit
import com.into.websoso.core.resource.R.string.tv_create_feed_characters_count
import com.into.websoso.core.resource.R.string.wset_create_feed_search_novel
import com.into.websoso.databinding.ActivityCreateFeedBinding
import com.into.websoso.ui.createFeed.CreateFeedViewModel.Companion.MAX_IMAGE_COUNT
import com.into.websoso.ui.createFeed.component.CreateFeedImageContainer
import com.into.websoso.ui.createFeed.model.CreatedFeedCategoryModel
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateFeedActivity : BaseActivity<ActivityCreateFeedBinding>(activity_create_feed) {
    @Inject
    lateinit var tracker: Tracker

    private val createFeedViewModel: CreateFeedViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }
    private val imagePickerLauncher: ActivityResultLauncher<DynamicLimitPhotoPicker.Input> = createFeedImagePickerLauncher()

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupCustomScroll()
        onCreateFeedClick()
        bindViewModel()
        setupObservers()
        setupEventCollectors()
        setupCreateFeedImageContainer()
        setupLoadingAnimation()
        createFeedViewModel.categories.setupCategoryChips()
        tracker.trackEvent("write")
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupCustomScroll() {
        binding.etCreateFeedContent.setOnTouchListener { view, event ->
            if (view.hasFocus()) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                    view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
        binding.etCreateFeedContent.movementMethod = ScrollingMovementMethod()
    }

    private fun onCreateFeedClick() {
        onBackPressedDispatcher.addCallback(this) {
            singleEventHandler.throttleFirst {
                val isEmptyCategory = createFeedViewModel.categories.any { it.isSelected }
                val isBlankContent = createFeedViewModel.content.value.isNullOrBlank()
                val isSelectedNovel = createFeedViewModel.selectedNovelTitle.value.isNullOrBlank()

                if (isEmptyCategory || !isBlankContent || !isSelectedNovel) {
                    CreatingFeedDialogFragment
                        .newInstance(event = ::finish)
                        .show(supportFragmentManager, CreatingFeedDialogFragment.TAG)
                    return@throttleFirst
                }
                if (!isFinishing) finish()
            }
        }

        binding.ivCreateFeedRemoveButton.setOnClickListener {
            binding.clCreateFeedNovelInfo.visibility = View.INVISIBLE
            createFeedViewModel.updateSelectedNovelClear()
        }

        binding.tvCreateFeedDoneButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                val editFeedModel = intent.getAdaptedParcelableExtra<EditFeedModel>(FEED)

                when {
                    editFeedModel == null -> createFeedViewModel.createFeed()
                    editFeedModel.feedId == DEFAULT_FEED_ID -> createFeedViewModel.createFeed()
                    editFeedModel.feedCategory.isEmpty() -> createFeedViewModel.createFeed()
                    else -> createFeedViewModel.editFeed(editFeedModel.feedId)
                }

                tracker.trackEvent("write_feed")
            }
        }

        binding.ivCreateFeedBackButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                val isEmptyCategory = createFeedViewModel.categories.any { it.isSelected }
                val isBlankContent = createFeedViewModel.content.value.isNullOrBlank()
                val isSelectedNovel = createFeedViewModel.selectedNovelTitle.value.isNullOrBlank()

                if (isEmptyCategory || !isBlankContent || !isSelectedNovel) {
                    CreatingFeedDialogFragment
                        .newInstance(event = ::finish)
                        .show(supportFragmentManager, CreatingFeedDialogFragment.TAG)
                    return@throttleFirst
                }
                if (!isFinishing) finish()
            }
        }

        binding.ivCreateFeedImage.setOnClickListener {
            singleEventHandler.throttleFirst {
                launchImagePicker()
            }
        }
    }

    private fun launchImagePicker() {
        val currentCount = createFeedViewModel.attachedImages.value.size
        if (currentCount < MAX_IMAGE_COUNT) {
            imagePickerLauncher.launch(
                DynamicLimitPhotoPicker.Input(
                    maxSelectable = MAX_IMAGE_COUNT - currentCount,
                ),
            )
        } else {
            showWebsosoSnackBar(binding.root, getString(create_feed_image_limit, MAX_IMAGE_COUNT), ic_blocked_user_snack_bar)
        }
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
        binding.viewModel = createFeedViewModel
    }

    private fun setupObservers() {
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
                getString(tv_create_feed_characters_count, it.length)
        }
    }

    private fun List<CreatedFeedCategoryModel>.setupCategoryChips() {
        forEach { category ->
            WebsosoChip(this@CreateFeedActivity)
                .apply {
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

    private fun setupEventCollectors() {
        createFeedViewModel.exceedingImageCountEvent.collectWithLifecycle(this) {
            showWebsosoSnackBar(
                binding.root,
                getString(create_feed_image_limit, MAX_IMAGE_COUNT),
                ic_blocked_user_snack_bar,
            )
        }
        createFeedViewModel.updateFeedSuccessEvent.collectWithLifecycle(this) {
            setResult(CreateFeed.RESULT_OK)
            finish()
        }
    }

    private fun setupCreateFeedImageContainer() {
        binding.cvCreateFeedImage.setContent {
            WebsosoTheme {
                CreateFeedImageContainer(createFeedViewModel) { index ->
                    singleEventHandler.throttleFirst {
                        createFeedViewModel.removeImage(index)
                    }
                }
            }
        }
    }

    private fun setupLoadingAnimation() {
        val gifImageLoader = ImageLoader
            .Builder(this)
            .components {
                add(ImageDecoderDecoder.Factory())
            }.build()
        binding.ivCreateFeedLoad.load(ic_load_load, gifImageLoader)
    }

    private fun createFeedImagePickerLauncher() =
        registerForActivityResult(DynamicLimitPhotoPicker()) { uris ->
            if (uris.isNullOrEmpty()) return@registerForActivityResult
            createFeedViewModel.addImages(uris)
        }

    companion object {
        private const val FEED = "FEED"
        private const val DEFAULT_FEED_ID = -1L

        fun getIntent(context: Context): Intent = Intent(context, CreateFeedActivity::class.java)

        fun getIntent(
            context: Context,
            editFeedModel: EditFeedModel,
        ): Intent =
            Intent(context, CreateFeedActivity::class.java).apply {
                putExtra(FEED, editFeedModel)
            }
    }
}
