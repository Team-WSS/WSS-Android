package com.into.websoso.ui.notice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.domain.model.Notice
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.notificationDetail.NotificationDetailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeActivity : ComponentActivity() {
    private val noticeViewModel: NoticeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WebsosoTheme {
                NoticeScreen(
                    viewModel = noticeViewModel,
                    onNoticeDetailClick = ::navigateToNotificationDetail,
                    onFeedDetailClick = ::navigateToFeedDetail,
                    onBackButtonClick = { finish() },
                )
            }
        }
    }

    private fun navigateToNotificationDetail(notification: Notice) {
        noticeViewModel.updateReadNotice(notification.id)
        startActivity(NotificationDetailActivity.getIntent(this, notification.intrinsicId))
    }

    private fun navigateToFeedDetail(notice: Notice) {
        noticeViewModel.updateReadNotice(notice.id)
        startActivity(FeedDetailActivity.getIntent(this, notice.intrinsicId))
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, NoticeActivity::class.java)
    }
}
