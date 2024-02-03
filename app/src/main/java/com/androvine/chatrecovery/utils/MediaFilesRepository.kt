package com.androvine.chatrecovery.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.activity.MainActivity
import java.io.File

class MediaFilesRepository(private val context: Context) {

    fun getImageFiles(): List<MainActivity.RecoveredMedia> {
        val imageList = mutableListOf<MainActivity.RecoveredMedia>()
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString() + File.separator + context.resources.getString(R.string.app_name) + "/Images"
        val file = File(path)
        val arrayOfFiles = dirListByAscendingDate(file)
        arrayOfFiles?.forEach {
            if (!it.isDirectory && checkIfImageFile(it)) {
                val uri = it.path.toString()
                val recoverMediaModel =
                    MainActivity.RecoveredMedia(uri, MainActivity.MediaType.IMAGE)
                imageList.add(recoverMediaModel)
            }
        }

        return imageList
    }

    fun getVideoFiles(): List<MainActivity.RecoveredMedia> {
        val videoList = mutableListOf<MainActivity.RecoveredMedia>()
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            .toString() + File.separator + context.resources.getString(R.string.app_name) + "/Videos"
        val file = File(path)
        val arrayOfFiles = dirListByAscendingDate(file)
        arrayOfFiles?.forEach {
            if (!it.isDirectory && checkIfVideoFile(it)) {
                val uri = it.path.toString()
                val recoverMediaModel =
                    MainActivity.RecoveredMedia(uri, MainActivity.MediaType.VIDEO)
                videoList.add(recoverMediaModel)
            }
        }

        return videoList
    }

    private fun dirListByAscendingDate(folder: File): Array<File>? {
        if (!folder.isDirectory) {
            return null
        }
        val sortedByDate = folder.listFiles() ?: return null
        if (sortedByDate.isEmpty() || sortedByDate.size <= 1) {
            return sortedByDate
        }
        sortedByDate.sortWith { object1, object2 ->
            (object1.lastModified() - object2.lastModified()).toInt()
        }
        return sortedByDate
    }

    private fun checkIfImageFile(file: File?): Boolean {
        var boolean = false

        try {
            if (file != null && file.hasExtension()) {
                val name = file.toURI().toString()
                Log.e("TAG", "checkIfImageFile: " + file.path)
                val lastPathSegment = name.substring(name.lastIndexOf("."))
                if (lastPathSegment == ".jpg" || lastPathSegment == ".jpeg" || lastPathSegment == ".png" || lastPathSegment == ".gif") {
                    boolean = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            boolean = false
        }



        return boolean
    }

    private fun checkIfVideoFile(file: File?): Boolean {
        var boolean = false

        try {

            if (file != null && file.hasExtension()) {
                val name = file.toURI().toString()
                Log.e("TAG", "checkIfVideoFile: " + file.path)

                val lastPathSegment = name.substring(name.lastIndexOf("."))
                if (lastPathSegment == ".mp4" || lastPathSegment == ".mkv" || lastPathSegment == ".avi" || lastPathSegment == ".webm") {
                    boolean = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            boolean = false
        }


        return boolean

    }

    private fun File.hasExtension(): Boolean {
        return name.lastIndexOf(".") != -1
    }

}
