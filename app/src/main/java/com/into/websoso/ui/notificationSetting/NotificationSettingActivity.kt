package com.into.websoso.ui.notificationSetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.databinding.ActivityNotificationSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingActivity :
    BaseActivity<ActivityNotificationSettingBinding>(R.layout.activity_notification_setting) {
    private val notificationSettingViewModel: NotificationSettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onBackButtonClick()
        setupObserver()
    }

    private fun bindViewModel() {
        binding.notificationSettingViewModel = notificationSettingViewModel
        binding.lifecycleOwner = this
        binding.onToggleClick = ::updateIsNotificationEnabled
    }

    private fun updateIsNotificationEnabled() {
        notificationSettingViewModel.updateNotificationEnabled()
    }

    private fun onBackButtonClick() {
        binding.ivNotificationSettingBackButton.setOnClickListener {
            finish()
        }
    }

    private fun setupObserver() {
        notificationSettingViewModel.isConnecting.observe(this) { isConnecting ->
            if (isConnecting) {
                binding.clNotificationSettingButton.isClickable = false
                return@observe
            }
            binding.clNotificationSettingButton.isClickable = true
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, NotificationSettingActivity::class.java)
    }
}
