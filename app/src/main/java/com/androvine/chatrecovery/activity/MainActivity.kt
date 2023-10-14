package com.androvine.chatrecovery.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.androvine.chatrecovery.ViewPagerAdapter
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
            }
        })
    }

}