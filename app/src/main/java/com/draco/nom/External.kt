package com.draco.nom

import android.content.Context
import android.os.Build
import android.view.WindowManager

const val notificationChannelId = "nom-notification"

fun getDefaultDisplay(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        context.display!!.displayId
    else {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.displayId
    }
}