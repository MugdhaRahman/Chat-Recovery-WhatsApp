package com.androvine.chatrecovery.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.databinding.ActivityPermissionBinding
import com.androvine.chatrecovery.databinding.BottomsheetPermisssionBinding
import com.androvine.chatrecovery.utils.PermNotificationUtils
import com.google.android.material.bottomsheet.BottomSheetDialog


class PermissionActivity : AppCompatActivity() {

    private val binding: ActivityPermissionBinding by lazy {
        ActivityPermissionBinding.inflate(layoutInflater)
    }

    private lateinit var permNotificationUtils: PermNotificationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        permNotificationUtils = PermNotificationUtils(this)

        checkPermissions()

        setupOnClicks()

    }

    private fun checkPermissions() {

        if (PermNotificationUtils.isNotificationPermissionGranted(this)) {
            binding.permissionImg.setImageResource(R.drawable.permission_done_img)
            binding.notificationTitle.text = "We’re ready to go"
            binding.notificationDesc.text = "Let’s recover some deleted message."
            binding.btnAllowText.text = "Get Started"
            binding.btnHowToAllow.visibility = View.GONE

        } else {
            binding.permissionImg.setImageResource(R.drawable.permission_img)
            binding.notificationTitle.text = "Allow notifications and never \nmiss a message"
            binding.notificationDesc.text =
                "allow notification and chat recovery will \nnrecover all the message for you.\nYou can see all the deleted and unseen \nmessage."
            binding.btnAllowText.text = "Ok"
            binding.btnHowToAllow.visibility = View.VISIBLE
        }

    }

    private fun setupOnClicks() {
        binding.btnAllow.setOnClickListener {
            if (!PermNotificationUtils.isNotificationPermissionGranted(this)) {
                permNotificationUtils.showNotificationDialog()
            }
        }

        binding.btnHowToAllow.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this)
            val bottomSheetPermission: BottomsheetPermisssionBinding =
                BottomsheetPermisssionBinding.inflate(
                    layoutInflater
                )
            bottomSheetDialog.setContentView(bottomSheetPermission.root)
            bottomSheetDialog.setCancelable(true)
            bottomSheetDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            bottomSheetDialog.window!!.setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            bottomSheetPermission.btnDismiss.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }


}