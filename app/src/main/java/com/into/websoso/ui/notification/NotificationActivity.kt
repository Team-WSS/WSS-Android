package com.into.websoso.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.core.common.util.isNotificationPermissionGranted
import com.into.websoso.core.common.util.setupSystemBarIconColor
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.notification.model.NotificationModel
import com.into.websoso.ui.notificationDetail.NotificationDetailActivity
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.setting.dialog.NotificationPermissionDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val notificationSettingLauncher = registerForActivityResult(
        StartActivityForResult(),
    ) {
        if (isNotificationPermissionGranted()) {
            notificationViewModel.updatePushMessageEnabled()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemBarIconColor(true)
        checkNotificationPermission()
        setContent {
            WebsosoTheme {
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onNotificationDetailClick = ::navigateToNotificationDetail,
                    onFeedDetailClick = ::navigateToFeedDetail,
                    onNovelDetailClick = ::navigateToNovelDetail,
                    onBackButtonClick = {
                        setResult(ResultFrom.Notification.RESULT_OK)
                        finish()
                    },
                )
            }
        }
    }

    private fun checkNotificationPermission() {
        if (isNotificationPermissionGranted()) return

        showNotificationSettingDialog()
    }

    private fun showNotificationSettingDialog() {
        NotificationPermissionDialog
            .newInstance()
            .apply {
                isCancelable = false
                setOnSetUpClickListener {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                    notificationSettingLauncher.launch(intent)
                }
            }.show(
                supportFragmentManager,
                NotificationPermissionDialog.NOTIFICATION_PERMISSION_DIALOG_TAG,
            )
    }

    private fun navigateToNotificationDetail(notification: NotificationModel) {
        notificationViewModel.updateReadNotification(notification.id)
        startActivity(NotificationDetailActivity.getIntent(this, notification.intrinsicId))
    }

    private fun navigateToFeedDetail(notification: NotificationModel) {
        notificationViewModel.updateReadNotification(notification.id)
        startActivity(FeedDetailActivity.getIntent(this, notification.intrinsicId, notification.id))
    }

    private fun navigateToNovelDetail(notification: NotificationModel) {
        notificationViewModel.updateReadNotification(notification.id)
        startActivity(NovelDetailActivity.getIntent(this, notification.intrinsicId, notification.id))
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
