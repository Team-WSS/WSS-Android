package com.into.websoso.ui.notificationSetting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.into.websoso.R
import com.into.websoso.core.common.ui.base.BaseActivity
import com.into.websoso.core.common.util.SingleEventHandler
import com.into.websoso.databinding.ActivityNotificationSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationSettingActivity : BaseActivity<ActivityNotificationSettingBinding>(R.layout.activity_notification_setting) {
    private val notificationSettingViewModel: NotificationSettingViewModel by viewModels()
    private val singleEventHandler: SingleEventHandler by lazy { SingleEventHandler.from() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindViewModel()
        onBackButtonClick()
    }

    private fun bindViewModel() {
        binding.notificationSettingViewModel = notificationSettingViewModel
        binding.lifecycleOwner = this
        binding.onToggleClick = ::updateIsNotificationEnabled
    }

    private fun updateIsNotificationEnabled() {
        val newCheckedState = !binding.scNotificationSettingToggle.isChecked
        binding.scNotificationSettingToggle.isChecked = newCheckedState

        singleEventHandler.debounce(coroutineScope = lifecycleScope) {
            notificationSettingViewModel.updateNotificationPushEnabled(newCheckedState)
        }
    }

    private fun onBackButtonClick() {
        binding.ivNotificationSettingBackButton.setOnClickListener {
            finish()
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, NotificationSettingActivity::class.java)
    }
}
