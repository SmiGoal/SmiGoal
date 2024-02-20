package com.example.smigoal.functions

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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.smigoal.BuildConfig
import com.example.smigoal.MainActivity
import com.example.smigoal.R
import com.example.smigoal.db.MessageEntity
import com.example.smigoal.models.Message
import com.example.smigoal.models.SMSServiceData
import com.example.smigoal.models.extractUrls
import io.flutter.plugin.common.MethodChannel

class SMSReceiver(private var channel: MethodChannel? = null) : BroadcastReceiver() {
    private val fullMessage = StringBuilder()
    private var messageBody = ""
    private var sender = ""
    private var timestamp: Long = 0
    private val BASE_URL = BuildConfig.BASE_URL

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            fullMessage.clear()
            messageBody = ""
            sender = ""
            timestamp = 0
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                messageBody = smsMessage.messageBody
                sender = smsMessage.originatingAddress!!
                timestamp = smsMessage.timestampMillis
                fullMessage.append(messageBody)
            }
            Log.i("test", intent.toString())
            Log.i("test", fullMessage.toString())
            Log.i("test", sender)
            Log.i("test", timestamp.toString())

            var message = fullMessage.toString()
            val urls = extractUrls(message)
            var containsUrl = urls.isNotEmpty()
            urls.forEach{ url ->
                message = message.replace(url, "")
            }

            RequestServer.getServerRequest(context, BASE_URL, Message(message, if(urls.isNotEmpty()) urls[0] else null), sender, containsUrl, timestamp)
        }
    }

    fun sendNotification(context: Context, entity: MessageEntity) {
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

        Log.i("test", "From ${entity.sender}, ${entity.timestamp}: Message: ${entity.message}\\n")

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)

        val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
        val notification = notificationBuilder.setSmallIcon(R.mipmap.icon_smigoal)
            .setContentTitle("메시지 도착")
            .setContentText("From ${entity.sender}, ${entity.timestamp}: Message: ${entity.message}\\n Result: ${if(entity.isSmishing) "SPAM" else "HAM"}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        notification.setContentIntent(pendingIntent)

        manager.notify(10, notification.build())
    }
}
