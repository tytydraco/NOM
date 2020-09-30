package com.draco.nom

import android.app.ActivityOptions
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager

class AppLauncher(
    private val context: Context,
    private val defaultDisplayId: Int,
    private val appId: String,
    private val external: Boolean = false
) {
    private fun launchActivity(displayId: Int): Boolean {
        val appIntent = context.packageManager.getLaunchIntentForPackage(appId) ?: return false
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val options = ActivityOptions.makeBasic()
        options.launchDisplayId = displayId
        try {
            context.startActivity(appIntent, options.toBundle())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun launch() {
        if (external) {
            val dm = context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
            val displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)

            /* Output to first usable display */
            if (displays.isNotEmpty())
                for (display in displays.reversed())
                    if (display.isValid && launchActivity(display.displayId))
                        return
        }

        /* Place on the default display */
        launchActivity(defaultDisplayId)
    }
}