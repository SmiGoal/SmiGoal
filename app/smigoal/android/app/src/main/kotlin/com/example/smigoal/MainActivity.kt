package com.example.smigoal

import android.content.Intent
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.example.smigoal.db.MessageDB
import com.example.smigoal.functions.SMSForegroundService
import com.example.smigoal.functions.SMSReceiver
import com.example.smigoal.models.SMSServiceData
import com.example.smigoal.models.SMSServiceData.channel
import com.example.smigoal.models.SMSServiceData.db
import com.example.smigoal.models.SMSServiceData.smsReceiver
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.android.KeyData.CHANNEL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : FlutterFragmentActivity() {
//    lateinit var db: MessageDB
    private val dbScope = CoroutineScope(Dispatchers.IO) // Coroutine Scope 정의

//    private val isServiceRunning = Observer<Boolean> { isRunning ->
//        if (!isRunning) {
//            Log.i("test", "service 상태 변화 감지")
//            SMSServiceData.startSMSService(this@MainActivity)
//            SMSServiceData.isServiceRunning.postValue(true)
//            registerSMSReceiver()
//            startSMSForegroundService()
//        }
//    }

    val permissions = arrayOf(
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.RECEIVE_SMS)

    val multiplePermissionLauncher = (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        val resultPermission = it.all{ map ->
            map.value
        }
        if(!resultPermission){
            //finish()
            Toast.makeText(this, "모든 권한 승인되어야 함", Toast.LENGTH_SHORT).show()
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        checkPermissions()
        init(flutterEngine)
        registerSMSReceiver()
        startSMSForegroundService()
    }

    fun allPermissionGranted() = permissions.all{
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions() {
        Log.i("test", "checkPermissions")
        if(!allPermissionGranted()) multiplePermissionLauncher.launch(permissions)
    }

    private fun init(flutterEngine: FlutterEngine) {
        Log.i("test", "init")
        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        smsReceiver = SMSReceiver(channel)
        db = MessageDB.getInstance(applicationContext)!!
    }

    fun registerSMSReceiver() {
        Log.i("test", "registerSMSReceiver")
        // SMSReceiver 등록
//        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION).apply {
//            priority = Int.MAX_VALUE
//        }
//        registerReceiver(smsReceiver, filter)
    }

    // 필요에 따라 onDestroy에서 SMSReceiver 해제
    override fun onDestroy() {
        unregisterReceiver(smsReceiver)
        super.onDestroy()
    }

    private fun startSMSForegroundService() {
        Log.i("test", "startSMSForegroundService")
        val foregroundServiceIntent = Intent(this, SMSForegroundService::class.java)
        startForegroundService(foregroundServiceIntent)
    }
}