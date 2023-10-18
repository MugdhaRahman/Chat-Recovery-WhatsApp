package com.androvine.chatrecovery.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.adapter.ViewPagerAdapter
import com.androvine.chatrecovery.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewPagerAdapter: ViewPagerAdapter by lazy {
        ViewPagerAdapter(
            supportFragmentManager, lifecycle
        )
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupBottomNav()


        val internalStorageDir = this.filesDir

        val largeIconDir = File(internalStorageDir, "avatars")
        if (!largeIconDir.exists()) {
            largeIconDir.mkdir()
        }

        val intentFilter = IntentFilter("new_item_message")
        intentFilter.addAction("new_item_call")
        registerReceiver(broadcastReceiver, intentFilter)


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

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "new_item_message") {
                Log.e("MainActivity", "newItemAdded Message")

            }

            if (intent?.action == "new_item_call") {
                Log.e("MainActivity", "newItemAdded")

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }


}