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
import java.lang.Float.min
import kotlin.math.abs

class NovelDetailActivity :
    BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()

    private var _popupBinding: MenuNovelDetailPopupBinding? = null
    private val popupBinding get() = _popupBinding ?: error("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModel()
        initPopupBinding()
        setupViewPager()
        setupTabLayout()
        setupToolbarButton()
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

    private fun setupToolbarButton() {
        binding.ablNovelDetail.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            updateImageViewColor(appBarLayout, verticalOffset)
        }
    }

    private fun updateImageViewColor(
        appBarLayout: AppBarLayout, verticalOffset: Int
    ) {
        val totalScrollRange = appBarLayout.totalScrollRange.toFloat()
        val offsetForColorChange =
            TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET * resources.displayMetrics.density
        val scrollPointForColorChange = totalScrollRange - offsetForColorChange

        val currentOffset = abs(verticalOffset) / scrollPointForColorChange
        val adjustedOffset = min(MAX_SCROLL_OFFSET, currentOffset)

        val colorWhenScrollAtTop = ContextCompat.getColor(this, R.color.gray_200_AEADB3)
        val colorWhenScrollAtBottom = ContextCompat.getColor(this, R.color.white)

        val currentColor =
            if (adjustedOffset == MAX_SCROLL_OFFSET) colorWhenScrollAtTop else colorWhenScrollAtBottom

        listOf(binding.ivNovelDetailNavigateBack, binding.ivNovelDetailMenu).forEach { imageView ->
            imageView.drawable.also { drawable ->
                DrawableCompat.setTint(
                    DrawableCompat.wrap(drawable).mutate(), currentColor
                )
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
            showPopupWindow(view, tempForReportError, userNovelId)
        }
    }

    private fun showPopupWindow(view: View, tempForReportError: Int, userNovelId: Int) {
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
                POPUP_MARGIN_END.toDp,
                POPUP_MARGIN_TOP.toDp,
                Gravity.END
            )
        }
    }

    private val Float.toPx: Float get() = this * Resources.getSystem().displayMetrics.density + 0.5f

    private val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    private fun initPopupBinding() {
        _popupBinding = MenuNovelDetailPopupBinding.inflate(layoutInflater)
        popupBinding.lifecycleOwner = this
        popupBinding.novelDetailViewModel = novelDetailViewModel
    }

    companion object {
        private const val INFO_FRAGMENT_PAGE = 0
        private const val FEED_FRAGMENT_PAGE = 1

        private const val TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET = 124
        private const val MAX_SCROLL_OFFSET = 1f

        private const val POPUP_MARGIN_END = -128
        private const val POPUP_MARGIN_TOP = 4
    }
}