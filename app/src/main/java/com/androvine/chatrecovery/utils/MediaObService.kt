package com.androvine.chatrecovery.utils

import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.androvine.chatrecovery.R
import java.io.File

class MediaObService : Service() {


    private lateinit var mFileObservers: Array<FileObserver>
    private lateinit var mContentObserver: ContentObserver
    private lateinit var yourSAFUri: Uri

    private val TAG = "ChatRecoveryMediaObService"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        yourSAFUri = PermSAFUtils.getSAFUri(this)

        Log.e(TAG, "onStartCommand: service started")

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val paths = arrayOf(
                "WhatsApp/Media/WhatsApp Audio",
                "WhatsApp/Media/WhatsApp Images",
                "WhatsApp/Media/WhatsApp Video"
            )

            Log.e(TAG, "onStartCommand: method called for below R")

            mFileObservers = Array(paths.size) { i ->
                val path = paths[i]
                object :
                    FileObserver(File(Environment.getExternalStorageDirectory(), path).toString()) {
                    override fun onEvent(event: Int, pathInside: String?) {
                        if (event == 2 || event == 128) {
                            Log.e(TAG, "Event type: $event on path: $pathInside")
                            handleEventBelowR(path, pathInside)
                        }
                    }
                }.apply { startWatching() }
            }
        } else {
            val contentUri = MediaStore.Files.getContentUri("external")
            mContentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean, uri: Uri?) {
                    handleEventRAndAbove(uri)
                }
            }
            contentResolver.registerContentObserver(contentUri, true, mContentObserver)
        }
        return START_STICKY
    }

    private fun handleEventBelowR(directoryPath: String, filePath: String?) {
        Log.e(TAG, "handleEventBelowR: directoryPath: $directoryPath filePath: $filePath")
        try {
            filePath?.let {
                val sourceFile =
                    File(Environment.getExternalStorageDirectory(), "$directoryPath/$it")
                val fullFilePath = sourceFile.path
                when {
                    fullFilePath.contains("WhatsApp Audio") -> MFileUtils.copyFileOrDirectory(
                        fullFilePath,
                        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}" +
                                "/${getString(R.string.app_name)}/Recovered Media/Audios"
                    )

                    fullFilePath.contains("WhatsApp Images") -> MFileUtils.copyFileOrDirectory(
                        fullFilePath,
                        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}" +
                                "/${getString(R.string.app_name)}/Recovered Media/Images"
                    )

                    fullFilePath.contains("WhatsApp Video") -> MFileUtils.copyFileOrDirectory(
                        fullFilePath,
                        "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}" +
                                "/${getString(R.string.app_name)}/Recovered Media/Videos"
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun handleEventRAndAbove(uri: Uri?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val sourceFile = DocumentFile.fromTreeUri(this, yourSAFUri)
            uri?.path?.let {
                when {
                    it.contains("WhatsApp Audio") -> sourceFile?.findFile("WhatsApp Audio")?.uri?.let {
                        MFileUtils.copyFileOrDirectory(
                            this,
                            it,
                            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)}" +
                                    "/${getString(R.string.app_name)}/Recovered Media/Audios"
                        )
                    }

                    it.contains("WhatsApp Images") -> sourceFile?.findFile("WhatsApp Images")?.uri?.let {
                        MFileUtils.copyFileOrDirectory(
                            this,
                            it,
                            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}" +
                                    "/${getString(R.string.app_name)}/Recovered Media/Images"
                        )
                    }

                    it.contains("WhatsApp Video") -> sourceFile?.findFile("WhatsApp Video")?.uri?.let {
                        MFileUtils.copyFileOrDirectory(
                            this,
                            it,
                            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}" +
                                    "/${getString(R.string.app_name)}/Recovered Media/Videos"
                        )
                    }

                    else -> {
                        Log.e(TAG, "handleEventRAndAbove: else called")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && ::mFileObservers.isInitialized) {
            mFileObservers.forEach { it.stopWatching() }
        } else {
            contentResolver.unregisterContentObserver(mContentObserver)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}