package com.draco.nom.recyclers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.draco.nom.R
import com.draco.nom.models.App
import com.google.android.material.textview.MaterialTextView
import java.util.*

class LauncherRecyclerAdapter(
    private val context: Context,
    var appList: List<App>
): RecyclerView.Adapter<LauncherRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
        val text = itemView.findViewById(R.id.text) as MaterialTextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun getItemId(position: Int): Long {
        return appList[position].name.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packageId = appList.toList()[position].id
        val packageName = appList.toList()[position].name
        val packageIcon = appList.toList()[position].icon

        holder.itemView.setOnClickListener {
            val appIntent = context.packageManager.getLaunchIntentForPackage(packageId) ?: return@setOnClickListener
            appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            try {
                context.startActivity(appIntent)
            } catch (_: Exception) {}
        }

        holder.itemView.setOnLongClickListener {
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            settingsIntent.data = Uri.fromParts("package", packageId, null)

            try {
                context.startActivity(settingsIntent)
            } catch (_: Exception) {}

            true
        }

        holder.text.text = packageName
        Glide.with(context)
            .load(packageIcon)
            .placeholder(R.drawable.ic_baseline_block_24)
            .into(holder.img)
    }
}
