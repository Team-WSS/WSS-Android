package com.into.websoso.core.common.util.message

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.into.websoso.core.resource.R.mipmap.ic_wss_logo
import com.into.websoso.core.resource.R.string.app_name
import com.into.websoso.core.resource.R.string.push_notification_channel_description
import com.into.websoso.core.resource.R.string.push_notification_default_body
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.main.MainActivity
import com.into.websoso.ui.notificationDetail.NotificationDetailActivity
import com.into.websoso.ui.novelDetail.NovelDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WSSFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var pushMessageRepository: PushMessageRepository

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val pushMessage = PushMessage.from(message.data) ?: return

        setupNotificationChannel()
        val pendingIntent = createPendingIntent(pushMessage)
        showNotification(
            title = pushMessage.title ?: getString(app_name),
            body = pushMessage.body ?: getString(push_notification_default_body),
            pendingIntent = pendingIntent,
        )
    }

    private fun setupNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(app_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = getString(push_notification_channel_description)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createPendingIntent(pushMessage: PushMessage): PendingIntent {
        val mainIntent = MainActivity.getIntent(this)
        val notificationId = pushMessage.notificationId

        val detailIntent = when (pushMessage.destination) {
            PushDestination.FEED -> FeedDetailActivity.getIntent(
                this,
                requireNotNull(pushMessage.feedId),
                notificationId,
            )

            PushDestination.NOVEL -> NovelDetailActivity.getIntent(
                this,
                requireNotNull(pushMessage.novelId),
                notificationId,
            )

            PushDestination.NOTIFICATION_DETAIL -> NotificationDetailActivity.getIntent(
                this,
                notificationId,
            )
        }

        return TaskStackBuilder.create(this).run {
            addNextIntent(mainIntent)
            addNextIntentWithParentStack(detailIntent)

            getPendingIntent(
                notificationId.toInt(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        pendingIntent: PendingIntent,
    ) {
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(ic_wss_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                pushMessageRepository.updateUserFCMToken(token)
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "websoso"
    }
}
