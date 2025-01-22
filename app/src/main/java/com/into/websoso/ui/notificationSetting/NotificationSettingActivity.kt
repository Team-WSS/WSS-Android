package com.into.websoso.ui.notificationSetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.ActivityNotificationSettingBinding

class NotificationSettingActivity :
    BaseActivity<ActivityNotificationSettingBinding>(R.layout.activity_notification_setting) {
    private val notificationSettingViewModel: NotificationSettingViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onBackButtonClick()
        onNotificationToggleClick()
    }

    private fun bindViewModel() {
        binding.notificationSettingViewModel = notificationSettingViewModel
        binding.lifecycleOwner = this
    }

    private fun onBackButtonClick() {
        binding.ivNotificationSettingBackButton.setOnClickListener {
            finish()
        }
    }

    private fun onNotificationToggleClick() {
        binding.clNotificationSettingButton.setOnClickListener {
            singleEventHandler.throttleFirst {
                notificationSettingViewModel.updateNotificationEnabled()
            }
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, NotificationSettingActivity::class.java)
    }
}
