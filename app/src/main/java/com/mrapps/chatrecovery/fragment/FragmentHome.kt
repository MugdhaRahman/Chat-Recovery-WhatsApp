package com.mrapps.chatrecovery.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrapps.chatrecovery.adapter.UserAdapter
import com.mrapps.chatrecovery.databinding.FragmentHomeBinding
import com.mrapps.chatrecovery.db.CallDBHelper
import com.mrapps.chatrecovery.db.MessageDBHelper
import com.mrapps.chatrecovery.models.MessageModel
import java.util.Locale

class FragmentHome : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var userAdapter: UserAdapter

    private var mQueryString: String? = null
    private val mHandler = Handler(
        Looper.getMainLooper()
    )

    private var currentUserList = mutableListOf<MessageModel>()

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


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

        binding.tvTotalCalls.text = CallDBHelper(requireContext()).getAllCall().size.toString()

        binding.tvTotalChats.text =
            MessageDBHelper(requireActivity()).getAllMessage().size.toString()

        binding.tvTotalUser.text = userAdapter.itemCount.toString()

        updateUI()

        setupSearchView()

    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                mQueryString = newText
                mHandler.postDelayed({

                    startSearch(mQueryString)

                }, 200)
                return true
            }
        })
    }

    private fun startSearch(query: String?) {
        if (query.isNullOrEmpty()) {
            loadMessageListData()
            binding.llEmptyChatHome.visibility = View.GONE
        } else {
            val lowercaseQuery = query.lowercase(Locale.ROOT)

            Log.e("TAG", "startSearch: $lowercaseQuery")

            Log.e("TAG", "startSearch: $currentUserList")

            val searchResults = currentUserList.filter {
                it.user.lowercase(Locale.ROOT).contains(lowercaseQuery)
            }

            if (searchResults.isEmpty()) {
                binding.llEmptyChatHome.visibility = View.VISIBLE
                userAdapter.updateList(emptyList())
            } else {
                binding.llEmptyChatHome.visibility = View.GONE
                userAdapter.updateList(searchResults)
            }
        }
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
        currentUserList = uniqueUserMessageList.toMutableList()

        userAdapter.updateList(uniqueUserMessageList)
    }


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "new_item_message") {
                loadMessageListData()
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val totalChats = MessageDBHelper(requireActivity()).getAllMessage().size

        if (totalChats == 0) {
            binding.llEmptyChatHome.visibility = View.VISIBLE
            binding.rvChatList.visibility = View.GONE
        } else {
            binding.llEmptyChatHome.visibility = View.GONE
            binding.rvChatList.visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

}