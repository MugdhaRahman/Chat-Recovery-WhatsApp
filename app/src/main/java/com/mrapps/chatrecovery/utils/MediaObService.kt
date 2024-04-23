package com.mrapps.chatrecovery.utils

import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.os.FileObserver
import android.os.IBinder
import android.util.Log
import com.mrapps.chatrecovery.R
import java.io.File


class MediaObService : Service() {

    private val TAG = "MediaObServiceStarted"
    private val sdkInt = SDK_INT
    private var fileObservers: Array<FileObserver>? = null
    private var contentObserver: ContentObserver? = null
    private var SAFUri: Uri? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        SAFUri = PermSAFUtils.getSAFUri(this)
        Log.e(TAG, "onStartCommand:  service started")

        if (SDK_INT < Build.VERSION_CODES.R) {
            val paths = arrayOf(
                "WhatsApp/Media/WhatsApp Images",
                "WhatsApp/Media/WhatsApp Video"
            )

            Log.e(TAG, "onStartCommand:  method called for below R")
        }



        return START_STICKY
    }


    private fun handleEventBelowR(directoryPath: String, filePath: String) {
        Log.e(TAG, "handleEventBelowR: directoryPath: $directoryPath filePath: $filePath")

        try {
            val sourceFile =
                File("${Environment.getExternalStorageDirectory()}/$directoryPath", filePath)
            val fullFilePath = sourceFile.path

            when {
                fullFilePath.contains("WhatsApp Images") -> FileUtilsNew.copyFileOrDirectory(
                    fullFilePath,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .toString() + File.separator + getString(R.string.app_name) + "/Images"
                )

                fullFilePath.contains("WhatsApp Video") -> FileUtilsNew.copyFileOrDirectory(
                    fullFilePath,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .toString() + File.separator + getString(R.string.app_name) + "/Videos"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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