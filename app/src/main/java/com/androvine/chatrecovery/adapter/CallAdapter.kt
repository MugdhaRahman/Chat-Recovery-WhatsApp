package com.androvine.chatrecovery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.databinding.ItemCallsBinding
import com.androvine.chatrecovery.models.CallModel
import com.androvine.chatrecovery.models.CallStatus
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class CallAdapter(private val context: Context, private val callList: MutableList<CallModel>) :
    RecyclerView.Adapter<CallAdapter.CallViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): CallAdapter.CallViewHolder {
        val binding = ItemCallsBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return CallViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {

        val callModel = callList[position]
        holder.binding.tvName.text = callModel.user

        val timestamp = callModel.time
        val dateFormat = SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault())
        val formattedTime = dateFormat.format(Date(timestamp))
        holder.binding.tvCallTime.text = formattedTime

        when (callModel.callStatus) {
            CallStatus.INCOMING -> {
                holder.binding.ivCallStatus.setImageResource(R.drawable.ic_incoming_call)
            }
            CallStatus.MISSED -> {
                holder.binding.ivCallStatus.setImageResource(R.drawable.ic_missed_call)
            }
            CallStatus.ONGOING -> {
                holder.binding.ivCallStatus.setImageResource(R.drawable.ic_incoming_call)
            }
        }


    }

    override fun getItemCount(): Int {
        return callList.size
    }


    class CallViewHolder(val binding: ItemCallsBinding) :
        RecyclerView.ViewHolder(binding.root)

}