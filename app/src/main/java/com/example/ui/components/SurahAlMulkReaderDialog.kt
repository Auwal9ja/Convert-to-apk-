package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppLocalizer
import com.example.data.local.SleepingAndWakingData
import com.example.ui.audio.DuaSpeaker
import com.example.ui.theme.QuranFontFamily

@Composable
fun SurahAlMulkHeroBanner(
    selectedLanguage: String,
    onOpenReader: () -> Unit,
    onPlaySurah: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("surah_mulk_hero_banner")
            .clickable { onOpenReader() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A237E), // Deep Midnight Indigo
                            Color(0xFF0D47A1), // Royal Navy Blue
                            Color(0xFF0F172A)  // Night Slate
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFFD4AF37).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("👑", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedLanguage == "Hausa") "TA FARKO A SHAFIN BARCI" else "FIRST ON SLEEPING PAGE",
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "30 Ayahs • Makkiyyah",
                            color = Color(0xFFBAE6FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Arabic Title Calligraphy
                Text(
                    text = "سُورَةُ الْمُلْكِ",
                    fontFamily = QuranFontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (selectedLanguage == "Hausa")
                        "Cikakken Karatun Suratul Mulk Kafin Barci"
                    else
                        "Surah Al-Mulk: Full Recitation Before Sleep",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1F5F9),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (selectedLanguage == "Hausa")
                        "«Lallai akwai wata sura a Alƙur'ani mai ayoyi 30 da ta yi ceto ga wani mutum har aka gafarta masa: Tabarakalladhi biyadihil-Mulk. Manzon Allah (ﷺ) ba ya yin barci har sai ya karanta ta.»"
                    else
                        "«There is a surah of thirty verses that interceded for a man until he was forgiven: Tabarakalladhi biyadihil-Mulk. The Prophet (ﷺ) would not sleep until reciting it.»",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onOpenReader,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD4AF37),
                            contentColor = Color(0xFF1E1B18)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_open_mulk_reader")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "Read Surah",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedLanguage == "Hausa") "Karanta (Ayoyi 1-30)" else "Read (Ayahs 1-30)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = onPlaySurah,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFF93C5FD)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_play_mulk_hero")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen Audio",
                            tint = if (isPlaying) Color(0xFFFDE047) else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlaying)
                                (if (selectedLanguage == "Hausa") "Tsayar da Audio" else "Pause Audio")
                            else
                                (if (selectedLanguage == "Hausa") "Saurari Karatu" else "Listen Audio"),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahAlMulkReaderDialog(
    speaker: DuaSpeaker,
    selectedLanguage: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Ayah-by-Ayah, 1: Mushaf Continuous, 2: Virtues & Falala, 3: Bedtime Etiquettes & Tasbih
    var arabicFontSize by remember { mutableFloatStateOf(24f) }
    val playingArabicId by speaker.isPlaying.collectAsStateWithLifecycle()
    val isPlayingSurah = playingArabicId == 5

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "سُورَةُ الْمُلْكِ",
                                fontFamily = QuranFontFamily,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa")
                                    "Karatun Kafin Barci (Ayoyi 1-30)"
                                else
                                    "Surah Al-Mulk (Ayahs 1-30)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_mulk_dialog")) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (isPlayingSurah) {
                                    speaker.stop()
                                } else {
                                    speaker.playArabic(5, SleepingAndWakingData.fullSurahAlMulkArabic)
                                }
                            },
                            modifier = Modifier.testTag("action_play_mulk_dialog")
                        ) {
                            Icon(
                                imageVector = if (isPlayingSurah) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = "Play/Pause Audio",
                                tint = if (isPlayingSurah) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Font Size Adjuster
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Girmar Rubutun Larabci:" else "Arabic Font Size:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { if (arabicFontSize > 18f) arabicFontSize -= 2f },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease Font Size", modifier = Modifier.size(18.dp))
                                }
                                Text(
                                    "${arabicFontSize.toInt()} sp",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { if (arabicFontSize < 38f) arabicFontSize += 2f },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase Font Size", modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Secondary Navigation Tabs
                PrimaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                if (selectedLanguage == "Hausa") "Aya-Aya" else "Ayah by Ayah",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Default.FormatListNumbered, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                if (selectedLanguage == "Hausa") "Mushaf" else "Mushaf View",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Default.AutoStories, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                if (selectedLanguage == "Hausa") "Falala" else "Virtues",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Default.Stars, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                if (selectedLanguage == "Hausa") "Tasbihi" else "Tasbih",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = { Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                // Tab Content
                when (selectedTab) {
                    0 -> SurahAlMulkAyahByAyahList(
                        speaker = speaker,
                        selectedLanguage = selectedLanguage,
                        arabicFontSize = arabicFontSize
                    )
                    1 -> SurahAlMulkMushafView(
                        speaker = speaker,
                        selectedLanguage = selectedLanguage,
                        arabicFontSize = arabicFontSize
                    )
                    2 -> SurahAlMulkVirtuesView(selectedLanguage = selectedLanguage)
                    3 -> BedtimeTasbihEtiquettesView(selectedLanguage = selectedLanguage)
                }
            }
        }
    }
}

@Composable
fun SurahAlMulkAyahByAyahList(
    speaker: DuaSpeaker,
    selectedLanguage: String,
    arabicFontSize: Float
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("surah_mulk_ayah_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bismillah Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontFamily = QuranFontFamily,
                        fontSize = (arabicFontSize + 2).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 30 Ayahs
        itemsIndexed(SleepingAndWakingData.surahAlMulkAyahs, key = { _, item -> item.number }) { _, ayah ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_ayah_${ayah.number}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header row: Ayah Number Circle & Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Text(
                                    text = "${ayah.number}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Copy Ayah
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val textToCopy = "${ayah.arabic}\n\n${ayah.transliteration}\n\n${if (selectedLanguage == "Hausa") ayah.translationHausa else ayah.translationEnglish}"
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Surah Al-Mulk Ayah ${ayah.number}", textToCopy))
                                    Toast.makeText(context, if (selectedLanguage == "Hausa") "An kwafi Ayah ${ayah.number}" else "Copied Ayah ${ayah.number}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.secondary)
                            }

                            // Play Ayah
                            IconButton(
                                onClick = {
                                    speaker.playArabic(1000 + ayah.number, ayah.arabic)
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Play Ayah",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Arabic Ayah
                    Text(
                        text = ayah.arabic,
                        fontFamily = QuranFontFamily,
                        fontSize = arabicFontSize.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        lineHeight = (arabicFontSize * 1.7f).sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Transliteration
                    Text(
                        text = ayah.transliteration,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Translation (Hausa / English)
                    val transText = if (selectedLanguage == "Hausa") ayah.translationHausa else ayah.translationEnglish
                    Text(
                        text = transText,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SurahAlMulkMushafView(
    speaker: DuaSpeaker,
    selectedLanguage: String,
    arabicFontSize: Float
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("surah_mulk_mushaf_view"),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(2.dp, Color(0xFFD4AF37).copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mushaf Header Ornament
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFD4AF37).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFD4AF37)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "سُورَةُ الْمُلْكِ",
                                fontFamily = QuranFontFamily,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "مَكِّيَّةٌ • ثَلَاثُونَ آيَةً",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        fontFamily = QuranFontFamily,
                        fontSize = (arabicFontSize + 4).sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Continuous Arabic text with Ayah Symbols ﴿١﴾
                    Text(
                        text = buildString {
                            SleepingAndWakingData.surahAlMulkAyahs.forEach { ayah ->
                                append(ayah.arabic)
                                append(" ﴿${ayah.number}﴾ ")
                            }
                        },
                        fontFamily = QuranFontFamily,
                        fontSize = arabicFontSize.sp,
                        lineHeight = (arabicFontSize * 2f).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SurahAlMulkVirtuesView(selectedLanguage: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("surah_mulk_virtues_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "⭐ Falalar Karanta Suratul Mulk a Sunnah" else "⭐ Virtues of Surah Al-Mulk in the Sunnah",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Suratul Mulk (Tabarakallazi) tana daga cikin manyan surorin Alkur'ani masu falala mai girma, musamman ga wanda yake karanta ta a kowace dare kafin ya kwanta barci."
                        else
                            "Surah Al-Mulk (Tabarakallazi) holds immense virtues in authentic hadiths, particularly for the one who recites it consistently every night before going to sleep.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Hadith 1: Ceto har sai an gafarta masa
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Tana Ceto ga Mai Karanta Ta Har a Gafarta Masa",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "عَنْ أَبِي هُرَيْرَةَ رَضِيَ اللَّهُ عَنْهُ، عَنِ النَّبِيِّ ﷺ قَالَ: «إِنَّ سُورَةً مِنَ الْقُرْآنِ ثَلَاثُونَ آيَةً شَفَعَتْ لِرَجُلٍ حَتَّى غُفِرَ لَهُ: تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ».",
                        fontFamily = QuranFontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Daga Abu Hurairah (RA), Manzon Allah (ﷺ) ya ce: 'Lallai a cikin Alƙur'ani akwai wata sura mai ayoyi talatin (30) da ta yi ceto ga wani mutum har sai da aka gafarta masa zunubansa; ita ce Tabarakalladhi biyadihil-Mulk.'"
                        else
                            "Abu Hurairah (RA) reported that the Prophet (ﷺ) said: 'Indeed, there is a surah in the Quran of thirty verses that interceded for a man until he was forgiven: Tabarakalladhi biyadihil-Mulk.'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Madogara: Sunan Abu Dawud (1400), Jami' At-Tirmidhi (2891). Sahih.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Hadith 2: Annabi ba ya yin barci har sai ya karanta ta
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Manzon Allah (ﷺ) Ba Ya Barci Har Sai Ya Karanta Ta",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "وَعَنْ جَابِرٍ رَضِيَ اللَّهُ عَنْهُ: «أَنَّ النَّبِيَّ ﷺ كَانَ لَا يَنَامُ حَتَّى يَقْرَأَ: الم تَنْزِيلُ، وَتَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ».",
                        fontFamily = QuranFontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Daga Jabir (RA): 'Manzon Allah (ﷺ) ya kasance ba ya yin barci a kowace dare har sai ya karanta Alif-Lam-Mim Tanzil (Suratus Sajdah) da Tabarakalladhi biyadihil-Mulk (Suratul Mulk).'"
                        else
                            "Jabir (RA) reported that the Prophet (ﷺ) would not sleep until he had recited Surah As-Sajdah and Surah Al-Mulk.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Madogara: Jami' At-Tirmidhi (2892), Musnad Ahmad (14249). Sahih.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Hadith 3: Al-Mani'ah (Kariya daga Azabar Kabari)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Kariya Daga Azabar Kabari (Al-Mani'ah)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "قَالَ عَبْدُ اللَّهِ بْنُ مَسْعُودٍ رَضِيَ اللَّهُ عَنْهُ: «مَنْ قَرَأَ تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ كُلَّ لَيْلَةٍ مَنَعَهُ اللَّهُ بِهَا مِنْ عَذَابِ الْقَبْرِ، وَكُنَّا فِي عَهْدِ رَسُولِ اللَّهِ ﷺ نُسَمِّيهَا الْمَانِعَةَ».",
                        fontFamily = QuranFontFamily,
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Babban Sahabi Abdullahi bn Mas'ud (RA) ya ce: 'Wanda duk ya karanta Tabarakalladhi biyadihil-mulk a kowace dare, Allah zai kare shi da ita daga azabar kabari. A zamanin Manzon Allah (ﷺ) muna kiranta Al-Mani'ah (Mai bayar da kariya da garkuwa daga azaba).'"
                        else
                            "Abdullah ibn Mas'ud (RA) said: 'Whoever reads Tabarakalladhi biyadihil-mulk every night, Allah will protect him from the torment of the grave. During the lifetime of the Messenger of Allah (ﷺ), we used to call it Al-Mani'ah (the protector).'",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Madogara: Sunan An-Nasa'i As-Sunan Al-Kubra (10547), Al-Hakim (2/498). Sahih.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun BedtimeTasbihEtiquettesView(selectedLanguage: String) {
    val context = LocalContext.current
    var subhanallahCount by remember { mutableIntStateOf(0) }
    var alhamdulillahCount by remember { mutableIntStateOf(0) }
    var allahuAkbarCount by remember { mutableIntStateOf(0) }

    fun triggerVibration() {
        try {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("bedtime_tasbih_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tasbih Card Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "📿 Tasbihin Kwanciya Barci (Tasbihin Fadima)" else "📿 Bedtime Tasbih of Fatima (RA)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (selectedLanguage == "Hausa")
                            "Subhanallah (33), Alhamdulillah (33), Allahu Akbar (34). Manzon Allah (ﷺ) ya ce ya fi bawa mai aiki alheri!"
                        else
                            "Subhanallah (33), Alhamdulillah (33), Allahu Akbar (34). Better than a servant for you!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Subhanallah Counter (33)
        item {
            TasbihCounterTile(
                arabic = "سُبْحَانَ اللَّهِ",
                transliteration = "Subhanallah (Tsarki ya tabbata ga Allah)",
                current = subhanallahCount,
                target = 33,
                accentColor = Color(0xFF10B981),
                onIncrement = {
                    if (subhanallahCount < 33) {
                        subhanallahCount++
                        triggerVibration()
                    }
                },
                onReset = { subhanallahCount = 0 }
            )
        }

        // Alhamdulillah Counter (33)
        item {
            TasbihCounterTile(
                arabic = "الْحَمْدُ لِلَّهِ",
                transliteration = "Alhamdulillah (Godiya ta tabbata ga Allah)",
                current = alhamdulillahCount,
                target = 33,
                accentColor = Color(0xFF3B82F6),
                onIncrement = {
                    if (alhamdulillahCount < 33) {
                        alhamdulillahCount++
                        triggerVibration()
                    }
                },
                onReset = { alhamdulillahCount = 0 }
            )
        }

        // Allahu Akbar Counter (34)
        item {
            TasbihCounterTile(
                arabic = "اللَّهُ أَكْبَرُ",
                transliteration = "Allahu Akbar (Allah ne Mafi Girma)",
                current = allahuAkbarCount,
                target = 34,
                accentColor = Color(0xFFD4AF37),
                onIncrement = {
                    if (allahuAkbarCount < 34) {
                        allahuAkbarCount++
                        triggerVibration()
                    }
                },
                onReset = { allahuAkbarCount = 0 }
            )
        }

        // Reset All Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(
                    onClick = {
                        subhanallahCount = 0
                        alhamdulillahCount = 0
                        allahuAkbarCount = 0
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (selectedLanguage == "Hausa") "Sake Fara Tasbihi" else "Reset All Counters")
                }
            }
        }
    }
}

@Composable
fun TasbihCounterTile(
    arabic: String,
    transliteration: String,
    current: Int,
    target: Int,
    accentColor: Color,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    val isFinished = current >= target

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFinished) accentColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, if (isFinished) accentColor else MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onIncrement() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = arabic,
                        fontFamily = QuranFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(
                        text = transliteration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = if (isFinished) accentColor else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isFinished) {
                            Icon(Icons.Default.Check, contentDescription = "Completed", tint = Color.White)
                        } else {
                            Text(
                                text = "$current",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { current.toFloat() / target.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = accentColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$current / $target",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )

                if (current > 0) {
                    TextButton(onClick = onReset, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)) {
                        Text("Reset", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
