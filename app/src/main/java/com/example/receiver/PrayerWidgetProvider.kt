package com.example.receiver

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import com.example.MainActivity
import com.example.R
import com.example.util.PrayerTimeManager
import com.example.util.WidgetContentManager
import com.example.util.WidgetContentType

/**
 * AppWidgetProvider for Zakiru Islamic Home Screen Widget.
 * Can display:
 * 1. Addu'o'i (Daily authentic Du'as)
 * 2. Azkar (Morning & Evening Adhkar, Tasbeeh)
 * 3. Surah & Quranic Ayahs (Ayatul Kursi, Suratul Mulk, etc.)
 * 4. Prayer Times & Next Salah countdown
 * 5. Combined Mode (Prayer countdown ribbon + Addu'a/Azkar/Surah)
 *
 * User can switch what they want to display from Settings, in-app dialog,
 * or cycle items directly from the widget via the Next (🔀) and Category buttons!
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
            ACTION_NEXT_WIDGET_CONTENT -> {
                val nextItem = WidgetContentManager.nextItem(context)
                updateAllWidgets(context)
                try {
                    val toastMsg = "${nextItem.title} ✓"
                    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {}
            }
            ACTION_CYCLE_WIDGET_MODE -> {
                val newMode = WidgetContentManager.toggleNextCategory(context)
                updateAllWidgets(context)
                try {
                    Toast.makeText(context, "An canza tsari: ${newMode.titleHa} ✓", Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {}
            }
            ACTION_REFRESH_WIDGET,
            ACTION_PRAYER_TIMES_UPDATED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                updateAllWidgets(context)
                if (action == ACTION_REFRESH_WIDGET) {
                    try {
                        Toast.makeText(context, "Widget ya sabunta ✓", Toast.LENGTH_SHORT).show()
                    } catch (_: Exception) {}
                }
            }
            ACTION_WIDGET_PINNED -> {
                try {
                    Toast.makeText(context, "An sanya Widget a Allon Waya cikin nasara! 📱", Toast.LENGTH_LONG).show()
                } catch (_: Exception) {}
            }
        }
    }

    companion object {
        const val TAG = "PrayerWidgetProvider"
        const val ACTION_REFRESH_WIDGET = "com.example.ACTION_REFRESH_WIDGET"
        const val ACTION_PRAYER_TIMES_UPDATED = "com.example.ACTION_PRAYER_TIMES_UPDATED"
        const val ACTION_WIDGET_PINNED = "com.example.ACTION_WIDGET_PINNED"
        const val ACTION_NEXT_WIDGET_CONTENT = "com.example.ACTION_NEXT_WIDGET_CONTENT"
        const val ACTION_CYCLE_WIDGET_MODE = "com.example.ACTION_CYCLE_WIDGET_MODE"

        /**
         * Refreshes all active Zakiru widgets placed on the launcher.
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
         * Directly prompts the Android Launcher to pin the Zakiru Widget to Home Screen.
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
         * Builds and populates the RemoteViews according to user-selected display mode.
         */
        fun updateWidgetViews(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            try {
                val views = RemoteViews(context.packageName, R.layout.widget_prayer_times)
                val contentType = WidgetContentManager.getSelectedContentType(context)
                val currentItem = WidgetContentManager.getCurrentItem(context)
                val selectedLanguage = com.example.data.local.AppLocalizer.getAppSelectedLanguage(context)
                val schedule = PrayerTimeManager.getTodaySchedule(context, selectedLanguage)

                // App Title / Category Badge
                val headerTitle = when (contentType) {
                    WidgetContentType.ADDUA -> if (selectedLanguage == "Hausa") "🤲 Zakiru • Addu'a" else if (selectedLanguage == "Arabic") "🤲 ذاكرو • أدعية" else "🤲 Zakiru • Du'a"
                    WidgetContentType.AZKAR -> if (selectedLanguage == "Hausa") "📿 Zakiru • Azkar" else if (selectedLanguage == "Arabic") "📿 ذاكرو • أذكار" else "📿 Zakiru • Adhkar"
                    WidgetContentType.SURAH -> if (selectedLanguage == "Hausa") "📖 Zakiru • Surah" else if (selectedLanguage == "Arabic") "📖 ذاكرو • سور" else "📖 Zakiru • Surah"
                    WidgetContentType.PRAYER_TIMES -> if (selectedLanguage == "Hausa") "🕌 Zakiru • Lokutan Sallah" else if (selectedLanguage == "Arabic") "🕌 ذاكرو • مواقيت الصلاة" else "🕌 Zakiru • Prayer Times"
                    WidgetContentType.COMBINED -> if (selectedLanguage == "Hausa") "🌟 Zakiru • Sallah & Addu'a" else if (selectedLanguage == "Arabic") "🌟 ذاكرو • صلاة ودعاء" else "🌟 Zakiru • Salah & Du'a"
                }
                views.setTextViewText(R.id.widget_app_title, headerTitle)

                // Location display
                val locationText = if (schedule.cityName.isNotBlank() && schedule.cityName != PrayerTimeManager.DEFAULT_CITY) {
                    if (schedule.countryName.isNotBlank()) "📍 ${schedule.cityName}, ${schedule.countryName}"
                    else "📍 ${schedule.cityName}"
                } else {
                    if (selectedLanguage == "Hausa") "📍 Wurin Da Kake" else "📍 Current Location"
                }
                views.setTextViewText(R.id.widget_location_text, locationText)

                // Hijri date display
                val hijriText = if (schedule.hijriDateStr.isNotBlank()) schedule.hijriDateStr else "1448 AH"
                views.setTextViewText(R.id.widget_hijri_date, hijriText)

                // Next prayer info
                val nextPrayer = schedule.nextPrayer
                val nextPrayerName = if (nextPrayer != null) com.example.data.local.AppLocalizer.getPrayerLocalizedName(nextPrayer.id, selectedLanguage) else "Azahar"
                val nextPrayerTime = nextPrayer?.formattedTime ?: ""
                val remainingStr = if (schedule.timeRemainingStr.isNotBlank()) schedule.timeRemainingStr else ""

                val maiZuwaLabel = when (selectedLanguage) {
                    "Hausa" -> "⏳ Mai zuwa"
                    "Arabic" -> "⏳ الصلاة القادمة"
                    else -> "⏳ Next"
                }
                val sauraLabel = when (selectedLanguage) {
                    "Hausa" -> "saura $remainingStr"
                    "Arabic" -> "متبقي $remainingStr"
                    else -> "$remainingStr left"
                }

                views.setTextViewText(R.id.widget_next_prayer_title, "$maiZuwaLabel: $nextPrayerName • $nextPrayerTime")
                views.setTextViewText(R.id.widget_next_prayer_countdown, if (remainingStr.isNotBlank()) sauraLabel else "✓")

                // Dynamic visibility based on selected mode
                when (contentType) {
                    WidgetContentType.PRAYER_TIMES -> {
                        views.setViewVisibility(R.id.widget_hero_card, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_prayers_row, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_islamic_content_section, View.GONE)
                    }
                    WidgetContentType.COMBINED -> {
                        views.setViewVisibility(R.id.widget_hero_card, View.VISIBLE)
                        views.setViewVisibility(R.id.widget_prayers_row, View.GONE)
                        views.setViewVisibility(R.id.widget_islamic_content_section, View.VISIBLE)
                    }
                    WidgetContentType.ADDUA,
                    WidgetContentType.AZKAR,
                    WidgetContentType.SURAH -> {
                        views.setViewVisibility(R.id.widget_hero_card, View.GONE)
                        views.setViewVisibility(R.id.widget_prayers_row, View.GONE)
                        views.setViewVisibility(R.id.widget_islamic_content_section, View.VISIBLE)
                    }
                }

                // If content section is visible, bind Dua/Azkar/Surah content
                if (contentType != WidgetContentType.PRAYER_TIMES) {
                    views.setTextViewText(R.id.widget_content_title, currentItem.title)
                    views.setTextViewText(R.id.widget_content_arabic, currentItem.arabic)
                    views.setTextViewText(R.id.widget_content_translation, currentItem.translation)
                    views.setTextViewText(R.id.widget_content_reference, currentItem.reference)
                }

                // If prayer row is visible, bind prayer times
                if (contentType == WidgetContentType.PRAYER_TIMES) {
                    val prayerMap = schedule.prayers.associateBy { it.id }

                    // Fajr
                    prayerMap["FAJR"]?.let {
                        val pName = com.example.data.local.AppLocalizer.getPrayerLocalizedName(it.id, selectedLanguage)
                        views.setTextViewText(R.id.widget_fajr_name, pName)
                        views.setTextViewText(R.id.widget_fajr_time, it.formattedTime)
                        val bgRes = if (it.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                        views.setInt(R.id.widget_fajr_container, "setBackgroundResource", bgRes)
                    }
                    // Sunrise
                    prayerMap["SUNRISE"]?.let {
                        val pName = com.example.data.local.AppLocalizer.getPrayerLocalizedName(it.id, selectedLanguage)
                        views.setTextViewText(R.id.widget_sunrise_name, pName)
                        views.setTextViewText(R.id.widget_sunrise_time, it.formattedTime)
                        val bgRes = if (it.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                        views.setInt(R.id.widget_sunrise_container, "setBackgroundResource", bgRes)
                    }
                    // Dhuhr
                    prayerMap["DHUHR"]?.let {
                        val pName = com.example.data.local.AppLocalizer.getPrayerLocalizedName(it.id, selectedLanguage)
                        views.setTextViewText(R.id.widget_dhuhr_name, pName)
                        views.setTextViewText(R.id.widget_dhuhr_time, it.formattedTime)
                        val bgRes = if (it.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                        views.setInt(R.id.widget_dhuhr_container, "setBackgroundResource", bgRes)
                    }
                    // Asr
                    prayerMap["ASR"]?.let {
                        val pName = com.example.data.local.AppLocalizer.getPrayerLocalizedName(it.id, selectedLanguage)
                        views.setTextViewText(R.id.widget_asr_name, pName)
                        views.setTextViewText(R.id.widget_asr_time, it.formattedTime)
                        val bgRes = if (it.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                        views.setInt(R.id.widget_asr_container, "setBackgroundResource", bgRes)
                    }
                    // Maghrib
                    prayerMap["MAGHRIB"]?.let {
                        val pName = com.example.data.local.AppLocalizer.getPrayerLocalizedName(it.id, selectedLanguage)
                        views.setTextViewText(R.id.widget_maghrib_name, pName)
                        views.setTextViewText(R.id.widget_maghrib_time, it.formattedTime)
                        val bgRes = if (it.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                        views.setInt(R.id.widget_maghrib_container, "setBackgroundResource", bgRes)
                    }
                    // Isha
                    prayerMap["ISHA"]?.let {
                        val pName = com.example.data.local.AppLocalizer.getPrayerLocalizedName(it.id, selectedLanguage)
                        views.setTextViewText(R.id.widget_isha_name, pName)
                        views.setTextViewText(R.id.widget_isha_time, it.formattedTime)
                        val bgRes = if (it.isNext) R.drawable.bg_widget_prayer_item_active else R.drawable.bg_widget_prayer_item
                        views.setInt(R.id.widget_isha_container, "setBackgroundResource", bgRes)
                    }
                }

                // Click on root opens MainActivity
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

                // Click on Next button cycles to next Dua/Azkar/Surah
                val nextContentIntent = Intent(context, PrayerWidgetProvider::class.java).apply {
                    action = ACTION_NEXT_WIDGET_CONTENT
                }
                val nextContentPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId + 3000,
                    nextContentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_btn_next, nextContentPendingIntent)

                // Click on header title cycles category mode
                val cycleModeIntent = Intent(context, PrayerWidgetProvider::class.java).apply {
                    action = ACTION_CYCLE_WIDGET_MODE
                }
                val cycleModePendingIntent = PendingIntent.getBroadcast(
                    context,
                    appWidgetId + 4000,
                    cycleModeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_app_title, cycleModePendingIntent)

                // Click on Refresh button refreshes widget
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

                // Commit update
                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                Log.e(TAG, "Error building widget views: ${e.message}", e)
            }
        }
    }
}
