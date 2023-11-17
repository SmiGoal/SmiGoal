package com.example.smigoal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import io.flutter.plugin.common.MethodChannel

class SMSReceiver() : BroadcastReceiver() {
    private lateinit var channel: MethodChannel

    fun setMethodChannel(channel: MethodChannel) {
        this.channel = channel
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val messageBody = smsMessage.messageBody
                val sender = smsMessage.originatingAddress
                val timestamp = smsMessage.timestampMillis
                Log.i("msg", messageBody)
                Log.i("msg", sender!!)
                Log.i("msg", timestamp.toString())

                // Flutter로 메시지 내용, 송신자, 시각 전달
                channel.invokeMethod("onReceivedSMS", mapOf(
                    "message" to messageBody,
                    "sender" to sender,
                    "timestamp" to timestamp
                ))
            }
        }
    }
}
