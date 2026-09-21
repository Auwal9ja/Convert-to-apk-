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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ui.theme.QuranFontFamily
import com.example.util.PrayerScheduleInfo
import com.example.util.PrayerTimeItem
import com.example.util.PrayerTimeManager
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
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

    // Scroll to current active/next prayer on initial render
    LaunchedEffect(scheduleInfo.prayers) {
        val nextIndex = scheduleInfo.prayers.indexOfFirst { it.isNext }
        if (nextIndex > 1) {
            // Center the active prayer
            scrollState.animateScrollTo(nextIndex * 200)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // =========================================================================
        // CARD 1: TOP HERO BANNER (Night Mosque, Bismillah, Gregorian & Hijri & City)
        // =========================================================================
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF07212A)),
            border = BorderStroke(1.dp, Color(0xFF104654)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prayer_times_hero_banner")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // High-resolution mosque at dusk with golden crescent moon and palm trees
                Image(
                    painter = painterResource(id = R.drawable.img_home_hero_bg_1789824178609),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Deep gradient overlay for clean contrast and authentic evening look
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xB3041922),
                                    Color(0x9907242E),
                                    Color(0xE603141B)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Calligraphy: ❖ بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ❖
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "❖",
                            color = Color(0xFFFDE68A).copy(alpha = 0.85f),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            fontFamily = QuranFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFBEB),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "❖",
                            color = Color(0xFFFDE68A).copy(alpha = 0.85f),
                            fontSize = 18.sp
                        )
                    }

                    // Gregorian Date: e.g. "Monday, September 21, 2026"
                    Text(
                        text = if (scheduleInfo.gregorianDateStr.isNotBlank()) scheduleInfo.gregorianDateStr else "Monday, September 21, 2026",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.95f),
                        textAlign = TextAlign.Center
                    )

                    // Two Pills: Hijri Date & Location
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Hijri Date Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xCC06252E),
                            border = BorderStroke(1.dp, Color(0xFF1B596A))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = Color(0xFFFDE68A),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = if (scheduleInfo.hijriDateStr.isNotBlank()) scheduleInfo.hijriDateStr else "9 Rabi'ul Akhir 1448 AH",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        // Right: Location Pill (Clickable for GPS selection)
                        Surface(
                            onClick = { showLocationDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xCC06252E),
                            border = BorderStroke(1.dp, Color(0xFF1B596A)),
                            modifier = Modifier.testTag("location_pill_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFFFDE68A),
                                    modifier = Modifier.size(15.dp)
                                )
                                val locationLabel = if (scheduleInfo.cityName.isNotBlank()) {
                                    if (scheduleInfo.countryName.isNotBlank()) "${scheduleInfo.cityName}, ${scheduleInfo.countryName}" else scheduleInfo.cityName
                                } else "Katsina, Nigeria"
                                Text(
                                    text = "$locationLabel ▾",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // CARD 2: LOKUTAN SALLAH (PRAYER TIMES CARD)
        // =========================================================================
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF061E28)),
            border = BorderStroke(1.dp, Color(0xFF0F4454)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("prayer_times_card")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header: 🕌 Lokutan Sallah + Kararrawa ➔ Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🕌", fontSize = 18.sp)
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "Lokutan Sallah"
                                "Arabic" -> "أوقات الصلاة"
                                "Yoruba" -> "Àkókò Àdúrà"
                                "Igbo" -> "Oge Ekpere"
                                "French" -> "Heures de Prière"
                                "Spanish" -> "Horarios de Oración"
                                "Urdu" -> "نماز کے اوقات"
                                else -> "Prayer Times"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Kararrawa ➔ Button
                    Surface(
                        onClick = onOpenAlarmsConfig,
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFF0A3442),
                        border = BorderStroke(1.dp, Color(0xFF185B70)),
                        modifier = Modifier.testTag("btn_alarms_athan_config")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Kararrawa"
                                    "Arabic" -> "التنبيهات"
                                    "French" -> "Alarmes"
                                    "Spanish" -> "Alarmas"
                                    "Yoruba" -> "Ìkìlọ̀"
                                    "Igbo" -> "Mkpọsa"
                                    else -> "Alarms"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Alarms",
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Next Prayer Countdown Ribbon: ⏳ Mai zuwa: Azahar | 12:24 PM (saura 1h 55m)
                val nextPrayer = scheduleInfo.nextPrayer
                val nextName = if (nextPrayer != null) getLocalizedPrayerName(nextPrayer.id, selectedLanguage) else "Azahar"
                val nextTime = nextPrayer?.formattedTime ?: "12:24 PM"
                val remainingStr = if (scheduleInfo.timeRemainingStr.isNotBlank()) scheduleInfo.timeRemainingStr else "1h 55m"

                val prefixText = when (selectedLanguage) {
                    "Hausa" -> "Mai zuwa:"
                    "Arabic" -> "الصلاة القادمة:"
                    "French" -> "Suivante :"
                    "Spanish" -> "Siguiente:"
                    "Yoruba" -> "Tókàn:"
                    "Igbo" -> "Na-esote:"
                    else -> "Next:"
                }
                val sauraText = when (selectedLanguage) {
                    "Hausa" -> "saura $remainingStr"
                    "Arabic" -> "متبقي $remainingStr"
                    "French" -> "dans $remainingStr"
                    "Spanish" -> "en $remainingStr"
                    "Yoruba" -> "ó kù $remainingStr"
                    "Igbo" -> "fọdụrụ $remainingStr"
                    else -> "in $remainingStr"
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF082E38),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⏳", fontSize = 14.sp)
                            Text(
                                text = "$prefixText $nextName",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA7F3D0)
                            )
                        }

                        Text(
                            text = "$nextTime ($sauraText)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFDE68A)
                        )
                    }
                }

                // Row of Prayer Cards flanked by < and > navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Arrow Button <
                    Surface(
                        onClick = {
                            coroutineScope.launch {
                                scrollState.animateScrollBy(-220f)
                            }
                        },
                        shape = CircleShape,
                        color = Color(0xFF092934),
                        border = BorderStroke(1.dp, Color(0xFF154857)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Scroll Left",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Scrollable Row of Prayer Cards
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        scheduleInfo.prayers.forEach { prayer ->
                            val isCurrent = prayer.isNext
                            val prayerDisplayName = getLocalizedPrayerName(prayer.id, selectedLanguage)
                            val shortName = if (prayer.id == "SUNRISE" && selectedLanguage == "Hausa") "Fitowar R..." else prayerDisplayName

                            Surface(
                                shape = RoundedCornerShape(15.dp),
                                color = if (isCurrent) Color(0xFF093933) else Color(0xFF072832),
                                border = if (isCurrent) {
                                    BorderStroke(1.8.dp, Color(0xFF34D399))
                                } else {
                                    BorderStroke(1.dp, Color(0xFF114352))
                                },
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(108.dp)
                                    .clickable {
                                        if (prayer.id != "SUNRISE") {
                                            val next = !prayer.isAlarmEnabled
                                            PrayerTimeManager.setPrayerAlarmEnabled(context, prayer.id, next)
                                            val msg = if (next) {
                                                if (selectedLanguage == "Hausa") "An kunna kararrawar $prayerDisplayName ✓" else "$prayerDisplayName alarm enabled ✓"
                                            } else {
                                                if (selectedLanguage == "Hausa") "An kashe kararrawar $prayerDisplayName" else "$prayerDisplayName alarm disabled"
                                            }
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            onScheduleUpdated()
                                        }
                                    }
                                    .testTag("prayer_card_${prayer.id}")
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Emoji icon
                                    Text(
                                        text = prayer.emoji,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    // Name
                                    Text(
                                        text = shortName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )

                                    // Time
                                    Text(
                                        text = prayer.formattedTime,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isCurrent) Color.White else Color(0xFF94A3B8),
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )

                                    // Current Badge if active
                                    if (isCurrent) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFF10B981)
                                        ) {
                                            Text(
                                                text = "Current",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF022C22),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(13.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Right Arrow Button >
                    Surface(
                        onClick = {
                            coroutineScope.launch {
                                scrollState.animateScrollBy(220f)
                            }
                        },
                        shape = CircleShape,
                        color = Color(0xFF092934),
                        border = BorderStroke(1.dp, Color(0xFF154857)),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Scroll Right",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Indicator Dots (5 dots, 1st dot active green)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (index == 0) 6.5.dp else 5.dp)
                                .clip(CircleShape)
                                .background(if (index == 0) Color(0xFF10B981) else Color(0xFF184958))
                        )
                    }
                }
            }
        }

        // =========================================================================
        // CARD 3: AYATUL QUR'AN CARD (Arabesque Corners, Classical Arabic, Hausa)
        // =========================================================================
        AyatulQuranHeroCard(
            selectedLanguage = selectedLanguage
        )
    }

    // =========================================================================
    // Location Selection Dialog (GPS Auto-Detection & City Display)
    // =========================================================================
    if (showLocationDialog) {
        Dialog(onDismissRequest = { if (!isDetectingLocation) showLocationDialog = false }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
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
                        shape = RoundedCornerShape(14.dp),
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
                                    fontSize = 11.5.sp,
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

/**
 * Ayatul Qur'an Card matching the screenshot with golden arabesque corner ornaments,
 * Arabic calligraphy verse, Hausa translation, and Surah citation.
 */
@Composable
fun AyatulQuranHeroCard(
    selectedLanguage: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF061E28)),
        border = BorderStroke(1.2.dp, Color(0xFF124B5C)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ayatul_quran_hero_card")
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Traditional Islamic Gold Arabesque Corner Ornaments on Canvas
            Canvas(modifier = Modifier.matchParentSize()) {
                val goldColor = Color(0xFFD4AF37).copy(alpha = 0.35f)
                val strokeW = 1.2.dp.toPx()
                val cornerSize = 42.dp.toPx()

                // Top-Left Corner Ornament
                drawArc(
                    color = goldColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(8.dp.toPx(), 8.dp.toPx()),
                    size = Size(cornerSize, cornerSize),
                    style = Stroke(width = strokeW)
                )
                drawArc(
                    color = goldColor,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(14.dp.toPx(), 14.dp.toPx()),
                    size = Size(cornerSize * 0.7f, cornerSize * 0.7f),
                    style = Stroke(width = strokeW)
                )

                // Top-Right Corner Ornament
                drawArc(
                    color = goldColor,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(size.width - cornerSize - 8.dp.toPx(), 8.dp.toPx()),
                    size = Size(cornerSize, cornerSize),
                    style = Stroke(width = strokeW)
                )
                drawArc(
                    color = goldColor,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(size.width - (cornerSize * 0.7f) - 14.dp.toPx(), 14.dp.toPx()),
                    size = Size(cornerSize * 0.7f, cornerSize * 0.7f),
                    style = Stroke(width = strokeW)
                )

                // Bottom-Left Corner Ornament
                drawArc(
                    color = goldColor,
                    startAngle = 90f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(8.dp.toPx(), size.height - cornerSize - 8.dp.toPx()),
                    size = Size(cornerSize, cornerSize),
                    style = Stroke(width = strokeW)
                )

                // Bottom-Right Corner Ornament
                drawArc(
                    color = goldColor,
                    startAngle = 0f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(size.width - cornerSize - 8.dp.toPx(), size.height - cornerSize - 8.dp.toPx()),
                    size = Size(cornerSize, cornerSize),
                    style = Stroke(width = strokeW)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Center Badge: 📖 Ayatul Qur'an
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color(0xFFFDE68A),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Ayatul Qur'an",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFDE68A)
                    )
                }

                // Arabic Verse in classical Quran font
                Text(
                    text = "”الَّذِينَ آمَنُوا وَتَطْمَئِنُّ قُلُوبُهُم بِذِكْرِ اللَّهِ ۗ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ“",
                    fontFamily = QuranFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                // Translation: Hausa or selected language
                Text(
                    text = when (selectedLanguage) {
                        "Hausa" -> "“Lallai ne, da ambaton Allah zukata ke samun natsuwa.”"
                        "Arabic" -> "“أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ”"
                        "French" -> "« En vérité, c'est par l'évocation d'Allah que les cœurs s'apaisent. »"
                        "Spanish" -> "« Ciertamente, en el recuerdo de Al-lah encuentran consuelo los corazones. »"
                        "Yoruba" -> "“Dájúdájú, pẹ̀lú ìrántí Olóhun ni àwọn ọkàn fi ń balẹ̀.”"
                        "Igbo" -> "“N'ezie, na ncheta Chineke ka obi na-enweta udo.”"
                        else -> "“Verily, in the remembrance of Allah do hearts find rest.”"
                    },
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    color = Color.White.copy(alpha = 0.94f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                // Surah Citation
                Text(
                    text = "— Suratul Ra'ad 13:28",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2DD4BF)
                )
            }
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

