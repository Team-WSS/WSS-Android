package com.teamwss.websoso.ui.novelDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.util.toFloatScaledByPx
import com.teamwss.websoso.common.util.toIntScaledByPx
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.databinding.ItemNovelDetailTooltipBinding
import com.teamwss.websoso.databinding.MenuNovelDetailPopupBinding
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter
import com.teamwss.websoso.ui.novelRating.NovelRatingActivity
import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelDetailActivity :
    BaseActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()

    private var _novelDetailMenuPopupBinding: MenuNovelDetailPopupBinding? = null
    private val novelDetailMenuPopupBinding get() = _novelDetailMenuPopupBinding ?: error("")
    private val novelDetailToolTipBinding: ItemNovelDetailTooltipBinding by lazy {
        ItemNovelDetailTooltipBinding.inflate(layoutInflater)
    }
    private var menuPopupWindow: PopupWindow? = null
    private var tooltipPopupWindow: PopupWindow? = null
    private val novelId by lazy { intent.getLongExtra(NOVEL_ID, 1L) } // TODO: 1L -> 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.onClick = onNovelDetailButtonClick()
        bindViewModel()
        setupPopupBinding()
        setupObserver()
        setupWebsosoLoadingLayout()
        setupViewPager()
        novelDetailViewModel.updateNovelDetail(novelId)
    }

    private fun bindViewModel() {
        binding.novelDetailViewModel = novelDetailViewModel
        binding.lifecycleOwner = this
    }

    private fun setupPopupBinding() {
        _novelDetailMenuPopupBinding = MenuNovelDetailPopupBinding.inflate(layoutInflater)
        novelDetailMenuPopupBinding.novelDetailViewModel = novelDetailViewModel
        novelDetailMenuPopupBinding.deleteUserNovel = ::showDeleteUserNovelAlertDialog
        novelDetailMenuPopupBinding.lifecycleOwner = this
    }

    private fun showDeleteUserNovelAlertDialog() {
        NovelAlertDialogFragment.newInstance(
            alertTitle = getString(R.string.novel_detail_remove_evaluate_alert_title),
            alertMessage = getString(R.string.novel_detail_remove_evaluate_alert_message),
            acceptButtonText = getString(R.string.novel_detail_remove_accept),
            cancelButtonText = getString(R.string.novel_detail_remove_cancel),
            onAcceptClick = ::deleteUserNovel,
        ).show(supportFragmentManager, NovelAlertDialogFragment.TAG)
        menuPopupWindow?.dismiss()
    }

    private fun deleteUserNovel() {
        novelDetailViewModel.deleteUserNovel(novelId)
        binding.tgNovelDetailReadStatus.clearChecked()
    }

    private fun setupViewPager() {
        binding.vpNovelDetail.adapter = NovelDetailPagerAdapter(this, novelId)
        setupTabLayout()
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

    private fun setupObserver() {
        novelDetailViewModel.novelDetailModel.observe(this) { novelDetail ->
            when (novelDetail.novel.novelTitle.isNotBlank()) {
                true -> {
                    binding.wllNovelDetail.setWebsosoLoadingVisibility(false)
                    binding.llNovelDetailInterest.isSelected =
                        novelDetail.userNovel.isUserNovelInterest
                    if (novelDetail.isFirstLaunched) setupTooltipWindow()
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
            val yOffset = 6.toIntScaledByPx()

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
            this.elevation = 14f.toFloatScaledByPx()
            showAsDropDown(
                binding.ivNovelDetailMenu,
                POPUP_MARGIN_END.toIntScaledByPx(),
                POPUP_MARGIN_TOP.toIntScaledByPx(),
                Gravity.END,
            )
        }
    }

    private fun onNovelDetailButtonClick() = object : NovelDetailClickListener {
        override fun onNavigateBackClick() {
            finish()
        }

        override fun onShowMenuClick() {
            showPopupWindow()
        }

        override fun onNavigateToNovelRatingClick(readStatus: ReadStatus) {
            navigateToNovelRating(readStatus)
        }
    }

    private fun navigateToNovelRating(readStatus: ReadStatus) {
        val intent = NovelRatingActivity.getIntent(
            context = this,
            novelId = novelId,
            readStatus = readStatus,
        )
        startActivity(intent)
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
