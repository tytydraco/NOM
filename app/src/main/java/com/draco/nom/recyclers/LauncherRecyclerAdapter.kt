package com.draco.nom.recyclers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.draco.nom.R
import com.draco.nom.models.AppInfo
import java.util.*

class LauncherRecyclerAdapter(
    private val context: Context,
    var appList: Array<AppInfo>
): RecyclerView.Adapter<LauncherRecyclerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
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

        holder.itemView.setOnClickListener {
            val appIntent = context.packageManager.getLaunchIntentForPackage(info.id)
            appIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            try {
                context.startActivity(appIntent)
            } catch (_: Exception) {}
        }

        holder.itemView.setOnLongClickListener {
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            settingsIntent.data = Uri.fromParts("package", info.id, null)

            try {
                context.startActivity(settingsIntent)
            } catch (_: Exception) {}
            return@setOnLongClickListener true
        }

        /* Setup app icons and labels */
        Glide.with(holder.img)
            .load(context.packageManager.getApplicationIcon(info.id))
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.ic_baseline_category_24)
            .thumbnail(0.5f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop()
            .into(holder.img)

        holder.name.text = info.label
        holder.img.contentDescription = info.label
    }
}
