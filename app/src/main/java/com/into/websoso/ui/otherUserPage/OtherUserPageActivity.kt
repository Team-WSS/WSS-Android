package com.into.websoso.ui.otherUserPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.tabs.TabLayoutMediator
import com.into.websoso.R.color.transparent
import com.into.websoso.R.color.white
import com.into.websoso.R.layout.activity_other_user_page
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.OtherUserProfileBack
import com.into.websoso.core.common.ui.model.ResultFrom.WithdrawUser
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.core.common.util.getS3ImageUrl
import com.into.websoso.core.common.util.toFloatPxFromDp
import com.into.websoso.core.common.util.toIntPxFromDp
import com.into.websoso.core.common.util.tracker.Tracker
import com.into.websoso.databinding.ActivityOtherUserPageBinding
import com.into.websoso.databinding.MenuOtherUserPagePopupBinding
import com.into.websoso.resource.R.drawable.img_loading_thumbnail
import com.into.websoso.resource.R.string.other_user_page_activity
import com.into.websoso.resource.R.string.other_user_page_library
import com.into.websoso.ui.otherUserPage.adapter.OtherUserPageViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtherUserPageActivity : BaseActivity<ActivityOtherUserPageBinding>(activity_other_user_page) {
    @Inject
    lateinit var tracker: Tracker

    private var _popupBinding: MenuOtherUserPagePopupBinding? = null
    private val popupBinding: MenuOtherUserPagePopupBinding
        get() = _popupBinding ?: error("OtherUserPageActivity")
    private var menuPopupWindow: PopupWindow? = null
    private val otherUserPageViewModel: OtherUserPageViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateUserId()
        bindViewModel()
        bindPopupMenu()
        setupObserver()
        setupViewPager()
        setupItemVisibilityOnToolBar()
        onBackButtonClick()
        onMoreButtonClick()
        handleBackPressed()
        tracker.trackEvent("other_mypage")
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

    private fun setupObserver() {
        otherUserPageViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.isLoading -> binding.wllOtherUserPage.setWebsosoLoadingVisibility(true)
                uiState.error -> binding.wllOtherUserPage.setLoadingLayoutVisibility(false)
                !uiState.isLoading -> {
                    binding.wllOtherUserPage.setWebsosoLoadingVisibility(false)
                }
            }

            when (uiState.otherUserProfile?.isProfilePublic) {
                true -> {
                    binding.vpOtherUserPage.visibility = View.VISIBLE
                    binding.clOtherUserPageNoPublic.visibility = View.GONE
                }

                false, null -> {
                    binding.vpOtherUserPage.visibility = View.GONE
                    binding.clOtherUserPageNoPublic.visibility = View.VISIBLE
                }
            }

            setUpMyProfileImage(uiState.otherUserProfile?.avatarImage.orEmpty())
        }

        otherUserPageViewModel.isWithdrawUser.observe(this) { isWithdrawUser ->
            if (isWithdrawUser) {
                setResult(WithdrawUser.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun setUpMyProfileImage(otherUserProfileUrl: String) {
        val updatedMyProfileImageUrl = binding.root.getS3ImageUrl(otherUserProfileUrl)
        binding.ivOtherUserPageUserProfile.load(updatedMyProfileImageUrl) {
            crossfade(true)
            error(img_loading_thumbnail)
            transformations(CircleCropTransformation())
        }
    }

    private fun setupViewPager() {
        val userId = intent.getLongExtra(USER_ID, 0L)

        val tabTitleItems = listOf(
            getText(other_user_page_library),
            getText(other_user_page_activity),
        )
        binding.vpOtherUserPage.adapter = OtherUserPageViewPagerAdapter(this, userId)

        TabLayoutMediator(binding.tlOtherUserPage, binding.vpOtherUserPage) { tab, position ->
            tab.text = tabTitleItems[position]
        }.attach()
    }

    private fun setupItemVisibilityOnToolBar() {
        binding.ablOtherUserPage.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val percentage = (totalScrollRange.toFloat() + verticalOffset) / totalScrollRange
            updateToolbarUi(percentage <= TOOLBAR_COLLAPSE_THRESHOLD)
        }
    }

    private fun updateToolbarUi(isCollapsed: Boolean) {
        with(binding) {
            val color = if (isCollapsed) white else transparent
            tbOtherUserPage.setBackgroundColor(
                ContextCompat.getColor(
                    this@OtherUserPageActivity,
                    color,
                ),
            )
            tvOtherUserPageStickyTitle.isVisible = isCollapsed
            clOtherUserPageUserProfile.isVisible = !isCollapsed
        }
    }

    private fun onBackButtonClick() {
        binding.ivOtherUserPageStickyBack.setOnClickListener {
            setResult(OtherUserProfileBack.RESULT_OK)
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

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            setResult(OtherUserProfileBack.RESULT_OK)
            finish()
        }
    }

    companion object {
        private const val TOOLBAR_COLLAPSE_THRESHOLD = 0f
        private const val POPUP_MARGIN_END = -80
        private const val POPUP_MARGIN_TOP = 0
        private const val USER_ID = "USER_ID"

        fun getIntent(
            context: Context,
            userId: Long,
        ): Intent =
            Intent(context, OtherUserPageActivity::class.java).apply {
                putExtra(USER_ID, userId)
            }
    }
}
