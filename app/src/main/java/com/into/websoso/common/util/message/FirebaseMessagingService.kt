package com.into.websoso.common.util.message

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.into.websoso.ui.feedDetail.FeedDetailActivity
import com.into.websoso.ui.main.MainActivity

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: "웹소소"
        val messageBody = message.notification?.body ?: "푸시 알림 메시지입니다"
        val feedId = message.data["feedId"] ?: ""

        val channelId = "웹소소"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelName = "Channel Name"
        val channelDescription = "Channel Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        channel.description = channelDescription
        notificationManager.createNotificationChannel(channel)

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val detailIntent = if (feedId.isNotEmpty()) {
            FeedDetailActivity.getIntent(this, feedId.toLong())
        } else {
            null
        }

        val pendingIntent = if (detailIntent != null) {
            TaskStackBuilder.create(this).run {
                addNextIntent(mainIntent)
                addNextIntentWithParentStack(detailIntent)
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
        } else {
            PendingIntent.getActivity(
                this,
                0,
                mainIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.into.websoso.R.mipmap.ic_wss_logo)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
