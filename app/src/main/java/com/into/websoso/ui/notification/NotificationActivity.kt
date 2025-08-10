package com.into.websoso.ui.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.core.common.util.setupWhiteStatusBar
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.notification.model.NotificationModel
import com.into.websoso.ui.notificationDetail.NotificationDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : ComponentActivity() {
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.setupWhiteStatusBar()

        setContent {
            WebsosoTheme {
                NotificationScreen(
                    viewModel = notificationViewModel,
                    onNotificationDetailClick = ::navigateToNotificationDetail,
                    onFeedDetailClick = ::navigateToFeedDetail,
                    onBackButtonClick = {
                        setResult(ResultFrom.Notification.RESULT_OK)
                        finish()
                    },
                )
            }
        }
    }

    private fun navigateToNotificationDetail(notification: NotificationModel) {
        notificationViewModel.updateReadNotification(notification.id)
        startActivity(NotificationDetailActivity.getIntent(this, notification.intrinsicId))
    }

    private fun navigateToFeedDetail(notification: NotificationModel) {
        notificationViewModel.updateReadNotification(notification.id)
        startActivity(FeedDetailActivity.getIntent(this, notification.intrinsicId, notification.id))
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, NotificationActivity::class.java)
    }
}
