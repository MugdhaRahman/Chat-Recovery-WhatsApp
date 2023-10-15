package com.androvine.chatrecovery.utils

import android.Manifest
import android.app.Activity
import androidx.core.app.ActivityCompat

class PermStorageUtils(private val activity: Activity) {


    companion object {


        const val STORAGE_REQUEST_CODE = 1402

        fun isStoragePermissionGranted(activity: Activity): Boolean {
            return ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == 0 && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == 0
        }


    }

    private val listOfPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    fun askStoragePermission() {
        ActivityCompat.requestPermissions(
            activity, listOfPermissions, STORAGE_REQUEST_CODE
        )
    }


}