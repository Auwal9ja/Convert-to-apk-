package com.example.ui.screens

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppLocalizer
import com.example.data.local.DuaDatabase
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaEntity
import com.example.receiver.MandatoryAdhkarManager
import com.example.receiver.MandatorySessionState
import com.example.ui.audio.DuaSpeaker
import com.example.ui.components.BannerAd
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

class MandatoryAdhkarActivity : ComponentActivity() {

    companion object {
        private const val TAG = "NOOR_ZIKIR_MANDATORY"
    }

    private var speaker: DuaSpeaker? = null
    private var audioManager: AudioManager? = null
    private var isSessionCompletedState by mutableStateOf(false)
    private var showExitWarningDialog by mutableStateOf(false)
    private var scheduleId: String = MandatoryAdhkarManager.SCHEDULE_ID_MORNING
    private var scheduleTitle: String = "Morning Zikir"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Configure flags to wake screen and display above keyguard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
        )

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as? android.app.KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager?.requestDismissKeyguard(this, null)
        }

        super.onCreate(savedInstanceState)

        speaker = DuaSpeaker(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        scheduleId = intent.getStringExtra("SCHEDULE_ID")
            ?: if (intent.getStringExtra("ADHKAR_TYPE") == "EVENING") MandatoryAdhkarManager.SCHEDULE_ID_EVENING else MandatoryAdhkarManager.SCHEDULE_ID_MORNING
        scheduleTitle = intent.getStringExtra("SCHEDULE_TITLE")
            ?: if (scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_EVENING) "Evening Zikir" else "Morning Zikir"
        val category = intent.getStringExtra("CATEGORY") ?: "Morning & Evening"
        val durationMinutes = intent.getIntExtra("DURATION_MINUTES", intent.getIntExtra("READING_DURATION", 15))

        Log.d(TAG, "session start: MandatoryAdhkarActivity launched for scheduleId=$scheduleId, title=$scheduleTitle, duration=${durationMinutes}m")

        // Intercept back button to prevent accidental premature exit
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isSessionCompletedState) {
                    finish()
                } else {
                    Log.d(TAG, "Back pressed while Mandatory Zikir session is ACTIVE. Showing warning dialog.")
                    showExitWarningDialog = true
                }
            }
        })

        setContent {
            MyApplicationTheme {
                MandatoryAdhkarSessionScreen(
                    scheduleId = scheduleId,
                    scheduleTitle = scheduleTitle,
                    category = category,
                    durationMinutes = durationMinutes,
                    speaker = speaker!!,
                    showExitDialog = showExitWarningDialog,
                    onDismissExitDialog = { showExitWarningDialog = false },
                    onConfirmEmergencyExit = {
                        showExitWarningDialog = false
                        speaker?.stop()
                        MandatoryAdhkarManager.cancelSession(this@MandatoryAdhkarActivity, scheduleId, scheduleTitle)
                        Toast.makeText(this@MandatoryAdhkarActivity, "Session interrupted.", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onSessionCompleted = {
                        isSessionCompletedState = true
                        speaker?.stop()
                        MandatoryAdhkarManager.completeSession(this@MandatoryAdhkarActivity, scheduleId, scheduleTitle)
                    },
                    onCloseAfterCompletion = {
                        speaker?.stop()
                        Toast.makeText(this@MandatoryAdhkarActivity, "May Allah accept your Adhkar.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        speaker?.shutdown()
        speaker = null
    }
}

@Composable
fun MandatoryAdhkarSessionScreen(
    scheduleId: String,
    scheduleTitle: String,
    category: String,
    durationMinutes: Int,
    speaker: DuaSpeaker,
    showExitDialog: Boolean,
    onDismissExitDialog: () -> Unit,
    onConfirmEmergencyExit: () -> Unit,
    onSessionCompleted: () -> Unit,
    onCloseAfterCompletion: () -> Unit
) {
    val context = LocalContext.current
    val totalSeconds = remember(durationMinutes) { durationMinutes * 60 }
    var secondsLeft by remember(durationMinutes) { mutableIntStateOf(totalSeconds) }
    var isCompleted by remember { mutableStateOf(false) }
    var duasList by remember { mutableStateOf<List<DuaEntity>>(emptyList()) }

    // Start session in state machine
    LaunchedEffect(scheduleId) {
        MandatoryAdhkarManager.startSession(
            context = context,
            scheduleId = scheduleId,
            scheduleTitle = scheduleTitle,
            category = category,
            durationMinutes = durationMinutes
        )
    }

    // Load Duas for this session
    LaunchedEffect(scheduleId, category) {
        val db = DuaDatabase.getDatabase(context)
        val dao = db.duaDao()
        try {
            if (dao.getDuaCount() == 0) {
                dao.insertDuas(DuaDatabaseSeeder.getSeedDuas())
            }
        } catch (_: Exception) {}

        var allCategoryDuas = dao.getDuasByCategory(category).firstOrNull() ?: emptyList()
        if (allCategoryDuas.isEmpty()) {
            allCategoryDuas = DuaDatabaseSeeder.getSeedDuas().filter { it.category == category }
        }
        if (allCategoryDuas.isEmpty()) {
            allCategoryDuas = dao.getAllDuas().firstOrNull() ?: DuaDatabaseSeeder.getSeedDuas()
        }

        val isMorning = scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_MORNING || scheduleTitle.contains("Morning", ignoreCase = true)
        val isEvening = scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_EVENING || scheduleTitle.contains("Evening", ignoreCase = true)

        val filteredList = allCategoryDuas.filter { dua ->
            val titleLower = dua.title.lowercase()
            val refLower = dua.reference.lowercase()
            if (isMorning) {
                !titleLower.contains("evening supplication") &&
                        !titleLower.contains("evening invocation") &&
                        !titleLower.contains("evening protection") &&
                        !refLower.contains("recited in the evening")
            } else if (isEvening) {
                !titleLower.contains("morning supplication") &&
                        !titleLower.contains("morning invocation") &&
                        !refLower.contains("recited 3 times in morning") &&
                        !refLower.contains("recited in the morning")
            } else {
                true
            }
        }

        duasList = if (filteredList.isNotEmpty()) filteredList else allCategoryDuas
    }

    // Countdown Timer Loop (Only begins when session screen is active)
    LaunchedEffect(secondsLeft, isCompleted) {
        if (!isCompleted) {
            if (secondsLeft > 0) {
                delay(1000L)
                secondsLeft -= 1
                if (secondsLeft % 30 == 0) {
                    Log.d("NOOR_ZIKIR_MANDATORY", "countdown: remaining=$secondsLeft / $totalSeconds seconds")
                }
            } else {
                isCompleted = true
                Log.d("NOOR_ZIKIR_MANDATORY", "countdown: Timer reached zero! Marking session completed.")
                onSessionCompleted()
            }
        }
    }

    val selectedLanguage = remember {
        context.getSharedPreferences("hisnul_muslim_prefs", Context.MODE_PRIVATE)
            .getString("selected_language", "English") ?: "English"
    }

    val deepGreen = Color(0xFF0D5C3A)
    val emeraldAccent = Color(0xFF1B8A5A)
    val goldAccent = Color(0xFFD4AF37)
    val darkCard = Color(0xFF112920)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF091A14)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header: Focus Mode Bar
            Surface(
                color = Color(0xFF0F261E),
                border = BorderStroke(1.dp, Color(0xFF1F4A3B)),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Brand & Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "NOOR ZIKIR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldAccent,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = scheduleTitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCompleted) deepGreen else Color(0xFF2C3E2D),
                            border = BorderStroke(1.dp, if (isCompleted) goldAccent else emeraldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isCompleted) goldAccent else Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (isCompleted) "Completed ✓" else "Focus Mode Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isCompleted) {
                        val minutes = secondsLeft / 60
                        val secs = secondsLeft % 60
                        val timeFormatted = String.format("%02d:%02d", minutes, secs)
                        val progressFraction = 1f - (secondsLeft.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = goldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Time Remaining:",
                                    fontSize = 14.sp,
                                    color = Color(0xFFB0C4BE),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Text(
                                text = timeFormatted,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = goldAccent,
                            trackColor = Color(0xFF1A382E)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Please complete your intentional Zikir session. The session ends automatically when the timer reaches 00:00.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF8EA89F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    } else {
                        // Completed Celebration Banner
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = deepGreen,
                            border = BorderStroke(1.5.dp, goldAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = goldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = "Alhamdulillah! Session Complete",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "May Allah accept your supplications and grant you peace and barakah.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFD4E8DF),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Recitation List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(duasList, key = { it.id }) { dua ->
                    val displayTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage)
                    val translationText = when (selectedLanguage) {
                        "Hausa" -> if (dua.translationHausa.isNotBlank()) dua.translationHausa else dua.translation
                        "Yoruba" -> if (dua.translationYoruba.isNotBlank()) dua.translationYoruba else dua.translation
                        "Igbo" -> if (dua.translationIgbo.isNotBlank()) dua.translationIgbo else dua.translation
                        else -> dua.translation
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = darkCard),
                        border = BorderStroke(1.dp, Color(0xFF224E3E))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = displayTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = goldAccent,
                                    modifier = Modifier.weight(1f)
                                )

                                val isPlaying = speaker.isPlaying.collectAsStateWithLifecycle().value == dua.id
                                IconButton(
                                    onClick = {
                                        if (isPlaying) speaker.stop() else speaker.speakArabic(dua.id, dua.arabic)
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Recite Arabic",
                                        tint = if (isPlaying) Color.Red else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Arabic text
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = dua.arabic,
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily.Serif,
                                    lineHeight = 38.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Transliteration
                            Text(
                                text = dua.transliteration,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF8CE0B7)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Translation
                            Text(
                                text = translationText,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFD0DFDA)
                            )

                            if (dua.reference.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ref: ${dua.reference}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = goldAccent
                                )
                            }
                        }
                    }
                }
            }

            BannerAd()

            // Bottom Action Area
            AnimatedVisibility(visible = isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onCloseAfterCompletion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = goldAccent,
                            contentColor = Color(0xFF091A14)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF091A14)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Finish & Close Session",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF091A14)
                        )
                    }
                }
            }
        }
    }

    // Friendly, Peaceful Premature Exit Warning Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = onDismissExitDialog,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = goldAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Mandatory Zikir Session is Active",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Please complete your scheduled Zikir session before leaving to build your daily spiritual consistency.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFD0DFDA)
                    )
                    Text(
                        text = "The session will complete automatically when the remaining time elapses.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = goldAccent
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissExitDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = deepGreen)
                ) {
                    Text("Continue Zikir", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onConfirmEmergencyExit) {
                    Text("Emergency Exit", color = Color(0xFFE57373))
                }
            },
            containerColor = Color(0xFF0F261E),
            textContentColor = Color.White
        )
    }
}
