package com.example.smigoal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Telephony
import android.util.Log
import androidx.core.app.NotificationCompat
import io.flutter.plugin.common.MethodChannel

class SMSReceiver(var channel: MethodChannel? = null) : BroadcastReceiver() {
//    private lateinit var channel: MethodChannel

//    fun setMethodChannel(channel: MethodChannel) {
//        this.channel = channel
//    }
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val messageBody = smsMessage.messageBody
                val sender = smsMessage.originatingAddress
                val timestamp = smsMessage.timestampMillis
                Log.i("msg", messageBody)
                Log.i("msg", sender!!)
                Log.i("msg", timestamp.toString())

                val notificationChannelId = "SmiGoal SMS Received Channel ID"
                val channelName = "SmiGoal SMS Receive Service"
                val notificationChannel = NotificationChannel(
                    notificationChannelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH,
                )
                notificationChannel.enableVibration(true)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.BLUE
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(notificationChannel)

                val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
                val notification = notificationBuilder
                    .setSmallIcon(R.mipmap.icon_smigoal)
                    .setContentTitle("메시지 도착")
                    .setContentText("From ${sender}, ${timestamp}: Message: ${messageBody}\\n")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)

                val intent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                notification.setContentIntent(pendingIntent)

                manager.notify(10, notification.build())

                // Flutter로 메시지 내용, 송신자, 시각 전달
                channel?.invokeMethod("onReceivedSMS", mapOf(
                    "message" to messageBody,
                    "sender" to sender,
                    "timestamp" to timestamp
                ))
            }
        }
    }
}
