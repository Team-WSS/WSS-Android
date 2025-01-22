package com.into.websoso.ui.notificationDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationDetailActivity : ComponentActivity() {
    private val notificationDetailViewModel: NotificationDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBackPressed()

        setContent {
            WebsosoTheme {
                NotificationDetailScreen(
                    viewModel = notificationDetailViewModel,
                    onBackButtonClick = { finish() },
                )
            }
        }
    }

    private fun handleBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }
    }

    companion object {
        const val NOTIFICATION_DETAIL_KEY = "NOTIFICATION_DETAIL_KEY"

        fun getIntent(
            context: Context,
            notificationId: Long,
        ): Intent =
            Intent(context, NotificationDetailActivity::class.java).apply {
                putExtra(NOTIFICATION_DETAIL_KEY, notificationId)
            }
    }
}
