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
import com.androvine.chatrecovery.adapter.UserAdapter
import com.androvine.chatrecovery.databinding.FragmentHomeBinding
import com.androvine.chatrecovery.db.CallDBHelper
import com.androvine.chatrecovery.db.MessageDBHelper
import com.androvine.chatrecovery.models.CallModel
import com.androvine.chatrecovery.models.MessageModel

class FragmentHome : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var userAdapter: UserAdapter

    private lateinit var callAdapter: CallAdapter
    private val callList = mutableListOf<CallModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        callAdapter = CallAdapter(mutableListOf())

        val intentFilter = IntentFilter("new_item_message")
        requireActivity().registerReceiver(broadcastReceiver, intentFilter)

        userAdapter = UserAdapter(requireContext(), mutableListOf())
        binding.rvChatList.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = userAdapter
        }



        return binding.root
    }

    override fun onResume() {
        super.onResume()

        loadMessageListData()
        loadCallListData()

        binding.tvTotalCalls.text = callAdapter.itemCount.toString()

        binding.tvTotalChats.text =
            MessageDBHelper(requireActivity()).getAllMessage().size.toString()
        binding.tvTotalUser.text = userAdapter.itemCount.toString()


    }


    private fun loadMessageListData() {
        val dbHelper = MessageDBHelper(requireActivity())
        val messageList = dbHelper.getAllMessage()

        val latestMessagesMap = mutableMapOf<String, MessageModel>()

        for (messageModel in messageList) {
            val existingLatestMessage = latestMessagesMap[messageModel.user]

            // Check if there's no existing latest message or if the current message is newer
            if (existingLatestMessage == null || messageModel.time > existingLatestMessage.time) {
                latestMessagesMap[messageModel.user] = messageModel
            }
        }

        val uniqueUserMessageList = latestMessagesMap.values.toList()

        userAdapter.updateList(uniqueUserMessageList)
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
            if (intent?.action == "new_item_message") {
                loadMessageListData()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

}