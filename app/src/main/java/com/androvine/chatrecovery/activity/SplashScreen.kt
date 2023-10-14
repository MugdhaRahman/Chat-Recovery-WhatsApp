package com.androvine.chatrecovery.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.androvine.chatrecovery.utils.IntroUtils
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.utils.PermNotificationUtils

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val introUtils = IntroUtils(this)
            if (introUtils.isFirstTimeLaunch()) {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            } else {
                if (!PermNotificationUtils.isNotificationPermissionGranted(this)) {
                    startActivity(Intent(this, PermissionActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }, 2000)


    }
}