package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.DuaEntity

/**
 * Featured Morning/Evening Azkar Hero Card matching the new design.
 */
@Composable
fun FeaturedAzkarHeroCard(
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) Color(0xFF092921) else Color(0xFFFFFDF9)
    val cardBorder = if (isDarkTheme) Color(0xFF144D3D) else Color(0xFFE8EFEA)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0F2D25)
    val textSecondary = if (isDarkTheme) Color(0xFFA7C7BD) else Color(0xFF526D65)

    val titleText = when (selectedLanguage) {
        "Hausa" -> "Azkar na Safe"
        "Arabic" -> "أذكار الصباح"
        "Yoruba" -> "Àwọn Adhkar Owurọ̀"
        "Igbo" -> "Azkar Ụtụtụ"
        "French" -> "Adhkar du Matin"
        else -> "Morning Adhkar"
    }

    val subtitleText = when (selectedLanguage) {
        "Hausa" -> "Fara ranar ka da albarka"
        "Arabic" -> "ابدأ يومك ببركة وذكر"
        "Yoruba" -> "Bẹrẹ ọjọ rẹ pẹlu ibukun"
        "Igbo" -> "Bido ụbọchị gị na ngọzi"
        "French" -> "Commencez votre journée bénie"
        else -> "Start your day with blessings"
    }

    val buttonLabel = when (selectedLanguage) {
        "Hausa" -> "Ci gaba"
        "Arabic" -> "متابعة"
        "Yoruba" -> "Tẹsiwaju"
        "Igbo" -> "Gaa n'ihu"
        "French" -> "Continuer"
        else -> "Continue"
    }

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.2.dp, cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onContinueClick() }
            .testTag("featured_azkar_hero_card")
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background subtle mosque skyline watermark on the right
            Image(
                painter = painterResource(id = R.drawable.daylight_mosque_sky_1791161967162),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(26.dp)),
                alpha = if (isDarkTheme) 0.12f else 0.20f
            )

            // Horizontal gradient scrim to keep left texts perfectly readable
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            0.0f to cardBg,
                            0.50f to cardBg.copy(alpha = 0.85f),
                            1.0f to Color.Transparent
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                // Top Row: Sun Badge + Count Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Amber Sun Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "☀️", fontSize = 18.sp)
                        }
                    }

                    // Count Pill
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isDarkTheme) Color(0xFF282315) else Color(0xFFFEF3C7),
                        border = BorderStroke(1.dp, if (isDarkTheme) Color(0xFF5A4920) else Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "21 Addu'a" else "21 Du'a",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkTheme) Color(0xFFFDE047) else Color(0xFF92400E),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = titleText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "21 Du'a",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textSecondary
                )

                Text(
                    text = subtitleText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Row: Progress bar + Ci gaba button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left: Progress
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        LinearProgressIndicator(
                            progress = { 0.33f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.5.dp)
                                .clip(RoundedCornerShape(50)),
                            color = Color(0xFF007A55),
                            trackColor = if (isDarkTheme) Color(0xFF134237) else Color(0xFFD6EDE3)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "7 / 21 completed",
                                fontSize = 11.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "33%",
                                fontSize = 11.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Right: Solid Emerald Green Button "Ci gaba ➔"
                    Surface(
                        onClick = onContinueClick,
                        shape = RoundedCornerShape(50),
                        color = Color(0xFF005C42),
                        modifier = Modifier.testTag("featured_continue_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = buttonLabel,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2x2 Grid of Quick Access Cards:
 * 1. Azkar na Yamma
 * 2. Sallah & Masallaci
 * 3. 99 Names
 * 4. Zikr Counter
 */
@Composable
fun QuickShortcutsGrid(
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onViewAllClick: () -> Unit,
    onEveningClick: () -> Unit,
    onPrayerClick: () -> Unit,
    onAsmaulHusnaClick: () -> Unit,
    onTasbihClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0A2B24)
    val accentGreen = if (isDarkTheme) Color(0xFF34D399) else Color(0xFF059669)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (selectedLanguage) {
                    "Hausa" -> "Shiga Da Sauri"
                    "Arabic" -> "الوصول السريع"
                    "Yoruba" -> "Àwọn Ọ̀nà Ìyára"
                    "Igbo" -> "Nweta Ngwa Ngwa"
                    "French" -> "Accès Rapide"
                    else -> "Quick Access"
                },
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewAllClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (selectedLanguage) {
                        "Hausa" -> "Duk Sana'u"
                        "Arabic" -> "عرض الكل"
                        "Yoruba" -> "Wo Gbogbo Rẹ"
                        "Igbo" -> "Hụ Ha Nile"
                        "French" -> "Voir Tout"
                        else -> "View All"
                    },
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentGreen
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentGreen,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Row 1 of 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Azkar na Yamma
            QuickAccessCard(
                iconEmoji = "🌙",
                iconBg = Color(0xFF2563EB),
                pillText = if (selectedLanguage == "Hausa") "18 Addu'a" else "18 Du'a",
                pillBg = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFEFF6FF),
                pillTextColor = if (isDarkTheme) Color(0xFF93C5FD) else Color(0xFF1D4ED8),
                title = when (selectedLanguage) {
                    "Hausa" -> "Azkar na Yamma"
                    "Arabic" -> "أذكار المساء"
                    "Yoruba" -> "Adhkar Ìrọ̀lẹ́"
                    "Igbo" -> "Azkar Anyasị"
                    "French" -> "Adhkar du Soir"
                    else -> "Evening Adhkar"
                },
                subtitle = when (selectedLanguage) {
                    "Hausa" -> "Kammala rānarka da kariya"
                    "Arabic" -> "حصن مسائك وراحتك"
                    "Yoruba" -> "Parí ọjọ́ rẹ pẹ̀lú ààbò"
                    "Igbo" -> "Mechie ụbọchị gị na nchebe"
                    "French" -> "Clôturez votre journée protégé"
                    else -> "End your day protected"
                },
                arrowColor = Color(0xFF2563EB),
                isDarkTheme = isDarkTheme,
                onClick = onEveningClick,
                modifier = Modifier.weight(1f)
            )

            // Card 2: Sallah & Masallaci
            QuickAccessCard(
                iconEmoji = "🕌",
                iconBg = Color(0xFF059669),
                pillText = if (selectedLanguage == "Hausa") "16 Addu'a" else "16 Du'a",
                pillBg = if (isDarkTheme) Color(0xFF0F3628) else Color(0xFFECFDF5),
                pillTextColor = if (isDarkTheme) Color(0xFFA7F3D0) else Color(0xFF047857),
                title = when (selectedLanguage) {
                    "Hausa" -> "Sallah & Masallaci"
                    "Arabic" -> "الصلاة والمسجد"
                    "Yoruba" -> "Sọláàti & Mọṣálasí"
                    "Igbo" -> "Ekpere & Ụlọ Alakụba"
                    "French" -> "Prière & Mosquée"
                    else -> "Prayer & Mosque"
                },
                subtitle = when (selectedLanguage) {
                    "Hausa" -> "Kula da alakar ka da Allah"
                    "Arabic" -> "صلتك الدائمة بربك"
                    "Yoruba" -> "Dabobo ajọṣepọ pẹlu Ọlọhun"
                    "Igbo" -> "Jikọọ onwe gị na Chineke"
                    "French" -> "Votre lien sacré avec Allah"
                    else -> "Keep your connection to Allah"
                },
                arrowColor = Color(0xFF059669),
                isDarkTheme = isDarkTheme,
                onClick = onPrayerClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2 of 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 3: 99 Names
            QuickAccessCard(
                iconEmoji = "⭐",
                iconBg = Color(0xFF8B5CF6),
                pillText = if (selectedLanguage == "Hausa") "4 Addu'a" else "99 Names",
                pillBg = if (isDarkTheme) Color(0xFF2E1A47) else Color(0xFFF5F3FF),
                pillTextColor = if (isDarkTheme) Color(0xFFDDD6FE) else Color(0xFF6D28D9),
                title = "99 Names",
                subtitle = "Asma'ul Husna",
                arrowColor = Color(0xFF8B5CF6),
                isDarkTheme = isDarkTheme,
                onClick = onAsmaulHusnaClick,
                modifier = Modifier.weight(1f)
            )

            // Card 4: Zikr Counter
            QuickAccessCard(
                iconEmoji = "📿",
                iconBg = Color(0xFF0D9488),
                pillText = if (selectedLanguage == "Hausa") "20 Addu'a" else "Tasbih",
                pillBg = if (isDarkTheme) Color(0xFF133935) else Color(0xFFF0FDFA),
                pillTextColor = if (isDarkTheme) Color(0xFF99F6E4) else Color(0xFF0F766E),
                title = when (selectedLanguage) {
                    "Hausa" -> "Zikr Counter"
                    "Arabic" -> "عداد التسبيح"
                    "French" -> "Compteur de Zikr"
                    else -> "Zikr Counter"
                },
                subtitle = when (selectedLanguage) {
                    "Hausa" -> "Manual Tasbih"
                    "Arabic" -> "تسبيح رقمي"
                    else -> "Manual Tasbih"
                },
                arrowColor = Color(0xFF0D9488),
                isDarkTheme = isDarkTheme,
                onClick = onTasbihClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickAccessCard(
    iconEmoji: String,
    iconBg: Color,
    pillText: String,
    pillBg: Color,
    pillTextColor: Color,
    title: String,
    subtitle: String,
    arrowColor: Color,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) Color(0xFF0D251F) else Color(0xFFF7FAF9)
    val cardBorder = if (isDarkTheme) Color(0xFF16473A) else Color(0xFFE4EDE7)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0A2B24)
    val textSecondary = if (isDarkTheme) Color(0xFFA7C7BD) else Color(0xFF5A756D)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Emoji Icon + Badge Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconBg,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = iconEmoji, fontSize = 18.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = pillBg
                ) {
                    Text(
                        text = pillText,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = pillTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = title,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Subtitle
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = arrowColor,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * "Ci gaba inda ka tsaya" (Continue where you left off) Card
 */
@Composable
fun ResumeReadingCard(
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDarkTheme) Color(0xFF0C2B22) else Color(0xFFEDF8F4)
    val cardBorder = if (isDarkTheme) Color(0xFF144D3D) else Color(0xFFC7EBDD)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0A2B24)
    val textSecondary = if (isDarkTheme) Color(0xFFA7C7BD) else Color(0xFF4A6B60)

    Surface(
        onClick = onContinueClick,
        shape = RoundedCornerShape(22.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("resume_reading_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left: Green Circle with Book Icon
            Surface(
                shape = CircleShape,
                color = Color(0xFF005C42),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Middle: Title, Category, Progress
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when (selectedLanguage) {
                        "Hausa" -> "Ci gaba inda ka tsaya"
                        "Arabic" -> "تابع من حيث توقفت"
                        "Yoruba" -> "Tẹsiwaju lati ibi ti o duro"
                        "Igbo" -> "Gaa n'ihu site na ebe ị kwụsịrị"
                        "French" -> "Reprendre votre lecture"
                        else -> "Continue where you left off"
                    },
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Morning Adhkar",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { 0.33f },
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xFF007A55),
                        trackColor = if (isDarkTheme) Color(0xFF134237) else Color(0xFFCFE8DE)
                    )
                    Text(
                        text = "33%",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                }

                Text(
                    text = "7 of 21 completed",
                    fontSize = 10.5.sp,
                    color = textSecondary
                )
            }

            // Right: Button "Ci gaba ➔"
            Surface(
                onClick = onContinueClick,
                shape = RoundedCornerShape(50),
                color = if (isDarkTheme) Color(0xFF165241) else Color(0xFFD4EDE2)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = when (selectedLanguage) {
                            "Hausa" -> "Ci gaba"
                            "Arabic" -> "متابعة"
                            "Yoruba" -> "Tẹsiwaju"
                            "Igbo" -> "Gaa n'ihu"
                            "French" -> "Continuer"
                            else -> "Continue"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF005C42)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF005C42),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Section "Karanta Na Yau" (Daily Verse / Quran Quote Panoramic Banner)
 */
@Composable
fun DailyQuranReflectionCard(
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onViewAllClick: () -> Unit,
    onReadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0A2B24)
    val accentGreen = if (isDarkTheme) Color(0xFF34D399) else Color(0xFF059669)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (selectedLanguage) {
                    "Hausa" -> "Karanta Na Yau"
                    "Arabic" -> "قراءة اليوم"
                    "Yoruba" -> "Kika Oni"
                    "Igbo" -> "Ọgụgụ Taa"
                    "French" -> "Lecture du Jour"
                    else -> "Daily Reading"
                },
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onViewAllClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (selectedLanguage) {
                        "Hausa" -> "Duk Alkaluman"
                        "Arabic" -> "جميع الآيات"
                        "Yoruba" -> "Gbogbo Àwọn Ẹsẹ"
                        "Igbo" -> "Akwụkwọ Nile"
                        "French" -> "Toutes les Lectures"
                        else -> "All Verses"
                    },
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentGreen
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentGreen,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // Panoramic Twilight Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B2139)),
            border = BorderStroke(1.dp, Color(0xFF1E3A5F)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onReadClick() }
                .testTag("daily_quran_reflection_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Background mosque silhouette under twilight sky with crescent
                Image(
                    painter = painterResource(id = R.drawable.img_home_hero_bg_1789824178609),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                // Deep gradient overlay
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.horizontalGradient(
                                0.0f to Color(0xF2091D33),
                                0.55f to Color(0xCC0D2845),
                                1.0f to Color(0x660F355E)
                            )
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left: Rounded Book Icon Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E40AF).copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, Color(0xFF3B82F6)),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CollectionsBookmark,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Middle Column: Quote + Citation + Dots
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "“Lallai, a cikin tunawa da Allah zuciya na samun kwanciyar hankali.”"
                                "Arabic" -> "“أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ”"
                                "French" -> "« En vérité, c'est par l'évocation d'Allah que les cœurs s'apaisent. »"
                                "Yoruba" -> "“Dájúdájú, pẹ̀lú ìrántí Olóhun ni àwọn ọkàn fi ń balẹ̀.”"
                                "Igbo" -> "“N'ezie, na ncheta Chineke ka obi na-enweta udo.”"
                                else -> "“Verily, in the remembrance of Allah do hearts find rest.”"
                            },
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 17.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "— Qur'ani 13:28",
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // 4 Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.4f))
                                )
                            }
                        }
                    }

                    // Right: White Pill Button "Karanta ➔"
                    Surface(
                        onClick = onReadClick,
                        shape = RoundedCornerShape(50),
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Bottom)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Karanta"
                                    "Arabic" -> "اقرأ"
                                    "French" -> "Lire"
                                    "Spanish" -> "Leer"
                                    "Yoruba" -> "Ka"
                                    "Igbo" -> "Gụọ"
                                    else -> "Read"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF064E3B),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
