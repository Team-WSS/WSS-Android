package com.teamwss.websoso.ui.novelDetail

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityNovelDetailBinding
import com.teamwss.websoso.databinding.MenuNovelDetailPopupBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import com.teamwss.websoso.ui.novelDetail.adapter.NovelDetailPagerAdapter
import com.teamwss.websoso.util.toFloatScaledByPx
import com.teamwss.websoso.util.toIntScaledByPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelDetailActivity : BindingActivity<ActivityNovelDetailBinding>(R.layout.activity_novel_detail) {
    private val novelDetailViewModel by viewModels<NovelDetailViewModel>()

    private var _popupBinding: MenuNovelDetailPopupBinding? = null
    private val popupBinding get() = _popupBinding ?: error("")
    private val dummyNovelId = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupPopupBinding()
        setupViewPager()
        setupTabLayout()
        setupObserver()
        setupWebsosoLoadingLayout()
        novelDetailViewModel.updateNovelDetail(dummyNovelId)
    }

    private fun bindViewModel() {
        binding.novelDetailViewModel = novelDetailViewModel
        binding.lifecycleOwner = this
    }

    private fun setupPopupBinding() {
        _popupBinding = MenuNovelDetailPopupBinding.inflate(layoutInflater)
        popupBinding.novelDetailViewModel = novelDetailViewModel
        popupBinding.lifecycleOwner = this
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

    private fun setupObserver() {
        novelDetailViewModel.novelDetail.observe(this) { novelDetail ->
            when (novelDetail.novel.novelTitle.isNotBlank()) {
                true -> {
                    binding.showPopupWindow = ::showPopupWindow
                    binding.wllNovelDetail.setWebsosoLoadingVisibility(false)
                    binding.llNovelDetailInterest.isSelected = novelDetail.userNovel.isUserNovelInterest
                }

                false -> binding.wllNovelDetail.setWebsosoLoadingVisibility(true)
            }
        }
        novelDetailViewModel.loading.observe(this) { isLoading ->
            if (isLoading) novelDetailViewModel.updateNovelDetail(dummyNovelId)
        }
        novelDetailViewModel.error.observe(this) { isError ->
            binding.wllNovelDetail.setErrorLayoutVisibility(isError)
        }
    }

    private fun setupWebsosoLoadingLayout() {
        binding.wllNovelDetail.setReloadButtonClickListener {
            novelDetailViewModel.updateNovelDetail(dummyNovelId)
            binding.wllNovelDetail.setErrorLayoutVisibility(false)
        }
    }

    private fun showPopupWindow() {
        PopupWindow(
            popupBinding.root,
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

    companion object {
        private const val INFO_FRAGMENT_PAGE = 0
        private const val FEED_FRAGMENT_PAGE = 1

        private const val POPUP_MARGIN_END = -128
        private const val POPUP_MARGIN_TOP = 4
    }
}
