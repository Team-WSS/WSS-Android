package com.into.websoso.ui.novelNotification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.into.websoso.core.common.util.setupSystemBarIconColor
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.ui.normalExplore.NormalExploreActivity
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import com.into.websoso.ui.novelNotification.model.NovelNotificationSubscriptionModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NovelNotificationListActivity : AppCompatActivity() {
    private val novelNotificationListViewModel: NovelNotificationListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemBarIconColor(true)
        setContent {
            WebsosoTheme {
                NovelNotificationListScreen(
                    viewModel = novelNotificationListViewModel,
                    onSubscriptionClick = ::navigateToNovelDetail,
                    onExploreClick = ::navigateToNormalExplore,
                    onBackButtonClick = ::finish,
                )
            }
        }
    }

    private fun navigateToNovelDetail(subscription: NovelNotificationSubscriptionModel) {
        startActivity(NovelDetailActivity.getIntent(this, subscription.novelId))
    }

    private fun navigateToNormalExplore() {
        startActivity(NormalExploreActivity.getIntent(this))
    }

    companion object {
        const val NOVEL_NOTIFICATION_TYPE = "NOVEL_NOTIFICATION_TYPE"

        fun getIntent(
            context: Context,
            notificationType: NovelNotificationType,
        ): Intent =
            Intent(context, NovelNotificationListActivity::class.java).apply {
                putExtra(NOVEL_NOTIFICATION_TYPE, notificationType.name)
            }
    }
}
