package com.androvine.chatrecovery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.activity.MainActivity
import com.androvine.chatrecovery.databinding.RecoverMediaItemBinding
import com.bumptech.glide.Glide
import java.util.Locale

class MediaAdapter(
    private val mediaList: MutableList<MainActivity.RecoveredMedia>
) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecoverMediaItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val mediaHolder = holder as ViewHolder
        val item = mediaList[position]

        Glide.with(mediaHolder.binding.recoveredMedia.context).load(item.uri)
            .into(mediaHolder.binding.recoveredMedia)

        if (item.type == MainActivity.MediaType.VIDEO) {
            mediaHolder.binding.ivVideo.visibility = View.VISIBLE
        } else {
            mediaHolder.binding.ivVideo.visibility = View.GONE
        }

    }


    private fun getFormattedSize(size: Long): String {
        val fileSize = size.toDouble()
        return when {
            fileSize < 1024 * 1024 -> String.format(
                Locale.getDefault(), "%.2f KB", fileSize / 1024
            )

            fileSize < 1024 * 1024 * 1024 -> String.format(
                Locale.getDefault(), "%.2f MB", fileSize / (1024 * 1024)
            )

            else -> String.format(
                Locale.getDefault(), "%.2f GB", fileSize / (1024 * 1024 * 1024)
            )
        }
    }


    override fun getItemCount(): Int {
        return mediaList.size
    }

    fun updateList(list: List<MainActivity.RecoveredMedia>) {
        mediaList.clear()
        mediaList.addAll(list)
        notifyDataSetChanged()
    }


    class ViewHolder(val binding: RecoverMediaItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}