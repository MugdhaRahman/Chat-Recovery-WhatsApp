package com.mrapps.chatrecovery.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mrapps.chatrecovery.R
import com.mrapps.chatrecovery.databinding.BottomsheetCallListBinding
import com.mrapps.chatrecovery.databinding.ItemCallsBinding
import com.mrapps.chatrecovery.db.CallDBHelper
import com.mrapps.chatrecovery.models.CallModel
import com.mrapps.chatrecovery.models.CallStatus
import com.mrapps.chatrecovery.models.CallType
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class CallAdapter(private val context: Context, private val callList: MutableList<CallModel>) :
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


//        if (callModel.callType == CallType.UNKNOWN) {
//            try {
//                val nextCallModel = callList[position + 1]
//                if (nextCallModel.callType == CallType.VIDEO) {
//                    holder.binding.ivCallType.setImageResource(R.drawable.ic_video_call)
//                } else if (nextCallModel.callType == CallType.AUDIO) {
//                    holder.binding.ivCallType.setImageResource(R.drawable.ic_calls)
//                }
//
//            } catch (e: IndexOutOfBoundsException) {
//                e.printStackTrace()
//            }
//        }


        holder.binding.root.setOnLongClickListener { it ->

            val dialog = BottomSheetDialog(it.context)
            val binding = BottomsheetCallListBinding.inflate(
                LayoutInflater.from(it.context),
                null,
                false
            )
            dialog.setContentView(binding.root)
            dialog.setCancelable(true)

            binding.btnDelete.setOnClickListener {
                deleteCall(position)
                dialog.dismiss()

            }

            binding.btnDeleteAll.setOnClickListener {
                deleteAllCalls()
                dialog.dismiss()
            }

            binding.btnDeleteByUser.setOnClickListener {
                deleteByUser(callModel.user)
                dialog.dismiss()
            }

            dialog.show()
            true
        }


    }


    override fun getItemCount(): Int {
        return callList.size
    }

    fun deleteCall(position: Int) {
        val deletedCall = callList[position]
        val dbHelper = CallDBHelper(context)
        val deletedRows = dbHelper.deleteCallItem(deletedCall.id)

        if (deletedRows > 0) {
            callList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    fun deleteByUser(user: String) {
        val dbHelper = CallDBHelper(context)
        val deletedRows = dbHelper.deleteByUser(user)

        if (deletedRows > 0) {
            callList.clear()
            notifyDataSetChanged()
        }
    }


    fun deleteAllCalls() {
        val dbHelper = CallDBHelper(context)
        val deletedRows = dbHelper.deleteAllCall()

        if (deletedRows > 0) {
            callList.clear()
            notifyDataSetChanged()
        }
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