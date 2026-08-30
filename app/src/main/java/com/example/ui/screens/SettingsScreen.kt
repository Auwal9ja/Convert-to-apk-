package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.app.TimePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.AppLocalizer
import com.example.audio.AthanPlayer
import android.speech.tts.TextToSpeech
import java.util.Locale
import com.example.util.CalculationMethod
import com.example.util.JuristicMethod
import com.example.util.PrayerTimeManager
import com.example.receiver.MandatoryAdhkarManager
import com.example.receiver.PrayerAlarmReceiver
import com.example.ui.components.BannerAd
import com.example.ui.components.SettingsInAppUpdateTile
import com.example.util.UpdateState

const val PRIVACY_POLICY_URL = "https://noorzikir.netlify.app/"
const val NAJAH_TECH_EMAIL = "najahtechng@gmail.com"

fun openPrivacyPolicy(context: Context, url: String = PRIVACY_POLICY_URL) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Privacy Policy: $url", Toast.LENGTH_LONG).show()
    }
}

fun openContactUsEmail(context: Context, recipient: String = NAJAH_TECH_EMAIL) {
    val subject = "Zakiru Muslim App - Tuntuba & Inquiry"
    val body = "Assalamu Alaikum Najah Tech,\n\nIna son yin tambaya / bayani game da manhajar Zakiru Muslim:\n\n\n---\nApp: Zakiru Muslim v1.0.0\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid OS: ${Build.VERSION.RELEASE}"
    
    // 1. Copy info to clipboard as a safe backup
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Zakiru Muslim Contact", "To: $recipient\nSubject: $subject\n\n$body")
        clipboard?.setPrimaryClip(clip)
    } catch (_: Exception) {}

    // 2. Try launching standard mailto intent
    val mailUri = Uri.parse("mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
    val mailIntent = Intent(Intent.ACTION_SENDTO, mailUri).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(mailIntent)
        Toast.makeText(context, "An buɗe Imel zuwa $recipient", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        // Fallback A: Try targeting Gmail directly
        try {
            val gmailIntent = Intent(Intent.ACTION_SENDTO, mailUri).apply {
                `package` = "com.google.android.gm"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(gmailIntent)
            Toast.makeText(context, "An buɗe Gmail zuwa $recipient", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            // Fallback B: Try generic ACTION_SEND chooser
            try {
                val genericIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(genericIntent, "Send Email"))
            } catch (_: Exception) {
                // Fallback C: Open Gmail web compose in browser
                try {
                    val webGmailUrl = "https://mail.google.com/mail/?view=cm&fs=1&to=${Uri.encode(recipient)}&su=${Uri.encode(subject)}&body=${Uri.encode(body)}"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webGmailUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(browserIntent)
                    Toast.makeText(context, "An buɗe Gmail a Browser zuwa $recipient", Toast.LENGTH_LONG).show()
                } catch (_: Exception) {
                    Toast.makeText(context, "Email: $recipient (An kwafi saƙonka)", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

fun sendFeedbackEmail(context: Context, category: String, messageText: String, recipient: String = NAJAH_TECH_EMAIL) {
    val subject = "[Zakiru Muslim Feedback] - $category"
    val body = "Assalamu Alaikum Najah Tech,\n\nGa ra'ayina / shawarata game da Zakiru Muslim:\n\n$messageText\n\n---\nCategory: $category\nApp: Zakiru Muslim v1.0.0\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid OS: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
    
    // 1. Copy feedback to clipboard as guaranteed backup so the user never loses it
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Zakiru Muslim Feedback", "To: $recipient\nSubject: $subject\n\n$body")
        clipboard?.setPrimaryClip(clip)
    } catch (_: Exception) {}

    // 2. Try launching standard mailto intent
    val mailUri = Uri.parse("mailto:$recipient?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
    val mailIntent = Intent(Intent.ACTION_SENDTO, mailUri).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(mailIntent)
        Toast.makeText(context, "An buɗe Imel zuwa $recipient", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        // Fallback A: Try targeting Gmail package directly
        try {
            val gmailIntent = Intent(Intent.ACTION_SENDTO, mailUri).apply {
                `package` = "com.google.android.gm"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(gmailIntent)
            Toast.makeText(context, "An buɗe Gmail zuwa $recipient", Toast.LENGTH_SHORT).show()
        } catch (_: Exception) {
            // Fallback B: Try generic ACTION_SEND chooser
            try {
                val genericIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                    putExtra(Intent.EXTRA_TEXT, body)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(genericIntent, "Send Feedback"))
            } catch (_: Exception) {
                // Fallback C: Open Gmail web compose in browser
                try {
                    val webGmailUrl = "https://mail.google.com/mail/?view=cm&fs=1&to=${Uri.encode(recipient)}&su=${Uri.encode(subject)}&body=${Uri.encode(body)}"
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webGmailUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(browserIntent)
                    Toast.makeText(context, "An buɗe Gmail a Browser zuwa $recipient", Toast.LENGTH_LONG).show()
                } catch (_: Exception) {
                    Toast.makeText(context, "Email: $recipient (An kwafi saƙonka)", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

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
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Intercept phone back button to close dialogs or navigate back cleanly to previous screen
    BackHandler(enabled = true) {
        when {
            showFeedbackDialog -> showFeedbackDialog = false
            showAboutDialog -> showAboutDialog = false
            else -> onNavigateBack()
        }
    }

    if (showFeedbackDialog) {
        FeedbackDialog(
            selectedLanguage = selectedLanguage,
            onDismiss = { showFeedbackDialog = false },
            onSubmit = { category, message ->
                sendFeedbackEmail(context, category, message)
                showFeedbackDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutAppDialog(
            selectedLanguage = selectedLanguage,
            onDismiss = { showAboutDialog = false },
            onOpenPrivacyPolicy = { openPrivacyPolicy(context) },
            onOpenContact = { openContactUsEmail(context) },
            onOpenWebsite = { openWebsite(context) }
        )
    }

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
                            text = if (selectedLanguage == "Hausa") "Saitin Zakiru Muslim & Harshe" else "Preferences & Configuration",
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
        },
        bottomBar = {
            BannerAd()
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

            // 3. PRAYER TIMES & ATHAN CONFIGURATION (LOKUTAN SALLAH & ATHEN)
            item {
                PrayerTimesAndAthanSettingsSection(
                    context = context,
                    selectedLanguage = selectedLanguage,
                    isDarkTheme = isDarkTheme
                )
            }

            // 4. SCHEDULED ADHKAR (MANDATORY SESSIONS)
            item {
                FullMandatoryAdhkarSection(context = context, selectedLanguage = selectedLanguage)
            }

            // 5. APP UPDATES (PLAY STORE)
            item {
                SettingsSectionCard(
                    title = if (selectedLanguage == "Hausa") "Sabunta Manhaja" else "App Updates",
                    icon = Icons.Default.SystemUpdate,
                    subtitle = if (selectedLanguage == "Hausa") "Duban sabon sigar Zakiru Muslim a Google Play" else "Check for the latest version on Google Play"
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

            // 5. PRIVACY, FEEDBACK, CONTACT & ABOUT
            item {
                SettingsSectionCard(
                    title = if (selectedLanguage == "Hausa") "Tsare Sirri & Tuntuba" else "Legal, Support & About",
                    icon = Icons.Default.Security,
                    subtitle = if (selectedLanguage == "Hausa") "Ka'idojin tsare sirri, aiko da ra'ayi da tuntubar mu" else "Privacy Policy, Send Feedback, Contact Us & About"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // 1. Privacy Policy
                        Surface(
                            onClick = { openPrivacyPolicy(context) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_privacy_policy_button")
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
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PrivacyTip,
                                        contentDescription = "Privacy Policy",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = AppLocalizer.getString("privacy_policy", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = AppLocalizer.getString("privacy_policy_subtitle", selectedLanguage),
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

                        // 2. Send Feedback
                        Surface(
                            onClick = { showFeedbackDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_send_feedback_button")
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
                                        .background(MaterialTheme.colorScheme.secondaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Feedback,
                                        contentDescription = "Send Feedback",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = AppLocalizer.getString("send_feedback", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = AppLocalizer.getString("send_feedback_subtitle", selectedLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // 3. Contact Us (Direct to Gmail / Email)
                        Surface(
                            onClick = { openContactUsEmail(context) },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_contact_us_button")
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
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Contact Us",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = AppLocalizer.getString("contact_us", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = AppLocalizer.getString("contact_us_subtitle", selectedLanguage),
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

                        // 4. About App
                        Surface(
                            onClick = { showAboutDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_about_button")
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
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "About",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = AppLocalizer.getString("about_us", selectedLanguage),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = AppLocalizer.getString("about_us_subtitle", selectedLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 6. RATE APP, MORE APPS & ABOUT NAJAH TECH
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
                                        text = if (selectedLanguage == "Hausa") "Bamu Tauraro 5 a Play Store ★★★★★" else "Rate Zakiru Muslim 5 Stars ★★★★★",
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

            // 7. FOOTER INFO
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Zakiru Muslim v1.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Built with ❤️ by Najah Tech (najahtechng@gmail.com)",
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

@Composable
fun FeedbackDialog(
    selectedLanguage: String,
    onDismiss: () -> Unit,
    onSubmit: (category: String, message: String) -> Unit
) {
    val categories = listOf(
        "💡 " + AppLocalizer.getString("feedback_type_suggestion", selectedLanguage),
        "🐛 " + AppLocalizer.getString("feedback_type_bug", selectedLanguage),
        "📖 " + AppLocalizer.getString("feedback_type_dua_correction", selectedLanguage),
        "❓ " + AppLocalizer.getString("feedback_type_question", selectedLanguage)
    )

    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var feedbackText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Feedback,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = AppLocalizer.getString("feedback_dialog_title", selectedLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = AppLocalizer.getString("feedback_type_label", selectedLanguage),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Category chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEachIndexed { index, categoryTitle ->
                        Surface(
                            onClick = { selectedCategoryIndex = index },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedCategoryIndex == index)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = BorderStroke(
                                1.dp,
                                if (selectedCategoryIndex == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = categoryTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedCategoryIndex == index)
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedCategoryIndex == index) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text(if (selectedLanguage == "Hausa") "Sakon Ra'ayi / Shawara" else "Feedback / Message") },
                    placeholder = { Text(AppLocalizer.getString("feedback_hint", selectedLanguage), fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 110.dp)
                        .testTag("feedback_input_field"),
                    maxLines = 5,
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (selectedLanguage == "Hausa")
                                "Za a tura zuwa najahtechng@gmail.com ta Gmail / Imel"
                            else
                                "Will be sent to najahtechng@gmail.com via email",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cat = categories.getOrNull(selectedCategoryIndex) ?: "General"
                    onSubmit(cat, feedbackText.trim())
                },
                enabled = feedbackText.isNotBlank(),
                modifier = Modifier.testTag("feedback_send_button")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(AppLocalizer.getString("feedback_send_btn", selectedLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppLocalizer.getString("feedback_cancel_btn", selectedLanguage))
            }
        }
    )
}

@Composable
fun AboutAppDialog(
    selectedLanguage: String,
    onDismiss: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenContact: () -> Unit,
    onOpenWebsite: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // App Logo / Emblem
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    Color(0xFFD4AF37)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "Zakiru Muslim",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }

                // Title & Version
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Zakiru Muslim",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "ذاكر المسلم • Version 1.0.0",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Description
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Zakiru Muslim manhaja ce ta Musulunci mai kunshe da ingantattun addu'o'in Hisnul Muslim, zikiri na safe da yamma, fassara a yaruka daban-daban (Hausa, English, Yorùbá, Igbo, Larabci...), kamfas din Alƙibla, sauti da jadawalin tunatarwa kyauta."
                        else
                            "Zakiru Muslim is a comprehensive Islamic fortress application featuring authentic Hisnul Muslim Duas, morning & evening Adhkar, multi-language translations, precision Qibla compass, audio recitations, and scheduled reminders.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Developer & Organization Info
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "Kamfani & Mai Bunkasawa:" else "Developer & Team:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Najah Tech Solutions",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Email: najahtechng@gmail.com",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Web: www.najahtech.com",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Privacy policy button
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenPrivacyPolicy()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocalizer.getString("privacy_policy", selectedLanguage), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    // Contact button
                    Button(
                        onClick = {
                            onDismiss()
                            onOpenContact()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocalizer.getString("contact_us", selectedLanguage), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    // Dismiss button
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (selectedLanguage == "Hausa") "Rufe" else "Close")
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerTimesAndAthanSettingsSection(
    context: Context,
    selectedLanguage: String,
    isDarkTheme: Boolean
) {
    var cityName by remember { mutableStateOf(PrayerTimeManager.getCityName(context)) }
    var countryName by remember { mutableStateOf(PrayerTimeManager.getCountryName(context)) }
    var selectedMethod by remember { mutableStateOf(PrayerTimeManager.getCalculationMethod(context)) }
    var selectedJuristic by remember { mutableStateOf(PrayerTimeManager.getJuristicMethod(context)) }
    var hijriOffset by remember { mutableIntStateOf(PrayerTimeManager.getHijriOffset(context)) }
    var athanMode by remember { mutableStateOf(PrayerTimeManager.getAthanMode(context)) }
    var athanSound by remember { mutableStateOf(PrayerTimeManager.getAthanSound(context)) }

    var fajrAlarm by remember { mutableStateOf(PrayerTimeManager.isPrayerAlarmEnabled(context, "FAJR")) }
    var dhuhrAlarm by remember { mutableStateOf(PrayerTimeManager.isPrayerAlarmEnabled(context, "DHUHR")) }
    var asrAlarm by remember { mutableStateOf(PrayerTimeManager.isPrayerAlarmEnabled(context, "ASR")) }
    var maghribAlarm by remember { mutableStateOf(PrayerTimeManager.isPrayerAlarmEnabled(context, "MAGHRIB")) }
    var ishaAlarm by remember { mutableStateOf(PrayerTimeManager.isPrayerAlarmEnabled(context, "ISHA")) }

    var showMethodDialog by remember { mutableStateOf(false) }
    var showJuristicDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }
    var showModeDialog by remember { mutableStateOf(false) }
    var isTestingAudio by remember { mutableStateOf(false) }

    SettingsSectionCard(
        title = if (selectedLanguage == "Hausa") "Lokutan Sallah & Kiran Sallah (Athan)" else "Prayer Times & Athan",
        icon = Icons.Default.AccessTime,
        subtitle = if (selectedLanguage == "Hausa") "Saitin Garuruwa, Hanyar Lissafi, Hijri da Kararrawar Athan" else "Location, Calculation Method, Hijri Adjustment & Athan Alarms"
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // 1. Selected Location Row
            Surface(
                onClick = { showCityDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_prayer_location_btn")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00796B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Wurin da Kake (Gari)" else "Prayer Location / City",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$cityName, $countryName",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 2. Calculation Method Row
            Surface(
                onClick = { showMethodDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_calc_method_btn")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Calculation",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Hanyar Lissafin Lokaci" else "Calculation Method",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedMethod.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 3. Asr Juristic Method Row
            Surface(
                onClick = { showJuristicDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_juristic_method_btn")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Asr Method",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Lissafin Lokacin La'asar (Asr)" else "Asr Juristic Method",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedJuristic.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 4. Hijri Date Offset Stepper
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "Daidaita Ranar Hijri" else "Hijri Date Adjustment",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (hijriOffset > 0) "+$hijriOffset kwana" else if (hijriOffset < 0) "$hijriOffset kwana" else "Daidai (0)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = {
                            if (hijriOffset > -2) {
                                hijriOffset--
                                PrayerTimeManager.setHijriOffset(context, hijriOffset)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("-1 Kwana", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            hijriOffset = 0
                            PrayerTimeManager.setHijriOffset(context, 0)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Sake Saita", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = {
                            if (hijriOffset < 2) {
                                hijriOffset++
                                PrayerTimeManager.setHijriOffset(context, hijriOffset)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+1 Kwana", fontSize = 12.sp)
                    }
                }
            }

            // 5. Alert Mode (Sound / Vibrate / Sound+Vibrate / Silent)
            val currentMode = AthanPlayer.AlertMode.fromId(athanMode)
            Surface(
                onClick = { showModeDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_athan_mode_btn")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0288D1).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (currentMode) {
                                AthanPlayer.AlertMode.SOUND_AND_VIBRATE -> Icons.Default.VolumeUp
                                AthanPlayer.AlertMode.SOUND_ONLY -> Icons.Default.VolumeUp
                                AthanPlayer.AlertMode.VIBRATE_ONLY -> Icons.Default.Vibration
                                AthanPlayer.AlertMode.SILENT -> Icons.Default.VolumeOff
                            },
                            contentDescription = "Alert Mode",
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Yanayin Kararrawa (Alert Mode)" else "Athan Alert Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedLanguage == "Hausa") currentMode.titleHa else currentMode.titleEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 6. Select Athan Sound (Makkah, Madinah, Al-Aqsa, Egypt, Vocal TTS, Soft Takbeer)
            val currentSound = AthanPlayer.AthanSound.fromId(athanSound)
            Surface(
                onClick = { showSoundDialog = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth().testTag("settings_athan_sound_btn")
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD4AF37).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Athan Sound",
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Zaɓin Sautin Athan (Sound)" else "Select Athan Sound",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedLanguage == "Hausa") currentSound.displayNameHa else currentSound.displayNameEn,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // 7. Individual Prayer Alarm Toggles
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (selectedLanguage == "Hausa") "Kunna / Kashe Kararrawar Kowace Sallah" else "Prayer Alarms & Notifications",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                val prayerToggles = listOf(
                    Triple("FAJR", "🌅 Asuba (Fajr)", fajrAlarm) to { next: Boolean ->
                        fajrAlarm = next
                        PrayerTimeManager.setPrayerAlarmEnabled(context, "FAJR", next)
                    },
                    Triple("DHUHR", "🌞 Azahar (Dhuhr)", dhuhrAlarm) to { next: Boolean ->
                        dhuhrAlarm = next
                        PrayerTimeManager.setPrayerAlarmEnabled(context, "DHUHR", next)
                    },
                    Triple("ASR", "⛅ La'asar (Asr)", asrAlarm) to { next: Boolean ->
                        asrAlarm = next
                        PrayerTimeManager.setPrayerAlarmEnabled(context, "ASR", next)
                    },
                    Triple("MAGHRIB", "🌇 Magariba (Maghrib)", maghribAlarm) to { next: Boolean ->
                        maghribAlarm = next
                        PrayerTimeManager.setPrayerAlarmEnabled(context, "MAGHRIB", next)
                    },
                    Triple("ISHA", "🌙 Isha'i (Isha)", ishaAlarm) to { next: Boolean ->
                        ishaAlarm = next
                        PrayerTimeManager.setPrayerAlarmEnabled(context, "ISHA", next)
                    }
                )

                prayerToggles.forEach { (item, onToggle) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.second,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = item.third,
                            onCheckedChange = { onToggle(it) },
                            modifier = Modifier.testTag("switch_alarm_${item.first}")
                        )
                    }
                }
            }

            // 8. Test Athan Audio Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isTestingAudio = true
                        PrayerAlarmReceiver.playAthanAudio(context) {
                            isTestingAudio = false
                        }
                        PrayerAlarmReceiver.triggerVibration(context)
                        val soundName = if (selectedLanguage == "Hausa") currentSound.displayNameHa else currentSound.displayNameEn
                        Toast.makeText(context, "Ana kunna: $soundName", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    val soundDisplayName = when (selectedLanguage) {
                        "Hausa" -> currentSound.displayNameHa
                        "Arabic" -> currentSound.displayNameEn
                        else -> currentSound.displayNameEn
                    }
                    Text(
                        text = if (selectedLanguage == "Hausa") "Saurari Sautin ($soundDisplayName)" else "Test Sound ($soundDisplayName)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (isTestingAudio) {
                    OutlinedButton(
                        onClick = {
                            AthanPlayer.stop()
                            isTestingAudio = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (selectedLanguage == "Hausa") "Tsaya" else "Stop", fontSize = 12.sp)
                    }
                }
            }
        }
    }

    // Calculation Method Picker Dialog
    if (showMethodDialog) {
        Dialog(onDismissRequest = { showMethodDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "Zaɓi Hanyar Lissafi" else "Select Calculation Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(CalculationMethod.values().toList()) { method ->
                            val isSel = selectedMethod == method
                            Surface(
                                onClick = {
                                    selectedMethod = method
                                    PrayerTimeManager.setCalculationMethod(context, method)
                                    showMethodDialog = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = method.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSel) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Juristic Method Picker Dialog
    if (showJuristicDialog) {
        Dialog(onDismissRequest = { showJuristicDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "Zaɓi Hanyar Lissafin La'asar" else "Select Asr Juristic Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    JuristicMethod.values().forEach { juristic ->
                        val isSel = selectedJuristic == juristic
                        Surface(
                            onClick = {
                                selectedJuristic = juristic
                                PrayerTimeManager.setJuristicMethod(context, juristic)
                                showJuristicDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = juristic.displayName,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // City Location Dialog in Settings
    if (showCityDialog) {
        Dialog(onDismissRequest = { showCityDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Zaɓi Garinku" else "Select Location",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = { showCityDialog = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    var isDetectingInSettings by remember { mutableStateOf(false) }
                    val settingsPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                        if (granted) {
                            isDetectingInSettings = true
                            PrayerTimeManager.tryDetectGpsLocation(context) { city, country, _, _ ->
                                isDetectingInSettings = false
                                cityName = city
                                countryName = country
                                Toast.makeText(context, if (selectedLanguage == "Hausa") "An sabunta: $city, $country" else "Updated: $city, $country", Toast.LENGTH_SHORT).show()
                                showCityDialog = false
                            }
                        } else {
                            isDetectingInSettings = false
                            Toast.makeText(context, if (selectedLanguage == "Hausa") "Ana buƙatar izinin GPS" else "GPS permission required", Toast.LENGTH_SHORT).show()
                        }
                    }

                    Button(
                        onClick = {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            if (hasFine || hasCoarse) {
                                isDetectingInSettings = true
                                PrayerTimeManager.tryDetectGpsLocation(context) { city, country, _, _ ->
                                    isDetectingInSettings = false
                                    cityName = city
                                    countryName = country
                                    Toast.makeText(context, if (selectedLanguage == "Hausa") "An sabunta: $city, $country" else "Updated: $city, $country", Toast.LENGTH_SHORT).show()
                                    showCityDialog = false
                                }
                            } else {
                                settingsPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        enabled = !isDetectingInSettings,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B))
                    ) {
                        if (isDetectingInSettings) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedLanguage == "Hausa") "Ana neman wuri ta GPS..." else "Detecting location via GPS...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(Icons.Default.MyLocation, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedLanguage == "Hausa") "Gano Wuri da GPS (Auto)" else "Detect Location via GPS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(PRESET_CITIES) { city ->
                            val isSel = cityName == city.name
                            Surface(
                                onClick = {
                                    PrayerTimeManager.setLocation(context, city.name, city.country, city.latitude, city.longitude)
                                    cityName = city.name
                                    countryName = city.country
                                    showCityDialog = false
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = city.name, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp)
                                        Text(text = city.country, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    if (isSel) {
                                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Alert Mode Selection Dialog
    if (showModeDialog) {
        Dialog(onDismissRequest = { showModeDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "Zaɓi Yanayin Kararrawa" else "Select Alert Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AthanPlayer.AlertMode.values().forEach { mode ->
                        val isSel = athanMode == mode.id
                        Surface(
                            onClick = {
                                athanMode = mode.id
                                PrayerTimeManager.setAthanMode(context, mode.id)
                                showModeDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedLanguage == "Hausa") mode.titleHa else mode.titleEn,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Athan Sound Selection Dialog with preview
    if (showSoundDialog) {
        Dialog(onDismissRequest = {
            showSoundDialog = false
            AthanPlayer.stop()
        }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Zaɓi Sautin Athan" else "Select Athan Sound",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = {
                            showSoundDialog = false
                            AthanPlayer.stop()
                        }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(AthanPlayer.AthanSound.values().toList()) { sound ->
                            val isSel = athanSound == sound.id
                            Surface(
                                onClick = {
                                    athanSound = sound.id
                                    PrayerTimeManager.setAthanSound(context, sound.id)
                                    AthanPlayer.playAthan(context, sound)
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (selectedLanguage == "Hausa") sound.displayNameHa else sound.displayNameEn,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.5.sp
                                        )
                                        Text(
                                            text = sound.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                AthanPlayer.playAthan(context, sound)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Preview",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        if (isSel) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showSoundDialog = false
                            AthanPlayer.stop()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(if (selectedLanguage == "Hausa") "Adana (Save)" else "Save Selection")
                    }
                }
            }
        }
    }
}

