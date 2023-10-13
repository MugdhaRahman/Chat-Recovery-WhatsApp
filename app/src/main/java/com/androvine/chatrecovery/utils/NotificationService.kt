package com.androvine.chatrecovery.utils

import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

    private lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {

    }

    override fun onDestroy() {
        super.onDestroy()
        sendBroadcast(Intent("com.android.Restart_Services"))
    }


}