package com.androvine.chatrecovery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    private val datePositions = HashMap<String, Int>()


    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {


        val messageModel = userList[position]

        holder.binding.msgText.text = messageModel.message


        val timestamp = messageModel.time
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))

        val msgDate = messageModel.time
        val date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val formattedDate = date.format(Date(msgDate))

        holder.binding.msgTime.text = formattedTime

        // Show date only if it's the first occurrence of the date
        if (!datePositions.containsKey(formattedDate) || datePositions[formattedDate] == position) {
            holder.binding.msgDate.text = formattedDate
            holder.binding.msgDate.visibility = View.VISIBLE
            datePositions[formattedDate] = position
        } else {
            holder.binding.msgDate.visibility = View.GONE
        }


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