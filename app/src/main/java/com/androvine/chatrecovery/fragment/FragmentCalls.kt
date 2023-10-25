package com.androvine.chatrecovery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.androvine.chatrecovery.adapter.CallAdapter
import com.androvine.chatrecovery.databinding.FragmentCallsBinding
import com.androvine.chatrecovery.db.CallDBHelper
import com.androvine.chatrecovery.models.CallModel


class FragmentCalls : Fragment() {

    private val binding: FragmentCallsBinding by lazy {
        FragmentCallsBinding.inflate(layoutInflater)
    }

    private val callList = mutableListOf<CallModel>()
    private lateinit var callAdapter: CallAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return binding.root
    }

}