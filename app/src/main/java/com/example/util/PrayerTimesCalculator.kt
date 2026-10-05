package com.example.util

import java.util.Calendar
import java.util.Date
import kotlin.math.*

enum class CalculationMethod(val displayName: String, val fajrAngle: Double, val ishaAngle: Double, val ishaMinutes: Double = 0.0) {
    MUSLIM_PRO("Muslim Pro (Standard / MWL - Fajr 18°, Isha 17°)", 18.0, 17.0),
    EGYPTIAN("Egyptian General Authority (Egypt, Africa, Nigeria)", 19.5, 17.5),
    MUSLIM_WORLD_LEAGUE("Muslim World League (MWL)", 18.0, 17.0),
    UMM_AL_QURA("Umm Al-Qura (Makkah, Saudi Arabia)", 18.5, 0.0, 90.0),
    KARACHI("Univ. of Islamic Sciences, Karachi (Pakistan, India)", 18.0, 18.0),
    NORTH_AMERICA("ISNA (North America / USA / Canada)", 15.0, 15.0),
    DUBAI("Dubai / Gulf Standard (UAE)", 18.2, 18.2),
    KUWAIT("Kuwait", 18.0, 17.5),
    QATAR("Qatar", 18.0, 0.0, 90.0),
    TEHRAN("Institute of Geophysics, Tehran", 17.7, 14.0)
}

enum class JuristicMethod(val displayName: String, val shadowFactor: Double) {
    SHAFI_MALIKI_HANBALI("Standard (Maliki, Shafi'i, Hanbali, Ja'fari)", 1.0),
    HANAFI("Hanafi", 2.0)
}

enum class HigherLatitudeRule {
    NONE,
    MID_NIGHT,
    ONE_SEVENTH,
    ANGLE_BASED
}

data class PrayerTimesResult(
    val fajr: Date,
    val sunrise: Date,
    val dhuhr: Date,
    val asr: Date,
    val sunset: Date,
    val maghrib: Date,
    val isha: Date
)

/**
 * Astronomical Prayer Times Calculator based on standard solar position algorithms (PrayTimes standard).
 */
object PrayerTimesCalculator {

    fun calculatePrayerTimes(
        calendar: Calendar,
        latitude: Double,
        longitude: Double,
        timezone: Double = calendar.timeZone.getOffset(calendar.timeInMillis) / 3600000.0,
        elevationMeters: Double = 0.0,
        method: CalculationMethod = CalculationMethod.MUSLIM_PRO,
        juristic: JuristicMethod = JuristicMethod.SHAFI_MALIKI_HANBALI,
        highLatRule: HigherLatitudeRule = HigherLatitudeRule.MID_NIGHT
    ): PrayerTimesResult {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val jd = julianDate(year, month, day) - longitude / (15.0 * 24.0)

        // Solar parameters
        val (sunDeclination, eqOfTime) = sunPosition(jd)

        // Solar Noon (Dhuhr) in hours standard time
        val noon = fixHour(12.0 + timezone - longitude / 15.0 - eqOfTime)

        // Sunrise and Sunset: Sun depression angle (-0.833° - refraction from elevation)
        val sunriseSunsetAngle = -(0.833 + 0.0347 * sqrt(elevationMeters.coerceAtLeast(0.0)))
        val sunriseHour = noon - sunAltitudeHourAngle(sunriseSunsetAngle, sunDeclination, latitude)
        val sunsetHour = noon + sunAltitudeHourAngle(sunriseSunsetAngle, sunDeclination, latitude)

        // Fajr: Sun is at negative fajrAngle below horizon
        val fajrHour = noon - sunAltitudeHourAngle(-method.fajrAngle, sunDeclination, latitude)

        // Asr: Sun altitude above horizon based on shadow length factor (positive angle)
        val diffLatDec = abs(latitude - sunDeclination)
        val asrAltitude = Math.toDegrees(atan(1.0 / (juristic.shadowFactor + tan(Math.toRadians(diffLatDec)))))
        val asrHour = noon + sunAltitudeHourAngle(asrAltitude, sunDeclination, latitude)

        // Maghrib
        val maghribHour = if (method == CalculationMethod.TEHRAN) {
            noon + sunAltitudeHourAngle(-4.5, sunDeclination, latitude)
        } else {
            sunsetHour
        }

        // Isha
        val ishaHour = if (method.ishaMinutes > 0.0) {
            maghribHour + method.ishaMinutes / 60.0
        } else {
            noon + sunAltitudeHourAngle(-method.ishaAngle, sunDeclination, latitude)
        }

        // Adjust for high latitudes if needed
        val adjustedFajr = adjustHighLatitude(fajrHour, sunriseHour, sunsetHour, method.fajrAngle, highLatRule, isMorning = true)
        val adjustedIsha = if (method.ishaMinutes > 0.0) ishaHour else adjustHighLatitude(ishaHour, sunriseHour, sunsetHour, method.ishaAngle, highLatRule, isMorning = false)

        return PrayerTimesResult(
            fajr = hourToDate(calendar, adjustedFajr),
            sunrise = hourToDate(calendar, sunriseHour),
            dhuhr = hourToDate(calendar, noon),
            asr = hourToDate(calendar, asrHour),
            sunset = hourToDate(calendar, sunsetHour),
            maghrib = hourToDate(calendar, maghribHour),
            isha = hourToDate(calendar, adjustedIsha)
        )
    }

    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Calculates Sun Declination (degrees) and Equation of Time (hours).
     */
    private fun sunPosition(julianDate: Double): Pair<Double, Double> {
        val d = julianDate - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))
        val e = 23.439 - 0.00000036 * d

        val dRad = Math.toRadians(e)
        val lRad = Math.toRadians(l)

        val sinDec = sin(dRad) * sin(lRad)
        val dec = Math.toDegrees(asin(sinDec))

        var ra = Math.toDegrees(atan2(cos(dRad) * sin(lRad), cos(lRad))) / 15.0
        ra = fixHour(ra)

        var eq = q / 15.0 - ra
        while (eq > 12.0) eq -= 24.0
        while (eq <= -12.0) eq += 24.0

        return Pair(dec, eq)
    }

    /**
     * Computes the hour angle (in hours) for a given sun altitude angle (in degrees).
     * Altitude is positive above horizon (e.g. Asr) and negative below horizon (e.g. Fajr, Sunset).
     */
    private fun sunAltitudeHourAngle(altitudeDegrees: Double, declination: Double, latitude: Double): Double {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val altRad = Math.toRadians(altitudeDegrees)

        val cosH = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        val clampedCosH = cosH.coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(clampedCosH)) / 15.0
    }

    private fun adjustHighLatitude(
        time: Double,
        sunrise: Double,
        sunset: Double,
        angle: Double,
        rule: HigherLatitudeRule,
        isMorning: Boolean
    ): Double {
        val nightDuration = (24.0 - sunset) + sunrise
        return when (rule) {
            HigherLatitudeRule.MID_NIGHT -> {
                val portion = nightDuration / 2.0
                if (isMorning) {
                    if (sunrise - time > portion || time.isNaN()) sunrise - portion else time
                } else {
                    if (time - sunset > portion || time.isNaN()) sunset + portion else time
                }
            }
            HigherLatitudeRule.ONE_SEVENTH -> {
                val portion = nightDuration / 7.0
                if (isMorning) {
                    if (sunrise - time > portion || time.isNaN()) sunrise - portion else time
                } else {
                    if (time - sunset > portion || time.isNaN()) sunset + portion else time
                }
            }
            HigherLatitudeRule.ANGLE_BASED -> {
                val portion = (angle / 60.0) * nightDuration
                if (isMorning) {
                    if (sunrise - time > portion || time.isNaN()) sunrise - portion else time
                } else {
                    if (time - sunset > portion || time.isNaN()) sunset + portion else time
                }
            }
            HigherLatitudeRule.NONE -> time
        }
    }

    private fun fixHour(hour: Double): Double {
        var h = hour - 24.0 * floor(hour / 24.0)
        if (h < 0) h += 24.0
        return h
    }

    private fun fixAngle(angle: Double): Double {
        var a = angle - 360.0 * floor(angle / 360.0)
        if (a < 0) a += 360.0
        return a
    }

    private fun hourToDate(baseCal: Calendar, hourFraction: Double): Date {
        val cal = baseCal.clone() as Calendar
        val hours = floor(hourFraction).toInt()
        val minutesFraction = (hourFraction - hours) * 60.0
        val minutes = floor(minutesFraction).toInt()
        val seconds = floor((minutesFraction - minutes) * 60.0).toInt()

        cal.set(Calendar.HOUR_OF_DAY, hours.coerceIn(0, 23))
        cal.set(Calendar.MINUTE, minutes.coerceIn(0, 59))
        cal.set(Calendar.SECOND, seconds.coerceIn(0, 59))
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }
}
