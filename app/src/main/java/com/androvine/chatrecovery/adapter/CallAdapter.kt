package com.androvine.chatrecovery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.databinding.ItemCallsBinding
import com.androvine.chatrecovery.models.CallModel

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

        holder.binding.tvCallTime.text = callModel.time.toString()


    }

    override fun getItemCount(): Int {
        return callList.size
    }

    fun updateList(list: List<CallModel>) {
        callList.clear()
        callList.addAll(list)
        notifyDataSetChanged()
    }


    class CallViewHolder(val binding: ItemCallsBinding) :
        RecyclerView.ViewHolder(binding.root)

}