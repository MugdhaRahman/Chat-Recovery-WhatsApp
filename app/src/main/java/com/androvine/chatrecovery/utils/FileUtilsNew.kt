package com.androvine.chatrecovery.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.IOException

object FileUtilsNew {

    fun copyFileOrDirectory(src: String, dest: String) {
        try {
            val sourceFile = File(src)
            val destinationFile = File(dest, sourceFile.name)

            if (sourceFile.isDirectory) {
                sourceFile.list()?.forEach {
                    copyFileOrDirectory(File(sourceFile, it).path, destinationFile.path)
                }
            } else {
                copyFile(sourceFile, destinationFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(source: File, destination: File) {
        if (!destination.parentFile?.exists()!!) {
            destination.parentFile?.mkdirs()
        }
        if (!destination.exists()) {
            destination.createNewFile()
        }

        source.inputStream().use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
                Log.d("mediaSaved", "media saved")
            }
        }
    }

    // Method for Android R and above using SAF
    private fun copyFileOrDirectory(context: Context, srcUri: Uri, destPath: String) {
        val srcFile = DocumentFile.fromSingleUri(context, srcUri) ?: return

        if (srcFile.isDirectory) {
            srcFile.listFiles().forEach {
                copyFileOrDirectory(context, it.uri, "$destPath/${it.name}")
            }
        } else {
            val destParent = File(destPath).let {
                DocumentFile.fromFile(it)
            }

            val destFile = srcFile.type?.let {
                srcFile.name?.let { it1 ->
                    destParent.createFile(
                        it,
                        it1
                    )
                }
            } ?: return

            copyFile(context, srcFile.uri, destFile.uri)
        }
    }

    private fun copyFile(context: Context, srcUri: Uri, destUri: Uri) {
        try {
            context.contentResolver.openInputStream(srcUri)?.use { input ->
                context.contentResolver.openOutputStream(destUri)?.use { output ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(input, output)
                    } else {
                        input.copyTo(output)
                    }
                }
            }
            Log.d("mediaSaved", "media saved via SAF")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
