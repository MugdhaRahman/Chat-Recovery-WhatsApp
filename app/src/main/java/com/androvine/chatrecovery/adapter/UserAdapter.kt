package com.androvine.chatrecovery.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.databinding.ItemUserBinding
import com.androvine.chatrecovery.models.MessageModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class UserAdapter(private val userList: MutableList<MessageModel>) :
    RecyclerView.Adapter<UserAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {


        val messageModel = userList[position]

        holder.binding.tvName.text = messageModel.user
        holder.binding.tvMessageSummary.text = messageModel.messageSummary


        val timestamp = messageModel.time
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))
        holder.binding.tvTime.text = formattedTime


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    //update userlist
    fun updateList(userList: List<MessageModel>) {
        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()
    }


    class MessageViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

}