package com.example.smigoal

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

        val channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        smsReceiver = SMSReceiver()
        smsReceiver.setMethodChannel(channel)

        // SMSReceiver 등록
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION).apply {
            priority = Int.MAX_VALUE
        }
        registerReceiver(smsReceiver, filter)
    }

    // 필요에 따라 onDestroy에서 SMSReceiver 해제
    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }
}