package com.androvine.chatrecovery.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.model.KeyPath
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.permissionMVVM.PermissionRepository
import com.androvine.chatrecovery.utils.IntroUtils
import org.koin.android.ext.android.inject

class SplashScreen : AppCompatActivity() {


    private val permissionRepository: PermissionRepository by inject()

   private lateinit var lottieAnimationView: LottieAnimationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        lottieAnimationView = findViewById(R.id.animation_view)

        lottieAnimationView.addValueCallback(
            KeyPath("**"),
            LottieProperty.COLOR_FILTER
        ) { PorterDuffColorFilter(Color.parseColor("#7A59DA"), PorterDuff.Mode.SRC_ATOP) }


        Handler(Looper.getMainLooper()).postDelayed({
            val introUtils = IntroUtils(this)
            if (introUtils.isFirstTimeLaunch()) {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            } else {
                if (!permissionRepository.hasNotificationPermission()) {
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