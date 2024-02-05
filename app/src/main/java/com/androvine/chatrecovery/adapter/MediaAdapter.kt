package com.androvine.chatrecovery.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.androvine.chatrecovery.activity.MainActivity
import com.androvine.chatrecovery.databinding.RecoverMediaItemBinding
import com.bumptech.glide.Glide
import java.io.File

class MediaAdapter(
    private val context: Context,
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

        mediaHolder.binding.recoveredMedia.setOnClickListener {

            try {

                val file = File(item.uri)
                val fileUri = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    file
                )

                val openFileIntent = Intent(Intent.ACTION_VIEW)
                openFileIntent.setDataAndType(
                    fileUri,
                    if (item.type == MainActivity.MediaType.VIDEO) "video/*" else "image/*"
                )
                openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val resInfoList: List<ResolveInfo> = context.packageManager.queryIntentActivities(
                    openFileIntent, PackageManager.MATCH_DEFAULT_ONLY
                )

                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName,
                        fileUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                mediaHolder.binding.recoveredMedia.context.startActivity(
                    Intent.createChooser(
                        openFileIntent,
                        "Open File"
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

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