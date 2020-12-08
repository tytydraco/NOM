package com.draco.nom.recyclers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.draco.nom.R
import com.draco.nom.utils.AppInfo
import com.google.android.material.imageview.ShapeableImageView
import java.util.*

class LauncherRecyclerAdapter(private val context: Context): RecyclerView.Adapter<LauncherRecyclerAdapter.ViewHolder>() {
    private var appList = emptyArray<AppInfo>()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ShapeableImageView
        val name = itemView.findViewById(R.id.name) as TextView

        val translationY = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y).apply {
            spring = SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        updateList()
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return appList.size
    }

    override fun getItemId(position: Int): Long {
        return appList[position].hashCode().toLong()
    }

    fun updateList() {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val activities = context.packageManager.queryIntentActivities(launcherIntent, 0)
        val newAppList = arrayListOf<AppInfo>()

        for (app in activities) {
            if (app.activityInfo.packageName == context.packageName)
                continue

            newAppList.add(
                AppInfo(
                    app.activityInfo.loadLabel(context.packageManager).toString(),
                    app.activityInfo.packageName
                )
            )
        }

        newAppList.sortBy {
            it.label.toLowerCase(Locale.getDefault())
        }

        if (!appList.contentEquals(newAppList.toTypedArray())) {
            appList = newAppList.toTypedArray()
            notifyDataSetChanged()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = appList[position]

        val appIntent = context.packageManager.getLaunchIntentForPackage(info.id)
        appIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        settingsIntent.data = Uri.fromParts("package", info.id, null)

        holder.itemView.setOnClickListener {
            try {
                context.startActivity(appIntent)
            } catch (_: Exception) {}
        }

        holder.itemView.setOnLongClickListener {
            try {
                context.startActivity(settingsIntent)
            } catch (_: Exception) {}
            return@setOnLongClickListener true
        }

        /* Setup app icons and labels */
        Glide.with(holder.img)
            .load(context.packageManager.getApplicationIcon(info.id))
            .into(holder.img)

        holder.name.text = info.label
        holder.img.contentDescription = info.label
    }
}
