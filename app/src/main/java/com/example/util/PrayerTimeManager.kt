package com.example.util

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.receiver.PrayerAlarmReceiver
import com.example.ui.screens.PRESET_CITIES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.*

data class PrayerTimeItem(
    val id: String, // "FAJR", "SUNRISE", "DHUHR", "ASR", "MAGHRIB", "ISHA"
    val nameEn: String,
    val nameHa: String,
    val nameAr: String,
    val emoji: String,
    val time: Date,
    val formattedTime: String,
    val isNext: Boolean = false,
    val isActiveNow: Boolean = false,
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
    val currentActivePrayer: PrayerTimeItem?,
    val timeRemainingStr: String,
    val nextPrayerTimeFormatted: String = ""
)

object PrayerTimeManager {

    const val TAG = "PrayerTimeManager"
    private const val PREFS_NAME = "prayer_time_prefs"

    // Reactive StateFlow to notify all screens instantly when location or calculation method updates
    private val _scheduleUpdateFlow = MutableStateFlow(System.currentTimeMillis())
    val scheduleUpdateFlow = _scheduleUpdateFlow.asStateFlow()

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

        _scheduleUpdateFlow.value = System.currentTimeMillis()

        // Reschedule alarms for new location
        try {
            reschedulePrayerAlarms(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error rescheduling alarms: ${e.message}")
        }
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
        _scheduleUpdateFlow.value = System.currentTimeMillis()
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
        _scheduleUpdateFlow.value = System.currentTimeMillis()
        reschedulePrayerAlarms(context)
    }

    fun getHijriOffset(context: Context): Int {
        return getPreferences(context).getInt("hijri_offset", 0)
    }

    fun setHijriOffset(context: Context, offset: Int) {
        getPreferences(context).edit().putInt("hijri_offset", offset).apply()
        _scheduleUpdateFlow.value = System.currentTimeMillis()
    }

    fun isPrayerAlarmEnabled(context: Context, prayerId: String): Boolean {
        if (prayerId == "SUNRISE") return false
        return getPreferences(context).getBoolean("alarm_enabled_$prayerId", true)
    }

    fun setPrayerAlarmEnabled(context: Context, prayerId: String, enabled: Boolean) {
        getPreferences(context).edit().putBoolean("alarm_enabled_$prayerId", enabled).apply()
        _scheduleUpdateFlow.value = System.currentTimeMillis()
        reschedulePrayerAlarms(context)
    }

    fun getAthanMode(context: Context): String {
        return getPreferences(context).getString("athan_mode", "SOUND_AND_VIBRATE") ?: "SOUND_AND_VIBRATE"
    }

    fun setAthanMode(context: Context, mode: String) {
        getPreferences(context).edit().putString("athan_mode", mode).apply()
    }

    fun getAthanSound(context: Context): String {
        return getPreferences(context).getString("athan_selected_sound", "SOFT_TAKBEER") ?: "SOFT_TAKBEER"
    }

    fun setAthanSound(context: Context, soundId: String) {
        getPreferences(context).edit().putString("athan_selected_sound", soundId).apply()
    }

    /**
     * Calculates the full prayer schedule dynamically based on exact current timestamp.
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

        // Determine next upcoming prayer and currently active prayer
        var nextId: String? = null
        var currentActiveId: String? = null

        for (i in rawList.indices) {
            val item = rawList[i]
            val prayerTime = item.third.second.time

            if (prayerTime > now && nextId == null) {
                nextId = item.first
            }

            if (prayerTime <= now) {
                currentActiveId = item.first
            }
        }

        // If all prayers today have passed (after Isha), next prayer is Fajr tomorrow
        if (nextId == null) {
            nextId = "FAJR"
        }
        if (currentActiveId == null) {
            // Before Fajr today (late night), current active state is Night / Post-Isha
            currentActiveId = "ISHA"
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
                isNext = (id == nextId),
                isActiveNow = (id == currentActiveId),
                isAlarmEnabled = meta.third
            )
        }

        val nextItem = items.find { it.id == nextId }
        val currentActiveItem = items.find { it.id == currentActiveId }

        val timeRemainingStr = if (nextItem != null) {
            var diff = nextItem.time.time - now
            if (diff < 0) {
                // Next is Fajr of tomorrow (add 24h)
                diff += 24 * 3600 * 1000L
            }
            val hours = (diff / (1000 * 60 * 60)).toInt()
            val mins = ((diff / (1000 * 60)) % 60).toInt()
            val secs = ((diff / 1000) % 60).toInt()
            if (hours > 0) "${hours}h ${mins}m" else if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
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
            currentActivePrayer = currentActiveItem,
            timeRemainingStr = timeRemainingStr,
            nextPrayerTimeFormatted = nextItem?.formattedTime ?: ""
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
     * Finds the closest preset city immediately based on lat/lng coordinates (instant).
     */
    fun findClosestPresetCity(lat: Double, lng: Double): Pair<String, String> {
        var minDistance = Double.MAX_VALUE
        var closestCity = DEFAULT_CITY
        var closestCountry = DEFAULT_COUNTRY

        for (city in PRESET_CITIES) {
            val dLat = Math.toRadians(city.latitude - lat)
            val dLng = Math.toRadians(city.longitude - lng)
            val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat)) * cos(Math.toRadians(city.latitude)) * sin(dLng / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1.0 - a))
            val distanceKm = 6371.0 * c
            if (distanceKm < minDistance) {
                minDistance = distanceKm
                closestCity = city.name
                closestCountry = city.country
            }
        }
        return Pair(closestCity, closestCountry)
    }

    /**
     * Resolves human-friendly city and country name without delay.
     */
    fun resolveLocationName(context: Context, lat: Double, lng: Double): Pair<String, String> {
        val (closestCity, closestCountry) = findClosestPresetCity(lat, lng)

        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: addr.featureName
                    val country = addr.countryName
                    if (!city.isNullOrBlank() && !country.isNullOrBlank()) {
                        return Pair(city, country)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Geocoder lookup exception: ${e.message}")
        }

        return Pair(closestCity, closestCountry)
    }

    /**
     * Fast, reliable GPS Auto-detection:
     * 1. Instantly reads any available cached location from any provider and sets location immediately.
     * 2. Concurrently requests high-accuracy live GPS/Network update to refine if needed.
     */
    fun tryDetectGpsLocation(
        context: Context,
        onSuccess: (cityName: String, country: String, lat: Double, lng: Double) -> Unit
    ) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            Log.w(TAG, "GPS permissions not granted")
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
        val mainHandler = Handler(Looper.getMainLooper())

        // Step 1: Immediate check for any available location from any provider
        var immediateLocation: Location? = null
        val providers = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)

        for (provider in providers) {
            try {
                if (locationManager.isProviderEnabled(provider)) {
                    val loc = locationManager.getLastKnownLocation(provider)
                    if (loc != null) {
                        if (immediateLocation == null || loc.time > immediateLocation.time || (loc.hasAccuracy() && loc.accuracy < immediateLocation.accuracy)) {
                            immediateLocation = loc
                        }
                    }
                }
            } catch (_: SecurityException) {} catch (_: Exception) {}
        }

        var hasHandledImmediate = false

        if (immediateLocation != null) {
            hasHandledImmediate = true
            val lat = immediateLocation.latitude
            val lng = immediateLocation.longitude
            // Instant resolution with nearest preset city so UI responds in 0 milliseconds
            val (fastCity, fastCountry) = findClosestPresetCity(lat, lng)
            setLocation(context, fastCity, fastCountry, lat, lng)
            onSuccess(fastCity, fastCountry, lat, lng)

            // Asynchronously resolve detailed Geocoder in background to refine city name
            Thread {
                val (resolvedCity, resolvedCountry) = resolveLocationName(context, lat, lng)
                if (resolvedCity != fastCity || resolvedCountry != fastCountry) {
                    mainHandler.post {
                        setLocation(context, resolvedCity, resolvedCountry, lat, lng)
                        onSuccess(resolvedCity, resolvedCountry, lat, lng)
                    }
                }
            }.start()
        }

        // Step 2: Request fresh live GPS update
        var liveFixReceived = false
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!liveFixReceived) {
                    liveFixReceived = true
                    try {
                        locationManager.removeUpdates(this)
                    } catch (_: Exception) {}

                    val lat = location.latitude
                    val lng = location.longitude
                    val (fastCity, fastCountry) = findClosestPresetCity(lat, lng)
                    setLocation(context, fastCity, fastCountry, lat, lng)
                    onSuccess(fastCity, fastCountry, lat, lng)

                    Thread {
                        val (resolvedCity, resolvedCountry) = resolveLocationName(context, lat, lng)
                        if (resolvedCity != fastCity || resolvedCountry != fastCountry) {
                            mainHandler.post {
                                setLocation(context, resolvedCity, resolvedCountry, lat, lng)
                                onSuccess(resolvedCity, resolvedCountry, lat, lng)
                            }
                        }
                    }.start()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            var updateRequested = false
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, listener, Looper.getMainLooper())
                updateRequested = true
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener, Looper.getMainLooper())
                updateRequested = true
            }

            if (updateRequested) {
                // Remove listener after 4 seconds to conserve battery
                mainHandler.postDelayed({
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (_: Exception) {}
                    if (!liveFixReceived && !hasHandledImmediate) {
                        val (lat, lng) = getSelectedLocation(context)
                        val city = getCityName(context)
                        val country = getCountryName(context)
                        onSuccess(city, country, lat, lng)
                    }
                }, 4000L)
            } else if (!hasHandledImmediate) {
                val (lat, lng) = getSelectedLocation(context)
                val city = getCityName(context)
                val country = getCountryName(context)
                onSuccess(city, country, lat, lng)
            }
        } catch (_: SecurityException) {
        } catch (_: Exception) {}
    }
}
