package com.draco.nom

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter(
        private var appList: ArrayList<AppInfo>,
        private val recyclerView: RecyclerView,
        private val packageManager: PackageManager
    ): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    fun updateList(newAppList: ArrayList<AppInfo>) {
        appList = newAppList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = appList[position]

        holder.img.setImageDrawable(info.img)
        holder.img.contentDescription = info.name

        val bundle = ActivityOptionsCompat.makeCustomAnimation(
            recyclerView.context,
            R.anim.slide_down_enter,
            android.R.anim.fade_out
        ).toBundle()

        holder.itemView.setOnClickListener {
            val intent = packageManager.getLaunchIntentForPackage(info.id)
            try {
                recyclerView.context.startActivity(intent, bundle)
            } catch (_: Exception) {}
        }

        holder.itemView.setOnLongClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", info.id, null)
            try {
                recyclerView.context.startActivity(intent, bundle)
            } catch (_: Exception) {}
            return@setOnLongClickListener true
        }
    }
}