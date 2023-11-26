package com.androvine.chatrecovery.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androvine.chatrecovery.adapter.MessageAdapter
import com.androvine.chatrecovery.databinding.ActivityMessageViewBinding
import com.androvine.chatrecovery.db.MessageDBHelper
import com.androvine.chatrecovery.models.MessageModel

class MessageViewActivity : AppCompatActivity() {

    private val binding: ActivityMessageViewBinding by lazy {
        ActivityMessageViewBinding.inflate(layoutInflater)
    }

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        val user = intent.getStringExtra("user")
        binding.toolbarTitle.text = user

        //get message by user
        val dbHelper = MessageDBHelper(this)
        val messageList = dbHelper.getMessageByUser(user!!)
        messageAdapter = MessageAdapter(this, messageList as MutableList<MessageModel>)
        binding.messageRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MessageViewActivity)
            adapter = messageAdapter
        }

    }
}