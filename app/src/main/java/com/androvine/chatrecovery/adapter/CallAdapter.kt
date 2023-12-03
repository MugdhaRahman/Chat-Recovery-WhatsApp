package com.androvine.chatrecovery.adapter

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.R
import com.androvine.chatrecovery.databinding.BottomsheetCallListBinding
import com.androvine.chatrecovery.databinding.ItemCallsBinding
import com.androvine.chatrecovery.models.CallModel
import com.androvine.chatrecovery.models.CallStatus
import com.androvine.chatrecovery.models.CallType
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class CallAdapter(private val callList: MutableList<CallModel>) :
    RecyclerView.Adapter<CallAdapter.CallViewHolder>() {
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): CallViewHolder {
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

        Log.e(
            "CallAdapter",
            "onBindViewHolder: " + position + " " + callModel.callStatus + " " + callModel.callType
        )


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

        when (callModel.callType) {
            CallType.AUDIO -> {
                holder.binding.ivCallType.setImageResource(R.drawable.ic_calls)
            }

            CallType.VIDEO -> {
                holder.binding.ivCallType.setImageResource(R.drawable.ic_video_call)
            }

            CallType.UNKNOWN -> {
                holder.binding.ivCallType.setImageResource(R.drawable.ic_calls)
            }
        }


        if (callModel.callType == CallType.UNKNOWN) {
            try {
                val nextCallModel = callList[position + 1]
                if (nextCallModel.callType == CallType.VIDEO) {
                    holder.binding.ivCallType.setImageResource(R.drawable.ic_video_call)
                } else if (nextCallModel.callType == CallType.AUDIO) {
                    holder.binding.ivCallType.setImageResource(R.drawable.ic_calls)
                }

            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }


        holder.binding.root.setOnLongClickListener { it ->

            val dialog = BottomSheetDialog(it.context)
            val binding = BottomsheetCallListBinding.inflate(
                LayoutInflater.from(it.context),
                null,
                false
            )
            dialog.setContentView(binding.root)
            dialog.setCancelable(true)

//            binding.btnDelete.setOnClickListener {
//                //show delete dialog
//
//                val dialog = Dialog(it.context)
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                dialog.setCancelable(true)
//                dialog.setContentView(R.layout.dialog_delete_files)
//                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
//                dialog.window!!.setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT
//                )
//
//                dialog.findViewById<androidx.appcompat.widget.AppCompatTextView>(R.id.cancel)
//                    .setOnClickListener {
//                        dialog.dismiss()
//                    }
//                dialog.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.delete)
//                    .setOnClickListener {
//                        //delete call item
//
//
//                        dialog.dismiss()
//                    }
//                dialog.show()
//
//                dialog.dismiss()
//
//            }

            dialog.show()
            true
        }


    }


    override fun getItemCount(): Int {
        Log.e("CallAdapter", "getItemCount: ${callList.size}")
        return callList.size
    }


    fun updateList(newCallList: List<CallModel>) {
        callList.clear()
        callList.addAll(newCallList)
        notifyDataSetChanged()
        Log.e("CallAdapter", "updateList: ${callList.size}")
    }


    class CallViewHolder(val binding: ItemCallsBinding) :
        RecyclerView.ViewHolder(binding.root)

}