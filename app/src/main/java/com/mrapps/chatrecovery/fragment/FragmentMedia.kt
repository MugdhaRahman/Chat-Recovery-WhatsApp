package com.mrapps.chatrecovery.fragment

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mrapps.chatrecovery.R
import com.mrapps.chatrecovery.activity.MainActivity
import com.mrapps.chatrecovery.adapter.MediaAdapter
import com.mrapps.chatrecovery.databinding.FragmentMediaBinding
import com.mrapps.chatrecovery.permissionMVVM.RecoverMediaViewModel
import com.mrapps.chatrecovery.utils.BuildVersion
import com.mrapps.chatrecovery.utils.MediaFilesRepository
import com.mrapps.chatrecovery.utils.PermSAFUtils
import com.mrapps.chatrecovery.utils.PermStorageUtils
import com.google.android.material.tabs.TabLayout

@Suppress("DEPRECATION")
class FragmentMedia : Fragment() {

    private val binding: FragmentMediaBinding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var permSAFUtils: PermSAFUtils
    private lateinit var permStorageUtils: PermStorageUtils
    private lateinit var repository: MediaFilesRepository
    private lateinit var viewModel: RecoverMediaViewModel


    private val imageList: MutableList<MainActivity.RecoveredMedia> = mutableListOf()
    private val videoList: MutableList<MainActivity.RecoveredMedia> = mutableListOf()

    private lateinit var recoverMediaAdapter: MediaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        setupIntentLauncher()

        checkPermissions()

        setupPermission()

        setupTabs()

        getFiles()

        setupRV()

        return binding.root
    }

    private fun setupTabs() {

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                when (tab?.position) {
                    0 -> {

                        binding.rvPhotos.visibility = View.VISIBLE
                        binding.rvVideos.visibility = View.GONE
                        loadImage()
                    }

                    1 -> {

                        binding.rvPhotos.visibility = View.GONE
                        binding.rvVideos.visibility = View.VISIBLE
                        loadVideo()

                    }
                }

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun checkPermissions() {

        if (BuildVersion.isAndroidR()) {
            if (PermSAFUtils.verifySAF(requireActivity())) {
                binding.permissionLayout.visibility = View.GONE
                binding.llMedia.visibility = View.VISIBLE
            } else {
                binding.permissionLayout.visibility = View.VISIBLE
                binding.llMedia.visibility = View.GONE
            }
        } else {
            if (PermStorageUtils.isStoragePermissionGranted(requireActivity())) {
                binding.permissionLayout.visibility = View.GONE
                binding.llMedia.visibility = View.VISIBLE
            } else {
                binding.permissionLayout.visibility = View.VISIBLE
                binding.llMedia.visibility = View.GONE
            }
        }
    }

    private fun setupPermission() {

        binding.btnAllow.setOnClickListener {
            if (BuildVersion.isAndroidR()) {
                permSAFUtils.showSAFDialog()
            } else {
                permStorageUtils.askStoragePermission()
            }
        }

        permStorageUtils.setPermissionCallback(object : PermStorageUtils.PermissionCallback {
            override fun onPermissionGranted() {
                binding.permissionLayout.visibility = View.GONE
                binding.llMedia.visibility = View.VISIBLE
            }

            override fun onPermissionDenied() {
                binding.imgPermission.setImageResource(R.drawable.img_permission_saf_2)
                binding.tvPermission.text =
                    "Storage permission is required to access media files \nplease try again."
                binding.tvAllow.text = "Try Again"

            }
        })


    }

    private fun setupIntentLauncher() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                permSAFUtils.addSAFPermission(result.data!!)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    permSAFUtils.showSAFDialog()
                }
            }
        }
        permSAFUtils = PermSAFUtils(requireActivity(), requestPermissionLauncher)
        permStorageUtils = PermStorageUtils(requireActivity())

    }


    private fun getFiles() {

        repository = MediaFilesRepository(requireActivity().applicationContext)
        viewModel = RecoverMediaViewModel(repository)
        recoverMediaAdapter = MediaAdapter(requireContext(), mutableListOf())

        viewModel.getFiles()

        viewModel.imageList.observe(requireActivity()) {
            imageList.clear()
            imageList.addAll(it)
            Log.e("TAG", "imageList: " + imageList.size)
            loadImage()

        }

        viewModel.videoList.observe(requireActivity()) {
            videoList.clear()
            videoList.addAll(it)
            Log.e("TAG", "videoList: " + videoList.size)
            loadVideo()
        }

    }

    private fun loadVideo() {
        recoverMediaAdapter.updateList(videoList)

        if (videoList.isEmpty()) {
            binding.llNoMedia.visibility = View.VISIBLE
        } else {
            binding.llNoMedia.visibility = View.GONE
        }
    }

    private fun loadImage() {
        recoverMediaAdapter.updateList(imageList)

        if (imageList.isEmpty()) {
            binding.llNoMedia.visibility = View.VISIBLE
        } else {
            binding.llNoMedia.visibility = View.GONE
        }
    }

    private fun setupRV() {
        binding.rvPhotos.apply {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = recoverMediaAdapter
        }

        binding.rvVideos.apply {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = recoverMediaAdapter
        }

    }


    override fun onResume() {
        super.onResume()
        checkPermissions()
        loadImage()
        binding.tabLayout.getTabAt(0)?.select()
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permStorageUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}