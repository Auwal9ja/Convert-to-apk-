package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppLocalizer
import com.example.data.local.DuaDatabase
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaEntity
import com.example.receiver.MandatoryAdhkarManager
import com.example.ui.components.BannerAd
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MandatoryAdhkarActivity : ComponentActivity() {

    private var isTimerFinishedState by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Wake screen & show over lockscreen if allowed (must be before super.onCreate for maximum compatibility)
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

        val adhkarType = intent.getStringExtra("ADHKAR_TYPE") ?: "MORNING"
        val durationMinutes = intent.getIntExtra("READING_DURATION", 3)

        // Lock back button during countdown
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTimerFinishedState) {
                    finish()
                } else {
                    Toast.makeText(
                        this@MandatoryAdhkarActivity,
                        "Please complete your mandatory reading time before exiting.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        setContent {
            MyApplicationTheme {
                MandatoryAdhkarScreen(
                    adhkarType = adhkarType,
                    durationMinutes = durationMinutes,
                    onTimerFinished = {
                        isTimerFinishedState = true
                    },
                    onFinishClick = {
                        if (adhkarType == "MORNING") {
                            MandatoryAdhkarManager.setMorningCompletedToday(this, true)
                        } else {
                            MandatoryAdhkarManager.setEveningCompletedToday(this, true)
                        }
                        Toast.makeText(this, "May Allah accept your Adhkar.", Toast.LENGTH_LONG).show()
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
}

@Composable
fun MandatoryAdhkarScreen(
    adhkarType: String,
    durationMinutes: Int,
    onTimerFinished: () -> Unit,
    onFinishClick: () -> Unit
) {
    val context = LocalContext.current
    var secondsLeft by remember { mutableStateOf(durationMinutes * 60) }
    var isFinished by remember { mutableStateOf(false) }
    var duasList by remember { mutableStateOf<List<DuaEntity>>(emptyList()) }

    val totalSeconds = remember { durationMinutes * 60 }

    LaunchedEffect(Unit) {
        val db = DuaDatabase.getDatabase(context)
        val dao = db.duaDao()
        try {
            if (dao.getDuaCount() == 0) {
                dao.insertDuas(DuaDatabaseSeeder.getSeedDuas())
            }
        } catch (_: Exception) {}

        var allCategoryDuas = dao.getDuasByCategory("Morning & Evening").firstOrNull() ?: emptyList()
        if (allCategoryDuas.isEmpty()) {
            allCategoryDuas = DuaDatabaseSeeder.getSeedDuas().filter { it.category == "Morning & Evening" }
        }

        val isMorning = adhkarType == "MORNING"
        val filteredList = allCategoryDuas.filter { dua ->
            val titleLower = dua.title.lowercase()
            val refLower = dua.reference.lowercase()
            if (isMorning) {
                !titleLower.contains("evening supplication") &&
                        !titleLower.contains("evening invocation") &&
                        !titleLower.contains("evening protection") &&
                        !refLower.contains("recited in the evening")
            } else {
                !titleLower.contains("morning supplication") &&
                        !titleLower.contains("morning invocation") &&
                        !refLower.contains("recited 3 times in morning") &&
                        !refLower.contains("recited in the morning")
            }
        }

        duasList = if (filteredList.isNotEmpty()) filteredList else allCategoryDuas
    }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        } else if (!isFinished) {
            isFinished = true
            onTimerFinished()
        }
    }

    val isMorning = adhkarType == "MORNING"
    val titleText = if (isMorning) "Mandatory Morning Adhkar" else "Mandatory Evening Adhkar"
    val icon = if (isMorning) Icons.Default.WbSunny else Icons.Default.NightsStay
    
    // Vivid rankadau Green and Black Colors
    val deepGreen = Color(0xFF1B5E20)
    val forestGreen = Color(0xFF2E7D32)
    val solidBlack = Color(0xFF000000)
    val darkTextBlack = Color(0xFF111111)
    val lightGreenBg = Color(0xFFE8F5E9)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF4F6F4)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header Timer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, forestGreen),
                colors = CardDefaults.cardColors(
                    containerColor = lightGreenBg
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = deepGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = titleText,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = deepGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isFinished) {
                        val minutes = secondsLeft / 60
                        val secs = secondsLeft % 60
                        val timeStr = String.format("%02d:%02d", minutes, secs)
                        val progress = 1f - (secondsLeft.toFloat() / totalSeconds.toFloat())

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = deepGreen
                            )
                            Text(
                                text = "Reading Time Remaining: $timeStr",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = deepGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = forestGreen,
                            trackColor = Color(0xFFC8E6C9)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = solidBlack
                            )
                            Text(
                                text = "Keep reading until timer ends to complete",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = solidBlack
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = deepGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "May Allah accept your Adhkar.",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = deepGreen
                            )
                        }
                    }
                }
            }

            val selectedLanguage = remember {
                context.getSharedPreferences("hisnul_muslim_prefs", Context.MODE_PRIVATE)
                    .getString("selected_language", "English") ?: "English"
            }

            // Duas Content List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, forestGreen),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = displayTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 24.sp,
                                color = deepGreen
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = dua.arabic,
                                fontSize = 25.sp,
                                lineHeight = 40.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth(),
                                color = solidBlack
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = dua.transliteration,
                                fontSize = 15.5.sp,
                                lineHeight = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = deepGreen
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = translationText,
                                fontSize = 15.sp,
                                lineHeight = 23.sp,
                                fontWeight = FontWeight.Medium,
                                color = darkTextBlack
                            )
                            if (dua.reference.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = dua.reference,
                                    fontSize = 13.5.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = deepGreen
                                )
                            }
                        }
                    }
                }
            }

            // Banner Ad
            BannerAd()

            // Bottom Action Area (Only shows finish button when timer complete)
            AnimatedVisibility(visible = isFinished) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onFinishClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = deepGreen
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Finish & Close Adhkar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
