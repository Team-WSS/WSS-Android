package com.into.websoso.core.common.util.message

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.into.websoso.R
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.main.MainActivity
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

        val receivedData = message.data
        if (receivedData.isEmpty()) return

        val title = receivedData["title"] ?: DEFAULT_TITLE
        val body = receivedData["body"] ?: DEFAULT_BODY
        val feedId = receivedData["feedId"]?.toLongOrNull() ?: return
        val notificationId = receivedData["notificationId"]?.toLongOrNull() ?: return

        setupNotificationChannel()
        val pendingIntent = createPendingIntent(
            feedId,
            notificationId,
        )
        showNotification(title, body, pendingIntent)
    }

    private fun setupNotificationChannel() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = CHANNEL_DESCRIPTION
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createPendingIntent(
        feedId: Long,
        notificationId: Long,
    ): PendingIntent {
        val mainIntent = MainActivity.getIntent(this)
        val detailIntent = FeedDetailActivity.getIntent(this, feedId, notificationId)

        return TaskStackBuilder.create(this).run {
            addNextIntent(mainIntent)
            addNextIntentWithParentStack(detailIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }

    private fun showNotification(
        title: String,
        body: String,
        pendingIntent: PendingIntent,
    ) {
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_wss_logo)
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
        private const val DEFAULT_TITLE = "웹소소"
        private const val DEFAULT_BODY = "푸시 알림 메시지입니다"
        private const val CHANNEL_ID = "websoso"
        private const val CHANNEL_NAME = "웹소소"
        private const val CHANNEL_DESCRIPTION = "웹소소 알림입니다."
    }
}
