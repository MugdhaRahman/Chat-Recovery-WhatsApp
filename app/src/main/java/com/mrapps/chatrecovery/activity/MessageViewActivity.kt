package com.mrapps.chatrecovery.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.mrapps.chatrecovery.R
import com.mrapps.chatrecovery.adapter.MessageAdapter
import com.mrapps.chatrecovery.databinding.ActivityMessageViewBinding
import com.mrapps.chatrecovery.db.MessageDBHelper
import com.mrapps.chatrecovery.models.MessageModel

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
        popupMenu.inflate(R.menu.share)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.actionShare -> {

                    val user = intent.getStringExtra("user")
                    val dbHelper = MessageDBHelper(this)
                    val messageList = dbHelper.getMessageByUser(user!!)
                    shareMessages(user, messageList)

                    true
                }

                else -> false
            }
        }

        //set menu item icon
        val menuItem = popupMenu.menu.findItem(R.id.actionShare)
        menuItem.setIcon(R.drawable.ic_share)



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
        stringBuilder.append("Recovered Messages from $user:\n\n")

        for (messageModel in messageList) {
            stringBuilder.append("${messageModel.user}: ${messageModel.message}\n")
        }

        return stringBuilder.toString()
    }


}