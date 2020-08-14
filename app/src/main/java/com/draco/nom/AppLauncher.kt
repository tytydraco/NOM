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
        val external = intent.getBooleanExtra("external", false)

        val appIntent = context.packageManager.getLaunchIntentForPackage(appId)
        appIntent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        if (external) {
            val dm = context.getSystemService(Service.DISPLAY_SERVICE) as DisplayManager
            val displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)

            if (displays.isNotEmpty()) {
                /* Output to first usable display */
                var success = false
                for (display in displays.reversed()) {
                    if (!display.isValid)
                        continue

                    success = launch(context, display.displayId, appIntent)
                    if (success)
                        break
                }

                /* Exit if we found a working display */
                if (success)
                    return
            }
        }

        /* Otherwise, launch on internal display */
        launch(context, 0, appIntent)
    }
}