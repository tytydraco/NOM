package com.draco.nom

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerAdapter(
    private var appList: ArrayList<Triple<String, String, Drawable>>,
    private val recyclerView: RecyclerView,
    private val sharedPrefs: SharedPreferences
): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
        val name = itemView.findViewById(R.id.name) as TextView
    }

    fun updateList(newAppList: ArrayList<Triple<String, String, Drawable>>) {
        if (appList != newAppList) {
            appList = newAppList
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun getItemId(position: Int): Long {
        return appList[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = appList[position]

        /* Launch app */
        holder.itemView.setOnClickListener {
            val external = sharedPrefs.getBoolean(recyclerView.context.getString(R.string.pref_default_external), false)
            val defaultDisplayId = getDisplayId(recyclerView.context)
            AppLauncher(recyclerView.context, defaultDisplayId, info.second, external).launch()
        }

        /* Settings for app */
        holder.itemView.setOnLongClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.fromParts("package", info.second, null)
            try {
                recyclerView.context.startActivity(intent)
            } catch (_: Exception) {}
            return@setOnLongClickListener true
        }

        /* Setup app icons and labels */
        val img = Glide.with(holder.img).load(info.third)

        if (sharedPrefs.getBoolean(recyclerView.context.getString(R.string.pref_circle_crop), false))
            img.circleCrop()

        img.into(holder.img)

        if (sharedPrefs.getBoolean(recyclerView.context.getString(R.string.pref_icon_labels), true))
            holder.name.text = info.first
        else
            holder.name.visibility = View.GONE
    }
}
