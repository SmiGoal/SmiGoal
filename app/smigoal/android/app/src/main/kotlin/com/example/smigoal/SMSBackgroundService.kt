package com.example.smigoal

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SMSBackgroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("test", "서비스를 시작할것입니다...")
        //startForeground(NOTIFICATION_SERVICE)
        return START_STICKY
    }
}