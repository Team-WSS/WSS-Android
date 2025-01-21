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

class WSSFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val receivedData = message.data

        if (receivedData.isEmpty()) return

        val title = receivedData["title"] ?: "웹소소"
        val body = receivedData["body"] ?: "푸시 알림 메시지입니다"
        val feedId = receivedData["feedId"] ?: ""

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
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val detailIntent = FeedDetailActivity.getIntent(this, feedId.toLong())

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(mainIntent)
            addNextIntentWithParentStack(detailIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(com.into.websoso.R.mipmap.ic_wss_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
