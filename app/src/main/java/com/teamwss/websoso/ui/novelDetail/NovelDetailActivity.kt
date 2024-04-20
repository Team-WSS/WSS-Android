package com.teamwss.websoso.ui.novelDetail

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.databinding.MenuNovelDetailPopupBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter
import kotlin.math.abs
import kotlin.math.min

class NovelDetailActivity :
    BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()

    private var _popupBinding: MenuNovelDetailPopupBinding? = null
    private val popupBinding get() = _popupBinding ?: error("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModel()
        setupPopupBinding()
        setupViewPager()
        setupTabLayout()
        setupAppBarOnOffListener()
        setupOnClickNovelDetailItem()
    }

    private fun setupViewModel() {
        binding.novelDetailViewModel = novelDetailViewModel
        binding.lifecycleOwner = this
    }

    private fun setupViewPager() {
        binding.vpNovelDetail.adapter = NovelDetailPagerAdapter(this)
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

    private fun setupAppBarOnOffListener() {
        binding.ablNovelDetail.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            updateImageViewColorByOffset(appBarLayout, verticalOffset)
        }
    }

    private fun updateImageViewColorByOffset(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val totalScrollRange = appBarLayout.totalScrollRange.toFloat()
        val offsetForColorChange = TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET * resources.displayMetrics.density
        val scrollPointForColorChange = totalScrollRange - offsetForColorChange

        val currentOffsetRatio = calculateCurrentOffsetRatio(verticalOffset, scrollPointForColorChange)
        val currentColor = calculateColorBasedOnScrollPosition(currentOffsetRatio)

        updateImageViewsColor(currentColor)
    }

    private fun calculateCurrentOffsetRatio(verticalOffset: Int, scrollPointForColorChange: Float): Float {
        val currentOffset = abs(verticalOffset) / scrollPointForColorChange
        return min(MAX_SCROLL_OFFSET, currentOffset)
    }

    private fun calculateColorBasedOnScrollPosition(offsetRatio: Float): Int {
        val colorWhenScrollAtTop = ContextCompat.getColor(this, R.color.gray_200_AEADB3)
        val colorWhenScrollAtBottom = ContextCompat.getColor(this, R.color.white)

        return if (offsetRatio == MAX_SCROLL_OFFSET) colorWhenScrollAtTop else colorWhenScrollAtBottom
    }

    private fun updateImageViewsColor(color: Int) {
        listOf(binding.ivNovelDetailNavigateBack, binding.ivNovelDetailMenu).forEach { imageView ->
            imageView.drawable?.let { drawable ->
                DrawableCompat.setTint(DrawableCompat.wrap(drawable).mutate(), color)
            }
        }
    }

    private fun setupOnClickNovelDetailItem() {
        binding.novelDetailClickListener = setupNovelDetailClickListener()
    }

    private fun setupNovelDetailClickListener() = object : NovelDetailClickListener {
        override fun onNovelDetailPopupClick(
            view: View,
            tempForReportError: Int,
            userNovelId: Int
        ) {
            showPopupWindow(tempForReportError, userNovelId)
        }
    }

    private fun showPopupWindow(tempForReportError: Int, userNovelId: Int) {
        PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            popupBinding.tempForReportError = tempForReportError
            popupBinding.userNovelId = userNovelId
            this.elevation = 14f.toPx
            showAsDropDown(
                binding.ivNovelDetailMenu,
                POPUP_MARGIN_END.toPx,
                POPUP_MARGIN_TOP.toPx,
                Gravity.END
            )
        }
    }

    private val Float.toPx: Float get() = this * Resources.getSystem().displayMetrics.density

    private val Int.toPx: Int get() = this * Resources.getSystem().displayMetrics.density.toInt()

    private fun setupPopupBinding() {
        _popupBinding = MenuNovelDetailPopupBinding.inflate(layoutInflater)
        popupBinding.lifecycleOwner = this
        popupBinding.novelDetailViewModel = novelDetailViewModel
    }

    companion object {
        private const val INFO_FRAGMENT_PAGE = 0
        private const val FEED_FRAGMENT_PAGE = 1

        private const val TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET = 124
        private const val MAX_SCROLL_OFFSET = 1f

        private const val POPUP_MARGIN_END = -180
        private const val POPUP_MARGIN_TOP = 4
    }
}