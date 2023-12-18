package com.androvine.chatrecovery.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

object MFileUtils {

    private const val TAG = "MFileUtils"

    fun copyFileOrDirectory(src: String, dest: String) {
        try {
            val sourceFile = File(src)
            val destFile = File(dest, sourceFile.name)

            if (sourceFile.isDirectory) {
                for (file in sourceFile.list() ?: emptyArray()) {
                    copyFileOrDirectory(File(sourceFile, file).path, destFile.path)
                }
            } else {
                copyFile(sourceFile, destFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File) {
        destFile.parentFile?.let { parent ->
            if (!parent.exists()) {
                parent.mkdirs()
            }
        }

        if (!destFile.exists()) {
            destFile.createNewFile()
        }

        var sourceChannel: FileChannel? = null
        var destChannel: FileChannel? = null

        try {
            sourceChannel = FileInputStream(sourceFile).channel
            destChannel = FileOutputStream(destFile).channel
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size())
            Log.d(TAG, "media saved")
        } finally {
            sourceChannel?.close()
            destChannel?.close()
        }
    }

    // Method for Android R and above using SAF
    fun copyFileOrDirectory(context: Context, srcUri: Uri, destPath: String) {
        val srcFile = DocumentFile.fromSingleUri(context, srcUri) ?: return

        if (srcFile.isDirectory) {
            for (childFile in srcFile.listFiles()) {
                copyFileOrDirectory(context, childFile.uri, "$destPath/${childFile.name}")
            }
        } else {
            val destParent = DocumentFile.fromFile(File(destPath))
            if (!destParent.exists() && destParent.isDirectory) {
                val parts = destPath.split(File.separator)
                val grandParent = DocumentFile.fromFile(
                    File(
                        destPath.substring(
                            0,
                            destPath.lastIndexOf(File.separator)
                        )
                    )
                )
                grandParent.createDirectory(parts.last())
            }

            val destFile = srcFile.type?.let {
                srcFile.name?.let { it1 ->
                    destParent.createFile(
                        it,
                        it1
                    )
                }
            }
            destFile?.let { copyFile(context, srcFile.uri, it.uri) }
        }
    }

    fun copyFile(context: Context, srcUri: Uri, destUri: Uri) {
        try {
            context.contentResolver.openInputStream(srcUri)?.use { inputStream ->
                context.contentResolver.openOutputStream(destUri)?.use { outputStream ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(inputStream, outputStream)
                    } else {
                        val buffer = ByteArray(4096)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                    }
                }
            }
            Log.d(TAG, "media saved via SAF")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}