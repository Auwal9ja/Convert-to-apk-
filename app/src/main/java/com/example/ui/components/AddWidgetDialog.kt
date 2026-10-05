package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.receiver.PrayerWidgetProvider
import com.example.util.PrayerScheduleInfo
import com.example.util.PrayerTimeManager

/**
 * Beautiful Dialog and Section for adding the Zakiru Prayer Times & Daily Schedule
 * Home Screen Widget with live preview and 1-tap Pin to Home Screen.
 */
@Composable
fun AddWidgetDialog(
    selectedLanguage: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val isPinSupported = remember { PrayerWidgetProvider.isPinSupported(context) }
    var pinRequested by remember { mutableStateOf(false) }

    val schedule = remember { PrayerTimeManager.getTodaySchedule(context, selectedLanguage) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = if (isDark) Color(0xFF071F28) else Color.White,
            border = BorderStroke(1.2.dp, if (isDark) Color(0xFF135B6E) else Color(0xFFD3E7DE)),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Widget na Allon Waya"
                                    "Arabic" -> "ويدجت الشاشة الرئيسية"
                                    "French" -> "Widget Écran d'accueil"
                                    "Spanish" -> "Widget Pantalla de Inicio"
                                    else -> "Home Screen Widget"
                                },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F261E)
                            )
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Lokutan sallah kai tsaye a allonka"
                                    "Arabic" -> "مواقيت الصلاة مباشرة على شاشتك"
                                    "French" -> "Heures de prière en direct"
                                    "Spanish" -> "Horarios de oración en vivo"
                                    else -> "Live prayer times on your screen"
                                },
                                fontSize = 12.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                // Interactive Live Preview of Widget
                Text(
                    text = when (selectedLanguage) {
                        "Hausa" -> "KIRAR WIDGET A ALLONKA"
                        "Arabic" -> "معاينة الويدجت"
                        "French" -> "APERÇU DU WIDGET"
                        "Spanish" -> "VISTA PREVIA DEL WIDGET"
                        else -> "WIDGET PREVIEW"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFF10B981),
                    modifier = Modifier.align(Alignment.Start)
                )

                WidgetPreviewCard(schedule = schedule, selectedLanguage = selectedLanguage)

                // Pin Button
                Button(
                    onClick = {
                        val success = PrayerWidgetProvider.requestPinWidget(context)
                        if (success) {
                            pinRequested = true
                            Toast.makeText(
                                context,
                                if (selectedLanguage == "Hausa") "Duba allonka don amincewa da sanya Widget 📱" else "Please confirm on home screen 📱",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // If pinning is not supported directly, show instructions
                            Toast.makeText(
                                context,
                                if (selectedLanguage == "Hausa") "Launcher ba ta ba da damar sakawa ta atomatik ba. Duba bayanin ƙasa." else "Launcher does not support direct pin. See guide below.",
                                Toast.LENGTH_LONG
                            ).show()
                            pinRequested = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_pin_widget_action"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (pinRequested) Icons.Default.CheckCircle else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> if (pinRequested) "An Nemi Sanyawa ✓ (Ƙara Kuma)" else "📱 Sanya a Allon Waya (Add to Home)"
                                "Arabic" -> if (pinRequested) "تم الطلب ✓" else "📱 إضافة إلى الشاشة الرئيسية"
                                "French" -> if (pinRequested) "Ajouté ✓" else "📱 Ajouter à l'écran d'accueil"
                                "Spanish" -> if (pinRequested) "Añadido ✓" else "📱 Añadir a Pantalla de Inicio"
                                else -> if (pinRequested) "Pin Requested ✓" else "📱 Add to Home Screen"
                            },
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Step-by-step instructions card (especially useful if launcher requires manual selection)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDark) Color(0xFF092530) else Color(0xFFF1F5F9),
                    border = BorderStroke(0.8.dp, if (isDark) Color(0xFF144554) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Yadda zaka saita da kanka:"
                                    "Arabic" -> "كيفية الإضافة يدويًا:"
                                    "French" -> "Comment ajouter manuellement :"
                                    "Spanish" -> "Cómo añadir manualmente:"
                                    else -> "How to add manually:"
                                },
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1E293B)
                            )
                        }

                        val steps = when (selectedLanguage) {
                            "Hausa" -> listOf(
                                "1. Koma babban allon wayarka (Home screen).",
                                "2. Danna ka riƙe yatsanka a wani wuri mara komai (Long press).",
                                "3. Zaɓi 'Widgets' ➔ Nemi 'Zakiru'.",
                                "4. Jawo allon lokutan sallah zuwa inda kake so."
                            )
                            "Arabic" -> listOf(
                                "1. انتقل إلى الشاشة الرئيسية لهاتفك.",
                                "2. اضغط مطولاً على أي مساحة فارغة.",
                                "3. اختر 'الأدوات' (Widgets) ➔ ابحث عن 'Zakiru'.",
                                "4. اسحب ويدجت مواقيت الصلاة وضعها في المكان المناسب."
                            )
                            else -> listOf(
                                "1. Go to your phone's Home screen.",
                                "2. Long press any empty space on the screen.",
                                "3. Tap 'Widgets' ➔ Search or find 'Zakiru'.",
                                "4. Drag and drop the Prayer Times Widget onto your screen."
                            )
                        }

                        steps.forEach { step ->
                            Text(
                                text = step,
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pixel-perfect Composable preview of the Zakiru Widget.
 */
@Composable
fun WidgetPreviewCard(
    schedule: PrayerScheduleInfo,
    selectedLanguage: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.2.dp, Color(0xFF1A5E70)),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF092530),
                            Color(0xFF051922),
                            Color(0xFF031117)
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🕌 Zakiru • Lokutan Sallah",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFBEB)
                        )
                        val locStr = if (schedule.cityName.isNotBlank() && schedule.cityName != PrayerTimeManager.DEFAULT_CITY) {
                            "📍 ${schedule.cityName}"
                        } else "📍 Kano, Nigeria"
                        Text(
                            text = locStr,
                            fontSize = 10.5.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (schedule.hijriDateStr.isNotBlank()) schedule.hijriDateStr else "1448 AH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE68A)
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E5D6E).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "↻", fontSize = 14.sp, color = Color(0xFFFDE68A))
                        }
                    }
                }

                // Next prayer ribbon
                val nextPrayer = schedule.nextPrayer
                val nextName = nextPrayer?.nameHa ?: "Azahar"
                val nextTime = nextPrayer?.formattedTime ?: "12:24 PM"
                val remStr = if (schedule.timeRemainingStr.isNotBlank()) schedule.timeRemainingStr else "1h 45m"

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF123E4D),
                    border = BorderStroke(1.dp, Color(0xFF2A859C)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⏳ Mai zuwa: $nextName • $nextTime",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047)
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFB45309).copy(alpha = 0.3f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B))
                        ) {
                            Text(
                                text = "saura $remStr",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE68A),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // 6 Prayer Times Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val prayers = schedule.prayers
                    prayers.forEach { p ->
                        val isNext = p.isNext
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isNext) Color(0xFF0D9488).copy(alpha = 0.25f) else Color(0xFF1E4F5D).copy(alpha = 0.15f),
                            border = BorderStroke(
                                if (isNext) 1.2.dp else 0.8.dp,
                                if (isNext) Color(0xFF2DD4BF) else Color(0xFF1E5D6E).copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(text = p.emoji, fontSize = 10.sp)
                                val shortName = if (p.id == "SUNRISE") "Rana" else p.nameHa
                                Text(
                                    text = shortName,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isNext) Color(0xFF2DD4BF) else Color(0xFFCBD5E1),
                                    maxLines = 1
                                )
                                Text(
                                    text = p.formattedTime,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
