package com.example.smigoal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

class BootCompletedReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("test", "핸드폰 부팅됐어요")
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // 백그라운드에서 실행할 작업을 여기에 구현
            Intent(context, SMSForegroundService::class.java).also {
                context.startForegroundService(it)
            }
        }
    }
}
