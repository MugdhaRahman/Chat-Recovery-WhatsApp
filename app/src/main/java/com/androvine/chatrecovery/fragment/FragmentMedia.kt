package com.androvine.chatrecovery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androvine.chatrecovery.databinding.FragmentMediaBinding

class FragmentMedia : Fragment() {

    private val binding: FragmentMediaBinding by lazy {
        FragmentMediaBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

}