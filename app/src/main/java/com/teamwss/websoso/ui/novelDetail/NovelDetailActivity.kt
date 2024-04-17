package com.teamwss.websoso.ui.novelDetail

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter
import java.lang.Float.min
import kotlin.math.abs

class NovelDetailActivity :
    BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()

    private var _spinnerPopupWindow: PopupWindow? = null
    private val spinnerPopupWindow get() = _spinnerPopupWindow ?: error("")
    private val novelDetailSpinnerMenuItems: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModel()
        setupViewPager()
        setupTabLayout()
        setupToolbarButton()

        observeMenuItems()
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

    private fun observeMenuItems() {
        novelDetailViewModel.novelDetailSpinnerMenuItems.observe(this) {
            showMenuSpinner()
        }
    }

    private fun showMenuSpinner() {
        initMenuItems()
        initPopupWindow()
        showPopupWindowAtBottomOfMenuIcon()
    }

    private fun initMenuItems() {
        novelDetailSpinnerMenuItems.clear()
        novelDetailViewModel.novelDetailSpinnerMenuItems.value?.let { items ->
            novelDetailSpinnerMenuItems.addAll(items.map {
                getString(
                    it
                )
            })
        }
    }

    private fun initPopupWindow() {
        _spinnerPopupWindow = createMenuPopupWindow(setupMenuListView(novelDetailSpinnerMenuItems))
    }

    private fun setupMenuListView(items: List<String>): ListView {
        return ListView(this).apply {
            adapter =
                ArrayAdapter(this@NovelDetailActivity, R.layout.item_custom_popup_drop_down, items)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                when (novelDetailSpinnerMenuItems[position]) {
                    getString(R.string.novel_detail_report_error) -> {
                        Snackbar.make(binding.root, "오류 제보", Snackbar.LENGTH_SHORT).show()
                    }

                    getString(R.string.novel_detail_remove_evaluate) -> {
                        Snackbar.make(binding.root, "평가 삭제", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun createMenuPopupWindow(listView: ListView): PopupWindow {
        return PopupWindow(
            listView, POPUP_WIDTH.toDp, WindowManager.LayoutParams.WRAP_CONTENT, true
        ).apply {
            setPopupWindowProperties()
        }
    }

    private fun showPopupWindowAtBottomOfMenuIcon() {
        spinnerPopupWindow.showAsDropDown(
            binding.ivNovelDetailMenu, POPUP_MARGIN_END.toDp, POPUP_MARGIN_TOP.toDp, Gravity.END
        )
    }

    private fun PopupWindow.setPopupWindowProperties() {
        isTouchable = true
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@NovelDetailActivity, R.drawable.bg_novel_detail_white_radius_12dp
            )
        )
    }

    private val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    override fun onPause() {
        _spinnerPopupWindow = null
        super.onPause()
    }

    override fun onDestroy() {
        _spinnerPopupWindow = null
        super.onDestroy()
    }

    companion object {
        private const val INFO_FRAGMENT_PAGE = 0
        private const val FEED_FRAGMENT_PAGE = 1

        private const val TOOLBAR_BUTTON_COLOR_CHANGE_OFFSET = 124
        private const val MAX_SCROLL_OFFSET = 1f

        private const val POPUP_WIDTH = 120
        private const val POPUP_MARGIN_END = -6
        private const val POPUP_MARGIN_TOP = 4
    }
}