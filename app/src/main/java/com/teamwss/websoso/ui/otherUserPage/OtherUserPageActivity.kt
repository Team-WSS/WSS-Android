package com.teamwss.websoso.ui.otherUserPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayoutMediator
import com.teamwss.websoso.R
import com.teamwss.websoso.common.ui.base.BaseActivity
import com.teamwss.websoso.common.util.SingleEventHandler
import com.teamwss.websoso.common.util.toFloatPxFromDp
import com.teamwss.websoso.common.util.toIntPxFromDp
import com.teamwss.websoso.databinding.ActivityOtherUserPageBinding
import com.teamwss.websoso.databinding.MenuOtherUserPagePopupBinding
import com.teamwss.websoso.ui.otherUserPage.adapter.OtherUserPageViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherUserPageActivity :
    BaseActivity<ActivityOtherUserPageBinding>(R.layout.activity_other_user_page) {
    private var _popupBinding: MenuOtherUserPagePopupBinding? = null
    private val popupBinding: MenuOtherUserPagePopupBinding
        get() = _popupBinding ?: error("OtherUserPageActivity")
    private var menuPopupWindow: PopupWindow? = null
    private val otherUserPageViewModel: OtherUserPageViewModel by viewModels()
    private val viewPagerAdapter: OtherUserPageViewPagerAdapter by lazy {
        OtherUserPageViewPagerAdapter(this)
    }
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateUserId()
        bindViewModel()
        bindPopupMenu()
        setUpViewPager()
        setUpItemVisibilityOnToolBar()
        onBackButtonClick()
        onMoreButtonClick()
    }

    private fun updateUserId() {
        val userId = intent.getLongExtra(USER_ID, 0L)
        otherUserPageViewModel.updateUserId(userId)
    }

    private fun bindViewModel() {
        binding.otherUserPageViewModel = otherUserPageViewModel
        binding.lifecycleOwner = this
    }

    private fun bindPopupMenu() {
        _popupBinding = MenuOtherUserPagePopupBinding.inflate(layoutInflater)
        popupBinding.lifecycleOwner = this
        popupBinding.onBlockedUserClick = ::showBlockUserDialog
    }

    private fun showBlockUserDialog() {
        val dialog = BlockUserDialogFragment.newInstance()
        dialog.show(supportFragmentManager, BlockUserDialogFragment.TAG)
        menuPopupWindow?.dismiss()
    }

    private fun setUpViewPager() {
        val tabTitleItems = listOf(
            getText(R.string.other_user_page_library),
            getText(R.string.other_user_page_activity)
        )
        binding.vpOtherUserPage.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tlOtherUserPage, binding.vpOtherUserPage) { tab, position ->
            tab.text = tabTitleItems[position]
        }.attach()
    }

    private fun setUpItemVisibilityOnToolBar() {
        binding.ablOtherUserPage.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = (totalScrollRange.toFloat() + verticalOffset) / totalScrollRange
            updateToolbarUi(percentage <= TOOLBAR_COLLAPSE_THRESHOLD)
        }
    }

    private fun updateToolbarUi(isCollapsed: Boolean) {
        with(binding) {
            val color = if (isCollapsed) R.color.white else R.color.transparent
            tbOtherUserPage.setBackgroundColor(
                ContextCompat.getColor(
                    this@OtherUserPageActivity,
                    color,
                )
            )
            tvOtherUserPageStickyTitle.isVisible = isCollapsed
            clOtherUserPageUserProfile.isVisible = !isCollapsed
        }
    }

    private fun onBackButtonClick() {
        binding.ivOtherUserPageStickyBack.setOnClickListener {
            finish()
        }
    }

    private fun onMoreButtonClick() {
        binding.ivOtherUserPageStickyKebab.setOnClickListener {
            singleEventHandler.throttleFirst {
                showMenu(binding.ivOtherUserPageStickyKebab)
            }
        }
    }

    private fun showMenu(view: View) {
        menuPopupWindow = PopupWindow(
            popupBinding.root,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true,
        ).apply {
            elevation = 2f.toFloatPxFromDp()
            showAsDropDown(
                view,
                POPUP_MARGIN_END.toIntPxFromDp(),
                POPUP_MARGIN_TOP.toIntPxFromDp(),
            )
        }
    }

    companion object {
        private const val TOOLBAR_COLLAPSE_THRESHOLD = 0f
        private const val POPUP_MARGIN_END = -80
        private const val POPUP_MARGIN_TOP = 0
        private const val USER_ID = "USER_ID"

        fun getIntent(context: Context, userId: Long): Intent {
            return Intent(context, OtherUserPageActivity::class.java).apply {
                putExtra(USER_ID, userId)
            }
        }
    }
}
