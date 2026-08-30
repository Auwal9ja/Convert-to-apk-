package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import com.example.receiver.PrayerAlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class PrayerTimeItem(
    val id: String, // "FAJR", "SUNRISE", "DHUHR", "ASR", "MAGHRIB", "ISHA"
    val nameEn: String,
    val nameHa: String,
    val nameAr: String,
    val emoji: String,
    val time: Date,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isAlarmEnabled: Boolean = true
)

data class PrayerScheduleInfo(
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val gregorianDateStr: String,
    val hijriDateStr: String,
    val prayers: List<PrayerTimeItem>,
    val nextPrayer: PrayerTimeItem?,
    val timeRemainingStr: String
)

object PrayerTimeManager {

    private const val TAG = "PrayerTimeManager"
    private const val PREFS_NAME = "prayer_time_prefs"

    // Default location: Kano, Nigeria
    const val DEFAULT_CITY = "Kano"
    const val DEFAULT_COUNTRY = "Nigeria"
    const val DEFAULT_LAT = 12.0022
    const val DEFAULT_LNG = 8.5920

    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSelectedLocation(context: Context): Pair<Double, Double> {
        val prefs = getPreferences(context)
        val lat = prefs.getFloat("latitude", DEFAULT_LAT.toFloat()).toDouble()
        val lng = prefs.getFloat("longitude", DEFAULT_LNG.toFloat()).toDouble()
        return Pair(lat, lng)
    }

    fun getCityName(context: Context): String {
        return getPreferences(context).getString("city_name", DEFAULT_CITY) ?: DEFAULT_CITY
    }

    fun getCountryName(context: Context): String {
        return getPreferences(context).getString("country_name", DEFAULT_COUNTRY) ?: DEFAULT_COUNTRY
    }

    fun setLocation(context: Context, cityName: String, countryName: String, lat: Double, lng: Double) {
        getPreferences(context).edit()
            .putString("city_name", cityName)
            .putString("country_name", countryName)
            .putFloat("latitude", lat.toFloat())
            .putFloat("longitude", lng.toFloat())
            .apply()

        // Reschedule alarms for new location
        reschedulePrayerAlarms(context)
    }

    fun getCalculationMethod(context: Context): CalculationMethod {
        val name = getPreferences(context).getString("calculation_method", CalculationMethod.EGYPTIAN.name)
        return try {
            CalculationMethod.valueOf(name ?: CalculationMethod.EGYPTIAN.name)
        } catch (_: Exception) {
            CalculationMethod.EGYPTIAN
        }
    }

    fun setCalculationMethod(context: Context, method: CalculationMethod) {
        getPreferences(context).edit().putString("calculation_method", method.name).apply()
        reschedulePrayerAlarms(context)
    }

    fun getJuristicMethod(context: Context): JuristicMethod {
        val name = getPreferences(context).getString("juristic_method", JuristicMethod.SHAFI_MALIKI_HANBALI.name)
        return try {
            JuristicMethod.valueOf(name ?: JuristicMethod.SHAFI_MALIKI_HANBALI.name)
        } catch (_: Exception) {
            JuristicMethod.SHAFI_MALIKI_HANBALI
        }
    }

    fun setJuristicMethod(context: Context, method: JuristicMethod) {
        getPreferences(context).edit().putString("juristic_method", method.name).apply()
        reschedulePrayerAlarms(context)
    }

    fun getHijriOffset(context: Context): Int {
        return getPreferences(context).getInt("hijri_offset", 0)
    }

    fun setHijriOffset(context: Context, offset: Int) {
        getPreferences(context).edit().putInt("hijri_offset", offset).apply()
    }

    fun isPrayerAlarmEnabled(context: Context, prayerId: String): Boolean {
        if (prayerId == "SUNRISE") return false
        return getPreferences(context).getBoolean("alarm_enabled_$prayerId", true)
    }

    fun setPrayerAlarmEnabled(context: Context, prayerId: String, enabled: Boolean) {
        getPreferences(context).edit().putBoolean("alarm_enabled_$prayerId", enabled).apply()
        reschedulePrayerAlarms(context)
    }

    fun getAthanMode(context: Context): String {
        return getPreferences(context).getString("athan_mode", "FULL_ATHAN") ?: "FULL_ATHAN"
    }

    fun setAthanMode(context: Context, mode: String) {
        getPreferences(context).edit().putString("athan_mode", mode).apply()
    }

    /**
     * Calculates the full prayer schedule for today.
     */
    fun getTodaySchedule(context: Context, language: String = "Hausa"): PrayerScheduleInfo {
        val (lat, lng) = getSelectedLocation(context)
        val method = getCalculationMethod(context)
        val juristic = getJuristicMethod(context)
        val hijriOffset = getHijriOffset(context)
        val cityName = getCityName(context)
        val countryName = getCountryName(context)

        val cal = Calendar.getInstance()
        val gregorianStr = HijriCalendarHelper.formatGregorianDate(cal, Locale.ENGLISH)
        val hijriStr = HijriCalendarHelper.getHijriDate(cal, hijriOffset).formatLocalized(language)

        val prayerTimesResult = PrayerTimesCalculator.calculatePrayerTimes(
            calendar = cal,
            latitude = lat,
            longitude = lng,
            method = method,
            juristic = juristic
        )

        val timeFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)

        val now = System.currentTimeMillis()

        val rawList = listOf(
            Triple("FAJR", Triple("Fajr", "Asuba", "الفجر"), Triple("🌅", prayerTimesResult.fajr, isPrayerAlarmEnabled(context, "FAJR"))),
            Triple("SUNRISE", Triple("Sunrise", "Fitowar Rana", "الشروق"), Triple("☀️", prayerTimesResult.sunrise, false)),
            Triple("DHUHR", Triple("Dhuhr", "Azahar", "الظهر"), Triple("🌞", prayerTimesResult.dhuhr, isPrayerAlarmEnabled(context, "DHUHR"))),
            Triple("ASR", Triple("Asr", "La'asar", "العصر"), Triple("⛅", prayerTimesResult.asr, isPrayerAlarmEnabled(context, "ASR"))),
            Triple("MAGHRIB", Triple("Maghrib", "Magariba", "المغرب"), Triple("🌇", prayerTimesResult.maghrib, isPrayerAlarmEnabled(context, "MAGHRIB"))),
            Triple("ISHA", Triple("Isha", "Isha'i", "العشاء"), Triple("🌙", prayerTimesResult.isha, isPrayerAlarmEnabled(context, "ISHA")))
        )

        // Find next upcoming prayer
        var nextId: String? = null
        for (item in rawList) {
            val prayerTime = item.third.second.time
            if (prayerTime > now) {
                nextId = item.first
                break
            }
        }
        // If all prayers today have passed, Fajr of tomorrow is next
        if (nextId == null) {
            nextId = "FAJR"
        }

        val items = rawList.map { (id, names, meta) ->
            PrayerTimeItem(
                id = id,
                nameEn = names.first,
                nameHa = names.second,
                nameAr = names.third,
                emoji = meta.first,
                time = meta.second,
                formattedTime = timeFormat.format(meta.second),
                isNext = id == nextId,
                isAlarmEnabled = meta.third
            )
        }

        val nextItem = items.find { it.id == nextId }
        val timeRemainingStr = if (nextItem != null) {
            var diff = nextItem.time.time - now
            if (diff < 0) {
                // Next is Fajr tomorrow (+24h approximately)
                diff += 24 * 3600 * 1000L
            }
            val hours = (diff / (1000 * 60 * 60)).toInt()
            val mins = ((diff / (1000 * 60)) % 60).toInt()
            if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
        } else ""

        return PrayerScheduleInfo(
            cityName = cityName,
            countryName = countryName,
            latitude = lat,
            longitude = lng,
            gregorianDateStr = gregorianStr,
            hijriDateStr = hijriStr,
            prayers = items,
            nextPrayer = nextItem,
            timeRemainingStr = timeRemainingStr
        )
    }

    /**
     * Schedules exact alarms for all enabled prayer times today and tomorrow.
     */
    fun reschedulePrayerAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val (lat, lng) = getSelectedLocation(context)
        val method = getCalculationMethod(context)
        val juristic = getJuristicMethod(context)

        val cal = Calendar.getInstance()
        val todayTimes = PrayerTimesCalculator.calculatePrayerTimes(
            calendar = cal,
            latitude = lat,
            longitude = lng,
            method = method,
            juristic = juristic
        )

        val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowTimes = PrayerTimesCalculator.calculatePrayerTimes(
            calendar = tomorrowCal,
            latitude = lat,
            longitude = lng,
            method = method,
            juristic = juristic
        )

        val prayersToSchedule = listOf(
            Pair("FAJR", Pair(todayTimes.fajr, tomorrowTimes.fajr)),
            Pair("DHUHR", Pair(todayTimes.dhuhr, tomorrowTimes.dhuhr)),
            Pair("ASR", Pair(todayTimes.asr, tomorrowTimes.asr)),
            Pair("MAGHRIB", Pair(todayTimes.maghrib, tomorrowTimes.maghrib)),
            Pair("ISHA", Pair(todayTimes.isha, tomorrowTimes.isha))
        )

        val now = System.currentTimeMillis()

        for ((prayerId, times) in prayersToSchedule) {
            val requestCode = getRequestCode(prayerId)
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = PrayerAlarmReceiver.ACTION_PRAYER_ALARM
                putExtra(PrayerAlarmReceiver.EXTRA_PRAYER_ID, prayerId)
            }

            val isEnabled = isPrayerAlarmEnabled(context, prayerId)
            if (!isEnabled) {
                // Cancel alarm
                val pi = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
                )
                if (pi != null) {
                    alarmManager.cancel(pi)
                    pi.cancel()
                }
                continue
            }

            val targetTime = if (times.first.time > now) times.first.time else times.second.time
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                val alarmClockInfo = AlarmManager.AlarmClockInfo(targetTime, pendingIntent)
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                Log.d(TAG, "Scheduled alarm for $prayerId at ${Date(targetTime)}")
            } catch (_: Exception) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed scheduling prayer alarm for $prayerId: ${e.message}")
                }
            }
        }
    }

    private fun getRequestCode(prayerId: String): Int {
        return when (prayerId) {
            "FAJR" -> 2001
            "DHUHR" -> 2002
            "ASR" -> 2003
            "MAGHRIB" -> 2004
            "ISHA" -> 2005
            else -> 2000
        }
    }

    /**
     * Auto-detect location using GPS or Network provider.
     */
    fun tryDetectGpsLocation(context: Context, onSuccess: (cityName: String, country: String, lat: Double, lng: Double) -> Unit) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
        try {
            var bestLocation: Location? = null
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            if (bestLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if (bestLocation != null) {
                val lat = bestLocation.latitude
                val lng = bestLocation.longitude
                setLocation(context, "Current Location", "GPS", lat, lng)
                onSuccess("Current Location", "GPS", lat, lng)
            }
        } catch (_: SecurityException) {
            // Permission not granted
        } catch (_: Exception) {}
    }
}
