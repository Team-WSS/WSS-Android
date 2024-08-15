package com.teamwss.websoso.ui.profileDisclosure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import com.teamwss.websoso.R
import com.teamwss.websoso.databinding.ActivityProfileDisclosureBinding
import com.teamwss.websoso.ui.common.base.BindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDisclosureActivity :
    BindingActivity<ActivityProfileDisclosureBinding>(R.layout.activity_profile_disclosure) {
    private val profileDisclosureViewModel: ProfileDisclosureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        setupTranslucentOnStatusBar()
        onBackButtonClick()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.lifecycleOwner = this
    }

    private fun setupTranslucentOnStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )
    }

    private fun onBackButtonClick() {
        binding.ivProfileDisclosureBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setupObserver() {
        profileDisclosureViewModel.isProfilePublic.observe(this) { isProfilePublic ->
            updateProfileDisclosureStatusButton(isProfilePublic)
        }
    }

    private fun updateProfileDisclosureStatusButton(isProfilePublic: Boolean) {
        val buttonImage = if (isProfilePublic) {
            R.drawable.btn_account_info_check_unselected
        } else {
            R.drawable.btn_account_info_check_selected
        }
        binding.ivProfileDisclosureStatusButton.setImageResource(buttonImage)
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileDisclosureActivity::class.java)
        }
    }
}