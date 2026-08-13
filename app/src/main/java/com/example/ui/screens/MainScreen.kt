package com.example.ui.screens

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.receiver.MandatoryAdhkarManager
import com.example.receiver.ReminderReceiver
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.ui.DuaViewModel
import com.example.ui.audio.DuaSpeaker
import com.example.ui.components.BannerAd
import java.util.Calendar

// Play Store redirection link for downloading more apps from developer
const val MORE_APPS_PLAYSTORE_URL = "https://play.google.com/store/apps"

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: DuaViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Library, 1 = Favorites, 2 = Reminders

    // Instantiate and manage our TTS Speaker
    val speaker = remember { DuaSpeaker(context) }
    DisposableEffect(Unit) {
        onDispose {
            speaker.shutdown()
        }
    }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val duas by viewModel.duas.collectAsStateWithLifecycle()
    val allDuas by viewModel.allDuas.collectAsStateWithLifecycle()
    val favorites by viewModel.favoriteDuas.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val arabicFontSize by viewModel.arabicFontSize.collectAsStateWithLifecycle()
    val completedDuas by viewModel.completedDuas.collectAsStateWithLifecycle()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsStateWithLifecycle()

    var isFontSizeDialogVisible by remember { mutableStateOf(false) }
    var isSettingsDialogVisible by remember { mutableStateOf(false) }

    if (isFirstLaunch) {
        OnboardingLanguageSelection(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onComplete = { viewModel.completeFirstLaunch() }
        )
    } else {
        Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_noor_zikir_logo),
                            contentDescription = "Noor zikir Logo",
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color(0xFFD4AF37), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            AppLocalizer.getString("app_title", selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
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

                    IconButton(
                        onClick = { isFontSizeDialogVisible = true },
                        modifier = Modifier.testTag("font_size_selector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Resize Text Font",
                            tint = MaterialTheme.colorScheme.secondary
                        )
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
                BannerAd()
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(AppLocalizer.getString("home", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_home")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.Book, contentDescription = "Library") },
                        label = { Text(AppLocalizer.getString("library", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_book")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorites"
                            )
                        },
                        label = { Text(AppLocalizer.getString("favorites", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_favorites")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.Notifications, contentDescription = "Reminders") },
                        label = { Text(AppLocalizer.getString("daily_reminders", selectedLanguage)) },
                        modifier = Modifier.testTag("nav_reminders")
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
                    onCategoryClick = { categoryName ->
                        viewModel.selectCategory(categoryName)
                        viewModel.setTargetDuaId(null)
                        selectedTab = 1
                    },
                    onSelectDua = { dua ->
                        viewModel.selectCategory(dua.category)
                        viewModel.setTargetDuaId(dua.id)
                        selectedTab = 1
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
                    completedDuas = completedDuas,
                    isDarkTheme = isDarkTheme
                )
                2 -> FavoritesTab(
                    viewModel = viewModel,
                    favorites = favorites,
                    speaker = speaker,
                    selectedLanguage = selectedLanguage,
                    arabicFontSize = arabicFontSize,
                    completedDuas = completedDuas,
                    isDarkTheme = isDarkTheme
                )
                3 -> RemindersTab(selectedLanguage = selectedLanguage)
            }
        }
    }

    if (isFontSizeDialogVisible) {
        AlertDialog(
            onDismissRequest = { isFontSizeDialogVisible = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(AppLocalizer.getString("text_and_font_size", selectedLanguage))
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Arabic, Transliteration & Translation",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Live preview of Arabic text
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ",
                            fontSize = arabicFontSize.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = (arabicFontSize * 1.5f).sp,
                            fontWeight = FontWeight.Medium,
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
                        fontSize = (arabicFontSize * 0.583f).sp,
                        lineHeight = (arabicFontSize * 0.85f).sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        color = if (isDarkTheme) MaterialTheme.colorScheme.secondary else Color(0xFF8B5E00),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    )

                    // Live preview of Translation text
                    Text(
                        text = "All praise is due to Allah, Lord of all the worlds",
                        fontSize = (arabicFontSize * 0.583f).sp,
                        lineHeight = (arabicFontSize * 0.85f).sp,
                        fontWeight = FontWeight.Normal,
                        color = if (isDarkTheme) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF1F2937),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "A-",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Slider(
                            value = arabicFontSize,
                            onValueChange = { viewModel.setArabicFontSize(it) },
                            valueRange = 18f..40f,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp).testTag("font_size_slider")
                        )
                        Text(
                            text = "A+",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Arabic: ${arabicFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Translation: ${(arabicFontSize * 0.583f).toInt()} sp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { isFontSizeDialogVisible = false }
                ) {
                    Text(AppLocalizer.getString("done", selectedLanguage))
                }
            }
        )
    }

    if (isSettingsDialogVisible) {
        SettingsDialog(
            onDismiss = { isSettingsDialogVisible = false },
            selectedLanguage = selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            arabicFontSize = arabicFontSize,
            onArabicFontSizeChange = { viewModel.setArabicFontSize(it) },
            context = context
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
                contentDescription = "Hisnul Muslim Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Beautiful dark semi-transparent overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x77000000))
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_noor_zikir_logo),
                    contentDescription = "Noor zikir Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color(0xFFECC76A), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        "نور الذكر",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        "Noor zikir • Light of Remembrance",
                        color = Color(0xFFECC76A),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
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
                    label = { Text(AppLocalizer.getString("all_topics", selectedLanguage)) },
                    modifier = Modifier.testTag("chip_all")
                )
            }
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) },
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

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                            arabicFontSize = arabicFontSize
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favorites, key = { it.id }) { dua ->
                    DuaItemCard(
                        dua = dua,
                        speaker = speaker,
                        selectedLanguage = selectedLanguage,
                        arabicFontSize = arabicFontSize,
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
                .padding(bottom = 24.dp)
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

fun getLocalizedResources(context: Context, language: String): android.content.res.Resources {
    val locale = when (language) {
        "Hausa" -> java.util.Locale("ha")
        "Yoruba" -> java.util.Locale("yo")
        "Igbo" -> java.util.Locale("ig")
        "Spanish" -> java.util.Locale("es")
        "French" -> java.util.Locale("fr")
        "Arabic" -> java.util.Locale("ar")
        "Urdu" -> java.util.Locale("ur")
        "Chinese" -> java.util.Locale("zh")
        else -> java.util.Locale("en")
    }
    val config = android.content.res.Configuration(context.resources.configuration)
    config.setLocale(locale)
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.resources
}

@Composable
fun DuaItemCard(
    dua: DuaEntity,
    speaker: DuaSpeaker,
    searchQuery: String = "",
    selectedLanguage: String = "English",
    arabicFontSize: Float = 24f,
    isCompleted: Boolean = false,
    isDarkTheme: Boolean = false,
    isTarget: Boolean = false,
    onCompleteToggle: () -> Unit = {},
    getTranslation: suspend (DuaEntity, String) -> Pair<String, String>,
    onFavoriteToggle: () -> Unit
) {
    val context = LocalContext.current

    var translationText by remember(dua.id, selectedLanguage) {
        val initial = when (selectedLanguage) {
            "Hausa" -> if (dua.translationHausa.isNotEmpty()) dua.translationHausa else dua.translation
            "Yoruba" -> if (dua.translationYoruba.isNotEmpty()) dua.translationYoruba else dua.translation
            "Igbo" -> if (dua.translationIgbo.isNotEmpty()) dua.translationIgbo else dua.translation
            else -> dua.translation
        }
        mutableStateOf(initial)
    }

    var referenceText by remember(dua.id, selectedLanguage) {
        val initialRef = if (selectedLanguage == "English") {
            dua.reference
        } else {
            com.example.data.local.DuaReferenceLocalization.getLocalizedReference(dua.id, selectedLanguage) ?: dua.reference
        }
        mutableStateOf(initialRef)
    }

    var isTranslating by remember(dua.id, selectedLanguage) {
        mutableStateOf(selectedLanguage != "English")
    }

    LaunchedEffect(dua.id, selectedLanguage) {
        if (selectedLanguage != "English") {
            try {
                val result = getTranslation(dua, selectedLanguage)
                translationText = result.first
                referenceText = result.second
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isTranslating = false
            }
        } else {
            translationText = dua.translation
            referenceText = dua.reference
            isTranslating = false
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

            // Arabic text layout (RTL) - Unchanged
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Text(
                    text = dua.arabic,
                    fontSize = arabicFontSize.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = (arabicFontSize * 1.6f).sp,
                    fontWeight = FontWeight.Medium,
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
                fontSize = 15.5.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) MaterialTheme.colorScheme.secondary else Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Translation text
            Column {
                Text(
                    text = translationText,
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
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
                            val localizedRes = getLocalizedResources(context, selectedLanguage)
                            val referenceLabel = localizedRes.getString(R.string.reference_and_virtue)
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
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp,
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
                // Audio recitation controllers
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
                            contentDescription = "Play Arabic",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Arabic", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
    arabicFontSize: Float = 24f
) {
    val playingArabicId by speaker.isPlaying.collectAsStateWithLifecycle()
    val isPlaying = playingArabicId == 2 // ID 2 for Master Forgiveness

    var translationText by remember(dua?.id, selectedLanguage) {
        val initial = when (selectedLanguage) {
            "Hausa" -> "Ya Allah, Kai ne Ubangijina, babu abin bautawa da gaskiya sai Kai. Ka halitta ni kuma ni bawanKa ne. Ina kan alkawarinKa da wa'adinKa gwargwadon ikona. Ina neman tsari da Kai daga sharrin abin da na aikata. Ina amsa muku ni'imarKa a kaina, kuma ina amsa zunubina. Don haka Ka gafarta mini, domin babu mai gafarta zunubai sai Kai."
            "Yoruba" -> "Allāhu n bẹ, Iwọ ni Ọlọrun mi, ko si ọba miran ti a gbọdọ jọsin fun afi Iwọ. Iwọ lo da mi, emi si ni ẹru Rẹ. Mo duro lori adehun Rẹ ati ileri Rẹ gẹgẹ bi agbara mi ti mọ. Mo tọrọ isadi lọdọ Rẹ lọwọ aburu ohun ti mo ṣe. Mo jẹwọ awọn ikẹ Rẹ lori mi, mo si jẹwọ ẹṣẹ mi. Nitori naa, rọ mi lẹṣẹ ji, nitori ko si ẹni ti n rọ ẹṣẹ ji afi Iwọ."
            "Igbo" -> "Chineke, Gị bụ Onyenwe m, ọ dịghị onye kwesịrị ofufe ma ọ bụghị Gị. Gị kere m, mụ onwe m bụkwa ohu Gị. Adị m n'elu nkwekọrịta Gị na nkwa Gị dịka ike m siri gaa. Ana m achọ ebe mgbaba n'aka Gị pụọ n'ihe ọjọọ niile m mere. Ana m ekwupụta amara Gị n'ebe m nọ, ana m ekwupụtakwa mmehie m. Ya mere meere m ebere gbaghara m, n'ihi na ọ dịghị onye ọzọ nwere ike ịgbaghara mmehie ma ọ bụghị Gị."
            else -> "O Allah, You are my Lord, there is none worthy of worship but You. You created me and I am your slave, and I am faithful to my covenant and my promise so far as I am able..."
        }
        mutableStateOf(initial)
    }

    var isTranslating by remember(dua?.id, selectedLanguage) {
        mutableStateOf(dua != null && selectedLanguage != "English" && selectedLanguage != "Hausa" && selectedLanguage != "Yoruba" && selectedLanguage != "Igbo")
    }

    LaunchedEffect(dua, selectedLanguage) {
        if (dua != null && selectedLanguage != "English") {
            try {
                val result = getTranslation(dua, selectedLanguage)
                translationText = result.first
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isTranslating = false
            }
        } else if (dua != null) {
            translationText = when (selectedLanguage) {
                "Hausa" -> if (dua.translationHausa.isNotEmpty()) dua.translationHausa else dua.translation
                "Yoruba" -> if (dua.translationYoruba.isNotEmpty()) dua.translationYoruba else dua.translation
                "Igbo" -> if (dua.translationIgbo.isNotEmpty()) dua.translationIgbo else dua.translation
                else -> dua.translation
            }
            isTranslating = false
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
                    fontFamily = FontFamily.Serif,
                    lineHeight = (arabicFontSize * 1.6f).sp,
                    fontWeight = FontWeight.Bold,
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
                    fontSize = (arabicFontSize * 0.583f).sp,
                    lineHeight = (arabicFontSize * 0.85f).sp,
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
    onCategoryClick: (String?) -> Unit,
    onSelectDua: (DuaEntity) -> Unit = {}
) {
    val completedCount = completedDuas.size
    val displayCompleted = if (completedCount > 0) completedCount else 12
    val displayTotal = if (totalDuasCount > 0) totalDuasCount else 19
    val progressFraction = (displayCompleted.toFloat() / displayTotal.toFloat()).coerceIn(0f, 1f)
    val progressPercent = (progressFraction * 100).toInt()

    var showMoreCategoriesDialog by remember { mutableStateOf(false) }

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
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
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
                    .height(180.dp)
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
                        .padding(24.dp),
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
                                fontSize = 32.sp,
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

        // Grid of Categories (2 Columns, 3 Rows)
        item {
            val categoriesList = listOf(
                CategoryGridItem(AppLocalizer.getString("morning_azkar", selectedLanguage), "Morning & Evening", "☀️", Icons.Default.WbSunny),
                CategoryGridItem(AppLocalizer.getString("evening_azkar", selectedLanguage), "Morning & Evening", "🌙", Icons.Default.NightsStay),
                CategoryGridItem(AppLocalizer.getString("daily_azkar", selectedLanguage), "Post-Salah Adhkar", "📅", Icons.Default.CalendarToday),
                CategoryGridItem(AppLocalizer.getString("sleep_azkar", selectedLanguage), "Sleeping & Waking Up", "🛌", Icons.Default.Hotel),
                CategoryGridItem(AppLocalizer.getString("quranic_azkar", selectedLanguage), "Hardship & Anxiety", "📖", Icons.Default.MenuBook),
                CategoryGridItem(AppLocalizer.getString("all_duas", selectedLanguage), "MORE_TRIGGER", "🔢", Icons.Default.FormatListNumbered)
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
                            goldAccent = goldAccent,
                            isDarkTheme = isDarkTheme,
                            onClick = {
                                if (categoriesList[i].dbCategory == "MORE_TRIGGER") {
                                    showMoreCategoriesDialog = true
                                } else {
                                    onCategoryClick(categoriesList[i].dbCategory)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        if (i + 1 < categoriesList.size) {
                            CategoryCard(
                                item = categoriesList[i + 1],
                                cardBg = cardBg,
                                cardBorder = cardBorder,
                                textPrimary = textPrimary,
                                goldAccent = goldAccent,
                                isDarkTheme = isDarkTheme,
                                onClick = {
                                    if (categoriesList[i + 1].dbCategory == "MORE_TRIGGER") {
                                        showMoreCategoriesDialog = true
                                    } else {
                                        onCategoryClick(categoriesList[i + 1].dbCategory)
                                    }
                                },
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

        // More Applications / Google Play Store Promotion Card
        item {
            val context = LocalContext.current
            Surface(
                onClick = { openMoreAppsStore(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_more_apps_card"),
                shape = RoundedCornerShape(20.dp),
                color = cardBg,
                border = BorderStroke(1.dp, cardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(goldAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GetApp,
                            contentDescription = "Download More Apps",
                            tint = goldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppLocalizer.getString("more_apps", selectedLanguage),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppLocalizer.getString("more_apps_subtitle", selectedLanguage),
                            fontSize = 12.sp,
                            color = textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open Store",
                        tint = goldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showMoreCategoriesDialog) {
        var dialogSearchQuery by remember { mutableStateOf("") }
        val filteredDuas = if (dialogSearchQuery.isBlank()) {
            allDuas
        } else {
            val q = dialogSearchQuery.trim()
            allDuas.filter {
                it.id.toString() == q ||
                it.title.contains(q, ignoreCase = true) ||
                AppLocalizer.getDuaTitle(it.id, it.title, selectedLanguage).contains(q, ignoreCase = true) ||
                it.category.contains(q, ignoreCase = true) ||
                AppLocalizer.getCategoryName(it.category, selectedLanguage).contains(q, ignoreCase = true) ||
                it.transliteration.contains(q, ignoreCase = true) ||
                it.arabic.contains(q, ignoreCase = true) ||
                it.translation.contains(q, ignoreCase = true) ||
                it.translationHausa.contains(q, ignoreCase = true) ||
                it.translationYoruba.contains(q, ignoreCase = true) ||
                it.translationIgbo.contains(q, ignoreCase = true)
            }
        }

        AlertDialog(
            onDismissRequest = { showMoreCategoriesDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppLocalizer.getString("all_duas", selectedLanguage) + " (${allDuas.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dialogSearchQuery,
                        onValueChange = { dialogSearchQuery = it },
                        placeholder = { Text(AppLocalizer.getString("search_placeholder", selectedLanguage), fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (dialogSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { dialogSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Surface(
                        onClick = {
                            showMoreCategoriesDialog = false
                            onCategoryClick(null)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📜 ", fontSize = 16.sp)
                            Text(
                                AppLocalizer.getString("all_topics", selectedLanguage) + " (${allDuas.size} Duas)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredDuas, key = { it.id }) { dua ->
                            Surface(
                                onClick = {
                                    showMoreCategoriesDialog = false
                                    onSelectDua(dua)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text(
                                            text = "${dua.id}",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val snippet = when (selectedLanguage) {
                                            "Hausa" -> if (dua.translationHausa.isNotBlank()) dua.translationHausa else dua.translation
                                            "Yoruba" -> if (dua.translationYoruba.isNotBlank()) dua.translationYoruba else dua.translation
                                            "Igbo" -> if (dua.translationIgbo.isNotBlank()) dua.translationIgbo else dua.translation
                                            else -> dua.translation
                                        }
                                        Text(
                                            text = "${AppLocalizer.getCategoryName(dua.category, selectedLanguage)} • $snippet",
                                            fontSize = 11.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMoreCategoriesDialog = false }) {
                    Text(AppLocalizer.getString("done", selectedLanguage))
                }
            }
        )
    }
}

data class CategoryGridItem(
    val title: String,
    val dbCategory: String?,
    val emoji: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun CategoryCard(
    item: CategoryGridItem,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    goldAccent: Color,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
            .height(96.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isDarkTheme) Color(0xFF132D27) else MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = goldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = item.emoji,
                    fontSize = 18.sp
                )
            }

            Text(
                text = item.title,
                color = textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
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
    var tempSelectedLanguage by remember { mutableStateOf(selectedLanguage) }

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
                // Noor Zikir Logo Emblem Display on Welcome Screen
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 16.dp)
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFD4AF37).copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                        .border(
                            BorderStroke(
                                2.5.dp,
                                Brush.linearGradient(
                                    listOf(Color(0xFFD4AF37), Color(0xFF1B5E20))
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_noor_zikir_logo),
                        contentDescription = "Noor zikir Logo",
                        modifier = Modifier
                            .size(102.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = "Noor zikir",
                    style = MaterialTheme.typography.headlineMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20), // Emerald Green
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "نور الذكر • LIGHT OF REMEMBRANCE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4AF37),
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                Text(
                    text = "Select your default translation language. You can change this anytime from the top bar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2E4039),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 20.dp)
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
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF1B5E20) else Color(0xFFD2E3DE)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("lang_onboarding_$langCode")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 20.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Button
            Button(
                onClick = onComplete,
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
                    text = "BEGIN SUPPLICATIONS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
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
                // FEATURED REDIRECTION CARD: Download More Apps / Google Play Store
                Surface(
                    onClick = {
                        openMoreAppsStore(context)
                    },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_more_apps_option")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GetApp,
                                contentDescription = "Download More Apps",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppLocalizer.getString("more_apps", selectedLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = AppLocalizer.getString("more_apps_subtitle", selectedLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open Play Store",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
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

                // Arabic Font Size Slider
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                text = AppLocalizer.getString("text_and_font_size", selectedLanguage),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = "${arabicFontSize.toInt()} sp",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = arabicFontSize,
                        onValueChange = onArabicFontSizeChange,
                        valueRange = 18f..40f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

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

    var morningEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_MORNING_ENABLED, false)) }
    var eveningEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_EVENING_ENABLED, false)) }
    var morningHour by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_MORNING_HOUR, 6)) }
    var morningMin by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_MORNING_MINUTE, 0)) }
    var eveningHour by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_EVENING_HOUR, 18)) }
    var eveningMin by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_EVENING_MINUTE, 0)) }
    var durationMinutes by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3)) }
    var fullscreenEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_FULLSCREEN_ENABLED, true)) }

    var isDurationDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Text(
            text = "Mandatory Adhkar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Morning Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Enable Mandatory Morning Adhkar",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Switch(
                checked = morningEnabled,
                onCheckedChange = { checked ->
                    morningEnabled = checked
                    prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_MORNING_ENABLED, checked).apply()
                    MandatoryAdhkarManager.scheduleAlarms(context)
                }
            )
        }

        if (morningEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Morning Reminder Time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = {
                        android.app.TimePickerDialog(
                            context,
                            { _, h, m ->
                                morningHour = h
                                morningMin = m
                                prefs.edit()
                                    .putInt(MandatoryAdhkarManager.KEY_MORNING_HOUR, h)
                                    .putInt(MandatoryAdhkarManager.KEY_MORNING_MINUTE, m)
                                    .apply()
                                MandatoryAdhkarManager.scheduleAlarms(context)
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
        }

        // Evening Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.NightsStay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Enable Mandatory Evening Adhkar",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Switch(
                checked = eveningEnabled,
                onCheckedChange = { checked ->
                    eveningEnabled = checked
                    prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_EVENING_ENABLED, checked).apply()
                    MandatoryAdhkarManager.scheduleAlarms(context)
                }
            )
        }

        if (eveningEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 34.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Evening Reminder Time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedButton(
                    onClick = {
                        android.app.TimePickerDialog(
                            context,
                            { _, h, m ->
                                eveningHour = h
                                eveningMin = m
                                prefs.edit()
                                    .putInt(MandatoryAdhkarManager.KEY_EVENING_HOUR, h)
                                    .putInt(MandatoryAdhkarManager.KEY_EVENING_MINUTE, m)
                                    .apply()
                                MandatoryAdhkarManager.scheduleAlarms(context)
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
        }

        // Reading Duration
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Reading Duration",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Box {
                OutlinedButton(
                    onClick = { isDurationDropdownExpanded = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$durationMinutes min",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }

                DropdownMenu(
                    expanded = isDurationDropdownExpanded,
                    onDismissRequest = { isDurationDropdownExpanded = false }
                ) {
                    listOf(1, 3, 5, 10).forEach { mins ->
                        DropdownMenuItem(
                            text = { Text("$mins minutes") },
                            onClick = {
                                durationMinutes = mins
                                prefs.edit().putInt(MandatoryAdhkarManager.KEY_READING_DURATION, mins).apply()
                                isDurationDropdownExpanded = false
                            },
                            leadingIcon = {
                                if (durationMinutes == mins) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // Enable Full Screen Reminder Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Enable Full Screen Reminder",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Switch(
                checked = fullscreenEnabled,
                onCheckedChange = { checked ->
                    fullscreenEnabled = checked
                    prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_FULLSCREEN_ENABLED, checked).apply()
                }
            )
        }

        // Overlay (Display Over Other Apps) Permission Banner for Auto-Open
        val canDrawOverlays = remember {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.provider.Settings.canDrawOverlays(context)
            } else {
                true
            }
        }

        var hasOverlayPermission by remember { mutableStateOf(canDrawOverlays) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasOverlayPermission) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                } else {
                    MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                }
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (hasOverlayPermission) Icons.Default.CheckCircle else Icons.Default.OpenInNew,
                        contentDescription = null,
                        tint = if (hasOverlayPermission) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = if (hasOverlayPermission) "Bude Kai Tsaye Yana Aiki (Auto-Open Active)" else "Bada Izinin Bude Kan Wasu Manhajoji",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (hasOverlayPermission) {
                        "Wannan zai sa manhajar ta bude kai tsaye a kan wayarka ko da kana cikin wani aiki ko wata manhaja dazarar lokacin Azkar yayi."
                    } else {
                        "Domin manhajar ta iya bude shafin Azkar kai tsaye ko da kana cikin amfani da wata manhaja (kamar WhatsApp ko Chrome), danna nan don kunna izinin."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!hasOverlayPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                ).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Kunna Izinin Bude Kai Tsaye (Allow Auto-Popup)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
