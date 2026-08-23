package com.example.ui.screens

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppLocalizer
import com.example.receiver.MandatoryAdhkarManager
import com.example.ui.components.SettingsInAppUpdateTile
import com.example.util.UpdateState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
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
    context: Context = LocalContext.current
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Saituna" else AppLocalizer.getString("settings_title", selectedLanguage),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (selectedLanguage == "Hausa") "Saitin Noor Zikir & Harshe" else "Preferences & Configuration",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_done_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 1. LANGUAGE SECTION
            item {
                SettingsSectionCard(
                    title = if (selectedLanguage == "Hausa") "Zaɓi Harshe / Language" else "Language / Harshe",
                    icon = Icons.Default.Language,
                    subtitle = if (selectedLanguage == "Hausa") "Harshen Fassara da Bayani" else "Select Translation & UI Language"
                ) {
                    val supportedLanguages = listOf(
                        "Hausa" to "🇳🇬 Hausa",
                        "English" to "🇬🇧 English",
                        "Yoruba" to "🇳🇬 Yorùbá",
                        "Igbo" to "🇳🇬 Igbo",
                        "Arabic" to "🇸🇦 العربية",
                        "French" to "🇫🇷 Français",
                        "Spanish" to "🇪🇸 Español",
                        "Urdu" to "🇵🇰 اردو",
                        "Chinese" to "🇨🇳 中文"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        supportedLanguages.chunked(3).forEach { rowLangs ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowLangs.forEach { (langKey, label) ->
                                    val isSelected = selectedLanguage.equals(langKey, ignoreCase = true)
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { onLanguageSelected(langKey) }
                                            .testTag("lang_select_$langKey"),
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 10.dp, horizontal = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                // If row has fewer than 3 items, fill empty spaces
                                repeat(3 - rowLangs.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // 2. DISPLAY & FONT SIZES
            item {
                SettingsSectionCard(
                    title = if (selectedLanguage == "Hausa") "Yanayin Dubawa & Rubutu" else "Appearance & Font Sizes",
                    icon = Icons.Default.Palette,
                    subtitle = if (selectedLanguage == "Hausa") "Canja Haske da Girman Haruffa" else "Theme & Typography Customization"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Dark Theme Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Yanayin Duhu (Dark Mode)" else AppLocalizer.getString("dark_mode", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (isDarkTheme) (if (selectedLanguage == "Hausa") "Manhaja tana cikin yanayin duhu" else "Dark theme active") else (if (selectedLanguage == "Hausa") "Manhaja tana cikin yanayin haske" else "Light theme active"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = onToggleTheme,
                                modifier = Modifier.testTag("settings_dark_mode_switch")
                            )
                        }

                        // Arabic Font Size Slider
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Girman Rubutun Larabci" else AppLocalizer.getString("arabic_script_size", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${arabicFontSize.toInt()} sp",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Live Arabic Preview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    fontSize = arabicFontSize.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { if (arabicFontSize > 18f) onArabicFontSizeChange(arabicFontSize - 1f) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = arabicFontSize,
                                    onValueChange = onArabicFontSizeChange,
                                    valueRange = 18f..42f,
                                    modifier = Modifier.weight(1f).testTag("arabic_font_slider")
                                )
                                FilledTonalIconButton(
                                    onClick = { if (arabicFontSize < 42f) onArabicFontSizeChange(arabicFontSize + 1f) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Translation Font Size Slider
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFields,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Girman Rubutun Fassara" else "Translation Font Size",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${textFontSize.toInt()} sp",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Live Translation Preview
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") "Da Sunan Allah Mai Rahama Mai Jin Kai." else "In the name of Allah, the Most Gracious, the Most Merciful.",
                                    fontSize = textFontSize.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilledTonalIconButton(
                                    onClick = { if (textFontSize > 12f) onTextFontSizeChange(textFontSize - 1f) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Slider(
                                    value = textFontSize,
                                    onValueChange = onTextFontSizeChange,
                                    valueRange = 12f..28f,
                                    modifier = Modifier.weight(1f).testTag("text_font_slider")
                                )
                                FilledTonalIconButton(
                                    onClick = { if (textFontSize < 28f) onTextFontSizeChange(textFontSize + 1f) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // 3. SCHEDULED ADHKAR (MANDATORY SESSIONS)
            item {
                FullMandatoryAdhkarSection(context = context, selectedLanguage = selectedLanguage)
            }

            // 4. APP UPDATES (PLAY STORE)
            item {
                SettingsSectionCard(
                    title = if (selectedLanguage == "Hausa") "Sabunta Manhaja" else "App Updates",
                    icon = Icons.Default.SystemUpdate,
                    subtitle = if (selectedLanguage == "Hausa") "Duban sabon sigar Noor Zikir a Google Play" else "Check for the latest version on Google Play"
                ) {
                    SettingsInAppUpdateTile(
                        updateState = updateState,
                        selectedLanguage = selectedLanguage,
                        isDarkTheme = isDarkTheme,
                        onCheckForUpdates = onCheckForUpdates,
                        onStartUpdate = onStartUpdate,
                        onCompleteUpdate = onCompleteUpdate
                    )
                }
            }

            // 5. RATE APP, MORE APPS & ABOUT NAJAH TECH
            item {
                SettingsSectionCard(
                    title = if (selectedLanguage == "Hausa") "Taimakawa & Bunkasa" else "Support & Developer",
                    icon = Icons.Default.Favorite,
                    subtitle = if (selectedLanguage == "Hausa") "Bamu tauraro 5 da ziyartar Najah Tech" else "Rate on Google Play & Explore more projects"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Rate on Google Play (5 Stars)
                        Surface(
                            onClick = {
                                try {
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
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0xFFD4AF37), MaterialTheme.colorScheme.primary))),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_rate_app_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(Color(0xFFD4AF37), Color(0xFF1B5E20)))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "5 Stars",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Bamu Tauraro 5 a Play Store ★★★★★" else "Rate Noor Zikir 5 Stars ★★★★★",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Taimaka wajen yaɗa wannan manhaja a Google Play" else "Support us with a 5-star review on Google Play",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // More Apps by Najah Tech
                        Surface(
                            onClick = { openMoreAppsStore(context) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_more_apps_button")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shop,
                                        contentDescription = "Play Store",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Samo Wasu Manhajoji (Play Store)" else AppLocalizer.getString("more_apps", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Duba sauran manhajojin Najah Tech" else "Explore more Islamic apps by Najah Tech",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Najah Tech Web & App Development Services
                        Surface(
                            onClick = { openWebsite(context) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.tertiary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Website",
                                        tint = MaterialTheme.colorScheme.onTertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Ayyukan Yanar Gizo & Manhajoji" else "Custom Web & Mobile Apps",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (selectedLanguage == "Hausa") "Najah Tech - Kwararru wajen Gina Manhajoji" else "Najah Tech - Web & App Development",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 6. FOOTER INFO
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Noor Zikir v1.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Built with ❤️ by Najah Tech",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            content()
        }
    }
}

@Composable
private fun FullMandatoryAdhkarSection(context: Context, selectedLanguage: String) {
    val prefs = remember { MandatoryAdhkarManager.getPrefs(context) }

    var morningHour by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_MORNING_HOUR, 6)) }
    var morningMinute by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_MORNING_MINUTE, 0)) }
    var morningDuration by remember { mutableIntStateOf(prefs.getInt("duration_${MandatoryAdhkarManager.SCHEDULE_ID_MORNING}", prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3))) }
    var morningEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_MORNING_ENABLED, true)) }

    var eveningHour by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_EVENING_HOUR, 18)) }
    var eveningMinute by remember { mutableIntStateOf(prefs.getInt(MandatoryAdhkarManager.KEY_EVENING_MINUTE, 0)) }
    var eveningDuration by remember { mutableIntStateOf(prefs.getInt("duration_${MandatoryAdhkarManager.SCHEDULE_ID_EVENING}", prefs.getInt(MandatoryAdhkarManager.KEY_READING_DURATION, 3))) }
    var eveningEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_EVENING_ENABLED, true)) }

    var soundEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_SOUND_ENABLED, true)) }
    var vibrationEnabled by remember { mutableStateOf(prefs.getBoolean(MandatoryAdhkarManager.KEY_VIBRATION_ENABLED, true)) }

    SettingsSectionCard(
        title = if (selectedLanguage == "Hausa") "Zikiri na Wajibi (Lokuta)" else "Scheduled Adhkar Sessions",
        icon = Icons.Default.AccessTime,
        subtitle = if (selectedLanguage == "Hausa") "Kariyar Zikirin Safe da Yamma ba tare da mantawa ba" else "Auto-launching Morning & Evening Adhkar"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Permissions Banner Check
            val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true

            if (!hasOverlay) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Ana Bukatar Izinin 'Display Over Other Apps'" else "Overlay Permission Required",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa") "Danna don ba da izini domin zikirin ya fito akan wayarka a kan kari." else "Allow overlay permission so Adhkar opens automatically on time.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    val intent = Intent(
                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:${context.packageName}")
                                    ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                                    context.startActivity(intent)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Bada Izini" else "Grant",
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Morning Session
            ScheduleSessionItem(
                title = if (selectedLanguage == "Hausa") "Zikirin Safe (Morning)" else "Morning Adhkar",
                icon = Icons.Default.WbSunny,
                enabled = morningEnabled,
                hour = morningHour,
                minute = morningMinute,
                duration = morningDuration,
                selectedLanguage = selectedLanguage,
                onToggle = {
                    morningEnabled = it
                    MandatoryAdhkarManager.saveSchedule(
                        context,
                        com.example.receiver.MandatorySchedule(
                            id = MandatoryAdhkarManager.SCHEDULE_ID_MORNING,
                            title = "Morning Zikir",
                            category = "Morning & Evening",
                            hour = morningHour,
                            minute = morningMinute,
                            durationMinutes = morningDuration,
                            enabled = it
                        )
                    )
                    val statusStr = if (it) {
                        if (selectedLanguage == "Hausa") "Zikirin Safe yana aiki" else "Morning Adhkar activated"
                    } else {
                        if (selectedLanguage == "Hausa") "An kashe Zikirin Safe" else "Morning Adhkar turned off"
                    }
                    Toast.makeText(context, statusStr, Toast.LENGTH_SHORT).show()
                },
                onTimeChange = { h, m ->
                    morningHour = h
                    morningMinute = m
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
                    val amPm = if (h >= 12) "PM" else "AM"
                    val h12 = if (h % 12 == 0) 12 else h % 12
                    val timeStr = String.format("%02d:%02d %s", h12, m, amPm)
                    val msg = if (selectedLanguage == "Hausa") "An saita Zikirin Safe: $timeStr" else "Morning Adhkar set to $timeStr"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                onDurationChange = {
                    morningDuration = it
                    MandatoryAdhkarManager.saveSchedule(
                        context,
                        com.example.receiver.MandatorySchedule(
                            id = MandatoryAdhkarManager.SCHEDULE_ID_MORNING,
                            title = "Morning Zikir",
                            category = "Morning & Evening",
                            hour = morningHour,
                            minute = morningMinute,
                            durationMinutes = it,
                            enabled = morningEnabled
                        )
                    )
                },
                context = context
            )

            // Evening Session
            ScheduleSessionItem(
                title = if (selectedLanguage == "Hausa") "Zikirin Yamma (Evening)" else "Evening Adhkar",
                icon = Icons.Default.WbTwilight,
                enabled = eveningEnabled,
                hour = eveningHour,
                minute = eveningMinute,
                duration = eveningDuration,
                selectedLanguage = selectedLanguage,
                onToggle = {
                    eveningEnabled = it
                    MandatoryAdhkarManager.saveSchedule(
                        context,
                        com.example.receiver.MandatorySchedule(
                            id = MandatoryAdhkarManager.SCHEDULE_ID_EVENING,
                            title = "Evening Zikir",
                            category = "Morning & Evening",
                            hour = eveningHour,
                            minute = eveningMinute,
                            durationMinutes = eveningDuration,
                            enabled = it
                        )
                    )
                    val statusStr = if (it) {
                        if (selectedLanguage == "Hausa") "Zikirin Yamma yana aiki" else "Evening Adhkar activated"
                    } else {
                        if (selectedLanguage == "Hausa") "An kashe Zikirin Yamma" else "Evening Adhkar turned off"
                    }
                    Toast.makeText(context, statusStr, Toast.LENGTH_SHORT).show()
                },
                onTimeChange = { h, m ->
                    eveningHour = h
                    eveningMinute = m
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
                    val amPm = if (h >= 12) "PM" else "AM"
                    val h12 = if (h % 12 == 0) 12 else h % 12
                    val timeStr = String.format("%02d:%02d %s", h12, m, amPm)
                    val msg = if (selectedLanguage == "Hausa") "An saita Zikirin Yamma: $timeStr" else "Evening Adhkar set to $timeStr"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                },
                onDurationChange = {
                    eveningDuration = it
                    MandatoryAdhkarManager.saveSchedule(
                        context,
                        com.example.receiver.MandatorySchedule(
                            id = MandatoryAdhkarManager.SCHEDULE_ID_EVENING,
                            title = "Evening Zikir",
                            category = "Morning & Evening",
                            hour = eveningHour,
                            minute = eveningMinute,
                            durationMinutes = it,
                            enabled = eveningEnabled
                        )
                    )
                },
                context = context
            )

            // Sound & Vibration Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sound Toggle
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val newVal = !soundEnabled
                            soundEnabled = newVal
                            prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_SOUND_ENABLED, newVal).apply()
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa") "Sauti" else "Sound",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_SOUND_ENABLED, it).apply()
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                // Vibration Toggle
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val newVal = !vibrationEnabled
                            vibrationEnabled = newVal
                            prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_VIBRATION_ENABLED, newVal).apply()
                        },
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa") "Girgiza" else "Vibration",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                vibrationEnabled = it
                                prefs.edit().putBoolean(MandatoryAdhkarManager.KEY_VIBRATION_ENABLED, it).apply()
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleSessionItem(
    title: String,
    icon: ImageVector,
    enabled: Boolean,
    hour: Int,
    minute: Int,
    duration: Int,
    selectedLanguage: String,
    onToggle: (Boolean) -> Unit,
    onTimeChange: (Int, Int) -> Unit,
    onDurationChange: (Int) -> Unit,
    context: Context
) {
    val formattedTime = remember(hour, minute) {
        val amPm = if (hour >= 12) "PM" else "AM"
        val h12 = if (hour % 12 == 0) 12 else hour % 12
        String.format("%02d:%02d %s", h12, minute, amPm)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
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
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.height(24.dp)
                )
            }

            AnimatedVisibility(visible = enabled) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Lokacin Fara Zikiri:" else "Session Start Time:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilledTonalButton(
                            onClick = {
                                TimePickerDialog(
                                    context,
                                    { _, selectedH, selectedM -> onTimeChange(selectedH, selectedM) },
                                    hour,
                                    minute,
                                    false
                                ).show()
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = formattedTime, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Tsawon Zikiri: $duration Minti" else "Duration: $duration mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = duration.toFloat(),
                            onValueChange = { onDurationChange(it.toInt()) },
                            valueRange = 1f..60f,
                            modifier = Modifier.width(140.dp)
                        )
                    }
                }
            }
        }
    }
}
