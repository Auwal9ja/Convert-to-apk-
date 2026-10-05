package com.example.receiver

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.MainActivity
import com.example.R
import com.example.util.PrayerScheduleInfo
import com.example.util.PrayerTimeManager

/**
 * AppWidgetProvider for Zakiru Prayer Times & Daily Schedule Home Screen Widget.
 * Displays real-time prayer schedule, Hijri date, location, next prayer countdown,
 * and 6 prayer times with dynamic highlighting and 1-tap refresh.
 */
class PrayerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateWidgetViews(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action ?: return
        when (action) {
            ACTION_REFRESH_WIDGET,
            ACTION_PRAYER_TIMES_UPDATED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                updateAllWidgets(context)
                if (action == ACTION_REFRESH_WIDGET) {
                    try {
                        Toast.makeText(context, "Lokutan Sallah sun sabunta ✓", Toast.LENGTH_SHORT).show()
                    } catch (_: Exception) {}
                }
            }
            ACTION_WIDGET_PINNED -> {
                try {
                    Toast.makeText(context, "An sanya Widget a Allon Waya cikin nasara! 🕌", Toast.LENGTH_LONG).show()
                } catch (_: Exception) {}
            }
        }
    }

    companion object {
        const val TAG = "PrayerWidgetProvider"
        const val ACTION_REFRESH_WIDGET = "com.example.ACTION_REFRESH_WIDGET"
        const val ACTION_PRAYER_TIMES_UPDATED = "com.example.ACTION_PRAYER_TIMES_UPDATED"
        const val ACTION_WIDGET_PINNED = "com.example.ACTION_WIDGET_PINNED"

        /**
         * Refreshes all active Zakiru widgets currently placed on the device launcher.
         */
        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, PrayerWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
                if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
                    for (widgetId in appWidgetIds) {
                        updateWidgetViews(context, appWidgetManager, widgetId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating widgets: ${e.message}", e)
            }
        }

        /**
         * Checks whether Android Launcher supports in-app pinning of widgets (Android 8.0+ / API 26+).
         */
        fun isPinSupported(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                appWidgetManager?.isRequestPinAppWidgetSupported == true
            } else {
                false
            }
        }

        /**
         * Directly prompts the Android Launcher to pin the Zakiru Prayer Times Widget to Home Screen.
         * Returns true if pinning request was accepted/launched, false if not supported.
         */
        fun requestPinWidget(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = context.getSystemService(AppWidgetManager::class.java)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    val myProvider = ComponentName(context, PrayerWidgetProvider::class.java)
                    val pinnedCallbackIntent = Intent(context, PrayerWidgetProvider::class.java).apply {
                        action = ACTION_WIDGET_PINNED
                    }
                    val successCallback = PendingIntent.getBroadcast(
                        context,
                        101,
                        pinnedCallbackIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    return appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
                }
            }
            return false
        }

        /**
         * Binds latest prayer times and localized data to RemoteViews layout.
         */
        fun updateWidgetViews(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            try {
                val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)
                val schedule = PrayerTimeManager.getTodaySchedule(context, "Hausa")

                // Location display
                val locationText = if (schedule.cityName.isNotBlank() && schedule.cityName != PrayerTimeManager.DEFAULT_CITY) {
                    if (schedule.countryName.isNotBlank()) "📍 ${schedule.cityName}, ${schedule.countryName}"
                    else "📍 ${schedule.cityName}"
                } else {
                    "📍 Wurin Da Kake"
                }
                views.setTextViewText(R.id.widget_location_text, locationText)

                // Hijri date display
                val hijriText = if (schedule.hijriDateStr.isNotBlank()) schedule.hijriDateStr else "1448 AH"
                views.setTextViewText(R.id.widget_hijri_date, hijriText)

                // Next prayer ribbon
                val nextPrayer = schedule.nextPrayer
                val nextPrayerName = nextPrayer?.nameHa ?: "Azahar"
                val nextPrayerTime = nextPrayer?.formattedTime ?: ""
                val remainingStr = if (schedule.timeRemainingStr.isNotBlank()) schedule.timeRemainingStr else ""

                views.setTextViewText(R.id.widget_next_prayer_title, "⏳ Mai zuwa: $nextPrayerName • $nextPrayerTime")
                views.setTextViewText(R.id.widget_next_prayer_countdown, if (remainingStr.isNotBlank()) "saura $remainingStr" else "Cikin lokaci")

                // Map prayer items
                val prayerMap = schedule.prayers.associateBy { it.id }

                // 1. Fajr
                prayerMap["FAJR"]?.let { fajr ->
                    views.setTextViewText(R.id.widget_fajr_name, fajr.nameHa)
                    views.setTextViewText(R.id.widget_fajr_time, fajr.formattedTime)
                    val bgRes = if (fajr.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                    views.setInt(R.id.widget_fajr_container, "setBackgroundResource", bgRes)
                }

                // 2. Sunrise
                prayerMap["SUNRISE"]?.let { sunrise ->
                    views.setTextViewText(R.id.widget_sunrise_name, sunrise.nameHa)
                    views.setTextViewText(R.id.widget_sunrise_time, sunrise.formattedTime)
                    val bgRes = if (sunrise.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                    views.setInt(R.id.widget_sunrise_container, "setBackgroundResource", bgRes)
                }

                // 3. Dhuhr
                prayerMap["DHUHR"]?.let { dhuhr ->
                    views.setTextViewText(R.id.widget_dhuhr_name, dhuhr.nameHa)
                    views.setTextViewText(R.id.widget_dhuhr_time, dhuhr.formattedTime)
                    val bgRes = if (dhuhr.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                    views.setInt(R.id.widget_dhuhr_container, "setBackgroundResource", bgRes)
                }

                // 4. Asr
                prayerMap["ASR"]?.let { asr ->
                    views.setTextViewText(R.id.widget_asr_name, asr.nameHa)
                    views.setTextViewText(R.id.widget_asr_time, asr.formattedTime)
                    val bgRes = if (asr.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                    views.setInt(R.id.widget_asr_container, "setBackgroundResource", bgRes)
                }

                // 5. Maghrib
                prayerMap["MAGHRIB"]?.let { maghrib ->
                    views.setTextViewText(R.id.widget_maghrib_name, maghrib.nameHa)
                    views.setTextViewText(R.id.widget_maghrib_time, maghrib.formattedTime)
                    val bgRes = if (maghrib.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                    views.setInt(R.id.widget_maghrib_container, "setBackgroundResource", bgRes)
                }

                // 6. Isha
                prayerMap["ISHA"]?.let { isha ->
                    views.setTextViewText(R.id.widget_isha_name, isha.nameHa)
                    views.setTextViewText(R.id.widget_isha_time, isha.formattedTime)
                    val bgRes = if (isha.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                    views.setInt(R.id.widget_isha_container, "setBackgroundResource", bgRes)
                }

                // Tap on entire widget opens app directly
                val appOpenIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                val appOpenPendingIntent = PendingIntent.getActivity(
                    context,
                    appWidgetId,
                    appOpenIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_root, appOpenPendingIntent)

                // Refresh button click triggers ACTION_REFRESH_WIDGET
                val refreshIntent = Intent(context, PrayerWidgetProvider::class.java).apply {
                    action = ACTION_REFRESH_WIDGET
                }
                val refreshPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId + 2000,
                    refreshIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_refresh, refreshPendingIntent)

                // Commit the update to AppWidgetManager
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Error building widget views: ${e.message}", e)
            }
        }
    }
}
