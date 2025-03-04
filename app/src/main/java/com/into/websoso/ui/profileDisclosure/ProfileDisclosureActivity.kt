package com.into.websoso.ui.profileDisclosure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.ChangeProfileDisclosure
import com.into.websoso.databinding.ActivityProfileDisclosureBinding
import com.into.websoso.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDisclosureActivity :
    BaseActivity<ActivityProfileDisclosureBinding>(R.layout.activity_profile_disclosure) {
    private val profileDisclosureViewModel: ProfileDisclosureViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onBackButtonClick()
        onProfileDisclosureButtonClick()
        onCompleteButtonClick()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.profileDisclosureViewModel = profileDisclosureViewModel
        binding.lifecycleOwner = this
    }

    private fun onBackButtonClick() {
        binding.ivProfileDisclosureBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onProfileDisclosureButtonClick() {
        binding.clProfileDisclosureButton.setOnClickListener {
            profileDisclosureViewModel.updateProfileStatus()
        }
    }

    private fun onCompleteButtonClick() {
        binding.tvProfileDisclosureCompleteButton.setOnClickListener {
            profileDisclosureViewModel.saveProfileDisclosureStatus()
        }
    }

    private fun setupObserver() {
        profileDisclosureViewModel.uiState.observe(this) { uiState ->
            when {
                uiState.loading -> {
                    binding.wllProfileDisclosure.setWebsosoLoadingVisibility(true)
                    binding.wllProfileDisclosure.setErrorLayoutVisibility(false)
                }

                uiState.error -> {
                    binding.wllProfileDisclosure.setWebsosoLoadingVisibility(false)
                    binding.wllProfileDisclosure.setErrorLayoutVisibility(true)
                }

                else -> {
                    binding.wllProfileDisclosure.setWebsosoLoadingVisibility(false)
                    binding.wllProfileDisclosure.setErrorLayoutVisibility(false)
                }
            }
        }

        profileDisclosureViewModel.isProfilePublic.observe(this) { isProfilePublic ->
            updateProfileDisclosureStatusButton(isProfilePublic)
        }

        profileDisclosureViewModel.isSaveStatusComplete.observe(this) { isSaveStatus ->
            if (isSaveStatus) {
                val intent = SettingActivity.getIntent(
                    this,
                    profileDisclosureViewModel.isProfilePublic.value ?: true,
                )
                setResult(ChangeProfileDisclosure.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun updateProfileDisclosureStatusButton(isProfilePublic: Boolean) {
        val buttonImage = when (isProfilePublic) {
            true -> R.drawable.img_account_info_check_unselected
            false -> R.drawable.img_account_info_check_selected
        }
        binding.ivProfileDisclosureStatusButton.setImageResource(buttonImage)
    }

    companion object {
        const val IS_PROFILE_PUBLIC = "isProfilePublic"

        fun getIntent(context: Context): Intent {
            return Intent(context, ProfileDisclosureActivity::class.java)
        }
    }
}
