package com.androvine.chatrecovery.fragment

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.databinding.FragmentMediaBinding
import com.androvine.chatrecovery.utils.BuildVersion
import com.androvine.chatrecovery.utils.PermSAFUtils
import com.androvine.chatrecovery.utils.PermStorageUtils

@Suppress("DEPRECATION")
class FragmentMedia : Fragment() {

    private val binding: FragmentMediaBinding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var permSAFUtils: PermSAFUtils
    private lateinit var permStorageUtils: PermStorageUtils

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        setupIntentLauncher()

        setupPermission()

        return binding.root
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

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permStorageUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}