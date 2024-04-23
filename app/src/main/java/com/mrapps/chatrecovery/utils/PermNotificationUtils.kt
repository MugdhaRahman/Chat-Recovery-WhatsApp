package com.mrapps.chatrecovery.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import com.mrapps.chatrecovery.databinding.DalogConsentNotificationBinding
import com.mrapps.chatrecovery.databinding.DialogPermissionNotificationBinding

class PermNotificationUtils(private val activity: Activity) {


    companion object {
        fun isNotificationPermissionGranted(activity: Activity): Boolean {
            val packageName = activity.packageName
            val flat = Settings.Secure.getString(
                activity.contentResolver, "enabled_notification_listeners"
            )
            if (!flat.isNullOrEmpty()) {
                val names = flat.split(":").toTypedArray()
                for (name in names) {
                    val cn = name.split("/")
                    if (cn[0] == packageName) {
                        return true
                    }
                }
            }
            return false
        }

    }

    private val prefName = "notifyConsent"
    private val prefNotifyConsent = "hasNotifyConsent"

    private val sharedPreferences: SharedPreferences =
        activity.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()


    fun showNotifyConsent() {
        if (sharedPreferences.getBoolean(prefNotifyConsent, false)) {
            showNotificationDialog()
        } else {
            val dialog = Dialog(activity)

            val binding = DalogConsentNotificationBinding.inflate(activity.layoutInflater)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(binding.root)

            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)


            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    binding.checkBoxWarning.visibility = View.GONE
                }
            }


            binding.btnConfirm.setOnClickListener {
                if (!binding.checkBox.isChecked) {
                    binding.checkBoxWarning.visibility = View.VISIBLE
                } else {
                    editor.putBoolean(prefNotifyConsent, true).apply()
                    dialog.dismiss()
                    askNotificationPermission()
                }
            }

            binding.btnDecline.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }


    }

    fun showNotificationDialog() {

        val dialog = Dialog(activity)
        val binding: DialogPermissionNotificationBinding =
            DialogPermissionNotificationBinding.inflate(activity.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)

        binding.btnAllow.setOnClickListener {
            dialog.dismiss()
            if (!sharedPreferences.getBoolean(prefNotifyConsent, false)) {
                showNotifyConsent()
            } else {
                askNotificationPermission()
            }
        }
        dialog.show()


    }

    fun askNotificationPermission() {
        if (!isNotificationPermissionGranted(activity)) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            activity.startActivity(intent)
        }

    }


}


