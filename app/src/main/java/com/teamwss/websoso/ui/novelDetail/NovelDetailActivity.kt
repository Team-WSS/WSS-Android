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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter

class NovelDetailActivity :
    BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()

    private var spinnerPopupWindow: PopupWindow? = null
    private val novelDetailSpinnerMenuItems: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViewModel()
        setupViewPager()
        setupTabLayout()

        observeMenuItems()
    }

    private fun setupViewModel() {
        binding.novelDetailViewModel = novelDetailViewModel
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
        spinnerPopupWindow = createMenuPopupWindow(setupMenuListView(novelDetailSpinnerMenuItems))
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
            listView,
            POPUP_WIDTH.toDp,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            setPopupWindowProperties()
        }
    }

    private fun showPopupWindowAtBottomOfMenuIcon() {
        spinnerPopupWindow?.showAsDropDown(
            binding.ivNovelDetailMenu,
            POPUP_MARGIN_END.toDp,
            POPUP_MARGIN_TOP.toDp,
            Gravity.END
        )
    }

    private fun PopupWindow.setPopupWindowProperties() {
        isTouchable = true
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@NovelDetailActivity,
                R.drawable.bg_novel_detail_white_radius_12dp
            )
        )
    }

    private val Int.toDp: Int get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    override fun onPause() {
        super.onPause()
        if (spinnerPopupWindow?.isShowing == true) {
            spinnerPopupWindow?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (spinnerPopupWindow?.isShowing == true) {
            spinnerPopupWindow?.dismiss()
        }
    }

    companion object {
        const val INFO_FRAGMENT_PAGE = 0
        const val FEED_FRAGMENT_PAGE = 1

        const val POPUP_WIDTH = 120
        const val POPUP_MARGIN_END = -6
        const val POPUP_MARGIN_TOP = 4
    }
}