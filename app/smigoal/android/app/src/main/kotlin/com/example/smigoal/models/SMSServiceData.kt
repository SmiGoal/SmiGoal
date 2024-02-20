package com.example.smigoal.models

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.smigoal.db.MessageDB
import com.example.smigoal.db.MessageEntity
import com.example.smigoal.functions.SMSForegroundService
import com.example.smigoal.functions.SMSReceiver
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SMSServiceData {
    // 서비스의 실행 성탸룰 나타내는 LiveData이다. 기본값은 false
    val isServiceRunning = MutableLiveData<Boolean>(false)
    var responseFromServer: MessageEntity? = null

    val CHANNEL = "com.example.smigoal/sms"
    lateinit var db: MessageDB
    lateinit var smsReceiver: SMSReceiver
    lateinit var channel: MethodChannel
    fun setResponseFromServer(context: Context, entity: MessageEntity) {
        val dbScope = CoroutineScope(Dispatchers.IO)
        dbScope.launch {
            db.messageDao().insertMessage(entity)
            withContext(Dispatchers.Main) {
                smsReceiver.sendNotification(context, entity)
                Log.i("test", "result : $entity")

                Log.i("test", channel.invokeMethod(
                    "onReceivedSMS", mapOf(
                        "message" to entity.message,
                        "sender" to entity.sender,
                        "result" to if (entity.isSmishing) "spam" else "ham",
                        "timestamp" to entity.timestamp
                    )
                ).toString())
            }
        }
    }

    // SMSService를 시작하는 메서드
    fun startSMSService(context: Context) {
        // 서비스가 실행 중이지 않은 경우에만 서비스를 시작한다.
        // 중복 동작과 ANR 방지 목적이다.
        if(!isServiceRun(context)) {
            Log.i("test", "Start Service")
            val intent = Intent(context, SMSForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
            Toast.makeText(context, "Service Start", Toast.LENGTH_SHORT).show()
        }
    }

    // SMSService를 중지하는 메서드
    fun stopSMSService(context: Context) {
        Log.i("test", "Stop Service")
        val intent = Intent(context, SMSForegroundService::class.java)
        context.stopService(intent)
    }

    // 서비스의 실행 상태를 반환하는 메서드
    private fun isServiceRun(context: Context): Boolean {
        Log.i("test", "isServiceRun")
        if (isServiceRunning.value == true) {
            return true
        }
        return false
    }
}