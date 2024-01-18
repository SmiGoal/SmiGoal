package com.example.smigoal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    override fun onReceive(context:Context, intent:Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // 백그라운드에서 실행할 작업을 여기에 구현
        }
    }
}
