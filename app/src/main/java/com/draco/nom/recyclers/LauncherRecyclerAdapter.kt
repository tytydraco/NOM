package com.draco.nom.recyclers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.draco.nom.databinding.RecyclerViewItemBinding
import com.draco.nom.models.App

class LauncherRecyclerAdapter(
    private val context: Context,
    var appList: List<App>
): RecyclerView.Adapter<LauncherRecyclerAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root) {
        val translationY = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y).apply {
            spring = SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_MEDIUM)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = appList.size

    override fun getItemId(position: Int) = appList[position].name.hashCode().toLong()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appList[position]

        holder.itemView.setOnClickListener {
            val appIntent = context
                .packageManager
                .getLaunchIntentForPackage(item.id) ?: return@setOnClickListener
            appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            try {
                context.startActivity(appIntent)
            } catch (_: Exception) {}
        }

        holder.itemView.setOnLongClickListener {
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            settingsIntent.data = Uri.fromParts("package", item.id, null)

            try {
                context.startActivity(settingsIntent)
            } catch (_: Exception) {}

            true
        }

        holder.binding.text.text = item.name
        Glide.with(context)
            .load(item.icon)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.binding.img)
    }
}
