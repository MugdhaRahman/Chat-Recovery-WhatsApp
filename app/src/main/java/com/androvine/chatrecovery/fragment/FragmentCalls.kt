package com.androvine.chatrecovery.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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

        val intentFilter = IntentFilter("new_item_call")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)

        callAdapter = CallAdapter(requireContext(), mutableListOf())
        binding.recyclerViewCalls.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = callAdapter
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadCallListData()
        updateUI()
    }


    private fun loadCallListData() {
        val dbHelper = CallDBHelper(requireContext())
        val callListData = dbHelper.getAllCall()

        callList.clear()
        callList.addAll(callListData)
        Log.e("FragmentCalls", "loadCallListData: " + callList.size)
        callAdapter.updateList(callList)
    }


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "new_item_call") {
                loadCallListData()
                updateUI()
            }
        }
    }

    fun updateUI() {
        val totalCall = callList.size

        if (totalCall == 0) {
            binding.llEmptyCall.visibility = View.VISIBLE
            binding.recyclerViewCalls.visibility = View.GONE
        } else {
            binding.llEmptyCall.visibility = View.GONE
            binding.recyclerViewCalls.visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

}