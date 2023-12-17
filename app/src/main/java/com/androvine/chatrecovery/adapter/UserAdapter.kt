package com.androvine.chatrecovery.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.activity.MessageViewActivity
import com.androvine.chatrecovery.databinding.ItemUserBinding
import com.androvine.chatrecovery.db.MessageDBHelper
import com.androvine.chatrecovery.models.MessageModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class UserAdapter(private val context: Context, private val userList: MutableList<MessageModel>) :
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
        holder.binding.messageItem.setOnClickListener {

            val intent = Intent(context, MessageViewActivity::class.java)
            intent.putExtra("user", messageModel.user)
            context.startActivity(intent)

        }


        val timestamp = messageModel.time
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))
        holder.binding.tvTime.text = formattedTime


        holder.binding.messageItem.setOnLongClickListener {

            holder.binding.tvTime.visibility = View.GONE
            holder.binding.ivDeleteUser.visibility = View.VISIBLE

            true
        }

        holder.binding.ivDeleteUser.setOnClickListener {

            val dialog = Dialog(it.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_delete_files)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            dialog.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.cancel)
                .setOnClickListener {
                    holder.binding.tvTime.visibility = View.VISIBLE
                    holder.binding.ivDeleteUser.visibility = View.GONE
                    dialog.dismiss()
                }
            dialog.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.delete)
                .setOnClickListener {

                    deleteUser(position)

                    holder.binding.tvTime.visibility = View.VISIBLE
                    holder.binding.ivDeleteUser.visibility = View.GONE

                    dialog.dismiss()
                }
            dialog.show()

        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }



    fun deleteUser(position: Int) {
        val deletedUser = userList[position]
        val dbHelper = MessageDBHelper(context)
        val deletedRows = dbHelper.deleteByUser(deletedUser.user)

        if (deletedRows > 0) {
            userList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    //update userList
    fun updateList(userList: List<MessageModel>) {
        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()
    }


    class MessageViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root)

}