package com.androvine.chatrecovery.activity

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.databinding.ActivityPermissionBinding
import com.androvine.chatrecovery.permissionMVVM.PermissionRepository
import com.androvine.chatrecovery.permissionMVVM.PermissionStatus
import com.androvine.chatrecovery.permissionMVVM.PermissionViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PermissionActivity : AppCompatActivity() {

    private val binding: ActivityPermissionBinding by lazy {
        ActivityPermissionBinding.inflate(layoutInflater)
    }


    private val viewModel: PermissionViewModel by viewModel()
    private val permissionRepository: PermissionRepository by inject()


    // permission intent launcher
    private val permissionIntentLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (permissionRepository.hasNotificationPermission()) {
            viewModel.handleNotificationPermissionResult(true)
            return@registerForActivityResult
        } else {
            viewModel.handleNotificationPermissionResult(false)
            return@registerForActivityResult
        }
    }

    private var currentStatus = PermissionStatus.INITIAL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        viewModel.checkNotificationPermission()

        viewModel.hasNotificationPermission.observe(this) {

            when (it) {
                PermissionStatus.INITIAL -> {
                    currentStatus = PermissionStatus.INITIAL
                    setupInitialView()
                }

                PermissionStatus.GRANTED -> {
                    currentStatus = PermissionStatus.GRANTED
                    allowedUI()
                }

                PermissionStatus.DENIED -> {
                    currentStatus = PermissionStatus.DENIED
                    deniedUI()
                }

                null -> {
                    setupInitialView()
                }
            }


        }

        binding.btnAllow.setOnClickListener {

            if (binding.checkboxPermission.isChecked) {
                if (currentStatus == PermissionStatus.INITIAL || currentStatus == PermissionStatus.DENIED) {
                    permissionIntentLauncher.launch(permissionRepository.getNotificationPermissionIntent())
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "Please agree and check the box", Toast.LENGTH_SHORT)
                    .show()
                binding.checkboxPermission.error = "Please agree and check the box"
            }
        }


        binding.checkboxPermission.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxPermission.error = null
            }
        }

    }

    private fun deniedUI() {

        binding.notificationTitle.text = "Notification Access Error"
        binding.notificationDesc.text =
            "Sorry, we can't recover your deleted messages without notification access."
        binding.permissionImg.setImageResource(R.drawable.ic_warning_permission)
        binding.permissionDescription.text =
            "Let's try again and allow notification access."
        binding.checkboxPermission.visibility = View.GONE
        binding.btnAllow.text = "Try Again"
        binding.btnPrivacyPolicy.text = "Privacy Policy"

        binding.btnHowToAllow.text =
            Html.fromHtml("<u>How to allow?</u>", Html.FROM_HTML_MODE_LEGACY)
        binding.btnHowToAllow.visibility = View.VISIBLE

    }

    private fun allowedUI() {

        binding.notificationTitle.text = "Notification Access Granted"
        binding.notificationDesc.text =
            "You're all set to recover your deleted messages."
        binding.permissionImg.setImageResource(R.drawable.ic_checkmark_permission)
        binding.permissionDescription.text =
            ""
        binding.checkboxPermission.visibility = View.INVISIBLE
        binding.btnAllow.text = "Let's Go"
        binding.btnPrivacyPolicy.text = "Privacy Policy"

        binding.btnHowToAllow.visibility = View.INVISIBLE

    }

    private fun setupInitialView() {

        binding.notificationTitle.text = "We need Notification Access"
        binding.notificationDesc.text =
            "Chat Recovery needs notification access to recover your deleted Whatsapp messages."
        binding.permissionImg.setImageResource(R.drawable.permission_img)
        binding.permissionDescription.text =
            "Notification access is required to run the apps core functionality. Permission will be used for monitoring and storing your deleted messages."
       binding.checkboxPermission.visibility = View.VISIBLE
        binding.checkboxPermission.text = "I agree to allow notification access"
        binding.btnAllow.text = "Allow Access"
        binding.btnPrivacyPolicy.text = "Privacy Policy"

        binding.btnHowToAllow.text =
            Html.fromHtml("<u>How to allow?</u>", Html.FROM_HTML_MODE_LEGACY)
        binding.btnHowToAllow.visibility = View.VISIBLE

    }



    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}