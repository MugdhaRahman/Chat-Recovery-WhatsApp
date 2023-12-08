package com.androvine.chatrecovery.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.androvine.chatrecovery.R
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

        binding.menuBtn.setOnClickListener {

            showPopupMenu(it)

        }

    }


    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.share) // Replace with your menu resource ID

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.share -> {

                    val user = intent.getStringExtra("user")
                    val dbHelper = MessageDBHelper(this)
                    val messageList = dbHelper.getMessageByUser(user!!)
                    shareMessages(user, messageList)

                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    private fun shareMessages(user: String, messageList: List<MessageModel>) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"

        val shareMessage = buildShareMessage(user, messageList)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)

        startActivity(Intent.createChooser(shareIntent, "Share Messages"))
    }


    private fun buildShareMessage(user: String, messageList: List<MessageModel>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("Messages for $user:\n\n")

        for (messageModel in messageList) {
            stringBuilder.append("${messageModel.user}: ${messageModel.message}\n")
        }

        return stringBuilder.toString()
    }


}