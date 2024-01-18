package com.example.smigoal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import io.flutter.embedding.android.KeyData.CHANNEL
import io.flutter.plugin.common.MethodChannel

class SMSForegroundService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // SMSReceiver 할당
        registerSMSReceiver()
        // 알림 생성 및 시작
        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        Log.i("test", "포그라운드 서비스")
        val notificationChannelId = "SmiGoal Notification Channel ID"
        // 안드로이드 Oreo 이상을 위한 알림 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "SmiGoal Foreground Service"
            val chan = NotificationChannel(
                notificationChannelId,
                channelName,
                NotificationManager.IMPORTANCE_NONE,
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("스미골(SmiGoal) 작동 중")
            .setContentText("스미골이 당신의 보안을 책임지고 있습니다!")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(55, notification)
    }

    private fun registerSMSReceiver() {

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}
