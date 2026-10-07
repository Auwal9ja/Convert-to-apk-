package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
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
import com.example.util.WidgetContentManager
import com.example.util.WidgetContentType
import com.example.util.WidgetItem

/**
 * Dialog for choosing what to display on the Zakiru Home Screen Widget
 * (Addu'a, Azkar, Surah, Lokutan Sallah, or Combined), picking specific items,
 * and 1-tap Pin to Home Screen.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddWidgetDialog(
    selectedLanguage: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    var pinRequested by remember { mutableStateOf(false) }

    var selectedMode by remember { mutableStateOf(WidgetContentManager.getSelectedContentType(context)) }
    var currentItem by remember { mutableStateOf(WidgetContentManager.getCurrentItem(context)) }
    var currentFontSize by remember { mutableStateOf(WidgetContentManager.getWidgetFontSize(context)) }
    var showFullListPicker by remember { mutableStateOf(false) }
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
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0284C7).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Widget na Allon Waya"
                                    "Arabic" -> "ويدجت الشاشة الرئيسية"
                                    else -> "Home Screen Widget"
                                },
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F261E)
                            )
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Zaɓi abin da kake so ya nuna a allonka"
                                    "Arabic" -> "اختر ما ترغب بعرضه على الشاشة"
                                    else -> "Choose what to display on your widget"
                                },
                                fontSize = 11.5.sp,
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

                // 1. SELECT WHAT TO DISPLAY (CHOOSE CONTENT CATEGORY)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            "Hausa" -> "ZAƁI ABIN DA WIDGET ZAI NUNA:"
                            "Arabic" -> "اختر محتوى الويدجت:"
                            else -> "CHOOSE WIDGET CONTENT:"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = Color(0xFF10B981)
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        WidgetContentType.values().forEach { mode ->
                            val isSelected = mode == selectedMode
                            Surface(
                                onClick = {
                                    selectedMode = mode
                                    WidgetContentManager.setSelectedContentType(context, mode)
                                    currentItem = WidgetContentManager.getCurrentItem(context)
                                    PrayerWidgetProvider.updateAllWidgets(context)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) {
                                    if (isDark) Color(0xFF0D423A) else Color(0xFFD1FAE5)
                                } else {
                                    if (isDark) Color(0xFF0A2934) else Color(0xFFF1F5F9)
                                },
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) Color(0xFF10B981) else if (isDark) Color(0xFF164756) else Color(0xFFCBD5E1)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(text = mode.icon, fontSize = 13.sp)
                                    Text(
                                        text = if (selectedLanguage == "Hausa") mode.titleHa.substringBefore(" (") else mode.titleEn,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                                        } else {
                                            if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                        }
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. SPECIFIC ITEMS PICKER (If Addua, Azkar, or Surah is selected)
                if (selectedMode != WidgetContentType.PRAYER_TIMES) {
                    val availableItems = WidgetContentManager.getItemsForCategory(selectedMode)
                    if (availableItems.isNotEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (selectedLanguage) {
                                        "Hausa" -> "ZAƁI KO CANZA DAGA JERIN:"
                                        "Arabic" -> "اختر عنصرًا محددًا:"
                                        else -> "SELECT SPECIFIC ITEM:"
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )

                                Surface(
                                    onClick = {
                                        currentItem = WidgetContentManager.nextItem(context)
                                        PrayerWidgetProvider.updateAllWidgets(context)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isDark) Color(0xFF0C3340) else Color(0xFFE0F2FE)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Next",
                                            tint = Color(0xFF0284C7),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = if (selectedLanguage == "Hausa") "Canza na gaba 🔀" else "Next 🔀",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0284C7)
                                        )
                                    }
                                }
                            }

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(availableItems) { item ->
                                    val isCur = item.id == currentItem.id
                                    Surface(
                                        onClick = {
                                            WidgetContentManager.setSelectedItem(context, item)
                                            currentItem = item
                                            PrayerWidgetProvider.updateAllWidgets(context)
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isCur) {
                                            if (isDark) Color(0xFF0D423A) else Color(0xFFD1FAE5)
                                        } else {
                                            if (isDark) Color(0xFF092530) else Color(0xFFF8FAFC)
                                        },
                                        border = BorderStroke(
                                            if (isCur) 1.2.dp else 0.8.dp,
                                            if (isCur) Color(0xFF10B981) else if (isDark) Color(0xFF144755) else Color(0xFFE2E8F0)
                                        )
                                    ) {
                                        Text(
                                            text = item.title,
                                            fontSize = 11.sp,
                                            fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCur) {
                                                if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                                            } else {
                                                if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                            },
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. BROWSE ALL DUAS & AZKAR FROM DATABASE
                Surface(
                    onClick = { showFullListPicker = true },
                    shape = RoundedCornerShape(13.dp),
                    color = if (isDark) Color(0xFF09313E) else Color(0xFFE0F2FE),
                    border = BorderStroke(1.2.dp, if (isDark) Color(0xFF1B6A7E) else Color(0xFF7DD3FC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_browse_all_duas_for_widget")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 13.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7).copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatListBulleted,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "📋 Zaɓi daga Dukkan Addu'o'i & Azkar" else "📋 Browse & Pick from All Duas & Azkar",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF0369A1)
                                )
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Bincika duk wata addu'a ko zikiri ka sanya a widget" else "Search & choose any specific Dua or Azkar",
                                    fontSize = 11.sp,
                                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 3.5. FONT SIZE SELECTOR
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "🔤 Girman Rubutun Widget"
                                "Arabic" -> "🔤 حجم خط الويدجت"
                                else -> "🔤 Widget Font Size"
                            },
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFFFDE68A) else Color(0xFF0369A1)
                        )
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> currentFontSize.labelHa
                                "Arabic" -> currentFontSize.labelAr
                                else -> currentFontSize.labelEn
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        com.example.util.WidgetFontSize.values().forEach { size ->
                            val isSel = currentFontSize == size
                            Surface(
                                onClick = {
                                    currentFontSize = size
                                    WidgetContentManager.setWidgetFontSize(context, size)
                                    PrayerWidgetProvider.updateAllWidgets(context)
                                    Toast.makeText(
                                        context,
                                        if (selectedLanguage == "Hausa") "An saita girman rubutu: ${size.labelHa} ✓" else "Font size set: ${size.labelEn} ✓",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) {
                                    if (isDark) Color(0xFF0D423A) else Color(0xFFD1FAE5)
                                } else {
                                    if (isDark) Color(0xFF092530) else Color(0xFFF8FAFC)
                                },
                                border = BorderStroke(
                                    if (isSel) 1.5.dp else 0.8.dp,
                                    if (isSel) Color(0xFF10B981) else if (isDark) Color(0xFF144755) else Color(0xFFCBD5E1)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = when (selectedLanguage) {
                                        "Hausa" -> size.labelHa
                                        "Arabic" -> size.labelAr
                                        else -> size.labelEn
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSel) {
                                        if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                                    } else {
                                        if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                    },
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // 4. LIVE INTERACTIVE PREVIEW
                Text(
                    text = when (selectedLanguage) {
                        "Hausa" -> "KIRAR WIDGET A ALLONKA"
                        "Arabic" -> "معاينة الويدجت"
                        else -> "WIDGET PREVIEW"
                    },
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = Color(0xFFFDE68A),
                    modifier = Modifier.align(Alignment.Start)
                )

                WidgetPreviewCard(
                    schedule = schedule,
                    selectedLanguage = selectedLanguage,
                    mode = selectedMode,
                    item = currentItem,
                    fontSize = currentFontSize
                )

                // 4. PIN BUTTON (1-TAP PIN TO HOME SCREEN)
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
                        .height(48.dp)
                        .testTag("btn_pin_widget_action"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669)
                    ),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (pinRequested) Icons.Default.CheckCircle else Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> if (pinRequested) "An Nemi Sanyawa ✓ (Ƙara Kuma)" else "📱 Sanya a Allon Waya (Add to Home)"
                                "Arabic" -> if (pinRequested) "تم الطلب ✓" else "📱 إضافة إلى الشاشة الرئيسية"
                                else -> if (pinRequested) "Pin Requested ✓" else "📱 Add to Home Screen"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 5. INSTRUCTIONS GUIDE
                Surface(
                    shape = RoundedCornerShape(13.dp),
                    color = if (isDark) Color(0xFF092530) else Color(0xFFF1F5F9),
                    border = BorderStroke(0.8.dp, if (isDark) Color(0xFF144554) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa") "Yadda zaka saita da kanka ko canza rubutu:" else "How to add or cycle items:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1E293B)
                            )
                        }

                        val steps = if (selectedLanguage == "Hausa") listOf(
                            "• A babban allon wayarka, zaka iya danna maɓallin 🔀 a jikin widget don canza Addu'a, Zikiri ko Surah nan take.",
                            "• Danna kan kanun widget ɗin don canzawa tsakanin Addu'a, Azkar, da Surah.",
                            "• Haka kuma zaka iya danna allon wayarka (long-press) ➔ Zaɓi 'Widgets' ➔ Zakiru."
                        ) else listOf(
                            "• On your home screen, tap the 🔀 button on the widget to cycle to the next Dua, Zikr, or Surah instantly.",
                            "• Tap the widget title badge to toggle between Dua, Azkar, and Surah modes.",
                            "• You can also long-press your home screen ➔ choose 'Widgets' ➔ Zakiru."
                        )

                        steps.forEach { step ->
                            Text(
                                text = step,
                                fontSize = 11.sp,
                                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFullListPicker) {
        SelectDuaForWidgetDialog(
            selectedLanguage = selectedLanguage,
            onItemSelected = { item ->
                currentItem = item
                selectedMode = item.type
            },
            onDismissRequest = {
                showFullListPicker = false
                currentItem = WidgetContentManager.getCurrentItem(context)
                selectedMode = WidgetContentManager.getSelectedContentType(context)
            }
        )
    }
}

/**
 * Pixel-perfect preview card matching exactly what will appear on the device home screen.
 */
@Composable
fun WidgetPreviewCard(
    schedule: PrayerScheduleInfo,
    selectedLanguage: String,
    mode: WidgetContentType = WidgetContentType.ADDUA,
    item: WidgetItem = WidgetContentManager.duasList[0],
    fontSize: com.example.util.WidgetFontSize = com.example.util.WidgetFontSize.NORMAL
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
                        val titleText = when (mode) {
                            WidgetContentType.ADDUA -> "🤲 Zakiru • Addu'a"
                            WidgetContentType.AZKAR -> "📿 Zakiru • Azkar"
                            WidgetContentType.SURAH -> "📖 Zakiru • Surah"
                            WidgetContentType.PRAYER_TIMES -> "🕌 Zakiru • Lokutan Sallah"
                            WidgetContentType.COMBINED -> "🌟 Zakiru • Sallah & Addu'a"
                        }
                        Text(
                            text = titleText,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFFBEB)
                        )
                        val locStr = if (schedule.cityName.isNotBlank() && schedule.cityName != PrayerTimeManager.DEFAULT_CITY) {
                            "📍 ${schedule.cityName}"
                        } else "📍 Kano, Nigeria"
                        Text(
                            text = locStr,
                            fontSize = 10.sp,
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
                            Text(text = "🔀", fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E5D6E).copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "↻", fontSize = 13.sp, color = Color(0xFFFDE68A))
                        }
                    }
                }

                // If COMBINED or PRAYER_TIMES, show next prayer ribbon
                if (mode == WidgetContentType.PRAYER_TIMES || mode == WidgetContentType.COMBINED) {
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
                                .padding(horizontal = 10.dp, vertical = 4.5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏳ Mai zuwa: $nextName • $nextTime",
                                fontSize = 11.5.sp,
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
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE68A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // If ADDUA, AZKAR, SURAH, or COMBINED: show the Islamic Content Card!
                if (mode != WidgetContentType.PRAYER_TIMES) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0D3540).copy(alpha = 0.7f),
                        border = BorderStroke(0.8.dp, Color(0xFF1E5F70)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF114C5C)
                            ) {
                                Text(
                                    text = item.title,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }

                            // Arabic Text
                            Text(
                                text = item.arabic,
                                fontSize = fontSize.scaleArabic.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFFBEB),
                                textAlign = TextAlign.Center,
                                lineHeight = (fontSize.scaleArabic * 1.35f).sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Transliteration (Karatun Lafazi)
                            if (item.transliteration.isNotBlank()) {
                                Text(
                                    text = item.transliteration,
                                    fontSize = fontSize.scaleTranslit.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = Color(0xFF93C5FD),
                                    textAlign = TextAlign.Center,
                                    lineHeight = (fontSize.scaleTranslit * 1.35f).sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // Full Translation
                            Text(
                                text = item.translation,
                                fontSize = fontSize.scaleTranslation.sp,
                                color = Color(0xFFCBD5E1),
                                textAlign = TextAlign.Center,
                                lineHeight = (fontSize.scaleTranslation * 1.35f).sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Authentic Reference
                            Text(
                                text = item.reference,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFFDE68A),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // If PRAYER_TIMES mode: show the 6 prayers row
                if (mode == WidgetContentType.PRAYER_TIMES) {
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
}
