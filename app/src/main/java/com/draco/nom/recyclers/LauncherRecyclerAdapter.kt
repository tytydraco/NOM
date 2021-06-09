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
import com.google.android.material.textview.MaterialTextView
import java.util.*

class LauncherRecyclerAdapter(
    private val context: Context,
    var packageIdList: List<String>
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
        return packageIdList.size
    }

    override fun getItemId(position: Int): Long {
        return packageIdList[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val packageId = packageIdList[position]

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

        try {
            val icon = context.packageManager.getApplicationIcon(packageId)
            Glide.with(context)
                .load(icon)
                .placeholder(R.drawable.ic_baseline_block_24)
                .thumbnail(0.5f)
                .into(holder.img)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            holder.text.text = context.packageManager
                .getApplicationInfo(packageId, 0)
                .loadLabel(context.packageManager)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
