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
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ui.components.AutoScrollSideBar
import com.example.util.VibrationHelper
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.FormatSize
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
import com.example.data.local.DuaTranslationLocalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppLocalizer
import com.example.ui.theme.QuranFontFamily
import com.example.data.local.DuaDatabase
import com.example.data.local.DuaDatabaseSeeder
import com.example.data.local.DuaEntity
import com.example.data.local.DuaReferenceLocalization
import com.example.receiver.MandatoryAdhkarManager
import com.example.receiver.MandatorySessionState
import com.example.ui.audio.DuaSpeaker
import com.example.ui.components.BannerAd
import com.example.ui.components.InterstitialAdHelper
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
        // Display above keyguard when triggered; do NOT keep screen awake infinitely to prevent battery drain
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        // Ensure FLAG_KEEP_SCREEN_ON is cleared so the system screen timeout operates normally
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as? android.app.KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager?.requestDismissKeyguard(this, null)
        }

        super.onCreate(savedInstanceState)

        InterstitialAdHelper.loadAd(this)
        speaker = DuaSpeaker(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager

        scheduleId = intent.getStringExtra("SCHEDULE_ID")
            ?: if (intent.getStringExtra("ADHKAR_TYPE") == "EVENING") MandatoryAdhkarManager.SCHEDULE_ID_EVENING else MandatoryAdhkarManager.SCHEDULE_ID_MORNING
        scheduleTitle = intent.getStringExtra("SCHEDULE_TITLE")
            ?: if (scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_EVENING) "Evening Zikir" else "Morning Zikir"
        val category = intent.getStringExtra("CATEGORY")
            ?: if (scheduleId == MandatoryAdhkarManager.SCHEDULE_ID_EVENING) "Evening Adhkar" else "Morning Adhkar"
        val durationMinutes = intent.getIntExtra("DURATION_MINUTES", intent.getIntExtra("READING_DURATION", 3))

        Log.d(TAG, "session start: MandatoryAdhkarActivity launched for scheduleId=$scheduleId, title=$scheduleTitle, duration=${durationMinutes}m")

        // Trigger two soft vibrations when Auto Azkar opens
        VibrationHelper.triggerTwoSoftVibrations(this)

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
                    onSessionCompleted = { wasRead ->
                        isSessionCompletedState = true
                        speaker?.stop()
                        MandatoryAdhkarManager.completeSession(this@MandatoryAdhkarActivity, scheduleId, scheduleTitle, wasRead)
                    },
                    onAutoClose = {
                        speaker?.stop()
                        finish()
                    },
                    onCloseAfterCompletion = {
                        speaker?.stop()
                        Toast.makeText(this@MandatoryAdhkarActivity, "May Allah accept your Adhkar.", Toast.LENGTH_LONG).show()
                        InterstitialAdHelper.showAdOnAppExit(this@MandatoryAdhkarActivity) {
                            finish()
                        }
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
    onSessionCompleted: (wasRead: Boolean) -> Unit,
    onAutoClose: () -> Unit,
    onCloseAfterCompletion: () -> Unit
) {
    val context = LocalContext.current
    val totalSeconds = remember(durationMinutes) { durationMinutes * 60 }
    var secondsLeft by remember(durationMinutes) { mutableIntStateOf(totalSeconds) }
    var isCompleted by remember { mutableStateOf(false) }
    var hasUserInteracted by remember { mutableStateOf(false) }
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

    // Countdown Timer Loop: Automatically closes the app when the session duration completes
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
                Log.d("NOOR_ZIKIR_MANDATORY", "countdown: Timer reached zero! Session finished. hasUserInteracted=$hasUserInteracted")
                onSessionCompleted(hasUserInteracted)
                // If user didn't actively interact, close automatically right away to save battery and display notification
                // If user did interact, close automatically after a short 2-second grace period
                val closeDelay = if (hasUserInteracted) 2000L else 500L
                delay(closeDelay)
                onAutoClose()
            }
        }
    }

    val sharedPrefs = remember {
        context.getSharedPreferences("hisnul_muslim_prefs", Context.MODE_PRIVATE)
    }

    val selectedLanguage = remember {
        sharedPrefs.getString("selected_language", "English") ?: "English"
    }

    var arabicFontSize by remember {
        mutableFloatStateOf(sharedPrefs.getFloat("arabic_font_size", 24f))
    }

    var textFontSize by remember {
        mutableFloatStateOf(sharedPrefs.getFloat("text_font_size", 16f))
    }

    var isFontSizeDialogVisible by remember { mutableStateOf(false) }

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
                    // App Brand & Badge & Font Resize Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val localizedScheduleTitle = when (scheduleId) {
                            MandatoryAdhkarManager.SCHEDULE_ID_MORNING -> when (selectedLanguage) {
                                "Hausa" -> "Zikirin Safe na Wajibi"
                                "Yoruba" -> "Zikiri Ọ̀sán Ti O Ṣe Kókó"
                                "Igbo" -> "Ekpere Ụtụtụ nke Iwu"
                                "Arabic" -> "أذكار الصباح الإلزامية"
                                "French" -> "Adhkar du Matin Obligatoire"
                                "Spanish" -> "Adhkar Matutino Obligatorio"
                                "Urdu" -> "لازمی صبح کے اذکار"
                                "Chinese" -> "早晨必念赞念"
                                else -> scheduleTitle
                            }
                            MandatoryAdhkarManager.SCHEDULE_ID_EVENING -> when (selectedLanguage) {
                                "Hausa" -> "Zikirin Yamma na Wajibi"
                                "Yoruba" -> "Zikiri Irọlẹ Ti O Ṣe Kókó"
                                "Igbo" -> "Ekpere Anyasị nke Iwu"
                                "Arabic" -> "أذكار المساء الإلزامية"
                                "French" -> "Adhkar du Soir Obligatoire"
                                "Spanish" -> "Adhkar Vespertino Obligatorio"
                                "Urdu" -> "لازمی شام کے اذکار"
                                "Chinese" -> "傍晚必念赞念"
                                else -> scheduleTitle
                            }
                            else -> scheduleTitle
                        }

                        val focusModeBadge = when (selectedLanguage) {
                            "Hausa" -> if (isCompleted) "An Kammala ✓" else "Yanayin Natsuwa"
                            "Yoruba" -> if (isCompleted) "A Ti Pari ✓" else "Ipo Ifọkanbalẹ"
                            "Igbo" -> if (isCompleted) "Emechara ✓" else "Ọnọdụ Iche Echiche"
                            "Arabic" -> if (isCompleted) "تم الإنجاز ✓" else "وضع التركيز"
                            "French" -> if (isCompleted) "Terminé ✓" else "Mode Concentration"
                            "Spanish" -> if (isCompleted) "Completado ✓" else "Modo Enfoque"
                            "Urdu" -> if (isCompleted) "مکمل ہو گیا ✓" else "فوکس موڈ فعال"
                            "Chinese" -> if (isCompleted) "已完成 ✓" else "专注模式"
                            else -> if (isCompleted) "Completed ✓" else "Focus Mode Active"
                        }

                        Column(modifier = Modifier.weight(1f, fill = false)) {
                            Text(
                                text = "ZAKIRU MUSLIM",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldAccent,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = localizedScheduleTitle,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Font Resize Quick Pill Button (Left/Center Indicator)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF16382B),
                                border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.6f)),
                                modifier = Modifier.clickable { isFontSizeDialogVisible = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = "Adjust Font Size",
                                        tint = goldAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "A⁻ / A⁺",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = goldAccent
                                    )
                                }
                            }

                            // Focus Mode Indicator
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isCompleted) deepGreen else Color(0xFF2C3E2D),
                                border = BorderStroke(1.dp, if (isCompleted) goldAccent else emeraldAccent)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isCompleted) goldAccent else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = focusModeBadge,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (!isCompleted) {
                        val minutes = secondsLeft / 60
                        val secs = secondsLeft % 60
                        val timeFormatted = String.format("%02d:%02d", minutes, secs)
                        val progressFraction = 1f - (secondsLeft.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

                        val timeRemainingLabel = when (selectedLanguage) {
                            "Hausa" -> "Lokacin da Ya Rage:"
                            "Yoruba" -> "Akoko Ti O Kù:"
                            "Igbo" -> "Oge Fọdụrụ:"
                            "Arabic" -> "الوقت المتبقي:"
                            "French" -> "Temps restant:"
                            "Spanish" -> "Tiempo restante:"
                            "Urdu" -> "باقی وقت:"
                            "Chinese" -> "剩余时间："
                            else -> "Time Remaining:"
                        }

                        val bannerInstruction = when (selectedLanguage) {
                            "Hausa" -> "Da fatan za a karanta zikirin da natsuwa. Zikiri zai kammala da kansa idan lokaci ya cika."
                            "Yoruba" -> "Jọwọ ka zikiri pẹlu ifọkanbalẹ. Yio pari laifọwọyi nigbati akoko ba pari."
                            "Igbo" -> "Biko gụọ ekpere gị na udo. Oge ga-agwụ n'onwe ya mgbe elekere ruru 00:00."
                            "Arabic" -> "يرجى قراءة الأذكار بخشوع وطمأنينة. ستنتهي الجلسة تلقائياً عند انتهاء الوقت."
                            "French" -> "Veuillez réciter vos invocations avec recueillement. La session se terminera automatiquement."
                            "Spanish" -> "Por favor recite sus súplicas con concentración. La sesión terminará automáticamente."
                            "Urdu" -> "براہ کرم خشوع و خضوع کے ساتھ اپنے اذکار مکمل کریں۔"
                            "Chinese" -> "请专注诵读赞念。倒计时结束后会话将自动完成。"
                            else -> "Please complete your intentional Zikir session. The session ends automatically when the timer reaches 00:00."
                        }

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
                                    text = timeRemainingLabel,
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
                            text = bannerInstruction,
                            fontSize = 11.5.sp,
                            color = Color(0xFF8EA89F),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    } else {
                        // Completed Celebration Banner
                        val celebrationTitle = when (selectedLanguage) {
                            "Hausa" -> "Alhamdulillah! An Kammala Zikiri"
                            "Yoruba" -> "Alhamdulillah! A Ti Pari Zikiri"
                            "Igbo" -> "Alhamdulillah! Emechara Ekpere"
                            "Arabic" -> "الحمد لله! اكتملت الجلسة"
                            "French" -> "Alhamdulillah! Session terminée"
                            "Spanish" -> "¡Alhamdulillah! Sesión completada"
                            "Urdu" -> "الحمد للہ! اذکار مکمل ہو گئے"
                            "Chinese" -> "一切赞颂全归安拉！会话完成"
                            else -> "Alhamdulillah! Session Complete"
                        }

                        val celebrationSub = when (selectedLanguage) {
                            "Hausa" -> "Allah Ya karbi addu'o'inku, Ya ba ku natsuwa da albarka."
                            "Yoruba" -> "Ki Allāhu gba awọn adura yin, ki O si fun yin ni alaafia ati ibukun."
                            "Igbo" -> "Ka Chineke nara ekpere gị ma nye gị udo na ngọzi."
                            "Arabic" -> "تقبل الله طاعاتكم ورزقكم السكينة والبركة."
                            "French" -> "Qu'Allah accepte vos invocations et vous accorde paix et bénédictions."
                            "Spanish" -> "Que Allah acepte sus súplicas y le conceda paz y bendiciones."
                            "Urdu" -> "اللہ تعالیٰ آپ کی دعائیں قبول فرمائے اور برکت عطا فرمائے۔"
                            "Chinese" -> "愿安拉接受您的祈祷，赐予您宁静与吉庆。"
                            else -> "May Allah accept your supplications and grant you peace and barakah."
                        }

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
                                        text = celebrationTitle,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = celebrationSub,
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

            val listState = rememberLazyListState()

            // Detect if user is actively scrolling or reading
            LaunchedEffect(listState.isScrollInProgress, listState.firstVisibleItemIndex) {
                if (listState.isScrollInProgress || listState.firstVisibleItemIndex > 0) {
                    if (!hasUserInteracted) {
                        hasUserInteracted = true
                        MandatoryAdhkarManager.markSessionAsRead(context, scheduleId)
                    }
                }
            }

            // Recitation List with Right-Side Draggable Auto-Scroll Controller Bar
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(duasList, key = { it.id }) { dua ->
                        val displayTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage)
                        val translationText = DuaTranslationLocalization.getLocalizedTranslation(
                            dua.id,
                            selectedLanguage,
                            dua.translation,
                            dua.translationHausa,
                            dua.translationYoruba,
                            dua.translationIgbo
                        )

                        val localizedReference = if (selectedLanguage == "English") {
                            dua.reference
                        } else {
                            DuaReferenceLocalization.getLocalizedReference(dua.id, selectedLanguage) ?: dua.reference
                        }

                        val referencePrefix = when (selectedLanguage) {
                            "Hausa" -> "Madogara"
                            "Yoruba" -> "Ìtọ́kasí"
                            "Igbo" -> "Ebe nsinyere"
                            "Arabic" -> "المرجع"
                            "French" -> "Référence"
                            "Spanish" -> "Referencia"
                            "Urdu" -> "حوالہ"
                            "Chinese" -> "出处"
                            else -> "Ref"
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
                                        fontSize = (textFontSize * 1.05f).sp,
                                        color = goldAccent,
                                        modifier = Modifier.weight(1f)
                                    )

                                    val isPlaying = speaker.isPlaying.collectAsStateWithLifecycle().value == dua.id
                                    IconButton(
                                        onClick = {
                                            hasUserInteracted = true
                                            MandatoryAdhkarManager.markSessionAsRead(context, scheduleId)
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
                                        fontSize = arabicFontSize.sp,
                                        fontFamily = QuranFontFamily,
                                        lineHeight = (arabicFontSize * 1.8f).sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Transliteration
                                Text(
                                    text = dua.transliteration,
                                    fontSize = (textFontSize * 0.95f).sp,
                                    lineHeight = (textFontSize * 1.5f).sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF8CE0B7)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Translation
                                Text(
                                    text = translationText,
                                    fontSize = textFontSize.sp,
                                    lineHeight = (textFontSize * 1.5f).sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFFD0DFDA)
                                )

                                if (localizedReference.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val formattedRef = if (localizedReference.startsWith(referencePrefix, ignoreCase = true) ||
                                        localizedReference.startsWith("Madogara", ignoreCase = true) ||
                                        localizedReference.startsWith("Reference", ignoreCase = true) ||
                                        localizedReference.startsWith("المرجع", ignoreCase = true) ||
                                        localizedReference.startsWith("Référence", ignoreCase = true) ||
                                        localizedReference.startsWith("Referencia", ignoreCase = true) ||
                                        localizedReference.startsWith("حوالہ", ignoreCase = true) ||
                                        localizedReference.startsWith("出处", ignoreCase = true)
                                    ) {
                                        localizedReference
                                    } else {
                                        "$referencePrefix: $localizedReference"
                                    }
                                    Text(
                                        text = formattedRef,
                                        fontSize = (textFontSize * 0.88f).sp,
                                        lineHeight = (textFontSize * 1.35f).sp,
                                        fontWeight = FontWeight.Medium,
                                        color = goldAccent
                                    )
                                }
                            }
                        }
                    }
                }

                // Draggable Auto-Scroll Side Slider
                AutoScrollSideBar(
                    listState = listState,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = true,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            // Bottom Action Area
            AnimatedVisibility(visible = isCompleted) {
                val finishButtonText = when (selectedLanguage) {
                    "Hausa" -> "Kammala & Fita"
                    "Yoruba" -> "Pari & Jade"
                    "Igbo" -> "Mechie Ekpere"
                    "Arabic" -> "إنهاء وإغلاق الجلسة"
                    "French" -> "Terminer et fermer"
                    "Spanish" -> "Terminar y cerrar"
                    "Urdu" -> "مکمل کریں اور بند کریں"
                    "Chinese" -> "完成并退出"
                    else -> "Finish & Close Session"
                }

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
                            text = finishButtonText,
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
        val dialogTitle = when (selectedLanguage) {
            "Hausa" -> "Ana Cikin Karatun Zikirin Wajibi"
            "Yoruba" -> "Ipo Zikiri Ti O Ṣe Kókó Wà Lọ́wọ́"
            "Igbo" -> "Oge Ekpere nke Iwu Na-aga N'ihu"
            "Arabic" -> "جلسة الأذكار الإلزامية نشطة حالياً"
            "French" -> "Session d'Adhkar Obligatoire en cours"
            "Spanish" -> "Sesión de Adhkar Obligatorio activa"
            "Urdu" -> "لازمی اذکار سیشن جاری ہے"
            "Chinese" -> "必念赞念正在进行中"
            else -> "Mandatory Zikir Session is Active"
        }

        val dialogBody = when (selectedLanguage) {
            "Hausa" -> "Da fatan za a kammala karatun zikirin da aka tsara kafin a rufe domin samun albarka da istiqama a kullum."
            "Yoruba" -> "Jọwọ pari akoko zikiri rẹ ṣaaju ki o to jade lati kọ ifaramọ ẹmi ojoojumọ."
            "Igbo" -> "Biko mezuo oge ekpere gị tupu ị pụọ iji wulite nkwụsi ike nke ime mmụọ kwa ụbọchị."
            "Arabic" -> "يرجى استكمال جلسة الأذكار المحددة قبل المغادرة للحفاظ على الاستمرارية الإيمانية اليومية."
            "French" -> "Veuillez terminer votre session d'adhkar avant de quitter afin de préserver votre régularité spirituelle."
            "Spanish" -> "Por favor complete su sesión de adhkar antes de salir para mantener su constancia espiritual diaria."
            "Urdu" -> "روزانہ روحانی استقامت کے لیے جانے سے پہلے اپنے مقررہ اذکار مکمل فرمائیں۔"
            "Chinese" -> "请在离开前完成预定的赞念会话，以培养日常信仰的坚持。"
            else -> "Please complete your scheduled Zikir session before leaving to build your daily spiritual consistency."
        }

        val dialogSub = when (selectedLanguage) {
            "Hausa" -> "Zikiri zai kammala da kansa da zarar lokacin da ya rage ya cika."
            "Yoruba" -> "Akoko zikiri yoo pari laifọwọyi nigbati akoko ba to."
            "Igbo" -> "Oge ahụ ga-agwụ n'onwe ya mgbe oge fọdụrụnụ ruru."
            "Arabic" -> "ستنتهي الجلسة تلقائياً عند انتهاء الوقت المتبقي."
            "French" -> "La session se terminera automatiquement à l'expiration du temps restant."
            "Spanish" -> "La sesión se completará automáticamente cuando expire el tiempo restante."
            "Urdu" -> "باقی وقت ختم ہونے پر سیشن خود بخود مکمل ہو جائے گا۔"
            "Chinese" -> "倒计时结束后，会话将自动完成。"
            else -> "The session will complete automatically when the remaining time elapses."
        }

        val continueText = when (selectedLanguage) {
            "Hausa" -> "Ci gaba da Zikiri"
            "Yoruba" -> "Tẹsiwaju Zikiri"
            "Igbo" -> "Gaa n'ihu n'Ekpere"
            "Arabic" -> "متابعة الأذكار"
            "French" -> "Continuer les Invocations"
            "Spanish" -> "Continuar con el Zikir"
            "Urdu" -> "اذکار جاری رکھیں"
            "Chinese" -> "继续赞念"
            else -> "Continue Zikir"
        }

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
                    text = dialogTitle,
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
                        text = dialogBody,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFD0DFDA)
                    )
                    Text(
                        text = dialogSub,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = goldAccent
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismissExitDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = deepGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(continueText, fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            containerColor = Color(0xFF0F261E),
            textContentColor = Color.White
        )
    }

    // Font Size Adjuster Dialog for Mandatory Adhkar
    if (isFontSizeDialogVisible) {
        MandatoryFontSizeDialog(
            selectedLanguage = selectedLanguage,
            arabicFontSize = arabicFontSize,
            textFontSize = textFontSize,
            onArabicFontSizeChange = { newSize ->
                arabicFontSize = newSize
                sharedPrefs.edit().putFloat("arabic_font_size", newSize).apply()
            },
            onTextFontSizeChange = { newSize ->
                textFontSize = newSize
                sharedPrefs.edit().putFloat("text_font_size", newSize).apply()
            },
            onDismiss = { isFontSizeDialogVisible = false }
        )
    }
}

@Composable
fun MandatoryFontSizeDialog(
    selectedLanguage: String,
    arabicFontSize: Float,
    textFontSize: Float,
    onArabicFontSizeChange: (Float) -> Unit,
    onTextFontSizeChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val goldAccent = Color(0xFFD4AF37)
    val deepGreen = Color(0xFF0D5C3A)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F261E),
        textContentColor = Color.White,
        icon = {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = null,
                tint = goldAccent,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = AppLocalizer.getString("adjust_font_size", selectedLanguage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Translation / Transliteration / Reference
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = AppLocalizer.getString("translation_transliteration_reference", selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8CE0B7)
                        )
                        Text(
                            text = "${textFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            color = goldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                if (textFontSize > 12f) onTextFontSizeChange(textFontSize - 1f)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF1B4234),
                                contentColor = goldAccent
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Slider(
                            value = textFontSize,
                            onValueChange = onTextFontSizeChange,
                            valueRange = 12f..28f,
                            colors = SliderDefaults.colors(
                                thumbColor = goldAccent,
                                activeTrackColor = goldAccent,
                                inactiveTrackColor = Color(0xFF1B4234)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilledTonalIconButton(
                            onClick = {
                                if (textFontSize < 28f) onTextFontSizeChange(textFontSize + 1f)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF1B4234),
                                contentColor = goldAccent
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Live Preview Box for Translations
                    Surface(
                        color = Color(0xFF091A14),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF1B4234)),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Bismillāh (Transliteration)",
                                fontSize = (textFontSize * 0.95f).sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF8CE0B7)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "In the name of Allah (Translation)",
                                fontSize = textFontSize.sp,
                                color = Color(0xFFD0DFDA)
                            )
                        }
                    }
                }

                // Section 2: Arabic Script
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = AppLocalizer.getString("arabic_script_size", selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = goldAccent
                        )
                        Text(
                            text = "${arabicFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            color = goldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                if (arabicFontSize > 18f) onArabicFontSizeChange(arabicFontSize - 1f)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF1B4234),
                                contentColor = goldAccent
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Slider(
                            value = arabicFontSize,
                            onValueChange = onArabicFontSizeChange,
                            valueRange = 18f..42f,
                            colors = SliderDefaults.colors(
                                thumbColor = goldAccent,
                                activeTrackColor = goldAccent,
                                inactiveTrackColor = Color(0xFF1B4234)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        FilledTonalIconButton(
                            onClick = {
                                if (arabicFontSize < 42f) onArabicFontSizeChange(arabicFontSize + 1f)
                            },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFF1B4234),
                                contentColor = goldAccent
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    // Live Preview Box for Arabic
                    Surface(
                        color = Color(0xFF091A14),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF1B4234)),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = arabicFontSize.sp,
                                fontFamily = QuranFontFamily,
                                lineHeight = (arabicFontSize * 1.8f).sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = deepGreen)
            ) {
                Text(
                    text = AppLocalizer.getString("close", selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    )
}
