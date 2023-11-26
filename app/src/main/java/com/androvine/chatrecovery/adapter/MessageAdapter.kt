package com.androvine.chatrecovery.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.activity.MessageViewActivity
import com.androvine.chatrecovery.databinding.ItemMessageBinding
import com.androvine.chatrecovery.models.MessageModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(
    private val context: Context,
    private val userList: MutableList<MessageModel>
) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {


        val messageModel = userList[position]

        holder.binding.msgText.text = messageModel.message


        val timestamp = messageModel.time
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))

        holder.binding.msgTime.text = formattedTime


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    //update userList
    fun updateList(userList: List<MessageModel>) {
        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()
    }


    class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

}