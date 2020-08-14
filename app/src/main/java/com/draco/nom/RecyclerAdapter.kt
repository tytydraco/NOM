package com.draco.nom

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.display.DisplayManager
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(
        private var appList: ArrayList<AppInfo>,
        private val recyclerView: RecyclerView,
        private val sharedPrefs: SharedPreferences
    ): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
        val name = itemView.findViewById(R.id.name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    private fun doLaunch(info: AppInfo, external: Boolean) {
        /* Start on specified display */
        val appIntent = Intent(recyclerView.context, AppLauncher::class.java)
        appIntent.putExtra("appId", info.id)
        appIntent.putExtra("external", external)
        recyclerView.context.sendBroadcast(appIntent)

        /* Check if we should display notification */
        val dm = recyclerView.context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
        val displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
        if (sharedPrefs.getBoolean("show_notification", false) && displays.isNotEmpty()) {
            /* Create manual intent for internal display */
            val internalAppIntent = Intent(recyclerView.context, AppLauncher::class.java)
            internalAppIntent.putExtra("appId", info.id)
            internalAppIntent.putExtra("external", false)
            val internalPendingIntent = PendingIntent.getBroadcast(recyclerView.context, 1, internalAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            /* Create manual intent for external display */
            val externalAppIntent = Intent(recyclerView.context, AppLauncher::class.java)
            externalAppIntent.putExtra("appId", info.id)
            externalAppIntent.putExtra("external", true)
            val externalPendingIntent = PendingIntent.getBroadcast(recyclerView.context, 2, externalAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            /* Create notification to resume */
            val notificationBuilder = NotificationCompat.Builder(recyclerView.context, notificationChannelId)
                .setSmallIcon(R.drawable.ic_baseline_devices_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SYSTEM)
                .setContentTitle(info.name)
                .setContentText("Refocus or move ${info.name} between displays.")
                .setLargeIcon(info.img?.toBitmap())
                .setOngoing(true)
                .addAction(R.drawable.ic_baseline_devices_24, "Internal", internalPendingIntent)
                .addAction(R.drawable.ic_baseline_devices_24, "External", externalPendingIntent)
            NotificationManagerCompat.from(recyclerView.context).notify(0, notificationBuilder.build())
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = appList[position]

        if (info.img != null)
            holder.img.setImageDrawable(info.img)
        
        if (sharedPrefs.getBoolean("icon_labels", true))
            holder.name.text = info.name

        /* Special configuration for app settings */
        if (info.id == "settings") {
            holder.itemView.setOnClickListener {
                val intent = Intent(recyclerView.context, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                recyclerView.context.startActivity(intent)
            }
            return
        }

        holder.itemView.setOnClickListener {
            doLaunch(info, sharedPrefs.getBoolean("default_external", false))
        }

        holder.itemView.setOnLongClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.fromParts("package", info.id, null)
            try {
                recyclerView.context.startActivity(intent)
            } catch (_: Exception) {}
            return@setOnLongClickListener true
        }
    }
}