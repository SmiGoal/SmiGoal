package com.example.smigoal.functions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.smigoal.R
import com.example.smigoal.models.SMSServiceData.isServiceRunning
import com.example.smigoal.models.SMSServiceData.stopSMSService

class SMSForegroundService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // 알림 생성 및 시작
        startForegroundService()
        return START_STICKY
    }

    private fun startForegroundService() {
        Log.i("test", "포그라운드 서비스")
        val notificationChannelId = "SmiGoal Notification Channel ID"
        // 안드로이드 Oreo 이상을 위한 알림 채널 생성
        val channelName = "SmiGoal Foreground Service"
        val channel = NotificationChannel(
            notificationChannelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH,
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.icon_smigoal)
            .setContentTitle("스미골(SmiGoal) 작동 중")
            .setContentText("스미골이 당신의 보안을 책임지고 있습니다!")
            .build()

        startForeground(55, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
//        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSMSService(applicationContext)
        isServiceRunning.postValue(false)
    }
}
