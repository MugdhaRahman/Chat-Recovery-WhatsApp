package com.androvine.chatrecovery.utils

import android.app.Notification.EXTRA_TEXT
import android.app.Notification.EXTRA_TITLE
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.androvine.chatrecovery.db.CallDBHelper
import com.androvine.chatrecovery.db.MessageDBHelper
import com.androvine.chatrecovery.models.CallModel
import com.androvine.chatrecovery.models.CallStatus
import com.androvine.chatrecovery.models.CallType
import com.androvine.chatrecovery.models.MessageModel
import java.io.File
import java.io.FileOutputStream

class NotificationService : NotificationListenerService() {

    private lateinit var context: Context
    private var largeIconBitmap: Bitmap? = null

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


    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        val packageName = sbn?.packageName
        if (packageName == "com.whatsapp") {
            val extras = sbn.notification?.extras

            val username = extras?.getString(EXTRA_TITLE)
            val message = extras?.getCharSequence(EXTRA_TEXT).toString()
            val userIcon = sbn.notification?.getLargeIcon()
            val messageTime = System.currentTimeMillis()
            val avatar = "avatar_" + username + "_image.png"

            Log.e(
                "NotificationService",
                "onNotificationPosted: " + username + " " + message + " " + messageTime
            )

            // load large icon
            largeIconBitmap = userIcon?.loadDrawables(context)?.toBitmap()
            val internalStorageDir = this.filesDir
            val largeIconDir = File(internalStorageDir, "avatars")

            // Check if the avatar image already exists
            val avatarFile = File(largeIconDir, avatar)
            if (!avatarFile.exists()) {
                // The image does not exist, so save it
                largeIconBitmap?.let { bitmap ->
                    val outputStream = FileOutputStream(avatarFile)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                }
            }


            if (!message.contains("new messages") && !message.contains("voice call") && !message.contains(
                    "video call"
                ) && !message.contains("missed call") && !message.contains("Missed Call")
            ) {
                // chat model
                val messageModel = MessageModel(
                    user = username.toString(),
                    message = message,
                    time = messageTime,
                    avatarFileName = avatar
                )

                // save chat model to database
                val messageDBHelper = MessageDBHelper(context)
                messageDBHelper.addMessageItem(messageModel)
                val intent = Intent("new_item_message")
                sendBroadcast(intent)

                Log.e(
                    "NotificationService",
                    "onNotificationPosted: " + messageModel.toString()
                )

            }

            val messageLower = message.lowercase()
            Log.e("message", "message.lowercase() 1: " + messageLower)

            if (messageLower.contains("incoming voice call")
                || messageLower.contains("ongoing voice call")
                || messageLower.contains("incoming video call")
                || messageLower.contains("ongoing video call")
                || messageLower.contains("missed call")
                || messageLower.contains("missed calls")
            ) {

                Log.e("message", "message.lowercase() 2: " + messageLower)


                val callType = if (messageLower.contains("voice")) {
                    CallType.AUDIO
                } else if (messageLower.contains("video")) {
                    CallType.VIDEO
                } else {
                    CallType.UNKNOWN
                }

                val callStatus = if (messageLower.contains("ongoing")) {
                    CallStatus.ONGOING
                } else if (messageLower.contains("incoming")) {
                    CallStatus.INCOMING
                } else {
                    CallStatus.MISSED
                }


                // call model
                val callModel = CallModel(
                    user = username.toString(),
                    callType = callType,
                    callStatus = callStatus,
                    time = messageTime,
                    avatarFileName = avatar
                )

                // save chat model to database
                val callDBHelper = CallDBHelper(context)
                callDBHelper.addCallItem(callModel)
                val intent = Intent("new_item_call")
                sendBroadcast(intent)

                Log.e(
                    "NotificationService",
                    "onNotificationPosted: " + callModel.toString()
                )
            }


        }


    }


    private fun Icon?.loadDrawables(context: Context): Drawable? {
        this ?: return null
        return try {
            this.loadDrawable(context)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Drawable?.toBitmap(): Bitmap? {
        if (this == null) return null
        if (this is BitmapDrawable) {
            return this.bitmap
        }
        val bitmap = Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
        return bitmap
    }


}
