package com.androvine.chatrecovery.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.adapter.ViewPagerAdapter
import com.androvine.chatrecovery.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewPagerAdapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(
            supportFragmentManager, lifecycle
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupBottomNav()

    }

    private fun setupBottomNav() {
        binding.viewPager.adapter = viewPagerAdapter
        binding.bottomBar.setOnItemSelectedListener {
            binding.viewPager.currentItem = it

        }
        binding.viewPager.registerOnPageChangeCallback(object :
            androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bottomBar.itemActiveIndex = position
                setStatusBar(position)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    private fun setStatusBar(position: Int) {

        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        when (position) {
            0 -> {
                binding.toolbarTitle.text = getString(R.string.app_name)
            }

            1 -> {
                binding.toolbarTitle.text = "Call"
            }

            2 -> {
                binding.toolbarTitle.text = "Media"
            }
        }

    }


}