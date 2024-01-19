package com.example.smigoal

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData

object SMSServiceData {
    // 서비스의 실행 성탸룰 나타내는 LiveData이다. 기본값은 false
    val isServiceRunning = MutableLiveData<Boolean>(false)

    // SMSService를 시작하는 메서드
    fun startSMSService(context: Context) {
        // 서비스가 실행 중이지 않은 경우에만 서비스를 시작한다.
        // 중복 동작과 ANR 방지 목적이다.
        if(!isServiceRun(context)) {
            val intent = Intent(context, SMSForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
            Toast.makeText(context, "Service Start", Toast.LENGTH_SHORT).show()
        }
    }

    // SMSService를 중지하는 메서드
    fun stopSMSService(context: Context) {
        val intent = Intent(context, SMSForegroundService::class.java)
        context.stopService(intent)
    }

    // 서비스의 실행 상태를 반환하는 메서드
    private fun isServiceRun(context: Context): Boolean {
        if (isServiceRunning.value == true) {
            return true
        }
        return false
    }
}