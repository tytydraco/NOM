package com.draco.nom

import android.app.ActivityOptions
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager

class AppLauncher: BroadcastReceiver() {
    private fun launch(context: Context, displayId: Int, intent: Intent): Boolean {
        val options = ActivityOptions.makeBasic()
        options.launchDisplayId = displayId
        try {
            context.startActivity(intent, options.toBundle())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun onReceive(context: Context, intent: Intent) {
        val appId = intent.getStringExtra("appId")!!

        /* Prefer to place on last valid external display */
        val external = intent.getBooleanExtra("external", false)

        val appIntent = context.packageManager.getLaunchIntentForPackage(appId) ?: return
        appIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        if (external) {
            val dm = context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
            val displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)

            if (displays.isNotEmpty()) {
                /* Output to first usable display */
                for (display in displays.reversed()) {
                    if (!display.isValid)
                        continue

                    /* Exit if we found a working display */
                    if (launch(context, display.displayId, appIntent))
                        return
                }
            }
        }

        /* Place on the default display */
        val defaultDisplayId = intent.getIntExtra("displayId", 0)
        launch(context, defaultDisplayId, appIntent)
    }
}