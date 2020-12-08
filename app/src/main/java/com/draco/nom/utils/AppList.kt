package com.draco.nom.utils

import android.content.Intent
import android.content.pm.PackageManager
import java.util.*
import kotlin.collections.ArrayList

class AppList(private val packageManager: PackageManager) {
    fun get(): Array<AppInfo> {
        val launcherIntent = Intent(Intent.ACTION_MAIN, null)
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val activities = packageManager.queryIntentActivities(launcherIntent, 0)
        val appList = ArrayList<AppInfo>()

        for (app in activities) {
            val info = AppInfo(
                    app.activityInfo.loadLabel(packageManager).toString(),
                    app.activityInfo.packageName
            )

            appList.add(info)
        }

        appList.sortBy {
            it.label.toLowerCase(Locale.getDefault())
        }

        return appList.toTypedArray()
    }
}