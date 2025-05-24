package com.into.websoso.ui.novelDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View.GONE
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnPreDrawListener
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.into.websoso.R.layout.activity_novel_detail
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.CreateFeed
import com.into.websoso.core.common.ui.model.ResultFrom.NovelDetailBack
import com.into.websoso.core.common.ui.model.ResultFrom.NovelRating
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.showWebsosoSnackBar
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.toIntPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.core.resource.R.drawable.ic_novel_detail_check
import com.into.websoso.core.resource.R.string.inquire_link
import com.into.websoso.core.resource.R.string.novel_detail_feed
import com.into.websoso.core.resource.R.string.novel_detail_feed_write
import com.into.websoso.core.resource.R.string.novel_detail_info
import com.into.websoso.core.resource.R.string.novel_detail_remove_accept
import com.into.websoso.core.resource.R.string.novel_detail_remove_cancel
import com.into.websoso.core.resource.R.string.novel_detail_remove_evaluate_alert_message
import com.into.websoso.core.resource.R.string.novel_detail_remove_evaluate_alert_title
import com.into.websoso.core.resource.R.string.novel_detail_remove_result
import com.into.websoso.databinding.ActivityNovelDetailBinding
import com.into.websoso.databinding.ItemNovelDetailTooltipBinding
import com.into.websoso.databinding.MenuNovelDetailPopupBinding
import com.into.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.into.websoso.ui.createFeed.CreateFeedActivity
import com.into.websoso.ui.feedDetail.model.EditFeedModel
import com.into.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter
import com.into.websoso.ui.novelDetail.model.NovelAlertModel
import com.into.websoso.ui.novelFeed.NovelFeedViewModel
import com.into.websoso.ui.novelInfo.NovelInfoViewModel
import com.into.websoso.ui.novelRating.NovelRatingActivity
import com.into.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NovelDetailActivity : BaseActivity<ActivityNovelDetailBinding>(activity_novel_detail) {
    @Inject
    lateinit var tracker: Tracker

    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()
    private val novelInfoViewModel by viewModels<NovelInfoViewModel>()
    private val novelFeedViewModel by viewModels<NovelFeedViewModel>()
    private var _novelDetailMenuPopupBinding: MenuNovelDetailPopupBinding? = null
    private val novelDetailMenuPopupBinding get() = _novelDetailMenuPopupBinding ?: error("")
    private val novelDetailToolTipBinding: ItemNovelDetailTooltipBinding by lazy {
        ItemNovelDetailTooltipBinding.inflate(layoutInflater)
    }
    private var menuPopupWindow: PopupWindow? = null
    private var tooltipPopupWindow: PopupWindow? = null
    private val novelId by lazy { intent.getLongExtra(NOVEL_ID, 0) }

    private val novelDetailResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(StartActivityForResult()) { result ->
            when (result.resultCode) {
                NovelRating.RESULT_OK -> {
                    novelInfoViewModel.updateNovelInfoWithDelay(novelId)
                    setResult(NovelDetailBack.RESULT_OK)
                }

                CreateFeed.RESULT_OK -> {
                    handleCreateFeedResult()
                }
            }
        }

    private fun handleCreateFeedResult() {
        novelInfoViewModel.updateNovelInfoWithDelay(novelId)
        novelFeedViewModel.updateFeedWithDelay(novelId, true)
        binding.vpNovelDetail.currentItem = FEED_FRAGMENT_PAGE
        showWebsosoSnackBar(
            view = binding.root,
            message = getString(novel_detail_feed_write),
            icon = ic_novel_detail_check,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.onClick = onNovelDetailButtonClick()
        bindViewModel()
        setupPopupBinding()
        setupObserver()
        setupWebsosoLoadingLayout()
        setupViewPager()
        novelDetailViewModel.updateNovelDetail(novelId)
        handleBackPressed()
        tracker.trackEvent("novel_info")
    }

    private fun bindViewModel() {
        binding.novelDetailViewModel = novelDetailViewModel
        binding.lifecycleOwner = this
    }

    private fun setupPopupBinding() {
        _novelDetailMenuPopupBinding = MenuNovelDetailPopupBinding.inflate(layoutInflater)
        novelDetailMenuPopupBinding.novelDetailViewModel = novelDetailViewModel
        novelDetailMenuPopupBinding.reportError = ::navigateToReportError
        novelDetailMenuPopupBinding.deleteUserNovel = ::showDeleteUserNovelAlertDialog
        novelDetailMenuPopupBinding.lifecycleOwner = this
    }

    private fun navigateToReportError() {
        tracker.trackEvent("contact_error")
        val inquireUrl = getString(inquire_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
        startActivity(intent)
    }

    private fun showDeleteUserNovelAlertDialog() {
        val novelAlertModel = NovelAlertModel(
            title = getString(novel_detail_remove_evaluate_alert_title),
            message = getString(novel_detail_remove_evaluate_alert_message),
            acceptButtonText = getString(novel_detail_remove_accept),
            cancelButtonText = getString(novel_detail_remove_cancel),
            onAcceptClick = { deleteUserNovel() },
        )
        NovelAlertDialogFragment
            .newInstance(novelAlertModel)
            .show(supportFragmentManager, NovelAlertDialogFragment.TAG)
        menuPopupWindow?.dismiss()
    }

    private fun deleteUserNovel() {
        tracker.trackEvent("rate_delete")
        novelDetailViewModel.deleteUserNovel(novelId)
        novelInfoViewModel.updateNovelInfoWithDelay(novelId)

        binding.tgNovelDetailReadStatus.clearChecked()

        showWebsosoSnackBar(
            view = binding.root,
            message = getString(novel_detail_remove_result),
            icon = ic_novel_detail_check,
        )
    }

    private fun setupViewPager() {
        binding.vpNovelDetail.adapter = NovelDetailPagerAdapter(this, novelId)
        setupTabLayout()
        setupOnPageChangeCallback()
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tlNovelDetail, binding.vpNovelDetail) { tab, position ->
            tab.text = when (position) {
                INFO_FRAGMENT_PAGE -> getString(novel_detail_info)
                FEED_FRAGMENT_PAGE -> getString(novel_detail_feed)
                else -> throw IllegalArgumentException()
            }
        }.attach()
    }

    private fun setupOnPageChangeCallback() {
        binding.vpNovelDetail.registerOnPageChangeCallback(
            object :
                OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateNovelFeedWriteButtonVisibility(position)
                }
            },
        )
    }

    private fun updateNovelFeedWriteButtonVisibility(position: Int) {
        binding.ivNovelFeedWrite.visibility = when (position) {
            FEED_FRAGMENT_PAGE -> VISIBLE
            else -> GONE
        }
    }

    private fun setupObserver() {
        novelDetailViewModel.novelDetailModel.observe(this) { novelDetail ->
            when (novelDetail.novel.isNovelNotBlank) {
                true -> {
                    binding.wllNovelDetail.setWebsosoLoadingVisibility(false)
                    binding.llNovelDetailInterest.isSelected =
                        novelDetail.userNovel.isUserNovelInterest
                    if (novelDetail.isFirstLaunched) setupTooltipWindow()
                    updateGenreImage(novelDetail.novel.novelGenreImage)
                }

                false -> binding.wllNovelDetail.setWebsosoLoadingVisibility(true)
            }
        }
        novelDetailViewModel.loading.observe(this) { isLoading ->
            if (isLoading) novelDetailViewModel.updateNovelDetail(novelId)
        }
        novelDetailViewModel.error.observe(this) { isError ->
            binding.wllNovelDetail.setErrorLayoutVisibility(isError)
        }
    }

    private fun setupTooltipWindow() {
        binding.btnNovelDetailWatched.isChecked = true
        setupTooltipBottomFramePosition()
        showTooltipWindow()
    }

    private fun setupTooltipBottomFramePosition() {
        binding.ctlNovelDetail.viewTreeObserver.addOnPreDrawListener(
            object :
                OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    binding.ctlNovelDetail.viewTreeObserver.removeOnPreDrawListener(this)

                    val layoutParams = binding.viewNovelDetailTooltipFrameBottom.layoutParams as LayoutParams
                    layoutParams.topMargin = binding.ctlNovelDetail.height
                    binding.viewNovelDetailTooltipFrameBottom.layoutParams = layoutParams

                    return true
                }
            },
        )
    }

    private fun showTooltipWindow() {
        binding.tgNovelDetailReadStatus.post {
            tooltipPopupWindow = PopupWindow(
                novelDetailToolTipBinding.root,
                WRAP_CONTENT,
                WRAP_CONTENT,
                true,
            ).apply {
                novelDetailToolTipBinding.root.measure(
                    UNSPECIFIED,
                    UNSPECIFIED,
                )

                val anchorViewWidth = binding.tgNovelDetailReadStatus.measuredWidth
                val popupWidth = novelDetailToolTipBinding.root.measuredWidth
                val xOffset = (anchorViewWidth - popupWidth) / 2
                val yOffset = 6.toIntPxFromDp()

                showAsDropDown(binding.tgNovelDetailReadStatus, xOffset, yOffset)

                novelDetailToolTipBinding.root.setOnClickListener { dismiss() }

                this.setOnDismissListener {
                    novelDetailViewModel.updateIsFirstLaunched()
                    binding.tgNovelDetailReadStatus.clearChecked()
                }
            }
        }
    }

    private fun setupWebsosoLoadingLayout() {
        binding.wllNovelDetail.setReloadButtonClickListener {
            setupViewPager()
            novelDetailViewModel.updateNovelDetail(novelId)
            binding.wllNovelDetail.setErrorLayoutVisibility(false)
        }
    }

    private fun showPopupWindow() {
        menuPopupWindow = PopupWindow(
            novelDetailMenuPopupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true,
        ).apply {
            this.elevation = 14f.toFloatPxFromDp()
            showAsDropDown(
                binding.ivNovelDetailMenu,
                POPUP_MARGIN_END.toIntPxFromDp(),
                POPUP_MARGIN_TOP.toIntPxFromDp(),
                Gravity.END,
            )
        }
    }

    private fun onNovelDetailButtonClick() =
        object : NovelDetailClickListener {
            override fun onNavigateBackClick() {
                setResult(NovelDetailBack.RESULT_OK)
                finish()
            }

            override fun onShowMenuClick() {
                showPopupWindow()
            }

            override fun onNavigateToNovelRatingClick(readStatus: ReadStatus) {
                if (novelDetailViewModel.novelDetailModel.value?.isLogin == false) {
                    binding.tgNovelDetailReadStatus.clearChecked()
                    showLoginRequestDialog()
                    return
                }
                navigateToNovelRating(readStatus)
            }

            override fun onNovelCoverClick(novelImageUrl: String) {
                NovelDetailCoverDialogFragment
                    .newInstance(novelImageUrl)
                    .show(supportFragmentManager, NovelDetailCoverDialogFragment.TAG)
            }

            override fun onNovelFeedWriteClick() {
                if (novelDetailViewModel.novelDetailModel.value?.isLogin == false) {
                    showLoginRequestDialog()
                    return
                }
                tracker.trackEvent("novel_write_btn")
                val editFeedModel = EditFeedModel(
                    novelId = novelId,
                    novelTitle = binding.tvNovelDetailTitle.text.toString(),
                    feedCategory = novelDetailViewModel.novelDetailModel.value
                        ?.novel
                        ?.getGenres
                        ?: emptyList(),
                    imageUrls = emptyList(),
                )
                val intent = CreateFeedActivity.getIntent(this@NovelDetailActivity, editFeedModel)
                novelDetailResultLauncher.launch(intent)
            }

            override fun onNovelInterestClick() {
                if (novelDetailViewModel.novelDetailModel.value?.isLogin == false) {
                    showLoginRequestDialog()
                    return
                }
                tracker.trackEvent("rate_love")
                novelDetailViewModel.updateUserInterest(novelId)
            }
        }

    private fun navigateToNovelRating(readStatus: ReadStatus) {
        if (novelDetailViewModel.novelDetailModel.value
                ?.novel
                ?.isNovelNotBlank != true
        ) {
            return
        }
        val intent = NovelRatingActivity.getIntent(
            context = this,
            novelId = novelId,
            readStatus = readStatus,
            isInterest = binding.llNovelDetailInterest.isSelected,
        )
        novelDetailResultLauncher.launch(intent)
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            setResult(NovelDetailBack.RESULT_OK)
            finish()
        }
    }

    private fun updateGenreImage(genreImage: String) {
        if (genreImage.isEmpty() || Patterns.WEB_URL.matcher(genreImage).matches()) return
        val updatedGenreImage = binding.root.getS3ImageUrl(genreImage)
        novelDetailViewModel.updateGenreImage(updatedGenreImage)
    }

    private fun showLoginRequestDialog() {
        val dialog = LoginRequestDialogFragment.newInstance()
        dialog.show(supportFragmentManager, LoginRequestDialogFragment.TAG)
    }

    override fun onResume() {
        super.onResume()
        binding.tgNovelDetailReadStatus.clearChecked()
        novelDetailViewModel.updateNovelDetail(novelId)
    }

    companion object {
        private const val INFO_FRAGMENT_PAGE = 0
        private const val FEED_FRAGMENT_PAGE = 1
        private const val NOVEL_ID = "NOVEL_ID"
        private const val POPUP_MARGIN_END = -128
        private const val POPUP_MARGIN_TOP = 4

        fun getIntent(
            context: Context,
            novelId: Long,
        ): Intent =
            Intent(context, NovelDetailActivity::class.java).apply {
                putExtra(NOVEL_ID, novelId)
            }
    }
}
