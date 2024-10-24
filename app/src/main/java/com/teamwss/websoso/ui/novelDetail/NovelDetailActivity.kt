package com.teamwss.websoso.ui.novelDetail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.ui.model.ResultFrom.CreateFeed
import com.teamwss.websoso.common.ui.model.ResultFrom.NovelDetailBack
import com.teamwss.websoso.common.ui.model.ResultFrom.NovelRating
import com.teamwss.websoso.common.util.getS3ImageUrl
import com.teamwss.websoso.common.util.showWebsosoSnackBar
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.databinding.ItemNovelDetailTooltipBinding
import com.teamwss.websoso.databinding.MenuNovelDetailPopupBinding
import com.teamwss.websoso.ui.common.dialog.LoginRequestDialogFragment
import com.teamwss.websoso.ui.createFeed.CreateFeedActivity
import com.teamwss.websoso.ui.feedDetail.model.EditFeedModel
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter
import com.teamwss.websoso.ui.novelDetail.model.NovelAlertModel
import com.teamwss.websoso.ui.novelInfo.NovelInfoViewModel
import com.teamwss.websoso.ui.novelRating.NovelRatingActivity
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelDetailActivity :
    BaseActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()
    private val novelInfoViewModel by viewModels<NovelInfoViewModel>()

    private var _novelDetailMenuPopupBinding: MenuNovelDetailPopupBinding? = null
    private val novelDetailMenuPopupBinding get() = _novelDetailMenuPopupBinding ?: error("")
    private val novelDetailToolTipBinding: ItemNovelDetailTooltipBinding by lazy {
        ItemNovelDetailTooltipBinding.inflate(layoutInflater)
    }
    private var menuPopupWindow: PopupWindow? = null
    private var tooltipPopupWindow: PopupWindow? = null
    private val novelId by lazy { intent.getLongExtra(NOVEL_ID, 0) }

    private val novelDetailResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            when (result.resultCode) {
                NovelRating.RESULT_OK -> novelInfoViewModel.updateNovelInfoWithDelay(novelId)
                CreateFeed.RESULT_OK -> novelInfoViewModel.updateNovelInfoWithDelay(novelId)
            }
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
        val inquireUrl = getString(R.string.inquire_link)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(inquireUrl))
        startActivity(intent)
    }

    private fun showDeleteUserNovelAlertDialog() {
        val novelAlertModel = NovelAlertModel(
            title = getString(R.string.novel_detail_remove_evaluate_alert_title),
            message = getString(R.string.novel_detail_remove_evaluate_alert_message),
            acceptButtonText = getString(R.string.novel_detail_remove_accept),
            cancelButtonText = getString(R.string.novel_detail_remove_cancel),
            onAcceptClick = { deleteUserNovel() },
        )
        NovelAlertDialogFragment.newInstance(novelAlertModel)
            .show(supportFragmentManager, NovelAlertDialogFragment.TAG)
        menuPopupWindow?.dismiss()
    }

    private fun deleteUserNovel() {
        novelDetailViewModel.deleteUserNovel(novelId)
        novelInfoViewModel.updateNovelInfoWithDelay(novelId)

        binding.tgNovelDetailReadStatus.clearChecked()

        showWebsosoSnackBar(
            view = binding.root,
            message = getString(R.string.novel_detail_remove_result),
            icon = R.drawable.ic_novel_detail_check,
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
                INFO_FRAGMENT_PAGE -> getString(R.string.novel_detail_info)
                FEED_FRAGMENT_PAGE -> getString(R.string.novel_detail_feed)
                else -> throw IllegalArgumentException()
            }
        }.attach()
    }

    private fun setupOnPageChangeCallback() {
        binding.vpNovelDetail.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateNovelFeedWriteButtonVisibility(position)
            }
        })
    }

    private fun updateNovelFeedWriteButtonVisibility(position: Int) {
        binding.ivNovelFeedWrite.visibility = when (position) {
            FEED_FRAGMENT_PAGE -> View.VISIBLE
            else -> View.GONE
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
        binding.ctlNovelDetail.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val layoutParams =
                    binding.viewNovelDetailTooltipFrameBottom.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topMargin = binding.ctlNovelDetail.height
                binding.viewNovelDetailTooltipFrameBottom.layoutParams = layoutParams
                binding.ctlNovelDetail.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun showTooltipWindow() {
        tooltipPopupWindow = PopupWindow(
            novelDetailToolTipBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true,
        ).apply {
            novelDetailToolTipBinding.root.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED,
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

    private fun onNovelDetailButtonClick() = object : NovelDetailClickListener {
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
            NovelDetailCoverDialogFragment.newInstance(novelImageUrl)
                .show(supportFragmentManager, NovelDetailCoverDialogFragment.TAG)
        }

        override fun onNovelFeedWriteClick() {
            if (novelDetailViewModel.novelDetailModel.value?.isLogin == false) {
                showLoginRequestDialog()
                return
            }
            val editFeedModel = EditFeedModel(
                novelId = novelId, novelTitle = binding.tvNovelDetailTitle.text.toString(),
            )
            val intent = CreateFeedActivity.getIntent(this@NovelDetailActivity, editFeedModel)
            novelDetailResultLauncher.launch(intent)
        }

        override fun onNovelInterestClick() {
            if (novelDetailViewModel.novelDetailModel.value?.isLogin == false) {
                showLoginRequestDialog()
                return
            }
            novelDetailViewModel.updateUserInterest(novelId)
        }
    }

    private fun navigateToNovelRating(readStatus: ReadStatus) {
        if (novelDetailViewModel.novelDetailModel.value?.novel?.isNovelNotBlank != true) return
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

        fun getIntent(context: Context, novelId: Long): Intent {
            return Intent(context, NovelDetailActivity::class.java).apply {
                putExtra(NOVEL_ID, novelId)
            }
        }
    }
}
