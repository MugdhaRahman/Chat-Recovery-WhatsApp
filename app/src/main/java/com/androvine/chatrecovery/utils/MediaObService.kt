package com.androvine.chatrecovery.utils

import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.FileObserver
import android.os.IBinder
import android.util.Log

class MediaObService : Service() {

    private val TAG = "MediaObServiceStarted"
    private val sdkInt = Build.VERSION.SDK_INT
    private var fileObservers: Array<FileObserver>? = null
    private var contentObserver: ContentObserver? = null
    private var SAFUri: Uri? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SAFUri = PermSAFUtils.getSAFUri(this)

        Log.e(TAG, "onStartCommand:  service started")


        return START_STICKY
    }


    override fun onDestroy() {
        if (sdkInt < Build.VERSION_CODES.R && fileObservers != null) {
            fileObservers?.forEach { it.stopWatching() }
        } else {
            contentResolver.unregisterContentObserver(contentObserver!!)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}