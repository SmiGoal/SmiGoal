package com.example.smigoal

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.IntentFilter
import android.provider.Telephony
import android.util.Log
import io.flutter.embedding.android.KeyData

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.smigoal/sms"
    private lateinit var smsReceiver: SMSReceiver

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, KeyData.CHANNEL)
        val smsReceiver = SMSReceiver(channel)

        // SMSReceiver 등록
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION).apply {
            priority = Int.MAX_VALUE
        }
        registerReceiver(smsReceiver, filter)
//        val backgroundServiceIntent = Intent(this, SMSBackgroundService::class.java)
        val foregroundServiceIntent = Intent(this, SMSForegroundService::class.java)

        Log.i("test", "main launched")
//        startService(backgroundServiceIntent)
        startService(foregroundServiceIntent)
    }

    // 필요에 따라 onDestroy에서 SMSReceiver 해제
    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }
}