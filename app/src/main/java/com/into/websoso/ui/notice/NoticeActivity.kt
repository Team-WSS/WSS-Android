package com.into.websoso.ui.notice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.into.websoso.designsystem.theme.WebsosoTheme
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.noticeDetail.NoticeDetailActivity
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
                    navigateToNoticeDetail = ::navigateToNoticeDetail,
                    navigateToFeedDetail = ::navigateToFeedDetail,
                )
            }
        }
    }

    private fun navigateToNoticeDetail(noticeId: Long) {
        startActivity(NoticeDetailActivity.getIntent(this, noticeId))
    }

    private fun navigateToFeedDetail(feedId: Long) {
        startActivity(FeedDetailActivity.getIntent(this, feedId))
    }

    companion object {
        fun getIntent(context: Context): Intent = Intent(context, NoticeActivity::class.java)
    }
}
