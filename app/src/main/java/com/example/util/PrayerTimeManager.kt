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

    // Default location fallback if not yet chosen
    const val DEFAULT_CITY = "Wurin Da Kake"
    const val DEFAULT_COUNTRY = ""
    const val DEFAULT_LAT = 9.0765 // Central baseline coordinates
    const val DEFAULT_LNG = 7.3986

    fun isLocationSet(context: Context): Boolean {
        return getPreferences(context).contains("city_name") &&
                (getPreferences(context).getString("city_name", "")?.isNotBlank() == true) &&
                getPreferences(context).getString("city_name", "") != DEFAULT_CITY
    }

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
        val city = getPreferences(context).getString("city_name", "")
        return if (!city.isNullOrBlank()) city else DEFAULT_CITY
    }

    fun getCountryName(context: Context): String {
        return getPreferences(context).getString("country_name", "") ?: DEFAULT_COUNTRY
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
     * Resolves human-friendly city and country name without snapping to hardcoded preset lists.
     * Works on Huawei and devices without Google Play Services by falling back to network reverse geocoding.
     */
    fun resolveLocationName(context: Context, lat: Double, lng: Double): Pair<String, String> {
        // Step 1: Standard Android Geocoder
        try {
            if (Geocoder.isPresent()) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: addr.featureName
                    val country = addr.countryName ?: ""
                    if (!city.isNullOrBlank()) {
                        return Pair(city, country)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Android Geocoder lookup exception: ${e.message}")
        }

        // Step 2: Network reverse-geocoding (especially for Huawei / devices without Google Mobile Services)
        try {
            val url = java.net.URL("https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=$lat&longitude=$lng&localityLanguage=en")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 3500
                readTimeout = 3500
                requestMethod = "GET"
                setRequestProperty("User-Agent", "ZakiruMuslim/1.0")
            }
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val json = org.json.JSONObject(response)
                val city = json.optString("city").ifBlank {
                    json.optString("locality").ifBlank {
                        json.optString("principalSubdivision")
                    }
                }
                val country = json.optString("countryName")
                if (city.isNotBlank()) {
                    return Pair(city, country)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network reverse geocoding exception: ${e.message}")
        }

        // Step 3: OpenStreetMap Nominatim fallback
        try {
            val url = java.net.URL("https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lng&format=json")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 3500
                readTimeout = 3500
                requestMethod = "GET"
                setRequestProperty("User-Agent", "ZakiruMuslim/1.0")
            }
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val json = org.json.JSONObject(response)
                val addr = json.optJSONObject("address")
                if (addr != null) {
                    val city = addr.optString("city").ifBlank {
                        addr.optString("town").ifBlank {
                            addr.optString("state").ifBlank {
                                addr.optString("county")
                            }
                        }
                    }
                    val country = addr.optString("country")
                    if (city.isNotBlank()) {
                        return Pair(city, country)
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Nominatim geocoding exception: ${e.message}")
        }

        return Pair("GPS Location", "")
    }

    /**
     * Fast, reliable GPS & Multi-source Auto-detection.
     * Engineered specifically for all Android devices including Huawei (without Google Play Services).
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

        // 1. Immediate check for cached location from ALL enabled providers
        var immediateLocation: Location? = null
        val enabledProviders = locationManager.getProviders(true)
        for (provider in enabledProviders) {
            try {
                val loc = locationManager.getLastKnownLocation(provider)
                if (loc != null) {
                    if (immediateLocation == null || loc.time > immediateLocation.time || (loc.hasAccuracy() && loc.accuracy < immediateLocation.accuracy)) {
                        immediateLocation = loc
                    }
                }
            } catch (_: SecurityException) {} catch (_: Exception) {}
        }

        var liveFixReceived = false

        if (immediateLocation != null) {
            applyLocationUpdate(context, immediateLocation.latitude, immediateLocation.longitude, mainHandler, onSuccess)
        } else {
            // Instant IP-based Geolocation fallback (super-fast ~200ms on Huawei and all phones while GPS acquires satellite lock)
            Thread {
                try {
                    val url = java.net.URL("http://ip-api.com/json/?fields=status,city,regionName,country,lat,lon")
                    val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                        connectTimeout = 3000
                        readTimeout = 3000
                        requestMethod = "GET"
                    }
                    if (conn.responseCode == 200) {
                        val resp = conn.inputStream.bufferedReader().use { it.readText() }
                        val json = org.json.JSONObject(resp)
                        if (json.optString("status") == "success") {
                            val city = json.optString("city").ifBlank { json.optString("regionName") }
                            val country = json.optString("country")
                            val lat = json.optDouble("lat")
                            val lon = json.optDouble("lon")
                            if (city.isNotBlank() && !lat.isNaN() && !lon.isNaN() && !liveFixReceived) {
                                mainHandler.post {
                                    if (!liveFixReceived) {
                                        setLocation(context, city, country, lat, lon)
                                        onSuccess(city, country, lat, lon)
                                    }
                                }
                            }
                        }
                    }
                } catch (_: Exception) {}
            }.start()
        }

        // 2. Request live GPS updates with a 20-second active window
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (!liveFixReceived) {
                    liveFixReceived = true
                    try {
                        locationManager.removeUpdates(this)
                    } catch (_: Exception) {}

                    applyLocationUpdate(context, location.latitude, location.longitude, mainHandler, onSuccess)
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        try {
            var updateRequested = false
            for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)) {
                if (locationManager.isProviderEnabled(provider)) {
                    locationManager.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
                    updateRequested = true
                }
            }

            if (updateRequested) {
                mainHandler.postDelayed({
                    try {
                        locationManager.removeUpdates(listener)
                    } catch (_: Exception) {}
                }, 20000L)
            }
        } catch (_: SecurityException) {
        } catch (_: Exception) {}
    }

    private fun applyLocationUpdate(
        context: Context,
        lat: Double,
        lng: Double,
        mainHandler: Handler,
        onSuccess: (cityName: String, country: String, lat: Double, lng: Double) -> Unit
    ) {
        val existingCity = getCityName(context)
        val existingCountry = getCountryName(context)
        val tempCity = if (existingCity != DEFAULT_CITY && existingCity.isNotBlank()) existingCity else "Wurin Da Kake"
        setLocation(context, tempCity, existingCountry, lat, lng)
        onSuccess(tempCity, existingCountry, lat, lng)

        // Asynchronously resolve real city and country in background
        Thread {
            val (resolvedCity, resolvedCountry) = resolveLocationName(context, lat, lng)
            if (resolvedCity.isNotBlank() && resolvedCity != "GPS Location") {
                mainHandler.post {
                    setLocation(context, resolvedCity, resolvedCountry, lat, lng)
                    onSuccess(resolvedCity, resolvedCountry, lat, lng)
                }
            }
        }.start()
    }
}
