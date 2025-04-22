package com.into.websoso.ui.profileDisclosure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.ui.model.ResultFrom.ChangeProfileDisclosure
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.ActivityProfileDisclosureBinding
import com.into.websoso.ui.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDisclosureActivity : BaseActivity<ActivityProfileDisclosureBinding>(R.layout.activity_profile_disclosure) {
    private val profileDisclosureViewModel: ProfileDisclosureViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onBackButtonClick()
    }

    private fun bindViewModel() {
        binding.profileDisclosureViewModel = profileDisclosureViewModel
        binding.lifecycleOwner = this
        binding.onToggleClick = ::updateProfileDisclosureStatus
    }

    private fun onBackButtonClick() {
        binding.ivProfileDisclosureBackButton.setOnClickListener {
            if (profileDisclosureViewModel.isChangedStatus.value == true) {
                val intent = SettingActivity.getIntent(
                    this,
                    profileDisclosureViewModel.isProfilePrivate.value?.not() ?: true,
                )
                setResult(ChangeProfileDisclosure.RESULT_OK, intent)
            }
            finish()
        }
    }

    private fun updateProfileDisclosureStatus() {
        val newCheckedState: Boolean = !binding.scProfileDisclosureToggle.isChecked
        binding.scProfileDisclosureToggle.isChecked = newCheckedState

        singleEventHandler.debounce(coroutineScope = lifecycleScope) {
            profileDisclosureViewModel.updateProfileStatus(newCheckedState)
        }
    }

    companion object {
        const val IS_PROFILE_PUBLIC = "isProfilePublic"

        fun getIntent(context: Context): Intent = Intent(context, ProfileDisclosureActivity::class.java)
    }
}
