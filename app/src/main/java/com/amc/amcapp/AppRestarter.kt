package com.amc.amcapp

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Process
import android.os.SystemClock
import androidx.annotation.RequiresPermission
import kotlin.system.exitProcess

object AppRestarter {
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun coldRestart(activity: Activity) {
        val appContext = activity.applicationContext

        // Get your launcher (MAIN) intent
        val launchIntent = appContext.packageManager
            .getLaunchIntentForPackage(appContext.packageName)
            ?.apply {
                // New task, fully cleared so it’s a fresh entry
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            } ?: return

        // Schedule the relaunch a moment in the future
        val pending = PendingIntent.getActivity(
            appContext,
            0,
            launchIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or
                    PendingIntent.FLAG_ONE_SHOT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val am = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Small delay so the OS has time after we kill the process
        am.setExact(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 150,
            pending
        )

        // Close your current task completely
        activity.finishAffinity()

        // Kill the process => next start is a TRUE cold start (Splash shows)
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}
