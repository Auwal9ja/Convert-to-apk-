package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.QuranFontFamily
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextOverflow
import android.net.Uri
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import com.example.R
import com.example.data.local.AppLocalizer
import com.example.data.local.DuaEntity
import com.example.data.local.DuaTranslationLocalization
import com.example.data.local.DuaReferenceLocalization
import com.example.receiver.MandatoryAdhkarManager
import com.example.receiver.ReminderReceiver
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.ui.DuaViewModel
import com.example.util.UpdateState
import com.example.ui.components.InAppUpdateBanner
import com.example.ui.components.SettingsInAppUpdateTile
import com.example.ui.audio.DuaSpeaker
import com.example.ui.components.BannerAd
import com.example.ui.components.AutoScrollSideBar
import java.util.Calendar

// Play Store redirection link for downloading more apps from developer
const val MORE_APPS_PLAYSTORE_URL = "https://play.google.com/store/apps"
const val COMPANY_WEBSITE_URL = "https://www.najahtech.com"

fun openMoreAppsStore(context: Context, playStoreUrl: String = MORE_APPS_PLAYSTORE_URL) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)).apply {
            setPackage("com.android.vending")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open Google Play Store", Toast.LENGTH_SHORT).show()
        }
    }
}

fun openWebsite(context: Context, websiteUrl: String = COMPANY_WEBSITE_URL) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Could not open website", Toast.LENGTH_SHORT).show()
    }
}

fun openPlayStoreRating(context: Context) {
    val packageName = context.packageName
    try {
        val rateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        }
        context.startActivity(rateIntent)
    } catch (_: Exception) {
        try {
            val webRateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webRateIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open Google Play Store", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DuaViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    updateState: UpdateState = UpdateState.Idle,
    onCheckForUpdates: () -> Unit = {},
    onStartUpdate: () -> Unit = {},
    onCompleteUpdate: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Home, 1 = Library, 2 = Qibla, 3 = Favorites, 4 = Reminders
    val tabBackStack = remember { mutableStateListOf<Int>() }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    var isFontSizeDialogVisible by remember { mutableStateOf(false) }
    var isSettingsDialogVisible by remember { mutableStateOf(false) }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val duas by viewModel.duas.collectAsStateWithLifecycle()
    val allDuas by viewModel.allDuas.collectAsStateWithLifecycle()
    val favorites by viewModel.favoriteDuas.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val textFontSize by viewModel.textFontSize.collectAsStateWithLifecycle()
    val completedDuas by viewModel.completedDuas.collectAsStateWithLifecycle()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsStateWithLifecycle()

    val ratePrefs = remember { context.getSharedPreferences("app_rate_prefs", Context.MODE_PRIVATE) }
    var showRateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isFirstLaunch) {
        if (!isFirstLaunch) {
            val hasRated = ratePrefs.getBoolean("has_rated", false)
            if (!hasRated) {
                val currentCount = ratePrefs.getInt("launch_count", 0) + 1
                ratePrefs.edit().putInt("launch_count", currentCount).apply()
                if (currentCount == 3) {
                    showRateDialog = true
                }
            }
        }
    }

    fun navigateToTab(targetTab: Int) {
        if (selectedTab != targetTab) {
            tabBackStack.add(selectedTab)
            selectedTab = targetTab
        }
    }

    // Comprehensive Back Button Handling: Prevents accidental app exit and navigates back hierarchically
    BackHandler(enabled = !isFirstLaunch) {
        when {
            isSettingsDialogVisible -> {
                isSettingsDialogVisible = false
            }
            isFontSizeDialogVisible -> {
                isFontSizeDialogVisible = false
            }
            selectedTab == 1 && searchQuery.isNotEmpty() -> {
                viewModel.setSearchQuery("")
            }
            selectedTab == 1 && selectedCategory != null -> {
                viewModel.selectCategory(null)
            }
            tabBackStack.isNotEmpty() -> {
                val previousTab = tabBackStack.removeAt(tabBackStack.lastIndex)
                selectedTab = previousTab
            }
            selectedTab != 0 -> {
                selectedTab = 0
            }
            else -> {
                // At Home Screen root: Double tap back button to confirm exit
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000L) {
                    (context as? Activity)?.finish()
                } else {
                    lastBackPressTime = currentTime
                    val exitMsg = when (selectedLanguage) {
                        "Hausa" -> "Latsa baya sau biyu domin fita daga Zakiru Muslim"
                        "Yoruba" -> "Tẹ bọtini pada lẹẹkansi lati jade"
                        "Igbo" -> "Pịa azụ ọzọ ka ịpụ"
                        "Arabic" -> "اضغط رجوع مرة أخرى للخروج من التطبيق"
                        "French" -> "Appuyez à nouveau pour quitter"
                        else -> "Press back again to exit Zakiru Muslim"
                    }
                    Toast.makeText(context, exitMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Instantiate and manage our TTS Speaker
    val speaker = remember { DuaSpeaker(context) }
    DisposableEffect(Unit) {
        onDispose {
            speaker.shutdown()
        }
    }

    if (isFirstLaunch) {
        OnboardingLanguageSelection(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onComplete = { viewModel.completeFirstLaunch() }
        )
    } else if (isSettingsDialogVisible) {
        SettingsScreen(
            onNavigateBack = { isSettingsDialogVisible = false },
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            arabicFontSize = arabicFontSize,
            onArabicFontSizeChange = { viewModel.setArabicFontSize(it) },
            textFontSize = textFontSize,
            onTextFontSizeChange = { viewModel.setTextFontSize(it) },
            updateState = updateState,
            onCheckForUpdates = onCheckForUpdates,
            onStartUpdate = onStartUpdate,
            onCompleteUpdate = onCompleteUpdate,
            context = context
        )
    } else {
        Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    Surface(
                        onClick = {
                            val appTitle = AppLocalizer.getString("app_title", selectedLanguage)
                            val shareBody = when (selectedLanguage) {
                                "Hausa" -> "🌙 *Zakiru Muslim - Addu'o'in Musulmi*\n\nKu sauki manhajar Zakiru Muslim domin samun cikakkun addu'o'in Hisnul Muslim, Zikirin Safiya da Marece, Ruqiya, da Addu'o'in Rabbana 40 tare da fassarar Hausa da sauran harsuna!\n\n📲 Sauke a Play Store:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                                "Yoruba" -> "🌙 *Zakiru Muslim*\n\nṢe igbasilẹ Zakiru Muslim fun awọn adua Hisnul Muslim ti o daju, Adhkar Owurọ ati Alẹ, Ruqyah, ati Awọn Adua Rabbana 40!\n\n📲 Ṣe igbasilẹ lori Play Store:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                                "Igbo" -> "🌙 *Zakiru Muslim*\n\nBudata ngwa Zakiru Muslim maka ekpere Hisnul Muslim zuru oke, Adhkar Ụtụtụ na Anyasị, Ruqyah na Ekpere Rabbana 40!\n\n📲 Budata na Play Store:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                                "Arabic" -> "🌙 *ذاكر المسلم - حصن المسلم والأذكار*\n\nحمل تطبيق ذاكر المسلم للأذكار اليومية الصحيحة، أذكار الصباح والمساء، الرقية الشرعية، و٤٠ دعاء ربنا من القرآن الكريم.\n\n📲 التحميل من متجر جوجل:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                                "French" -> "🌙 *Zakiru Muslim - Invocations & Adhkar*\n\nTéléchargez l'application Zakiru Muslim pour les invocations authentiques de Hisnul Muslim, Adhkar du matin et du soir, Ruqyah et les 40 Duas Rabbana !\n\n📲 Télécharger sur Google Play:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                                else -> "🌙 *Zakiru Muslim - Daily Duas & Adhkar*\n\nDownload Zakiru Muslim app for authentic Islamic supplications (Hisnul Muslim), Morning & Evening Adhkar, Ruqyah healing, and 40 Quranic Rabbana Duas with multi-language translations!\n\n📲 Get it on Google Play:\nhttps://play.google.com/store/apps/details?id=${context.packageName}"
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, appTitle)
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Zakiru Muslim via"))
                        },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFD4AF37),
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .testTag("app_share_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Zakiru Muslim App",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Raba"
                                    "Yoruba" -> "Pin"
                                    "Igbo" -> "Kekọrịta"
                                    "Arabic" -> "مشاركة"
                                    "French" -> "Partager"
                                    "Spanish" -> "Compartir"
                                    "Urdu" -> "شیئر"
                                    "Chinese" -> "分享"
                                    else -> "Share"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                title = {},
                actions = {
                    var isLangMenuExpanded by remember { mutableStateOf(false) }

                    Box {
                        Surface(
                            onClick = { isLangMenuExpanded = true },
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            border = BorderStroke(
                                1.5.dp,
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFD4AF37), // Polished gold
                                        MaterialTheme.colorScheme.primary
                                    )
                                )
                            ),
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .testTag("language_selector")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    Color(0xFFD4AF37),
                                                    Color(0xFF1B5E20)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Language Globe",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    text = when (selectedLanguage) {
                                        "English" -> "EN"
                                        "Hausa" -> "HA"
                                        "Yoruba" -> "YO"
                                        "Igbo" -> "IG"
                                        "Spanish" -> "ES"
                                        "French" -> "FR"
                                        "Arabic" -> "AR"
                                        "Urdu" -> "UR"
                                        "Chinese" -> "ZH"
                                        else -> selectedLanguage.take(2).uppercase()
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = isLangMenuExpanded,
                            onDismissRequest = { isLangMenuExpanded = false }
                        ) {
                            val languages = listOf(
                                "English" to "🇬🇧 English",
                                "Hausa" to "🇳🇬 Hausa (Harshen Hausa)",
                                "Yoruba" to "🇳🇬 Yoruba (Èdè Yorùbá)",
                                "Igbo" to "🇳🇬 Igbo (Asụsụ Igbo)",
                                "Arabic" to "🇸🇦 Arabic (العربية)",
                                "French" to "🇫🇷 French (Français)",
                                "Spanish" to "🇪🇸 Spanish (Español)",
                                "Urdu" to "🇵🇰 Urdu (اردو)",
                                "Chinese" to "🇨🇳 Chinese (中文)"
                            )
                            languages.forEach { (langKey, displayLabel) ->
                                val subtitle = when (langKey) {
                                    "English", "Hausa", "Yoruba", "Igbo" -> "Offline (Built-in)"
                                    else -> "Cloud AI (Caches Offline)"
                                }
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = displayLabel,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (selectedLanguage == langKey) FontWeight.Bold else FontWeight.Normal
                                            )
                                            Text(
                                                text = subtitle,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.setLanguage(langKey)
                                        isLangMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        if (selectedLanguage == langKey) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Surface(
                        onClick = { isFontSizeDialogVisible = true },
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        border = BorderStroke(
                            1.2.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFFD4AF37),
                                    MaterialTheme.colorScheme.primary
                                )
                            )
                        ),
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .testTag("font_size_selector")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Resize Text Font",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = "Aa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = { onToggleTheme(!isDarkTheme) },
                        modifier = Modifier.testTag("theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    IconButton(
                        onClick = { isSettingsDialogVisible = true },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = AppLocalizer.getString("settings", selectedLanguage),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Column {
                if (selectedTab == 0) {
                    BannerAd()
                }
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { navigateToTab(0) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(AppLocalizer.getString("home", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { navigateToTab(1) },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Library") },
                        label = { Text(AppLocalizer.getString("library", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_book")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { navigateToTab(2) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Qibla") },
                        label = { Text(AppLocalizer.getString("qibla", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_qibla")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { navigateToTab(3) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorites"
                            )
                        },
                        label = { Text(AppLocalizer.getString("favorites", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_favorites")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 4,
                        onClick = { navigateToTab(4) },
                        icon = { Icon(Icons.Default.TouchApp, contentDescription = "Tasbeeh") },
                        label = { Text(AppLocalizer.getString("tasbeeh", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_tasbeeh")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeTab(
                    completedDuas = completedDuas,
                    totalDuasCount = if (allDuas.isNotEmpty()) allDuas.size else duas.size,
                    allDuas = allDuas,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme,
                    updateState = updateState,
                    onStartUpdate = onStartUpdate,
                    onCompleteUpdate = onCompleteUpdate,
                    onCategoryClick = { categoryName ->
                        viewModel.selectCategory(categoryName)
                        viewModel.setTargetDuaId(null)
                        navigateToTab(1)
                    },
                    onSelectDua = { dua ->
                        viewModel.selectCategory(dua.category)
                        viewModel.setTargetDuaId(dua.id)
                        navigateToTab(1)
                    },
                    onNavigateToLibraryWithSearch = { query ->
                        viewModel.selectCategory(null)
                        viewModel.setSearchQuery(query)
                        viewModel.setTargetDuaId(null)
                        navigateToTab(1)
                    },
                    onNavigateToQibla = {
                        navigateToTab(2)
                    }
                )
                1 -> LibraryTab(
                    viewModel = viewModel,
                    duas = duas,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    searchQuery = searchQuery,
                    speaker = speaker,
                    selectedLanguage = selectedLanguage,
                    arabicFontSize = arabicFontSize,
                    textFontSize = textFontSize,
                    completedDuas = completedDuas,
                    isDarkTheme = isDarkTheme
                )
                2 -> QiblaCompassScreen(
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme
                )
                3 -> FavoritesTab(
                    viewModel = viewModel,
                    favorites = favorites,
                    speaker = speaker,
                    selectedLanguage = selectedLanguage,
                    arabicFontSize = arabicFontSize,
                    textFontSize = textFontSize,
                    completedDuas = completedDuas,
                    isDarkTheme = isDarkTheme
                )
                4 -> TasbeehScreen(
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }

    if (isFontSizeDialogVisible) {
        AlertDialog(
            onDismissRequest = { isFontSizeDialogVisible = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = AppLocalizer.getString("text_and_font_size", selectedLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = AppLocalizer.getString("font_sync_hint", selectedLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Quick Preset Chips (Karami, Daidai, Babba, Babba Sosai)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val presets = listOf(
                            Triple(if (selectedLanguage == "Hausa") "Ƙarami" else "Small", 13.5f, 20f),
                            Triple(if (selectedLanguage == "Hausa") "Daidai" else "Normal", 16f, 24f),
                            Triple(if (selectedLanguage == "Hausa") "Babba" else "Large", 19f, 29f),
                            Triple(if (selectedLanguage == "Hausa") "Babba Sosai" else "X-Large", 23f, 35f)
                        )
                        presets.forEach { (label, tSize, aSize) ->
                            val isSelected = (textFontSize.toInt() == tSize.toInt() && arabicFontSize.toInt() == aSize.toInt())
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setTextFontSize(tSize)
                                    viewModel.setArabicFontSize(aSize)
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Live preview container Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Live preview of Arabic text
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                                    fontSize = arabicFontSize.sp,
                                    fontFamily = QuranFontFamily,
                                    lineHeight = (arabicFontSize * 1.8f).sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp)
                                )
                            }

                            // Live preview of Transliteration text
                            Text(
                                text = "Al-hamdu lillahi rabbil-'alamin",
                                fontSize = (textFontSize * 0.97f).sp,
                                lineHeight = (textFontSize * 1.45f).sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDarkTheme) MaterialTheme.colorScheme.secondary else Color(0xFF1B5E20),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            )

                            // Live preview of Translation text
                            Text(
                                text = if (selectedLanguage == "Hausa") "Godiya ta tabbata ga Allah Ubangijin talikai" else "All praise is due to Allah, Lord of all the worlds",
                                fontSize = textFontSize.sp,
                                lineHeight = (textFontSize * 1.5f).sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF111111),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            )

                            // Live preview of Reference
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Madogara: Suratul Fatiha 1:2" else "Reference: Surah Al-Fatihah 1:2",
                                    fontSize = (textFontSize * 0.88f).sp,
                                    lineHeight = (textFontSize * 1.35f).sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Slider 1: Translation, Transliteration & Reference
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppLocalizer.getString("translation_transliteration_reference", selectedLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${textFontSize.toInt()} sp",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Left indicator & Decrement button (A-)
                            FilledTonalIconButton(
                                onClick = {
                                    if (textFontSize > 12f) viewModel.setTextFontSize(textFontSize - 1f)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("A-", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }

                            Slider(
                                value = textFontSize,
                                onValueChange = { viewModel.setTextFontSize(it) },
                                valueRange = 12f..28f,
                                modifier = Modifier.weight(1f).testTag("text_font_size_slider")
                            )

                            // Right indicator & Increment button (A+)
                            FilledTonalIconButton(
                                onClick = {
                                    if (textFontSize < 28f) viewModel.setTextFontSize(textFontSize + 1f)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("A+", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Slider 2: Arabic Script Font Size
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppLocalizer.getString("arabic_script_size", selectedLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "${arabicFontSize.toInt()} sp",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Left indicator & Decrement button (A-)
                            FilledTonalIconButton(
                                onClick = {
                                    if (arabicFontSize > 18f) viewModel.setArabicFontSize(arabicFontSize - 1f)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("A-", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                            }

                            Slider(
                                value = arabicFontSize,
                                onValueChange = { viewModel.setArabicFontSize(it) },
                                valueRange = 18f..42f,
                                modifier = Modifier.weight(1f).testTag("arabic_font_size_slider")
                            )

                            // Right indicator & Increment button (A+)
                            FilledTonalIconButton(
                                onClick = {
                                    if (arabicFontSize < 42f) viewModel.setArabicFontSize(arabicFontSize + 1f)
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Text("A+", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.setTextFontSize(16f)
                        viewModel.setArabicFontSize(24f)
                    }
                ) {
                    Text(AppLocalizer.getString("reset_default_size", selectedLanguage))
                }
            },
            confirmButton = {
                Button(
                    onClick = { isFontSizeDialogVisible = false }
                ) {
                    Text(AppLocalizer.getString("done", selectedLanguage))
                }
            }
        )
    }

    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFD4AF37), Color(0xFF1B5E20))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Stars",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Text(
                    text = AppLocalizer.getString("rate_dialog_title", selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = AppLocalizer.getString("rate_dialog_msg", selectedLanguage),
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 5 Golden Interactive Stars Row
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        repeat(5) { index ->
                            IconButton(
                                onClick = {
                                    ratePrefs.edit().putBoolean("has_rated", true).apply()
                                    showRateDialog = false
                                    openPlayStoreRating(context)
                                },
                                modifier = Modifier.size(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star ${index + 1}",
                                    tint = Color(0xFFD4AF37),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    // Direct Rate 5 Stars Button
                    Surface(
                        onClick = {
                            ratePrefs.edit().putBoolean("has_rated", true).apply()
                            showRateDialog = false
                            openPlayStoreRating(context)
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rate_dialog_rate_now_button")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1B5E20), Color(0xFFD4AF37))
                                    )
                                )
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppLocalizer.getString("rate_dialog_btn_rate", selectedLanguage),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        ratePrefs.edit().putBoolean("has_rated", true).apply()
                        showRateDialog = false
                    },
                    modifier = Modifier.testTag("rate_dialog_never_button")
                ) {
                    Text(
                        text = AppLocalizer.getString("rate_dialog_btn_never", selectedLanguage),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showRateDialog = false },
                    modifier = Modifier.testTag("rate_dialog_later_button")
                ) {
                    Text(
                        text = AppLocalizer.getString("rate_dialog_btn_later", selectedLanguage),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        )
    }
    }
}

@Composable
fun LibraryTab(
    viewModel: DuaViewModel,
    duas: List<DuaEntity>,
    categories: List<String>,
    selectedCategory: String?,
    searchQuery: String,
    speaker: DuaSpeaker,
    selectedLanguage: String,
    arabicFontSize: Float,
    textFontSize: Float = 16f,
    completedDuas: Set<Int>,
    isDarkTheme: Boolean = false
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hisnul_muslim_banner),
                contentDescription = "Zakiru Muslim Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Beautiful dark semi-transparent overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x77000000))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    "ذاكر المسلم",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    "Zakiru Muslim • The Fortress of Remembrance",
                    color = Color(0xFFECC76A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("search_field"),
            placeholder = { Text(AppLocalizer.getString("search_placeholder", selectedLanguage)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                    }
                }
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        // Categories Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    leadingIcon = {
                        Text(text = "✨", fontSize = 14.sp)
                    },
                    label = { Text(AppLocalizer.getString("all_topics", selectedLanguage)) },
                    modifier = Modifier.testTag("chip_all")
                )
            }
            items(categories) { category ->
                val emoji = when (category) {
                    "Morning & Evening" -> "☀️"
                    "Sleeping & Waking Up" -> "🌙"
                    "Prayers & Mosque" -> "🕌"
                    "Post-Salah Adhkar" -> "📿"
                    "Ablution & Purification" -> "💧"
                    "Eating & Drinking" -> "🍽️"
                    "Dressing" -> "👕"
                    "Travel & Home" -> "🚗"
                    "Hardship & Anxiety" -> "🤲"
                    "Protection & Evil Eye" -> "🛡️"
                    "Visiting the Sick" -> "🩺"
                    "Good Manners" -> "🤝"
                    "Greetings & Social" -> "💬"
                    "Rain & Wind" -> "🌧️"
                    "Market & Shopping" -> "🛒"
                    "Grave & Funeral" -> "⚰️"
                    "Fasting & Ramadan" -> "🏮"
                    "Hajj & Umrah" -> "🕋"
                    "Marriage & Family" -> "💍"
                    "Repentance & Seeking Forgiveness" -> "🧎"
                    "Ruqyah" -> "🌿"
                    "40 Rabbana Duas" -> "📖"
                    else -> "✨"
                }
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) },
                    leadingIcon = {
                        Text(text = emoji, fontSize = 14.sp)
                    },
                    label = { Text(AppLocalizer.getCategoryName(category, selectedLanguage)) },
                    modifier = Modifier.testTag("chip_$category")
                )
            }
        }

        // Duas List
        if (duas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No Results",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        AppLocalizer.getString("no_duas_found", selectedLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        AppLocalizer.getString("try_searching_other", selectedLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        } else {
            val listState = rememberLazyListState()
            val targetDuaId by viewModel.targetDuaId.collectAsStateWithLifecycle()

            LaunchedEffect(targetDuaId, duas) {
                targetDuaId?.let { id ->
                    val index = duas.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        val hasFeatured = searchQuery.isEmpty() && selectedCategory == null
                        val itemIndex = if (hasFeatured) index + 1 else index
                        listState.animateScrollToItem(itemIndex)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (searchQuery.isEmpty() && selectedCategory == null) {
                        item {
                            val featuredDua = duas.find { it.id == 2 }
                            FeaturedCard(
                                dua = featuredDua,
                                speaker = speaker,
                                selectedLanguage = selectedLanguage,
                                getTranslation = { d, lang -> viewModel.getTranslationAndReference(d, lang) },
                                arabicFontSize = arabicFontSize,
                                textFontSize = textFontSize
                            )
                        }
                    }
                    items(duas, key = { it.id }) { dua ->
                        DuaItemCard(
                            dua = dua,
                            speaker = speaker,
                            searchQuery = searchQuery,
                            selectedLanguage = selectedLanguage,
                            arabicFontSize = arabicFontSize,
                            textFontSize = textFontSize,
                            isCompleted = completedDuas.contains(dua.id),
                            isDarkTheme = isDarkTheme,
                            isTarget = targetDuaId == dua.id,
                            onCompleteToggle = { viewModel.toggleCompleted(dua.id) },
                            getTranslation = { d, lang -> viewModel.getTranslationAndReference(d, lang) },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(dua.id, dua.isFavorite)
                            }
                        )
                    }
                }

                // Modern Draggable Auto-Scroll Side Slider
                AutoScrollSideBar(
                    listState = listState,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun FavoritesTab(
    viewModel: DuaViewModel,
    favorites: List<DuaEntity>,
    speaker: DuaSpeaker,
    selectedLanguage: String,
    arabicFontSize: Float,
    textFontSize: Float = 16f,
    completedDuas: Set<Int>,
    isDarkTheme: Boolean = false
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            AppLocalizer.getString("my_favorites", selectedLanguage),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "No Favorites",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        AppLocalizer.getString("no_favorites_title", selectedLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        AppLocalizer.getString("no_favorites_body", selectedLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val listState = rememberLazyListState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favorites, key = { it.id }) { dua ->
                        DuaItemCard(
                            dua = dua,
                            speaker = speaker,
                            selectedLanguage = selectedLanguage,
                            arabicFontSize = arabicFontSize,
                            textFontSize = textFontSize,
                            isCompleted = completedDuas.contains(dua.id),
                            isDarkTheme = isDarkTheme,
                            onCompleteToggle = { viewModel.toggleCompleted(dua.id) },
                            getTranslation = { d, lang -> viewModel.getTranslationAndReference(d, lang) },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(dua.id, dua.isFavorite)
                            }
                        )
                    }
                }

                // Modern Draggable Auto-Scroll Side Slider
                AutoScrollSideBar(
                    listState = listState,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
fun RemindersTab(selectedLanguage: String = "English") {
    val context = LocalContext.current
    var isMorningEnabled by remember { mutableStateOf(false) }
    var isEveningEnabled by remember { mutableStateOf(false) }
    
    // Check initial alarm state from shared prefs
    val sharedPref = remember { context.getSharedPreferences("reminders_prefs", Context.MODE_PRIVATE) }
    
    LaunchedEffect(Unit) {
        isMorningEnabled = sharedPref.getBoolean("morning_enabled", false)
        isEveningEnabled = sharedPref.getBoolean("evening_enabled", false)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                Toast.makeText(context, AppLocalizer.getString("daily_reminders", selectedLanguage), Toast.LENGTH_LONG).show()
            }
        }
    )

    fun checkAndRequestPermission(onGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val status = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (status == PackageManager.PERMISSION_GRANTED) {
                onGranted()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            AppLocalizer.getString("reminders_title", selectedLanguage),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            AppLocalizer.getString("reminders_subtitle", selectedLanguage),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Morning Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "☀️",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            AppLocalizer.getString("morning_azkar", selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            AppLocalizer.getString("everyday_7am", selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Switch(
                    checked = isMorningEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            checkAndRequestPermission {
                                scheduleAlarm(context, "MORNING", 7, 0)
                                sharedPref.edit().putBoolean("morning_enabled", true).apply()
                                isMorningEnabled = true
                                Toast.makeText(context, AppLocalizer.getString("morning_azkar", selectedLanguage) + " - " + AppLocalizer.getString("reminders_active", selectedLanguage), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            cancelAlarm(context, "MORNING")
                            sharedPref.edit().putBoolean("morning_enabled", false).apply()
                            isMorningEnabled = false
                            Toast.makeText(context, AppLocalizer.getString("morning_azkar", selectedLanguage) + " - " + AppLocalizer.getString("reminders_off", selectedLanguage), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("switch_morning")
                )
            }
        }

        // Evening Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "🌙",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    Column {
                        Text(
                            AppLocalizer.getString("evening_azkar", selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            AppLocalizer.getString("everyday_530pm", selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Switch(
                    checked = isEveningEnabled,
                    onCheckedChange = { checked ->
                        if (checked) {
                            checkAndRequestPermission {
                                scheduleAlarm(context, "EVENING", 17, 30)
                                sharedPref.edit().putBoolean("evening_enabled", true).apply()
                                isEveningEnabled = true
                                Toast.makeText(context, AppLocalizer.getString("evening_azkar", selectedLanguage) + " - " + AppLocalizer.getString("reminders_active", selectedLanguage), Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            cancelAlarm(context, "EVENING")
                            sharedPref.edit().putBoolean("evening_enabled", false).apply()
                            isEveningEnabled = false
                            Toast.makeText(context, AppLocalizer.getString("evening_azkar", selectedLanguage) + " - " + AppLocalizer.getString("reminders_off", selectedLanguage), Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.testTag("switch_evening")
                )
            }
        }

        // Mandatory Daily Adhkar (Auto-Open Over Any App) Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mandatory Adhkar (Auto-Open / Bude Kai Tsaye)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Opens automatically over any running app when the time arrives.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Testing Auto-Open in 3 seconds. Switch to another app!", Toast.LENGTH_LONG).show()
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                com.example.receiver.MandatoryAdhkarManager.triggerTestNow(context, "MORNING")
                            }, 3000L)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Auto-Open Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Info Tip
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Tips",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    "Our local reminder engine uses Android system scheduling, meaning it runs entirely offline and draws zero background battery.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun scheduleAlarm(context: Context, type: String, hour: Int, minute: Int) {
    ReminderReceiver.scheduleDailyReminder(context, type, hour, minute)
}

fun cancelAlarm(context: Context, type: String) {
    ReminderReceiver.cancelReminder(context, type)
}

@Composable
fun DuaItemCard(
    dua: DuaEntity,
    speaker: DuaSpeaker,
    searchQuery: String = "",
    selectedLanguage: String = "English",
    arabicFontSize: Float = 24f,
    textFontSize: Float = 16f,
    isCompleted: Boolean = false,
    isDarkTheme: Boolean = false,
    isTarget: Boolean = false,
    onCompleteToggle: () -> Unit = {},
    getTranslation: suspend (DuaEntity, String) -> Pair<String, String>,
    onFavoriteToggle: () -> Unit
) {
    val context = LocalContext.current

    var translationText by remember(dua.id, selectedLanguage) {
        val initial = DuaTranslationLocalization.getLocalizedTranslation(
            dua.id,
            selectedLanguage,
            dua.translation,
            dua.translationHausa,
            dua.translationYoruba,
            dua.translationIgbo
        )
        mutableStateOf(initial)
    }

    var referenceText by remember(dua.id, selectedLanguage) {
        val initialRef = if (selectedLanguage == "English") {
            dua.reference
        } else {
            DuaReferenceLocalization.getLocalizedReference(dua.id, selectedLanguage) ?: dua.reference
        }
        mutableStateOf(initialRef)
    }

    var isTranslating by remember(dua.id, selectedLanguage) {
        mutableStateOf(false)
    }

    LaunchedEffect(dua.id, selectedLanguage) {
        val localTrans = DuaTranslationLocalization.getLocalizedTranslation(
            dua.id,
            selectedLanguage,
            dua.translation,
            dua.translationHausa,
            dua.translationYoruba,
            dua.translationIgbo
        )
        val localRef = if (selectedLanguage == "English") {
            dua.reference
        } else {
            DuaReferenceLocalization.getLocalizedReference(dua.id, selectedLanguage) ?: dua.reference
        }

        translationText = localTrans
        referenceText = localRef

        if (selectedLanguage != "English" && localTrans == dua.translation) {
            isTranslating = true
            try {
                val result = getTranslation(dua, selectedLanguage)
                translationText = result.first
                referenceText = result.second
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isTranslating = false
            }
        }
    }

    // Observe speaker playing state
    val playingArabicId by speaker.isPlaying.collectAsStateWithLifecycle()
    val playingTranslationId by speaker.isPlayingTranslation.collectAsStateWithLifecycle()

    val isArabicPlaying = playingArabicId == dua.id
    val isTranslationPlaying = playingTranslationId == dua.id

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = if (isTarget) BorderStroke(2.dp, if (isDarkTheme) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTarget) 6.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dua_card_${dua.id}")
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Category & Favorites Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dua.category.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCompleteToggle,
                        modifier = Modifier.size(36.dp).testTag("btn_complete_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Mark Complete",
                            tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.testTag("btn_favorite_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (dua.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite Toggle",
                            tint = if (dua.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Supplication Title
            Text(
                text = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage),
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic text layout (RTL) - Quranic Typography
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = dua.arabic,
                    fontSize = arabicFontSize.sp,
                    fontFamily = QuranFontFamily,
                    lineHeight = (arabicFontSize * 1.85f).sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Transliteration text (phonetics)
            Text(
                text = dua.transliteration,
                fontSize = (textFontSize * 0.97f).sp,
                lineHeight = (textFontSize * 1.5f).sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) MaterialTheme.colorScheme.secondary else Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation text
            Column {
                Text(
                    text = translationText,
                    fontSize = textFontSize.sp,
                    lineHeight = (textFontSize * 1.5f).sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF111111)
                )

                if (isTranslating) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val translatingMsg = if (selectedLanguage == "Hausa" || selectedLanguage == "Yoruba" || selectedLanguage == "Igbo") {
                        "Translating reference & virtue with AI..."
                    } else {
                        "Translating to $selectedLanguage with AI..."
                    }
                    Text(
                        text = translatingMsg,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Permanent Reference & Virtue section
            Column(modifier = Modifier.padding(top = 12.dp)) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            val referenceLabel = AppLocalizer.getString("reference_and_virtue", selectedLanguage)
                            Text(
                                text = referenceLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = referenceText,
                            fontSize = (textFontSize * 0.88f).sp,
                            lineHeight = (textFontSize * 1.35f).sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF1B5E20)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Row: Audio play buttons, share
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Audio recitation controllers (Arabic Quranic/Adhkar pronunciation)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            if (isArabicPlaying) speaker.stop() else speaker.speakArabic(dua.id, dua.arabic)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isArabicPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_play_arabic_${dua.id}")
                    ) {
                        Icon(
                            imageVector = if (isArabicPlaying) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Play Arabic Audio",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val playLabel = if (isArabicPlaying) {
                            if (selectedLanguage == "Hausa") "Tsayar" else "Stop"
                        } else {
                            if (selectedLanguage == "Hausa") "Saurari (Koyi Furtawa)" else "Listen & Pronounce"
                        }
                        Text(playLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Share controller
                Row {
                    IconButton(
                        onClick = {
                            val localizedTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage)
                            val shareBody = "${localizedTitle}\n\n${dua.arabic}\n\n${translationText}\n\nRef: ${referenceText}"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareBody)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Supplication via"))
                        },
                        modifier = Modifier.testTag("btn_share_${dua.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Supplication",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedCard(
    dua: DuaEntity?,
    speaker: DuaSpeaker,
    selectedLanguage: String,
    getTranslation: suspend (DuaEntity, String) -> Pair<String, String>,
    arabicFontSize: Float = 24f,
    textFontSize: Float = 16f
) {
    val playingArabicId by speaker.isPlaying.collectAsStateWithLifecycle()
    val isPlaying = playingArabicId == 2 // ID 2 for Master Forgiveness

    var translationText by remember(dua?.id, selectedLanguage) {
        val initial = if (dua != null) {
            DuaTranslationLocalization.getLocalizedTranslation(
                dua.id,
                selectedLanguage,
                dua.translation,
                dua.translationHausa,
                dua.translationYoruba,
                dua.translationIgbo
            )
        } else {
            "O Allah, You are my Lord, there is none worthy of worship but You..."
        }
        mutableStateOf(initial)
    }

    var isTranslating by remember(dua?.id, selectedLanguage) {
        mutableStateOf(false)
    }

    LaunchedEffect(dua, selectedLanguage) {
        if (dua != null) {
            val localTrans = DuaTranslationLocalization.getLocalizedTranslation(
                dua.id,
                selectedLanguage,
                dua.translation,
                dua.translationHausa,
                dua.translationYoruba,
                dua.translationIgbo
            )
            translationText = localTrans

            if (selectedLanguage != "English" && localTrans == dua.translation) {
                isTranslating = true
                try {
                    val result = getTranslation(dua, selectedLanguage)
                    translationText = result.first
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    isTranslating = false
                }
            }
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .testTag("featured_dua_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "FEATURED ADHKAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Sayyidul Istighfar",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Arabic text (RTL)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
                    fontSize = arabicFontSize.sp,
                    fontFamily = QuranFontFamily,
                    lineHeight = (arabicFontSize * 1.85f).sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Translation (phonetic or meaning) based on selected language
            Column {
                Text(
                    text = translationText,
                    fontSize = textFontSize.sp,
                    lineHeight = (textFontSize * 1.5f).sp,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )

                if (isTranslating) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Translating with AI...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Master Forgiveness",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }

                Button(
                    onClick = {
                        if (isPlaying) {
                            speaker.stop()
                        } else {
                            speaker.speakArabic(
                                2,
                                "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ"
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Stop" else "Listen",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "Stop" else "Listen",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTab(
    completedDuas: Set<Int>,
    totalDuasCount: Int,
    allDuas: List<DuaEntity> = emptyList(),
    selectedLanguage: String,
    isDarkTheme: Boolean,
    updateState: UpdateState = UpdateState.Idle,
    onStartUpdate: () -> Unit = {},
    onCompleteUpdate: () -> Unit = {},
    onCategoryClick: (String?) -> Unit,
    onSelectDua: (DuaEntity) -> Unit = {},
    onNavigateToLibraryWithSearch: (String) -> Unit = {},
    onNavigateToQibla: () -> Unit = {}
) {
    val completedCount = completedDuas.size
    val displayCompleted = if (completedCount > 0) completedCount else 12
    val displayTotal = if (totalDuasCount > 0) totalDuasCount else 19
    val progressFraction = (displayCompleted.toFloat() / displayTotal.toFloat()).coerceIn(0f, 1f)
    val progressPercent = (progressFraction * 100).toInt()

    var isBannerDismissed by remember { mutableStateOf(false) }
    var homeSearchQuery by remember { mutableStateOf("") }

    val filteredDuas = remember(homeSearchQuery, allDuas, selectedLanguage) {
        if (homeSearchQuery.isBlank()) {
            emptyList()
        } else {
            val q = homeSearchQuery.trim()
            allDuas.filter { dua ->
                dua.title.contains(q, ignoreCase = true) ||
                AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage).contains(q, ignoreCase = true) ||
                dua.arabic.contains(q, ignoreCase = true) ||
                dua.transliteration.contains(q, ignoreCase = true) ||
                dua.translation.contains(q, ignoreCase = true) ||
                dua.translationHausa.contains(q, ignoreCase = true) ||
                dua.translationYoruba.contains(q, ignoreCase = true) ||
                dua.translationIgbo.contains(q, ignoreCase = true) ||
                dua.reference.contains(q, ignoreCase = true) ||
                dua.category.contains(q, ignoreCase = true) ||
                AppLocalizer.getCategoryName(dua.category, selectedLanguage).contains(q, ignoreCase = true)
            }
        }
    }

    val bgGradient = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF040E0C),
                Color(0xFF0C2420),
                Color(0xFF081412)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF2F7F5),
                Color(0xFFE5EEEB),
                Color(0xFFF6F9F8)
            )
        )
    }

    val cardBg = if (isDarkTheme) Color(0xFF0E221E) else MaterialTheme.colorScheme.surface
    val cardBorder = if (isDarkTheme) Color(0xFF1B3D34) else MaterialTheme.colorScheme.outlineVariant
    val textPrimary = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
    val textSecondary = if (isDarkTheme) Color(0xFFAEC4BE) else MaterialTheme.colorScheme.onSurfaceVariant
    val goldAccent = if (isDarkTheme) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isBannerDismissed && (updateState is UpdateState.UpdateAvailable || updateState is UpdateState.Downloading || updateState is UpdateState.Downloaded)) {
            item {
                InAppUpdateBanner(
                    updateState = updateState,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme,
                    onStartUpdate = onStartUpdate,
                    onCompleteUpdate = onCompleteUpdate,
                    onDismiss = { isBannerDismissed = true }
                )
            }
        }

        // Celestial Hero Header Card
        item {
            val heroBg = if (isDarkTheme) {
                Brush.radialGradient(
                    colors = listOf(Color(0xFF143B33), Color(0xFF0A221E)),
                    center = Offset(350f, 150f),
                    radius = 450f
                )
            } else {
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0F5257), Color(0xFF186F6B)),
                    center = Offset(350f, 150f),
                    radius = 450f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(heroBg)
            ) {
                // Interactive celestial starry background
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Golden crescent moon
                    drawCircle(color = Color(0xFFD4AF37), radius = 28.dp.toPx(), center = Offset(size.width - 50.dp.toPx(), 45.dp.toPx()))
                    drawCircle(color = if (isDarkTheme) Color(0xFF0A221E) else Color(0xFF186F6B), radius = 26.dp.toPx(), center = Offset(size.width - 58.dp.toPx(), 41.dp.toPx()))

                    // Sparkling celestial stars
                    drawCircle(color = Color(0xBBFFFFFF), radius = 2.dp.toPx(), center = Offset(size.width - 120.dp.toPx(), 25.dp.toPx()))
                    drawCircle(color = Color(0x66FFFFFF), radius = 1.5.dp.toPx(), center = Offset(size.width - 80.dp.toPx(), 110.dp.toPx()))
                    drawCircle(color = Color(0xDDFFFFFF), radius = 2.5.dp.toPx(), center = Offset(size.width - 160.dp.toPx(), 65.dp.toPx()))
                    drawCircle(color = Color(0x77FFFFFF), radius = 1.2.dp.toPx(), center = Offset(40.dp.toPx(), 30.dp.toPx()))
                    drawCircle(color = Color(0x99FFFFFF), radius = 2.dp.toPx(), center = Offset(120.dp.toPx(), 140.dp.toPx()))
                    drawCircle(color = Color(0x55FFFFFF), radius = 1.5.dp.toPx(), center = Offset(220.dp.toPx(), 40.dp.toPx()))
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                AppLocalizer.getString("app_title", selectedLanguage),
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }

                    Column {
                        Text(
                            AppLocalizer.getString("app_slogan", selectedLanguage),
                            color = Color(0xFFD4AF37),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Top Search Bar for Duas & Zikir (Dan Gurbi Mai Kyau na Bincike)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) Color(0xFF0F2620) else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.2.dp,
                    if (homeSearchQuery.isNotEmpty()) goldAccent else cardBorder
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = goldAccent.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = goldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = homeSearchQuery,
                            onValueChange = { homeSearchQuery = it },
                            placeholder = {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Nemo addu'a, zikiri, ko fassara..."
                                           else if (selectedLanguage == "Arabic") "ابحث عن أي دعاء أو ذكر..."
                                           else if (selectedLanguage == "Yoruba") "Ṣàwárí àwọn àdúrà, zikiri..."
                                           else if (selectedLanguage == "Igbo") "Chọọ ekpere ma ọ bụ zikir..."
                                           else AppLocalizer.getString("search_placeholder", selectedLanguage),
                                    fontSize = 14.sp,
                                    color = textSecondary.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                cursorColor = goldAccent,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("home_search_input_field")
                        )

                        if (homeSearchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { homeSearchQuery = "" },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = textSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            FilledTonalIconButton(
                                onClick = { onNavigateToLibraryWithSearch(homeSearchQuery) },
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = goldAccent,
                                    contentColor = if (isDarkTheme) Color.Black else Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Search in Library",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Quick Search Suggestion Pills when search query is empty
                    if (homeSearchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        val quickPills = when (selectedLanguage) {
                            "Hausa" -> listOf(
                                "☀️ Safe & Yamma" to "Morning & Evening",
                                "🤲 Istighfari" to "Repentance & Seeking Forgiveness",
                                "📿 Bayan Sallah" to "Post-Salah Adhkar",
                                "🛡️ Kariya" to "Protection & Evil Eye",
                                "🛌 Barci" to "Sleeping & Waking Up",
                                "🕌 Sallah" to "Prayers & Mosque",
                                "🕋 Hajji & Umra" to "Hajj & Umrah",
                                "💍 Aure & Iyali" to "Marriage & Family",
                                "🍽️ Abinci" to "Eating & Drinking",
                                "🌧️ Ruwa & Iska" to "Rain & Wind"
                            )
                            "Arabic" -> listOf(
                                "☀️ الصباح والمساء" to "Morning & Evening",
                                "🤲 الاستغفار" to "Repentance & Seeking Forgiveness",
                                "📿 بعد الصلاة" to "Post-Salah Adhkar",
                                "🛡️ الرقية والحماية" to "Protection & Evil Eye",
                                "🛌 النوم" to "Sleeping & Waking Up",
                                "🕌 الصلاة والمسجد" to "Prayers & Mosque",
                                "🕋 الحج والعمرة" to "Hajj & Umrah",
                                "💍 الأسرة" to "Marriage & Family",
                                "🍽️ الطعام" to "Eating & Drinking"
                            )
                            "Yoruba" -> listOf(
                                "☀️ Owurọ̀ & Alẹ́" to "Morning & Evening",
                                "🤲 Ironupiwada" to "Repentance & Seeking Forgiveness",
                                "📿 Lẹ́yìn Sọláàti" to "Post-Salah Adhkar",
                                "🛡️ Ààbò" to "Protection & Evil Eye",
                                "🛌 Isún" to "Sleeping & Waking Up",
                                "🕌 Sọláàti" to "Prayers & Mosque",
                                "🕋 Hajj & Umrah" to "Hajj & Umrah",
                                "💍 Igbeyawo" to "Marriage & Family"
                            )
                            "Igbo" -> listOf(
                                "☀️ Ụtụtụ & Anyasị" to "Morning & Evening",
                                "🤲 Nchegharị" to "Repentance & Seeking Forgiveness",
                                "📿 Mgbe Ekpere" to "Post-Salah Adhkar",
                                "🛡️ Nchebe" to "Protection & Evil Eye",
                                "🛌 Ụra" to "Sleeping & Waking Up",
                                "🕌 Ekpere" to "Prayers & Mosque",
                                "🕋 Hajj & Umrah" to "Hajj & Umrah",
                                "💍 Ezinụlọ" to "Marriage & Family"
                            )
                            else -> listOf(
                                "☀️ Morning & Evening" to "Morning & Evening",
                                "🤲 Istighfar" to "Repentance & Seeking Forgiveness",
                                "📿 Post-Salah" to "Post-Salah Adhkar",
                                "🛡️ Protection" to "Protection & Evil Eye",
                                "🛌 Sleep" to "Sleeping & Waking Up",
                                "🕌 Prayers" to "Prayers & Mosque",
                                "🕋 Hajj & Umrah" to "Hajj & Umrah",
                                "💍 Family" to "Marriage & Family",
                                "🍽️ Food & Drink" to "Eating & Drinking"
                            )
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(vertical = 2.dp)
                        ) {
                            items(quickPills) { (label, category) ->
                                Surface(
                                    onClick = { onCategoryClick(category) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isDarkTheme) Color(0xFF143B33) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                    border = BorderStroke(0.8.dp, goldAccent.copy(alpha = 0.35f))
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDarkTheme) Color(0xFFECC76A) else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Search Results on Home Screen (When Searching)
        if (homeSearchQuery.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "Sakamakon Bincike (${filteredDuas.size})"
                               else if (selectedLanguage == "Arabic") "نتائج البحث (${filteredDuas.size})"
                               else "Search Results (${filteredDuas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )

                    if (filteredDuas.isNotEmpty()) {
                        TextButton(
                            onClick = { onNavigateToLibraryWithSearch(homeSearchQuery) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Duba Duka a Library ➔" else "View in Library ➔",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldAccent
                            )
                        }
                    }
                }
            }

            if (filteredDuas.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = goldAccent,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa") "Babu addu'ar da ta dace da \"$homeSearchQuery\""
                                       else if (selectedLanguage == "Arabic") "لم يتم العثور على نتائج لـ \"$homeSearchQuery\""
                                       else "No duas found for \"$homeSearchQuery\"",
                                color = textPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            OutlinedButton(
                                onClick = { homeSearchQuery = "" },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Goge Bincike" else "Clear Search",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredDuas.take(12)) { dua ->
                    val localizedTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage)
                    val localizedCategory = AppLocalizer.getCategoryName(dua.category, selectedLanguage)
                    val localizedTrans = DuaTranslationLocalization.getLocalizedTranslation(
                        dua.id,
                        selectedLanguage,
                        dua.translation,
                        dua.translationHausa,
                        dua.translationYoruba,
                        dua.translationIgbo
                    )

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectDua(dua) }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = goldAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = localizedCategory,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = goldAccent,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Text(
                                    text = "#${dua.id}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textSecondary
                                )
                            }

                            Text(
                                text = localizedTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            if (dua.arabic.isNotBlank()) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Text(
                                        text = dua.arabic,
                                        fontSize = 17.sp,
                                        fontFamily = QuranFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        lineHeight = 28.sp,
                                        color = if (isDarkTheme) Color(0xFFE0ECE8) else Color(0xFF133830),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            if (localizedTrans.isNotBlank()) {
                                Text(
                                    text = localizedTrans,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = textSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                FilledTonalButton(
                                    onClick = { onSelectDua(dua) },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = goldAccent.copy(alpha = 0.2f),
                                        contentColor = goldAccent
                                    )
                                ) {
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Bude Addu'a ➔" else "Open Dua ➔",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Today's Progress Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular progress indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(76.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF329F84),
                            strokeWidth = 7.dp,
                            trackColor = if (isDarkTheme) Color(0xFF132D27) else Color(0xFFE0EFEA)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$progressPercent%",
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            AppLocalizer.getString("todays_progress", selectedLanguage),
                            color = textPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (completedCount > 0) AppLocalizer.getString("great_keep_going", selectedLanguage) else AppLocalizer.getString("establish_daily_shield", selectedLanguage),
                            color = goldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "$displayCompleted / $displayTotal " + AppLocalizer.getString("completed", selectedLanguage),
                            color = textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go to Library",
                        tint = goldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }


        // Grid of All Categories and Azkar
        item {
            val countLabel = when (selectedLanguage) {
                "Hausa" -> "Addu'a"
                "Arabic" -> "دعاء"
                "Yoruba" -> "Adua"
                "Igbo" -> "Ekpere"
                "French" -> "Duas"
                else -> "Duas"
            }

            val categoriesList = listOf(
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Morning & Evening", selectedLanguage),
                    dbCategory = "Morning & Evening",
                    emoji = "☀️",
                    gradient = listOf(Color(0xFFFFA000), Color(0xFFFF6F00), Color(0xFFD84315)),
                    duaCount = allDuas.count { it.category.equals("Morning & Evening", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Sleeping & Waking Up", selectedLanguage),
                    dbCategory = "Sleeping & Waking Up",
                    emoji = "🌙",
                    gradient = listOf(Color(0xFF3949AB), Color(0xFF1E88E5), Color(0xFF0D47A1)),
                    duaCount = allDuas.count { it.category.equals("Sleeping & Waking Up", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Prayers & Mosque", selectedLanguage),
                    dbCategory = "Prayers & Mosque",
                    emoji = "🕌",
                    gradient = listOf(Color(0xFF00897B), Color(0xFF004D40), Color(0xFF00796B)),
                    duaCount = allDuas.count { it.category.equals("Prayers & Mosque", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Post-Salah Adhkar", selectedLanguage),
                    dbCategory = "Post-Salah Adhkar",
                    emoji = "📿",
                    gradient = listOf(Color(0xFF00ACC1), Color(0xFF00838F), Color(0xFF006064)),
                    duaCount = allDuas.count { it.category.equals("Post-Salah Adhkar", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Ablution & Purification", selectedLanguage),
                    dbCategory = "Ablution & Purification",
                    emoji = "💧",
                    gradient = listOf(Color(0xFF039BE5), Color(0xFF0288D1), Color(0xFF01579B)),
                    duaCount = allDuas.count { it.category.equals("Ablution & Purification", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Eating & Drinking", selectedLanguage),
                    dbCategory = "Eating & Drinking",
                    emoji = "🍽️",
                    gradient = listOf(Color(0xFFFB8C00), Color(0xFFE65100), Color(0xFFBF360C)),
                    duaCount = allDuas.count { it.category.equals("Eating & Drinking", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Dressing", selectedLanguage),
                    dbCategory = "Dressing",
                    emoji = "👕",
                    gradient = listOf(Color(0xFF8E24AA), Color(0xFF6A1B9A), Color(0xFF4A148C)),
                    duaCount = allDuas.count { it.category.equals("Dressing", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Travel & Home", selectedLanguage),
                    dbCategory = "Travel & Home",
                    emoji = "🚗",
                    gradient = listOf(Color(0xFF0097A7), Color(0xFF00838F), Color(0xFF006064)),
                    duaCount = allDuas.count { it.category.equals("Travel & Home", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Hardship & Anxiety", selectedLanguage),
                    dbCategory = "Hardship & Anxiety",
                    emoji = "🤲",
                    gradient = listOf(Color(0xFF43A047), Color(0xFF2E7D32), Color(0xFF1B5E20)),
                    duaCount = allDuas.count { it.category.equals("Hardship & Anxiety", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Protection & Evil Eye", selectedLanguage),
                    dbCategory = "Protection & Evil Eye",
                    emoji = "🛡️",
                    gradient = listOf(Color(0xFF546E7A), Color(0xFF37474F), Color(0xFF263238)),
                    duaCount = allDuas.count { it.category.equals("Protection & Evil Eye", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Visiting the Sick", selectedLanguage),
                    dbCategory = "Visiting the Sick",
                    emoji = "🩺",
                    gradient = listOf(Color(0xFFE91E63), Color(0xFFC2185B), Color(0xFF880E4F)),
                    duaCount = allDuas.count { it.category.equals("Visiting the Sick", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Good Manners", selectedLanguage),
                    dbCategory = "Good Manners",
                    emoji = "🤝",
                    gradient = listOf(Color(0xFF8D6E63), Color(0xFF6D4C41), Color(0xFF3E2723)),
                    duaCount = allDuas.count { it.category.equals("Good Manners", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Greetings & Social", selectedLanguage),
                    dbCategory = "Greetings & Social",
                    emoji = "💬",
                    gradient = listOf(Color(0xFF26A69A), Color(0xFF00897B), Color(0xFF004D40)),
                    duaCount = allDuas.count { it.category.equals("Greetings & Social", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Rain & Wind", selectedLanguage),
                    dbCategory = "Rain & Wind",
                    emoji = "🌧️",
                    gradient = listOf(Color(0xFF455A64), Color(0xFF37474F), Color(0xFF263238)),
                    duaCount = allDuas.count { it.category.equals("Rain & Wind", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Market & Shopping", selectedLanguage),
                    dbCategory = "Market & Shopping",
                    emoji = "🛒",
                    gradient = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20), Color(0xFF004D40)),
                    duaCount = allDuas.count { it.category.equals("Market & Shopping", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Grave & Funeral", selectedLanguage),
                    dbCategory = "Grave & Funeral",
                    emoji = "⚰️",
                    gradient = listOf(Color(0xFF616161), Color(0xFF424242), Color(0xFF212121)),
                    duaCount = allDuas.count { it.category.equals("Grave & Funeral", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Fasting & Ramadan", selectedLanguage),
                    dbCategory = "Fasting & Ramadan",
                    emoji = "🏮",
                    gradient = listOf(Color(0xFFC2185B), Color(0xFFAD1457), Color(0xFF4A0E17)),
                    duaCount = allDuas.count { it.category.equals("Fasting & Ramadan", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Hajj & Umrah", selectedLanguage),
                    dbCategory = "Hajj & Umrah",
                    emoji = "🕋",
                    gradient = listOf(Color(0xFFD4AF37), Color(0xFF8D6E63), Color(0xFF212121)),
                    duaCount = allDuas.count { it.category.equals("Hajj & Umrah", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Marriage & Family", selectedLanguage),
                    dbCategory = "Marriage & Family",
                    emoji = "💍",
                    gradient = listOf(Color(0xFFEC407A), Color(0xFFD81B60), Color(0xFF880E4F)),
                    duaCount = allDuas.count { it.category.equals("Marriage & Family", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Repentance & Seeking Forgiveness", selectedLanguage),
                    dbCategory = "Repentance & Seeking Forgiveness",
                    emoji = "🧎",
                    gradient = listOf(Color(0xFF00796B), Color(0xFF004D40), Color(0xFF04261E)),
                    duaCount = allDuas.count { it.category.equals("Repentance & Seeking Forgiveness", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("Ruqyah", selectedLanguage),
                    dbCategory = "Ruqyah",
                    emoji = "🌿",
                    gradient = listOf(Color(0xFF689F38), Color(0xFF558B2F), Color(0xFF33691E)),
                    duaCount = allDuas.count { it.category.equals("Ruqyah", ignoreCase = true) }
                ),
                CategoryGridItem(
                    title = AppLocalizer.getCategoryName("40 Rabbana Duas", selectedLanguage),
                    dbCategory = "40 Rabbana Duas",
                    emoji = "📖",
                    gradient = listOf(Color(0xFFD4AF37), Color(0xFFA0781A), Color(0xFF422F07)),
                    duaCount = allDuas.count { it.category.equals("40 Rabbana Duas", ignoreCase = true) }
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in categoriesList.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CategoryCard(
                            item = categoriesList[i],
                            cardBg = cardBg,
                            cardBorder = cardBorder,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            goldAccent = goldAccent,
                            isDarkTheme = isDarkTheme,
                            countLabel = countLabel,
                            onClick = { onCategoryClick(categoriesList[i].dbCategory) },
                            modifier = Modifier.weight(1f)
                        )
                        if (i + 1 < categoriesList.size) {
                            CategoryCard(
                                item = categoriesList[i + 1],
                                cardBg = cardBg,
                                cardBorder = cardBorder,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                goldAccent = goldAccent,
                                isDarkTheme = isDarkTheme,
                                countLabel = countLabel,
                                onClick = { onCategoryClick(categoriesList[i + 1].dbCategory) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Quran Verse / Quote of the Day
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = goldAccent.copy(alpha = 0.8f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“" + AppLocalizer.getString("quran_quote", selectedLanguage) + "”",
                        color = textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = AppLocalizer.getString("quran_ref", selectedLanguage),
                        color = goldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Compact More Applications & Company Website Card
        item {
            val context = LocalContext.current
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_more_apps_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Option 1: Play Store Download
                    Surface(
                        onClick = { openMoreAppsStore(context) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDarkTheme) Color(0xFF132D27) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(goldAccent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shop,
                                    contentDescription = "Play Store",
                                    tint = goldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Samo Wasu Manhajoji (Play Store)" else AppLocalizer.getString("more_apps", selectedLanguage),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Google Play Store" else AppLocalizer.getString("more_apps_subtitle", selectedLanguage),
                                    fontSize = 10.5.sp,
                                    color = textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Play Store",
                                tint = goldAccent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Option 2: Company Website (Najah Tech - Web & App Development CTA)
                    Surface(
                        onClick = { openWebsite(context) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDarkTheme) Color(0xFF0C1F1B) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(0.8.dp, goldAccent.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(goldAccent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Web & App Development",
                                    tint = goldAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Najah Tech",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = textPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(goldAccent.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Web & App Dev",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = goldAccent
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Kuna son Website ko Mobile App? Tuntube mu a www.najahtech.com" else "Need a custom Website or Mobile App? Contact us at www.najahtech.com",
                                    fontSize = 10.5.sp,
                                    color = textSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 13.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Visit Website",
                                tint = goldAccent,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

data class CategoryGridItem(
    val title: String,
    val dbCategory: String?,
    val emoji: String,
    val gradient: List<Color>,
    val duaCount: Int = 0
)

@Composable
fun CategoryCard(
    item: CategoryGridItem,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    goldAccent: Color,
    isDarkTheme: Boolean,
    countLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = modifier
            .height(122.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Realistic 3D Miniature Badge Container with glossy gradient highlight
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.1f)
                            )
                        )
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = item.gradient,
                                    center = Offset(22f, 18f),
                                    radius = 50f
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.emoji,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Dua Count Badge Pill
                if (item.duaCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDarkTheme) Color(0xFF143B33) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(0.6.dp, goldAccent.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "${item.duaCount} $countLabel",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFFECC76A) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Text(
                text = item.title,
                color = textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.5.sp
            )
        }
    }
}

@Composable
fun OnboardingLanguageSelection(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var currentStep by remember { mutableIntStateOf(0) } // 0: Language, 1: Permissions/Overlay/Battery
    var tempSelectedLanguage by remember { mutableStateOf(selectedLanguage) }

    // Live permission states
    var hasOverlayPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true
        )
    }

    var hasBatteryExemption by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                pm.isIgnoringBatteryOptimizations(context.packageName)
            } else true
        )
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        )
    }

    var canScheduleExact by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.canScheduleExactAlarms()
            } else true
        )
    }

    // Step-by-step permission prompt dialog state (0 = None, 1 = Notif, 2 = Battery, 3 = Overlay)
    var activePermissionStep by remember { mutableIntStateOf(0) }

    // Sequential helper that checks and prompts for missing permissions one-by-one
    fun proceedNextPermissionStep(fromStep: Int) {
        val needsNotif = !hasNotificationPermission
        val needsBattery = !hasBatteryExemption && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        val needsOverlay = !hasOverlayPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        when (fromStep) {
            0 -> {
                // Starting initial check
                if (needsNotif) {
                    activePermissionStep = 1
                } else if (needsBattery) {
                    activePermissionStep = 2
                } else if (needsOverlay) {
                    activePermissionStep = 3
                } else {
                    activePermissionStep = 0
                    onComplete()
                }
            }
            1 -> {
                // After Notification
                if (needsBattery) {
                    activePermissionStep = 2
                } else if (needsOverlay) {
                    activePermissionStep = 3
                } else {
                    activePermissionStep = 0
                    onComplete()
                }
            }
            2 -> {
                // After Battery
                if (needsOverlay) {
                    activePermissionStep = 3
                } else {
                    activePermissionStep = 0
                    onComplete()
                }
            }
            3 -> {
                // After Overlay
                activePermissionStep = 0
                onComplete()
            }
        }
    }

    // Permission launcher for Android 13+ Notifications
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            com.example.receiver.OneSignalHelper.optInPush(context)
            Toast.makeText(
                context,
                if (tempSelectedLanguage == "Hausa") "An kunna sanarwa ✓" else "Notifications enabled ✓",
                Toast.LENGTH_SHORT
            ).show()
        }
        if (activePermissionStep == 1) {
            proceedNextPermissionStep(1)
        }
    }

    fun launchNotificationRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            try {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    fun launchBatteryOptimizationRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
        }
    }

    fun launchOverlayPermissionRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(context, "Please enable Display Over Apps in Settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Refresh permission states upon returning from system settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hasOverlayPermission = Settings.canDrawOverlays(context)
                    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    hasBatteryExemption = pm.isIgnoringBatteryOptimizations(context.packageName)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasNotificationPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    hasNotificationPermission = NotificationManagerCompat.from(context).areNotificationsEnabled()
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    canScheduleExact = am.canScheduleExactAlarms()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val isHausa = tempSelectedLanguage == "Hausa"

    // =========================================================================
    // STEP-BY-STEP PERMISSION PROMPT DIALOGS (ONE-BY-ONE WIZARD)
    // =========================================================================
    if (activePermissionStep > 0) {
        AlertDialog(
            onDismissRequest = {
                // Advance to next step even if dialog dismissed
                proceedNextPermissionStep(activePermissionStep)
            },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (activePermissionStep) {
                            1 -> Icons.Default.NotificationsActive
                            2 -> Icons.Default.BatteryChargingFull
                            else -> Icons.Default.Layers
                        },
                        contentDescription = null,
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFD4AF37).copy(alpha = 0.18f),
                        border = BorderStroke(0.8.dp, Color(0xFFD4AF37))
                    ) {
                        Text(
                            text = when (activePermissionStep) {
                                1 -> if (isHausa) "Mataki 1 cikin 3" else "Step 1 of 3"
                                2 -> if (isHausa) "Mataki 2 cikin 3" else "Step 2 of 3"
                                else -> if (isHausa) "Mataki 3 cikin 3" else "Step 3 of 3"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = when (activePermissionStep) {
                            1 -> if (isHausa) "Kunna Izinin Sanarwa" else "Enable Notifications"
                            2 -> if (isHausa) "Cire Takunkumin Baturi" else "Allow Background Running"
                            else -> if (isHausa) "Bada Izinin Allon Zikiri (Overlay)" else "Allow Display Over Other Apps"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = when (activePermissionStep) {
                            1 -> if (isHausa)
                                "Wannan izini yana ba Zakiru Muslim damar aiko maka da sanarwa da kararrawar zikirin safe da yamma a ainihin lokacinsu domin kada ka manta."
                            else
                                "Allows Zakiru Muslim to deliver timely audio alerts, vibrations, and notifications for all your morning and evening supplications."
                            2 -> if (isHausa)
                                "Wayoyi kamar Samsung, Tecno, Infinix, Xiaomi, Oppo suna kashe manhajoji a bayan fage. Cire Zakiru Muslim daga takunkumin baturi don zikirin safe da yamma ya riƙa fita kan lokaci ba tare da jinkiri ba."
                            else
                                "Device battery savers (Samsung, Tecno, Infinix, Xiaomi, Oppo) put apps to sleep. Exempting Zakiru Muslim guarantees your morning and evening focus alarms trigger punctually."
                            else -> if (isHausa)
                                "Wannan izini yana ba da damar allon zikiri ya fito kai tsaye a kan wayarka koda kana amfani da wani app (kamar WhatsApp ko Browser) ko wayar tana ajiye lokacin da lokacin zikiri yayi."
                            else
                                "Allows Zakiru Muslim to pop up full-screen Adhkar reading sessions directly over other apps at scheduled times so you never miss your daily focus sessions."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E4039),
                        textAlign = TextAlign.Start
                    )

                    // Current status indicator
                    val isStepGranted = when (activePermissionStep) {
                        1 -> hasNotificationPermission
                        2 -> hasBatteryExemption
                        else -> hasOverlayPermission
                    }

                    if (isStepGranted) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE8F5E9),
                            border = BorderStroke(1.dp, Color(0xFF1B5E20)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(16.dp))
                                Text(
                                    text = if (isHausa) "An saita wannan izini cikin nasara! ✓" else "This permission is granted! ✓",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val isStepGranted = when (activePermissionStep) {
                    1 -> hasNotificationPermission
                    2 -> hasBatteryExemption
                    else -> hasOverlayPermission
                }

                if (!isStepGranted) {
                    Button(
                        onClick = {
                            when (activePermissionStep) {
                                1 -> launchNotificationRequest()
                                2 -> launchBatteryOptimizationRequest()
                                3 -> launchOverlayPermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = when (activePermissionStep) {
                                1 -> if (isHausa) "Bada Izinin Sanarwa" else "Allow Notifications"
                                2 -> if (isHausa) "Cire Takunkumi Yanzu" else "Allow Background Run"
                                else -> if (isHausa) "Bada Izinin Allon Zikiri" else "Grant Overlay Permission"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = { proceedNextPermissionStep(activePermissionStep) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = if (activePermissionStep == 3) (if (isHausa) "Kammala & Fara App" else "Finish & Start App") else (if (isHausa) "Ci gaba zuwa Na Gaba →" else "Continue to Next →"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF4F9F6),
                        Color(0xFFE8F2EE)
                    )
                )
            )
            .safeDrawingPadding()
    ) {
        if (currentStep == 0) {
            // STEP 0: LANGUAGE SELECTION
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    // Moon and Stars Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val center = Offset(size.width / 2, size.height / 2)
                            // Draw golden crescent moon
                            drawCircle(
                                color = Color(0xFFD4AF37),
                                radius = 32.dp.toPx(),
                                center = center
                            )
                            drawCircle(
                                color = Color(0xFFF4F9F6),
                                radius = 30.dp.toPx(),
                                center = center - Offset(9.dp.toPx(), 4.dp.toPx())
                            )

                            val stars = listOf(
                                center + Offset(-55.dp.toPx(), -18.dp.toPx()),
                                center + Offset(60.dp.toPx(), -10.dp.toPx()),
                                center + Offset(28.dp.toPx(), -45.dp.toPx()),
                                center + Offset(-30.dp.toPx(), 36.dp.toPx()),
                                center + Offset(40.dp.toPx(), 30.dp.toPx())
                            )
                            stars.forEach { pos ->
                                drawCircle(
                                    color = Color(0xFF1B5E20).copy(alpha = 0.6f),
                                    radius = 2.dp.toPx(),
                                    center = pos
                                )
                            }
                        }
                    }

                    Text(
                        text = "Zakiru Muslim",
                        style = MaterialTheme.typography.headlineMedium,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "THE FORTRESS OF REMEMBRANCE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )

                    Text(
                        text = "Select your preferred translation language / Zaɓi harshen da kake so:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF2E4039),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 14.dp)
                    )

                    // Scrollable Languages List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        val languages = listOf(
                            "English" to "🇬🇧 English (English)",
                            "Hausa" to "🇳🇬 Hausa (Harshen Hausa)",
                            "Yoruba" to "🇳🇬 Yoruba (Èdè Yorùbá)",
                            "Igbo" to "🇳🇬 Igbo (Asụsụ Igbo)",
                            "Spanish" to "🇪🇸 Spanish (Español)",
                            "French" to "🇫🇷 French (Français)",
                            "Arabic" to "🇸🇦 Arabic (العربية)",
                            "Urdu" to "🇵🇰 Urdu (اردو)",
                            "Chinese" to "🇨🇳 Chinese (中文)"
                        )

                        items(languages) { (langCode, displayName) ->
                            val isSelected = tempSelectedLanguage == langCode
                            Surface(
                                onClick = {
                                    tempSelectedLanguage = langCode
                                    onLanguageSelected(langCode)
                                },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF1B5E20) else Color(0xFFD2E3DE)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("lang_onboarding_$langCode")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 18.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = displayName,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF132D27)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color(0xFF1B5E20),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Next Step Button
                Button(
                    onClick = { currentStep = 1 },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B5E20),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_onboarding_next_setup"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (tempSelectedLanguage == "Hausa") "CI GABA ZUWA SAITIN IZINI" else "CONTINUE TO PERMISSIONS SETUP",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp
                        )
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        } else {
            // STEP 1: PERMISSIONS, SCREEN OVERLAY & BATTERY SETUP
            val allGranted = hasOverlayPermission && hasBatteryExemption && hasNotificationPermission

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Shield / Settings Header Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = if (isHausa) "Saitin Izini & Allon Zikiri" else "Permissions & System Setup",
                        style = MaterialTheme.typography.headlineSmall,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isHausa)
                            "Domin allon zikiri na wajibi (Mandatory Adhkar) ya fito kai tsaye koda kana amfani da wani app ko wayarka tana kulle, da fatan a saita waɗannan izini 3:"
                        else
                            "To ensure your mandatory morning and evening focus sessions trigger reliably and display over other apps when scheduled, please configure these 3 permissions:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334B42),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    // CARD 1: SCREEN OVERLAY (DISPLAY OVER OTHER APPS)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasOverlayPermission) Color(0xFFE8F5E9) else Color.White,
                        border = BorderStroke(
                            width = if (hasOverlayPermission) 1.5.dp else 1.dp,
                            color = if (hasOverlayPermission) Color(0xFF1B5E20) else Color(0xFFD2E3DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = null,
                                        tint = if (hasOverlayPermission) Color(0xFF1B5E20) else Color(0xFFD4AF37)
                                    )
                                    Text(
                                        text = if (isHausa) "1. Allon Zikiri (Screen Overlay)" else "1. Display Over Other Apps",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF132D27)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (hasOverlayPermission) Color(0xFF1B5E20) else Color(0xFFFFF3E0)
                                ) {
                                    Text(
                                        text = if (hasOverlayPermission) (if (isHausa) "An Bada ✓" else "Granted ✓") else (if (isHausa) "Ba a Saita Ba" else "Pending"),
                                        color = if (hasOverlayPermission) Color.White else Color(0xFFE65100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isHausa)
                                    "Yana ba Zakiru Muslim damar buɗe allon zikiri kai tsaye lokacin da lokacin zikirin safe ko na yamma yayi koda kana wani app."
                                else
                                    "Allows Zakiru Muslim to pop up full-screen Adhkar recitation sessions directly over other apps at scheduled times.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4A6058)
                            )

                            if (!hasOverlayPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Button(
                                    onClick = { launchOverlayPermissionRequest() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                                ) {
                                    Text(
                                        text = if (isHausa) "Bada Izinin Allon Zikiri (Overlay)" else "Grant Screen Overlay Permission",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // CARD 2: BATTERY OPTIMIZATION EXEMPTION
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasBatteryExemption) Color(0xFFE8F5E9) else Color.White,
                        border = BorderStroke(
                            width = if (hasBatteryExemption) 1.5.dp else 1.dp,
                            color = if (hasBatteryExemption) Color(0xFF1B5E20) else Color(0xFFD2E3DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BatteryChargingFull,
                                        contentDescription = null,
                                        tint = if (hasBatteryExemption) Color(0xFF1B5E20) else Color(0xFFD4AF37)
                                    )
                                    Text(
                                        text = if (isHausa) "2. Cire Takunkumin Baturi" else "2. Battery Optimization",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF132D27)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (hasBatteryExemption) Color(0xFF1B5E20) else Color(0xFFFFF3E0)
                                ) {
                                    Text(
                                        text = if (hasBatteryExemption) (if (isHausa) "An Shirya ✓" else "Configured ✓") else (if (isHausa) "Ba a Saita Ba" else "Pending"),
                                        color = if (hasBatteryExemption) Color.White else Color(0xFFE65100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isHausa)
                                    "Kada tsarin wayarka (Samsung, Tecno, Infinix, Xiaomi) ya kashe ko ya toshe zikirin safe da yamma a bayan fage."
                                else
                                    "Ensures device power savers (Samsung, Tecno, Infinix, Xiaomi, Oppo) don't delay or kill scheduled Zikir alarm sessions.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4A6058)
                            )

                            if (!hasBatteryExemption && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Button(
                                    onClick = { launchBatteryOptimizationRequest() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                                ) {
                                    Text(
                                        text = if (isHausa) "Cire Takunkumin Baturi (Allow Background)" else "Disable Battery Optimization",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    // CARD 3: NOTIFICATIONS & EXACT ALARM
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (hasNotificationPermission && canScheduleExact) Color(0xFFE8F5E9) else Color.White,
                        border = BorderStroke(
                            width = if (hasNotificationPermission && canScheduleExact) 1.5.dp else 1.dp,
                            color = if (hasNotificationPermission && canScheduleExact) Color(0xFF1B5E20) else Color(0xFFD2E3DE)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = if (hasNotificationPermission) Color(0xFF1B5E20) else Color(0xFFD4AF37)
                                    )
                                    Text(
                                        text = if (isHausa) "3. Sanarwa & Kararrawa" else "3. Notifications & Alarms",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF132D27)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (hasNotificationPermission && canScheduleExact) Color(0xFF1B5E20) else Color(0xFFFFF3E0)
                                ) {
                                    Text(
                                        text = if (hasNotificationPermission && canScheduleExact) (if (isHausa) "An Kunna ✓" else "Enabled ✓") else (if (isHausa) "Ba a Saita Ba" else "Pending"),
                                        color = if (hasNotificationPermission && canScheduleExact) Color.White else Color(0xFFE65100),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (isHausa)
                                    "Yana ba da damar aiko maka da sanarwa da kararrawar zikiri da sauran addu'o'in yau da kullum a ainihin lokaci."
                                else
                                    "Allows Zakiru Muslim to deliver exact time alerts, vibrations, and notifications for all daily supplications.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF4A6058)
                            )

                            if (!hasNotificationPermission) {
                                Button(
                                    onClick = { launchNotificationRequest() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                                ) {
                                    Text(
                                        text = if (isHausa) "Kunna Izinin Sanarwa" else "Allow Notifications",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (!canScheduleExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                OutlinedButton(
                                    onClick = {
                                        try {
                                            val intent = Intent(
                                                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                                Uri.parse("package:${context.packageName}")
                                            ).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            }
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isHausa) "Bada Izinin Kararrawa (Exact Alarm)" else "Allow Exact Alarms",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // If any permission is not yet set, start step-by-step guided prompt flow!
                            proceedNextPermissionStep(0)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_complete_onboarding"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (isHausa) "FARA AMFANI DA ZAKIRU MUSLIM" else "START USING ZAKIRU MUSLIM",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    TextButton(
                        onClick = { currentStep = 0 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isHausa) "← Koma Zaɓin Harshe" else "← Back to Language Selection",
                            color = Color(0xFF1B5E20),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    arabicFontSize: Float,
    onArabicFontSizeChange: (Float) -> Unit,
    textFontSize: Float = 16f,
    onTextFontSizeChange: (Float) -> Unit = {},
    updateState: UpdateState = UpdateState.Idle,
    onCheckForUpdates: () -> Unit = {},
    onStartUpdate: () -> Unit = {},
    onCompleteUpdate: () -> Unit = {},
    context: Context
) {
    var isLangDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = AppLocalizer.getString("settings_title", selectedLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // COMPACT CARDS: Download More Apps, Rate App & Company Website
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Rate on Play Store (5 Stars)
                    Surface(
                        onClick = {
                            try {
                                context.getSharedPreferences("app_rate_prefs", Context.MODE_PRIVATE)
                                    .edit().putBoolean("has_rated", true).apply()
                                val rateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                }
                                context.startActivity(rateIntent)
                            } catch (_: Exception) {
                                val webRateIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(webRateIntent)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0xFFD4AF37), MaterialTheme.colorScheme.primary))),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_rate_app_option")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFF1B5E20)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Rate on Play Store",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Bamu Tauraro 5 a Play Store ★★★★★" else "Rate Zakiru Muslim 5 Stars ★★★★★",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Taimaka wajen yaɗa wannan manhaja a Google Play" else "Support us with a 5-star rating on Google Play Store",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Play Store",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Play Store Compact Option
                    Surface(
                        onClick = { openMoreAppsStore(context) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settings_more_apps_option")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shop,
                                    contentDescription = "Play Store",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Samo Wasu Manhajoji (Play Store)" else AppLocalizer.getString("more_apps", selectedLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Google Play Store" else AppLocalizer.getString("more_apps_subtitle", selectedLanguage),
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Play Store",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Company Website Compact Option (Najah Tech - Web & App Development CTA)
                    Surface(
                        onClick = { openWebsite(context) },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = "Web & App Development",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Najah Tech",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "Web & App Dev",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(1.dp))
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Kuna son Website ko Mobile App? Tuntube mu a www.najahtech.com" else "Need a custom Website or Mobile App? Contact us at www.najahtech.com",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 13.sp
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open Website",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Language Option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = AppLocalizer.getString("select_language", selectedLanguage),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Box {
                        OutlinedButton(
                            onClick = { isLangDropdownExpanded = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(text = selectedLanguage, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = isLangDropdownExpanded,
                            onDismissRequest = { isLangDropdownExpanded = false }
                        ) {
                            val languages = listOf("English", "Hausa", "Yoruba", "Igbo", "Spanish", "French", "Arabic", "Urdu", "Chinese")
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang) },
                                    onClick = {
                                        onLanguageSelected(lang)
                                        isLangDropdownExpanded = false
                                    },
                                    leadingIcon = {
                                        if (selectedLanguage == lang) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Theme Mode Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = if (isDarkTheme) AppLocalizer.getString("dark_mode", selectedLanguage) else AppLocalizer.getString("light_mode", selectedLanguage),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme(it) }
                    )
                }

                // Text & Font Size Controls
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Translation / Transliteration / Reference Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = AppLocalizer.getString("translation_transliteration_reference", selectedLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "${textFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                if (textFontSize > 12f) onTextFontSizeChange(textFontSize - 1f)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }

                        Slider(
                            value = textFontSize,
                            onValueChange = onTextFontSizeChange,
                            valueRange = 12f..28f,
                            modifier = Modifier.weight(1f)
                        )

                        FilledTonalIconButton(
                            onClick = {
                                if (textFontSize < 28f) onTextFontSizeChange(textFontSize + 1f)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Arabic Script Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = AppLocalizer.getString("arabic_script_size", selectedLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "${arabicFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                if (arabicFontSize > 18f) onArabicFontSizeChange(arabicFontSize - 1f)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                        }

                        Slider(
                            value = arabicFontSize,
                            onValueChange = onArabicFontSizeChange,
                            valueRange = 18f..42f,
                            modifier = Modifier.weight(1f)
                        )

                        FilledTonalIconButton(
                            onClick = {
                                if (arabicFontSize < 42f) onArabicFontSizeChange(arabicFontSize + 1f)
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }

                // In-App Update Tile
                SettingsInAppUpdateTile(
                    updateState = updateState,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme,
                    onCheckForUpdates = onCheckForUpdates,
                    onStartUpdate = onStartUpdate,
                    onCompleteUpdate = onCompleteUpdate
                )

                // Mandatory Adhkar Settings Section
                MandatoryAdhkarSettingsSection(context = context)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = AppLocalizer.getString("close", selectedLanguage),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
fun MandatoryAdhkarSettingsSection(context: Context) {
    val prefs = remember { MandatoryAdhkarManager.getPrefs(context) }

    var morningEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_MORNING_ENABLED, true)) }
    var eveningEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_EVENING_ENABLED, true)) }

    var morningHour by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_MORNING_HOUR, 6)) }
    var morningMin by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_MORNING_MINUTE, 0)) }
    var morningDuration by remember { mutableIntStateOf(prefs.getInt("duration_${MandatoryAdhkarManager.SCHEDULE_ID_MORNING}", prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3))) }

    var eveningHour by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_EVENING_HOUR, 18)) }
    var eveningMin by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_EVENING_MINUTE, 0)) }
    var eveningDuration by remember { mutableIntStateOf(prefs.getInt("duration_${MandatoryAdhkarManager.SCHEDULE_ID_EVENING}", prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3))) }

    var soundEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_SOUND_ENABLED, true)) }
    var vibrationEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_VIBRATION_ENABLED, true)) }
    var fullscreenEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_FULLSCREEN_ENABLED, true)) }

    var isMorningDurExpanded by remember { mutableStateOf(false) }
    var isEveningDurExpanded by remember { mutableStateOf(false) }

    val isIgnoringBattery = remember { MandatoryAdhkarManager.isIgnoringBatteryOptimizations(context) }
    var hasBatteryExemption by remember { mutableStateOf(isIgnoringBattery) }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val canScheduleExact = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            true
        }
    }

    val canDrawOverlays = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            android.provider.Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    var hasOverlayPermission by remember { mutableStateOf(canDrawOverlays) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Scheduled Zikir Sessions (Mandatory Mode)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = "Automated daily focus sessions to build steadfast consistency in your morning and evening supplications.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // 1. Morning Schedule Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Morning Zikir (Safe)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Switch(
                        checked = morningEnabled,
                        onCheckedChange = { checked ->
                            morningEnabled = checked
                            MandatoryAdhkarManager.saveSchedule(
                                context,
                                com.example.receiver.MandatorySchedule(
                                    id = MandatoryAdhkarManager.SCHEDULE_ID_MORNING,
                                    title = "Morning Zikir",
                                    category = "Morning & Evening",
                                    hour = morningHour,
                                    minute = morningMin,
                                    durationMinutes = morningDuration,
                                    enabled = checked
                                )
                            )
                        }
                    )
                }

                if (morningEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start Time:", style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(
                            onClick = {
                                android.app.TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        morningHour = h
                                        morningMin = m
                                        MandatoryAdhkarManager.saveSchedule(
                                            context,
                                            com.example.receiver.MandatorySchedule(
                                                id = MandatoryAdhkarManager.SCHEDULE_ID_MORNING,
                                                title = "Morning Zikir",
                                                category = "Morning & Evening",
                                                hour = h,
                                                minute = m,
                                                durationMinutes = morningDuration,
                                                enabled = morningEnabled
                                            )
                                        )
                                    },
                                    morningHour,
                                    morningMin,
                                    false
                                ).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            val amPm = if (morningHour >= 12) "PM" else "AM"
                            val h12 = if (morningHour % 12 == 0) 12 else morningHour % 12
                            Text(
                                text = String.format("%02d:%02d %s", h12, morningMin, amPm),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duration:", style = MaterialTheme.typography.bodySmall)
                        Box {
                            OutlinedButton(
                                onClick = { isMorningDurExpanded = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("$morningDuration min", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.padding(start = 2.dp))
                            }
                            DropdownMenu(
                                expanded = isMorningDurExpanded,
                                onDismissRequest = { isMorningDurExpanded = false }
                            ) {
                                listOf(3, 5, 10, 15, 20, 30).forEach { mins ->
                                    DropdownMenuItem(
                                        text = { Text("$mins minutes") },
                                        onClick = {
                                            morningDuration = mins
                                            isMorningDurExpanded = false
                                            MandatoryAdhkarManager.saveSchedule(
                                                context,
                                                com.example.receiver.MandatorySchedule(
                                                    id = MandatoryAdhkarManager.SCHEDULE_ID_MORNING,
                                                    title = "Morning Zikir",
                                                    category = "Morning & Evening",
                                                    hour = morningHour,
                                                    minute = morningMin,
                                                    durationMinutes = mins,
                                                    enabled = morningEnabled
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Evening Schedule Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NightsStay,
                            contentDescription = null,
                            tint = Color(0xFF1E88E5),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Evening Zikir (Yamma)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Switch(
                        checked = eveningEnabled,
                        onCheckedChange = { checked ->
                            eveningEnabled = checked
                            MandatoryAdhkarManager.saveSchedule(
                                context,
                                com.example.receiver.MandatorySchedule(
                                    id = MandatoryAdhkarManager.SCHEDULE_ID_EVENING,
                                    title = "Evening Zikir",
                                    category = "Morning & Evening",
                                    hour = eveningHour,
                                    minute = eveningMin,
                                    durationMinutes = eveningDuration,
                                    enabled = checked
                                )
                            )
                        }
                    )
                }

                if (eveningEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start Time:", style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(
                            onClick = {
                                android.app.TimePickerDialog(
                                    context,
                                    { _, h, m ->
                                        eveningHour = h
                                        eveningMin = m
                                        MandatoryAdhkarManager.saveSchedule(
                                            context,
                                            com.example.receiver.MandatorySchedule(
                                                id = MandatoryAdhkarManager.SCHEDULE_ID_EVENING,
                                                title = "Evening Zikir",
                                                category = "Morning & Evening",
                                                hour = h,
                                                minute = m,
                                                durationMinutes = eveningDuration,
                                                enabled = eveningEnabled
                                            )
                                        )
                                    },
                                    eveningHour,
                                    eveningMin,
                                    false
                                ).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            val amPm = if (eveningHour >= 12) "PM" else "AM"
                            val h12 = if (eveningHour % 12 == 0) 12 else eveningHour % 12
                            Text(
                                text = String.format("%02d:%02d %s", h12, eveningMin, amPm),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Duration:", style = MaterialTheme.typography.bodySmall)
                        Box {
                            OutlinedButton(
                                onClick = { isEveningDurExpanded = true },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("$eveningDuration min", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.padding(start = 2.dp))
                            }
                            DropdownMenu(
                                expanded = isEveningDurExpanded,
                                onDismissRequest = { isEveningDurExpanded = false }
                            ) {
                                listOf(3, 5, 10, 15, 20, 30).forEach { mins ->
                                    DropdownMenuItem(
                                        text = { Text("$mins minutes") },
                                        onClick = {
                                            eveningDuration = mins
                                            isEveningDurExpanded = false
                                            MandatoryAdhkarManager.saveSchedule(
                                                context,
                                                com.example.receiver.MandatorySchedule(
                                                    id = MandatoryAdhkarManager.SCHEDULE_ID_EVENING,
                                                    title = "Evening Zikir",
                                                    category = "Morning & Evening",
                                                    hour = eveningHour,
                                                    minute = eveningMin,
                                                    durationMinutes = mins,
                                                    enabled = eveningEnabled
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sound & Vibration Options
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Alarm Sound & Chimes", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = soundEnabled,
                onCheckedChange = {
                    soundEnabled = it
                    prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_SOUND_ENABLED, it).apply()
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Vibration Alert", style = MaterialTheme.typography.bodyMedium)
            Switch(
                checked = vibrationEnabled,
                onCheckedChange = {
                    vibrationEnabled = it
                    prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_VIBRATION_ENABLED, it).apply()
                }
            )
        }

        // RELIABLE REMINDER SETUP & BATTERY OPTIMIZATION BANNER
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasBatteryExemption) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                } else {
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.45f)
                }
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (hasBatteryExemption) Icons.Default.CheckCircle else Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = if (hasBatteryExemption) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = if (hasBatteryExemption) "Reliable Reminders Active ✓" else "Allow Reliable Background Reminders",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (hasBatteryExemption) {
                        "Battery optimizations are configured so your scheduled Zikir sessions will trigger punctually on Samsung, Tecno, Infinix, Xiaomi and other devices."
                    } else {
                        "Many phone manufacturers (Samsung, Xiaomi, Tecno, Infinix, Oppo, Vivo) aggressively sleep background tasks. Exclude Zakiru Muslim from battery restrictions to guarantee on-time sessions."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!hasBatteryExemption && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                                    Uri.parse("package:${context.packageName}")
                                ).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Allow Reliable Reminders (Battery Setup)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Exact Alarms setting if restricted
                if (!canScheduleExact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(
                                    android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                    Uri.parse("package:${context.packageName}")
                                ).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Grant Exact Alarm Permission", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Display Over Apps (Overlay)
                if (!hasOverlayPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(
                                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                ).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Allow Direct Screen Popup (Overlay)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Test Trigger Button
                Button(
                    onClick = {
                        Toast.makeText(context, "Testing Scheduled Zikir Session in 3 seconds...", Toast.LENGTH_LONG).show()
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            MandatoryAdhkarManager.triggerTestNow(context, MandatoryAdhkarManager.SCHEDULE_ID_MORNING)
                        }, 3000L)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Test Scheduled Session Now (3s)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // =========================================================================
        // DEVELOPER / DEBUG DIAGNOSTICS SECTION (System Verification)
        // =========================================================================
        var isDebugExpanded by remember { mutableStateOf(false) }
        val schedulesList = remember(morningHour, morningMin, morningEnabled, eveningHour, eveningMin, eveningEnabled) {
            MandatoryAdhkarManager.getAllSchedules(context)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Mandatory Schedule Diagnostics",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    IconButton(
                        onClick = { isDebugExpanded = !isDebugExpanded },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isDebugExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Diagnostics"
                        )
                    }
                }

                if (isDebugExpanded) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // System Permissions Status
                    Text(
                        text = "SYSTEM PERMISSION STATUS:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val hasNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    } else {
                        androidx.core.app.NotificationManagerCompat.from(context).areNotificationsEnabled()
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Exact Alarm Permission:", fontSize = 12.sp)
                        Text(
                            text = if (canScheduleExact) "YES (Granted)" else "NO (Restricted)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canScheduleExact) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Notification Permission:", fontSize = 12.sp)
                        Text(
                            text = if (hasNotif) "YES (Granted)" else "NO (Disabled)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasNotif) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Battery Exemption:", fontSize = 12.sp)
                        Text(
                            text = if (hasBatteryExemption) "YES (Unrestricted)" else "NO (Optimized)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasBatteryExemption) Color(0xFF2E7D32) else Color(0xFFF57F17)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Screen Overlay (Popup):", fontSize = 12.sp)
                        Text(
                            text = if (hasOverlayPermission) "YES (Granted)" else "NO (Manual Launch)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasOverlayPermission) Color(0xFF2E7D32) else Color(0xFFF57F17)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    Text(
                        text = "LIVE SCHEDULE REGISTRATIONS:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    schedulesList.forEach { sch ->
                        val amPm = if (sch.hour >= 12) "PM" else "AM"
                        val h12 = if (sch.hour % 12 == 0) 12 else sch.hour % 12
                        val formattedConfigured = String.format("%02d:%02d %s", h12, sch.minute, amPm)
                        val formattedNext = MandatoryAdhkarManager.formatTimestamp(sch.nextOccurrence)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Schedule: ${sch.title}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (sch.enabled) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                    ) {
                                        Text(
                                            text = if (sch.enabled) "ENABLED" else "DISABLED",
                                            color = if (sch.enabled) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text("• Configured time: $formattedConfigured (${sch.durationMinutes} min)", fontSize = 11.sp)
                                Text(
                                    text = "• Next occurrence: $formattedNext",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (sch.enabled) Color(0xFF1B5E20) else Color.Gray
                                )
                                Text(
                                    text = "• Last triggered: ${if (sch.lastTriggeredOccurrence.isNotBlank()) sch.lastTriggeredOccurrence else "None"}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• Last completed: ${if (sch.lastCompletedOccurrence.isNotBlank()) sch.lastCompletedOccurrence else "None"}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "• Alarm registered: ${if (sch.enabled && sch.nextOccurrence > 0L) "YES (Exact AlarmClock Active)" else "NO"}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (sch.enabled) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            MandatoryAdhkarManager.recoverAndRescheduleAll(context, "MANUAL_DIAGNOSTICS_RESYNC")
                            Toast.makeText(context, "All schedules recalculated and alarms re-registered ✓", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Force Recalculate & Resync Alarms", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
