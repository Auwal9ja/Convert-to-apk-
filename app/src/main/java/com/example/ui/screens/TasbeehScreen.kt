package com.example.ui.screens

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppLocalizer
import com.example.ui.theme.QuranFontFamily
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class TasbeehDhikr(
    val id: String,
    val arabic: String,
    val label: String,
    val targetCount: Int,
    val isCustom: Boolean = false
)

data class TasbeehSession(
    val id: String,
    val arabic: String,
    val label: String,
    val count: Int,
    val target: Int,
    val timestamp: Long
)

val DEFAULT_TASBEEH_PRESETS = listOf(
    TasbeehDhikr(
        id = "astaghfirullah",
        arabic = "أَسْتَغْفِرُ اللَّهَ",
        label = "Astaghfirullah",
        targetCount = 100
    ),
    TasbeehDhikr(
        id = "subhanallah",
        arabic = "سُبْحَانَ اللَّهِ",
        label = "SubhanAllah",
        targetCount = 33
    ),
    TasbeehDhikr(
        id = "alhamdulillah",
        arabic = "الْحَمْدُ لِلَّهِ",
        label = "Alhamdulillah",
        targetCount = 33
    ),
    TasbeehDhikr(
        id = "allahuakbar",
        arabic = "اللَّهُ أَكْبَرُ",
        label = "Allahu Akbar",
        targetCount = 34
    ),
    TasbeehDhikr(
        id = "lailahaillallah",
        arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ",
        label = "La ilaha illallah",
        targetCount = 100
    ),
    TasbeehDhikr(
        id = "subhanallah_bihamdihi",
        arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
        label = "SubhanAllahi wa Bihamdihi",
        targetCount = 100
    ),
    TasbeehDhikr(
        id = "lahawla",
        arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        label = "La hawla wa la quwwata illa billah",
        targetCount = 100
    ),
    TasbeehDhikr(
        id = "salawat",
        arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى مُحَمَّدٍ",
        label = "Allahumma Salli wa Sallim Ala Muhammad",
        targetCount = 100
    ),
    TasbeehDhikr(
        id = "yunus_dua",
        arabic = "لَّا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
        label = "Dua Yunus (A.S)",
        targetCount = 40
    ),
    TasbeehDhikr(
        id = "hasbunallah",
        arabic = "حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ",
        label = "Hasbunallahu wa Ni'mal Wakeel",
        targetCount = 100
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasbeehScreen(
    selectedLanguage: String = "Hausa",
    isDarkTheme: Boolean = false
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val sharedPrefs = remember { context.getSharedPreferences("tasbeeh_prefs", Context.MODE_PRIVATE) }

    // Sound and vibration settings
    var isSoundEnabled by remember {
        mutableStateOf(sharedPrefs.getBoolean("sound_enabled", true))
    }
    var isVibrationEnabled by remember {
        mutableStateOf(sharedPrefs.getBoolean("vibration_enabled", true))
    }

    // Custom Tasbeeh items persistence
    var customItems by remember {
        mutableStateOf(loadCustomTasbeeh(sharedPrefs))
    }

    val allDhikrs = remember(customItems) {
        DEFAULT_TASBEEH_PRESETS + customItems
    }

    var selectedIndex by remember {
        mutableIntStateOf(
            sharedPrefs.getInt("selected_index", 0).coerceIn(0, allDhikrs.size - 1)
        )
    }

    val currentDhikr = allDhikrs.getOrElse(selectedIndex) { allDhikrs[0] }

    // Counts per dhikr id
    var currentCount by remember(currentDhikr.id) {
        mutableIntStateOf(sharedPrefs.getInt("count_${currentDhikr.id}", 0))
    }

    // Recent Sessions
    var recentSessions by remember {
        mutableStateOf(loadRecentSessions(sharedPrefs))
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Animation scale on tap
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    fun performTapFeedback() {
        if (isVibrationEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            } catch (_: Exception) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
        if (isSoundEnabled) {
            try {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            } catch (_: Exception) {}
        }
    }

    fun performGoalCelebration() {
        if (isVibrationEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    val timing = longArrayOf(0, 100, 80, 150)
                    vibrator?.vibrate(VibrationEffect.createWaveform(timing, -1))
                }
            } catch (_: Exception) {}
        }
        Toast.makeText(
            context,
            AppLocalizer.getString("goal_reached", selectedLanguage),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun saveRecentSession(dhikr: TasbeehDhikr, countToRecord: Int) {
        if (countToRecord <= 0) return
        val newSession = TasbeehSession(
            id = UUID.randomUUID().toString(),
            arabic = dhikr.arabic,
            label = dhikr.label,
            count = countToRecord,
            target = dhikr.targetCount,
            timestamp = System.currentTimeMillis()
        )
        val updated = (listOf(newSession) + recentSessions).take(30)
        recentSessions = updated
        saveRecentSessions(sharedPrefs, updated)
    }

    fun handleCountIncrement() {
        performTapFeedback()
        val newCount = currentCount + 1
        currentCount = newCount
        sharedPrefs.edit().putInt("count_${currentDhikr.id}", newCount).apply()

        if (newCount == currentDhikr.targetCount) {
            performGoalCelebration()
            saveRecentSession(currentDhikr, newCount)
        }
    }

    fun handleReset() {
        if (currentCount > 0) {
            saveRecentSession(currentDhikr, currentCount)
        }
        currentCount = 0
        sharedPrefs.edit().putInt("count_${currentDhikr.id}", 0).apply()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF00897B),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 16.dp, end = 8.dp)
                    .testTag("fab_add_tasbeeh")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = AppLocalizer.getString("add_tasbeeh_counter", selectedLanguage),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header / Quick Category Chips
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp)
                ) {
                    // Control row: Title & Toggles (Sound & Vibration)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppLocalizer.getString("tasbeeh", selectedLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val next = !isSoundEnabled
                                    isSoundEnabled = next
                                    sharedPrefs.edit().putBoolean("sound_enabled", next).apply()
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = AppLocalizer.getString("sound_toggle", selectedLanguage),
                                    tint = if (isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val next = !isVibrationEnabled
                                    isVibrationEnabled = next
                                    sharedPrefs.edit().putBoolean("vibration_enabled", next).apply()
                                }
                            ) {
                                Icon(
                                    imageVector = if (isVibrationEnabled) Icons.Default.Vibration else Icons.Default.Smartphone,
                                    contentDescription = AppLocalizer.getString("vibration_toggle", selectedLanguage),
                                    tint = if (isVibrationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    // Dhikr Chips Row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allDhikrs.indices.toList()) { index ->
                            val item = allDhikrs[index]
                            val isSelected = index == selectedIndex

                            Surface(
                                onClick = {
                                    selectedIndex = index
                                    sharedPrefs.edit().putInt("selected_index", index).apply()
                                },
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.testTag("dhikr_chip_${item.id}")
                            ) {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Circular Counter Display Area
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .scale(animatedScale)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                isPressed = true
                                handleCountIncrement()
                                isPressed = false
                            }
                        )
                        .testTag("tasbeeh_count_circle"),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = if (currentDhikr.targetCount > 0) {
                        (currentCount.toFloat() / currentDhikr.targetCount.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    val primaryGreen = Color(0xFF00897B)
                    val trackColor = if (isDarkTheme) Color(0xFF1E3A34) else Color(0xFFE0F2F1)

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 14.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2
                        val centerOffset = Offset(size.width / 2, size.height / 2)

                        // Background track
                        drawCircle(
                            color = trackColor,
                            radius = radius,
                            center = centerOffset,
                            style = Stroke(width = strokeWidth)
                        )

                        // Animated Foreground Arc
                        val sweepAngle = progress * 360f
                        if (sweepAngle > 0f) {
                            drawArc(
                                color = primaryGreen,
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(centerOffset.x - radius, centerOffset.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Content inside the circle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Arabic Text
                        Text(
                            text = currentDhikr.arabic,
                            fontFamily = QuranFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Large Counter Number
                        Text(
                            text = "$currentCount",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF00897B),
                            lineHeight = 56.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Target Text (of 100 / of 33)
                        Text(
                            text = "${AppLocalizer.getString("count_of", selectedLanguage)} ${currentDhikr.targetCount}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // "Tap to count" Subtext
                Text(
                    text = AppLocalizer.getString("tap_to_count", selectedLanguage),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Reset Button
                TextButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.testTag("btn_reset_tasbeeh")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocalizer.getString("reset", selectedLanguage),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Recent Sessions Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppLocalizer.getString("recent_sessions", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFFD4AF37)
                        )

                        if (recentSessions.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    recentSessions = emptyList()
                                    saveRecentSessions(sharedPrefs, emptyList())
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = AppLocalizer.getString("clear_sessions", selectedLanguage),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    if (recentSessions.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = AppLocalizer.getString("no_sessions_yet", selectedLanguage),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // Recent Session items matching screenshot style
            items(recentSessions) { session ->
                val timeFormatted = remember(session.timestamp) {
                    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    sdf.format(Date(session.timestamp))
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Arabic Text on left
                        Text(
                            text = session.arabic,
                            fontFamily = QuranFontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Label / Transliteration & Time
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = session.label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // Badge count (e.g. 35×)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF00897B).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${session.count}×",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF00897B),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Text(
                                text = timeFormatted,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = AppLocalizer.getString("reset", selectedLanguage),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "${currentDhikr.label}: ${currentCount} -> 0"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        handleReset()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(AppLocalizer.getString("reset", selectedLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(AppLocalizer.getString("cancel", selectedLanguage))
                }
            }
        )
    }

    // Add Tasbeeh Counter Dialog / Bottom Sheet (matching Screenshot 2)
    if (showAddDialog) {
        var newArabic by remember { mutableStateOf("") }
        var newLabel by remember { mutableStateOf("") }
        var newTarget by remember { mutableStateOf("33") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = AppLocalizer.getString("add_tasbeeh_counter", selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Arabic Input
                    Column {
                        Text(
                            text = AppLocalizer.getString("arabic_text", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newArabic,
                            onValueChange = { newArabic = it },
                            placeholder = { Text("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", fontFamily = QuranFontFamily) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tasbeeh_arabic"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Label Input
                    Column {
                        Text(
                            text = AppLocalizer.getString("label_name", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newLabel,
                            onValueChange = { newLabel = it },
                            placeholder = { Text("SubhanAllah") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tasbeeh_label"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Target Count Input
                    Column {
                        Text(
                            text = AppLocalizer.getString("target_count", selectedLanguage),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newTarget,
                            onValueChange = { newTarget = it.filter { char -> char.isDigit() } },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("33") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_tasbeeh_target"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = newTarget.toIntOrNull()?.coerceAtLeast(1) ?: 33
                        val finalLabel = newLabel.trim().ifEmpty { "Tasbeeh" }
                        val finalArabic = newArabic.trim().ifEmpty { "ذِكْرُ اللَّهِ" }

                        val newItem = TasbeehDhikr(
                            id = UUID.randomUUID().toString(),
                            arabic = finalArabic,
                            label = finalLabel,
                            targetCount = target,
                            isCustom = true
                        )

                        val updated = customItems + newItem
                        customItems = updated
                        saveCustomTasbeeh(sharedPrefs, updated)

                        // Select the newly added item
                        selectedIndex = allDhikrs.size
                        showAddDialog = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_add_tasbeeh"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B))
                ) {
                    Text(
                        text = AppLocalizer.getString("add_counter", selectedLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            },
            dismissButton = null
        )
    }
}

private fun loadCustomTasbeeh(prefs: android.content.SharedPreferences): List<TasbeehDhikr> {
    val raw = prefs.getString("custom_tasbeeh_json", null) ?: return emptyList()
    return try {
        val array = JSONArray(raw)
        val list = mutableListOf<TasbeehDhikr>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                TasbeehDhikr(
                    id = obj.getString("id"),
                    arabic = obj.getString("arabic"),
                    label = obj.getString("label"),
                    targetCount = obj.getInt("targetCount"),
                    isCustom = true
                )
            )
        }
        list
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveCustomTasbeeh(prefs: android.content.SharedPreferences, list: List<TasbeehDhikr>) {
    try {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("arabic", item.arabic)
                put("label", item.label)
                put("targetCount", item.targetCount)
            }
            array.put(obj)
        }
        prefs.edit().putString("custom_tasbeeh_json", array.toString()).apply()
    } catch (_: Exception) {}
}

private fun loadRecentSessions(prefs: android.content.SharedPreferences): List<TasbeehSession> {
    val raw = prefs.getString("recent_sessions_json", null) ?: return emptyList()
    return try {
        val array = JSONArray(raw)
        val list = mutableListOf<TasbeehSession>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                TasbeehSession(
                    id = obj.getString("id"),
                    arabic = obj.getString("arabic"),
                    label = obj.getString("label"),
                    count = obj.getInt("count"),
                    target = obj.getInt("target"),
                    timestamp = obj.getLong("timestamp")
                )
            )
        }
        list
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveRecentSessions(prefs: android.content.SharedPreferences, list: List<TasbeehSession>) {
    try {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("arabic", item.arabic)
                put("label", item.label)
                put("count", item.count)
                put("target", item.target)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("recent_sessions_json", array.toString()).apply()
    } catch (_: Exception) {}
}
