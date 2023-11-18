package com.example.smigoal

import android.content.Intent
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.IntentFilter
import android.provider.Telephony

class MainActivity : FlutterActivity() {
    private val CHANNEL = "com.example.smigoal/sms"
    private lateinit var smsReceiver: SMSReceiver

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        val serviceIntent = Intent(this, SMSBackgroundService::class.java)

        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        smsReceiver = SMSReceiver(channel)

        // SMSReceiver 등록
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION).apply {
            priority = Int.MAX_VALUE
        }
        registerReceiver(smsReceiver, filter)
        startService(serviceIntent)
    }

    // 필요에 따라 onDestroy에서 SMSReceiver 해제
    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }
}