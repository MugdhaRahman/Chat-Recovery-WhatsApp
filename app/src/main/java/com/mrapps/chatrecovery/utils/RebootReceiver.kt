package com.mrapps.chatrecovery.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RebootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            context?.startService(Intent(context, ChatRecoveryMediaObService::class.java))
            context?.startService(Intent(context, NotificationService::class.java))
        }
    }

}