package com.mrapps.chatrecovery.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mrapps.chatrecovery.R
import com.mrapps.chatrecovery.databinding.ActivityIntroBinding
import com.mrapps.chatrecovery.utils.IntroUtils
import com.mrapps.chatrecovery.utils.PermNotificationUtils

class IntroActivity : AppCompatActivity() {

    private val binding: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(layoutInflater)
    }

    private lateinit var layouts: IntArray

    private lateinit var introUtils: IntroUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        introUtils = IntroUtils(this)

        setupWindow()

        setUpLayouts()

        setUpClickListeners()


    }


    private fun setupWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION") window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    private fun setUpLayouts() {

        layouts = intArrayOf(
            R.layout.intro_screen_1, R.layout.intro_screen_2, R.layout.intro_screen_3
        )

        setUpFirstAndSecondPage()


        binding.viewPager.adapter = IntroAdapter(this, layouts)

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {

                when (position) {
                    0 -> setUpFirstAndSecondPage()
                    1 -> setUpSecondPage()
                    2 -> setUpThirdPage()
                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }


        })

    }

    private fun setUpClickListeners() {

        if (!introUtils.isFirstTimeLaunch()) {
            goToHome()
        }

        binding.startButton.setOnClickListener {
            goToHome()
        }

        binding.lottieNext.setOnClickListener {

            if (binding.viewPager.currentItem == 0) {
                binding.viewPager.setCurrentItem(1, true)
            } else if (binding.viewPager.currentItem == 1) {
                binding.viewPager.setCurrentItem(2, true)
            }

        }

    }

    private fun setUpFirstAndSecondPage() {
        binding.startButton.visibility = View.GONE
        binding.lottieNext.visibility = View.VISIBLE
        binding.ivDot.setImageResource(R.drawable.intro_dot_1)
    }

    private fun setUpSecondPage() {
        binding.startButton.visibility = View.GONE
        binding.lottieNext.visibility = View.VISIBLE
        binding.ivDot.setImageResource(R.drawable.intro_dot_2)
    }

    private fun setUpThirdPage() {
        binding.startButton.visibility = View.VISIBLE
        binding.lottieNext.visibility = View.GONE
        binding.ivDot.setImageResource(R.drawable.intro_dot_3)
    }

    private fun goToHome() {
        introUtils.setFirstTimeLaunch(false)

        if (!PermNotificationUtils.isNotificationPermissionGranted(this)) {
            startActivity(Intent(this, PermissionActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }


    class IntroAdapter(private val context: Context, private val layouts: IntArray) :
        PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }


}