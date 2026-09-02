package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.data.local.AppLocalizer
import com.example.ui.theme.QuranFontFamily
import com.example.util.CalculationMethod
import com.example.util.JuristicMethod
import com.example.util.PrayerScheduleInfo
import com.example.util.PrayerTimeItem
import com.example.util.PrayerTimeManager

@Composable
fun PrayerTimesHeaderCard(
    scheduleInfo: PrayerScheduleInfo,
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onOpenAlarmsConfig: () -> Unit = {},
    onScheduleUpdated: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLocationDialog by remember { mutableStateOf(false) }
    var isDetectingLocation by remember { mutableStateOf(false) }

    fun startGpsDetection() {
        isDetectingLocation = true
        PrayerTimeManager.tryDetectGpsLocation(context) { city, country, _, _ ->
            isDetectingLocation = false
            val locDisplay = if (country.isNotBlank()) "$city, $country" else city
            Toast.makeText(
                context,
                if (selectedLanguage == "Hausa") "An sabunta wurin ku: $locDisplay"
                else "Location updated: $locDisplay",
                Toast.LENGTH_SHORT
            ).show()
            onScheduleUpdated()
            showLocationDialog = false
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            startGpsDetection()
        } else {
            isDetectingLocation = false
            Toast.makeText(
                context,
                if (selectedLanguage == "Hausa") "Ana buƙatar izinin GPS don gano wuri"
                else "Location permission required for GPS",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Auto-detect GPS location on initial composition if permission is already granted
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            PrayerTimeManager.tryDetectGpsLocation(context) { _, _, _, _ ->
                onScheduleUpdated()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Hero Green Islamic Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 20.dp, bottomEnd = 20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = if (isDarkTheme) {
                            listOf(Color(0xFF042F24), Color(0xFF064E3B), Color(0xFF0A3B2F))
                        } else {
                            listOf(Color(0xFF006C4E), Color(0xFF075E45), Color(0xFF0B4636))
                        }
                    )
                )
                .testTag("prayer_times_hero_banner")
        ) {
            // Subtle Islamic Hexagonal / Geometric Pattern in background
            Canvas(modifier = Modifier.matchParentSize()) {
                val patternColor = Color.White.copy(alpha = 0.05f)
                val step = 36.dp.toPx()
                for (x in 0..(size.width / step).toInt() + 1) {
                    for (y in 0..(size.height / step).toInt() + 1) {
                        val cx = x * step + (if (y % 2 == 1) step / 2 else 0f)
                        val cy = y * step
                        drawCircle(color = patternColor, radius = 10.dp.toPx(), center = Offset(cx, cy), style = Stroke(1.dp.toPx()))
                        drawCircle(color = patternColor, radius = 4.dp.toPx(), center = Offset(cx, cy))
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Centered Bismillah Calligraphy in Arabic
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontFamily = QuranFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(1.dp))

                // Gregorian Date (e.g. Sunday, August 30, 2026)
                Text(
                    text = scheduleInfo.gregorianDateStr,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center
                )

                // Hijri Date (e.g. 17 Rabi' Al-Awwal 1448 AH)
                Text(
                    text = scheduleInfo.hijriDateStr,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFDE68A), // Gentle Islamic gold/cream
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Clickable Location Pin
                Surface(
                    onClick = { showLocationDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.15f),
                    border = BorderStroke(0.8.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.testTag("location_pill_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color(0xFFFDE68A),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${scheduleInfo.cityName}, ${scheduleInfo.countryName}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change location",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Floating Curved Prayer Times Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF0F2620) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                if (isDarkTheme) Color(0xFF1B3D34) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prayer_times_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp)
            ) {
                // Header Row: 🕌 Prayer Times and Alarms ➔
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🕌", fontSize = 16.sp)
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "Lokutan Sallah"
                                "Arabic" -> "أوقات الصلاة"
                                "Yoruba" -> "Àkókò Àdúrà"
                                "Igbo" -> "Oge Ekpere"
                                "French" -> "Heures de Prière"
                                "Spanish" -> "Horarios de Oración"
                                "Urdu" -> "نماز کے اوقات"
                                "Chinese" -> "礼拜时间"
                                else -> "Prayer Times"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFF6EE7B7) else Color(0xFF00796B)
                        )
                    }

                    // Alarms ➔ Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onOpenAlarmsConfig()
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("btn_alarms_athan_config")
                    ) {
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "Ƙararrawa"
                                "Arabic" -> "التنبيهات"
                                "Yoruba" -> "Ìkìlọ̀"
                                "Igbo" -> "Mkpọsa"
                                "French" -> "Alarmes"
                                "Spanish" -> "Alarmas"
                                "Urdu" -> "الارمز"
                                "Chinese" -> "提醒"
                                else -> "Alarms"
                            },
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Alarms",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                // Next Upcoming Prayer Live Countdown Ribbon
                val nextPrayer = scheduleInfo.nextPrayer
                if (nextPrayer != null && scheduleInfo.timeRemainingStr.isNotBlank()) {
                    val nextName = getLocalizedPrayerName(nextPrayer.id, selectedLanguage)
                    val nextPrefix = when (selectedLanguage) {
                        "Hausa" -> "Mai zuwa: "
                        "Arabic" -> "الصلاة القادمة: "
                        "Yoruba" -> "Tókàn: "
                        "Igbo" -> "Na-esote: "
                        "French" -> "Suivante : "
                        "Spanish" -> "Siguiente: "
                        "Urdu" -> "اگلی نماز: "
                        "Chinese" -> "下一次: "
                        else -> "Next: "
                    }

                    val remainingText = when (selectedLanguage) {
                        "Hausa" -> "saura ${scheduleInfo.timeRemainingStr}"
                        "Arabic" -> "متبقي ${scheduleInfo.timeRemainingStr}"
                        "Yoruba" -> "ó kù ${scheduleInfo.timeRemainingStr}"
                        "Igbo" -> "fọdụrụ ${scheduleInfo.timeRemainingStr}"
                        "French" -> "dans ${scheduleInfo.timeRemainingStr}"
                        "Spanish" -> "en ${scheduleInfo.timeRemainingStr}"
                        "Urdu" -> "باقی ${scheduleInfo.timeRemainingStr}"
                        "Chinese" -> "剩余 ${scheduleInfo.timeRemainingStr}"
                        else -> "in ${scheduleInfo.timeRemainingStr}"
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDarkTheme) Color(0xFF143B33) else Color(0xFFE8F5E9),
                        border = BorderStroke(0.8.dp, if (isDarkTheme) Color(0xFF26735E) else Color(0xFFC8E6C9)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp, end = 2.dp, bottom = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "⏳",
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$nextPrefix$nextName",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color(0xFF6EE7B7) else Color(0xFF2E7D32)
                                )
                            }

                            Text(
                                text = "${nextPrayer.formattedTime} ($remainingText)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) Color(0xFFFDE68A) else Color(0xFFE65100)
                            )
                        }
                    }
                }

                // 6 Prayer Columns in a single row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scheduleInfo.prayers.forEach { prayer ->
                        PrayerColumnItem(
                            prayer = prayer,
                            selectedLanguage = selectedLanguage,
                            isDarkTheme = isDarkTheme,
                            onToggleAlarm = {
                                if (prayer.id != "SUNRISE") {
                                    val next = !prayer.isAlarmEnabled
                                    PrayerTimeManager.setPrayerAlarmEnabled(context, prayer.id, next)
                                    val name = getLocalizedPrayerName(prayer.id, selectedLanguage)
                                    val msg = if (next) {
                                        when (selectedLanguage) {
                                            "Hausa" -> "An kunna kararrawar $name ✓"
                                            "Arabic" -> "تم تفعيل تنبيه صلاة $name ✓"
                                            "French" -> "Alarme de $name activée ✓"
                                            "Spanish" -> "Alarma de $name activada ✓"
                                            "Yoruba" -> "Ìkìlọ̀ $name ti bẹ̀rẹ̀ ✓"
                                            "Igbo" -> "Emechiela mkpọsa $name ✓"
                                            else -> "$name alarm enabled ✓"
                                        }
                                    } else {
                                        when (selectedLanguage) {
                                            "Hausa" -> "An kashe kararrawar $name"
                                            "Arabic" -> "تم إيقاف تنبيه صلاة $name"
                                            "French" -> "Alarme de $name désactivée"
                                            "Spanish" -> "Alarma de $name desactivada"
                                            "Yoruba" -> "Ti pa ìkìlọ̀ $name"
                                            "Igbo" -> "E gbanyụrụ mkpọsa $name"
                                            else -> "$name alarm disabled"
                                        }
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    onScheduleUpdated()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Location Selection Dialog
    if (showLocationDialog) {
        Dialog(onDismissRequest = { if (!isDetectingLocation) showLocationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Saitin Wuri (GPS)" else "Location Settings (GPS)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { showLocationDialog = false },
                            enabled = !isDetectingLocation,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Current Location Display Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Wurin Da Kake Yanzu:" else "Current Detected Location:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (scheduleInfo.countryName.isNotBlank()) "${scheduleInfo.cityName}, ${scheduleInfo.countryName}" else scheduleInfo.cityName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Auto GPS Detect / Refresh Button with active spinner
                    Button(
                        onClick = {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            if (hasFine || hasCoarse) {
                                startGpsDetection()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        enabled = !isDetectingLocation,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        if (isDetectingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedLanguage == "Hausa") "Ana neman ainihin wurinku ta GPS..." else "Detecting live GPS location...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedLanguage == "Hausa") "Sabunta Wurin Da Kake Ta GPS" else "Refresh Location via GPS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Manhajar tana amfani da ainihin wurin da kake (GPS) domin lissafin ingantattun lokutan salloli 5 da alkibla ba tare da zaɓen gari ba."
                        else
                            "The app automatically detects your exact GPS location for accurate 5 daily prayer times and Qibla direction without needing manual city selection.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerColumnItem(
    prayer: PrayerTimeItem,
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onToggleAlarm: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = getLocalizedPrayerName(prayer.id, selectedLanguage)

    val isNext = prayer.isNext
    val highlightBg = if (isDarkTheme) Color(0xFF0D3B31) else Color(0xFFD1FAE5)
    val activeTextColor = if (isDarkTheme) Color(0xFF34D399) else Color(0xFF065F46)
    val inactiveTextColor = if (isDarkTheme) Color(0xFFFFFFFF) else Color(0xFF0F172A)
    val timeTextColor = if (isDarkTheme) Color(0xFFF1F5F9) else Color(0xFF1E293B)

    Surface(
        onClick = onToggleAlarm,
        shape = RoundedCornerShape(14.dp),
        color = if (isNext) highlightBg else Color.Transparent,
        border = if (isNext) BorderStroke(1.8.dp, if (isDarkTheme) Color(0xFF34D399) else Color(0xFF059669)) else BorderStroke(0.6.dp, if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)),
        modifier = modifier
            .padding(horizontal = 1.5.dp)
            .testTag("prayer_item_${prayer.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
        ) {
            // Prayer Emoji / Icon
            Text(
                text = prayer.emoji,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Prayer Name (Bold & Crisp)
            Text(
                text = displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isNext) activeTextColor else inactiveTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Prayer Time (Bold & High Contrast)
            Text(
                text = prayer.formattedTime,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isNext) activeTextColor else timeTextColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getLocalizedPrayerName(prayerId: String, language: String): String {
    return when (prayerId) {
        "FAJR" -> when (language) {
            "Hausa" -> "Asuba"
            "Yoruba" -> "Fajr"
            "Igbo" -> "Fajr"
            "Arabic" -> "الفجر"
            "French" -> "Fajr"
            "Spanish" -> "Fajr"
            "Urdu" -> "فجر"
            "Chinese" -> "晨礼"
            else -> "Fajr"
        }
        "SUNRISE" -> when (language) {
            "Hausa" -> "Fitowar Rana"
            "Yoruba" -> "Ìyọjú Oòrùn"
            "Igbo" -> "Ọpụpụ Anyanwụ"
            "Arabic" -> "الشروق"
            "French" -> "Lever"
            "Spanish" -> "Amanecer"
            "Urdu" -> "طلوع آفتاب"
            "Chinese" -> "日出"
            else -> "Sunrise"
        }
        "DHUHR" -> when (language) {
            "Hausa" -> "Azahar"
            "Yoruba" -> "Dhuhr"
            "Igbo" -> "Dhuhr"
            "Arabic" -> "الظهر"
            "French" -> "Dhuhr"
            "Spanish" -> "Dhuhr"
            "Urdu" -> "ظہر"
            "Chinese" -> "晌礼"
            else -> "Dhuhr"
        }
        "ASR" -> when (language) {
            "Hausa" -> "La'asar"
            "Yoruba" -> "Asr"
            "Igbo" -> "Asr"
            "Arabic" -> "العصر"
            "French" -> "Asr"
            "Spanish" -> "Asr"
            "Urdu" -> "عصر"
            "Chinese" -> "晡礼"
            else -> "Asr"
        }
        "MAGHRIB" -> when (language) {
            "Hausa" -> "Magariba"
            "Yoruba" -> "Maghrib"
            "Igbo" -> "Maghrib"
            "Arabic" -> "المغرب"
            "French" -> "Maghrib"
            "Spanish" -> "Maghrib"
            "Urdu" -> "مغرب"
            "Chinese" -> "昏礼"
            else -> "Maghrib"
        }
        "ISHA" -> when (language) {
            "Hausa" -> "Isha'i"
            "Yoruba" -> "Isha"
            "Igbo" -> "Isha"
            "Arabic" -> "العشاء"
            "French" -> "Isha"
            "Spanish" -> "Isha"
            "Urdu" -> "عشاء"
            "Chinese" -> "宵礼"
            else -> "Isha"
        }
        else -> prayerId
    }
}

