package com.draco.nom

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerAdapter(
    private var appList: ArrayList<Pair<String, String>>,
    private val recyclerView: RecyclerView
): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(recyclerView.context)

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val img = itemView.findViewById(R.id.img) as ImageView
        val name = itemView.findViewById(R.id.name) as TextView

        val translationY: SpringAnimation = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y)
            .setSpring(
                SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_MEDIUM)
            )
    }

    fun updateList(newAppList: ArrayList<Pair<String, String>>) {
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

    private fun getDisplayId(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            context.display!!.displayId
        else {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.displayId
        }
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
        val drawable = recyclerView.context.packageManager.getApplicationIcon(info.second)
        val img = Glide.with(holder.img).load(drawable)

        if (sharedPrefs.getBoolean(recyclerView.context.getString(R.string.pref_circle_crop), false))
            img.circleCrop()

        img.into(holder.img)

        if (sharedPrefs.getBoolean(recyclerView.context.getString(R.string.pref_icon_labels), true))
            holder.name.text = info.first
        else
            holder.name.visibility = View.GONE
    }
}
