package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.floor

data class HijriDate(
    val day: Int,
    val monthNumber: Int,
    val monthNameArabic: String,
    val monthNameEnglish: String,
    val monthNameHausa: String,
    val year: Int
) {
    fun formatLocalized(language: String): String {
        val monthName = when (language) {
            "Arabic" -> monthNameArabic
            "Hausa" -> monthNameHausa
            else -> monthNameEnglish
        }
        return "$day $monthName $year AH"
    }
}

object HijriCalendarHelper {

    private val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' Al-Awwal", "Rabi' Al-Thani",
        "Jumada Al-Awwal", "Jumada Al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu Al-Qi'dah", "Dhu Al-Hijjah"
    )

    private val HIJRI_MONTHS_AR = listOf(
        "مُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الآخِر",
        "جُمَادَى الأُولَى", "جُمَادَى الآخِرَة", "رَجَب", "شَعْبَان",
        "رَمَضَان", "شَوَّال", "ذُو القَعْدَة", "ذُو الحِجَّة"
    )

    private val HIJRI_MONTHS_HA = listOf(
        "Muharram", "Safar", "Rabi'ul Awwal", "Rabi'ul Akhir",
        "Jumada Ula", "Jumada Thani", "Rajab", "Sha'aban",
        "Ramadan", "Shawwal", "Zul-Qa'adah", "Zul-Hijjah"
    )

    /**
     * Converts a Gregorian Date to Hijri Date based on astronomical conversion algorithm.
     * @param offsetDays Manual adjustment (+1, -1, +2, -2)
     */
    fun getHijriDate(calendar: Calendar = Calendar.getInstance(), offsetDays: Int = 0): HijriDate {
        val cal = calendar.clone() as Calendar
        if (offsetDays != 0) {
            cal.add(Calendar.DAY_OF_YEAR, offsetDays)
        }

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Julian Day calculation
        val m = if (month <= 2) month + 12 else month
        val y = if (month <= 2) year - 1 else year

        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        val jd = floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5

        // Epoch of Hijri Calendar is JD 1948439.5
        val epoch = 1948439.5
        val daysSinceEpoch = jd - epoch

        // 30-year Islamic cycle has 10631 days
        val cycle = floor(daysSinceEpoch / 10631.0)
        val remainingDaysInCycle = daysSinceEpoch - cycle * 10631.0

        // Determine year in cycle (11 leap years in 30-year cycle)
        var hYear = floor((remainingDaysInCycle - 0.1335) / 354.366)
        val hYearTotal = (cycle * 30 + hYear + 1).toInt()

        // Days passed in this year
        val yearStartJd = epoch + cycle * 10631.0 + floor(hYear * 354.366 + 0.1335)
        val dayInYear = (jd - yearStartJd).toInt()

        // Month lengths alternate 30 and 29
        val monthLengths = intArrayOf(30, 29, 30, 29, 30, 29, 30, 29, 30, 29, 30, 29)
        val isLeapYear = (11 * hYearTotal + 14) % 30 < 11
        if (isLeapYear) {
            monthLengths[11] = 30
        }

        var accumulated = 0
        var hMonth = 1
        var hDay = 1
        for (i in 0 until 12) {
            if (dayInYear < accumulated + monthLengths[i]) {
                hMonth = i + 1
                hDay = (dayInYear - accumulated) + 1
                break
            }
            accumulated += monthLengths[i]
        }

        val clampedMonth = hMonth.coerceIn(1, 12)
        val clampedDay = hDay.coerceIn(1, 30)

        return HijriDate(
            day = clampedDay,
            monthNumber = clampedMonth,
            monthNameArabic = HIJRI_MONTHS_AR[clampedMonth - 1],
            monthNameEnglish = HIJRI_MONTHS_EN[clampedMonth - 1],
            monthNameHausa = HIJRI_MONTHS_HA[clampedMonth - 1],
            year = hYearTotal
        )
    }

    fun formatGregorianDate(calendar: Calendar = Calendar.getInstance(), locale: Locale = Locale.getDefault()): String {
        val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", locale)
        return sdf.format(calendar.time)
    }
}
