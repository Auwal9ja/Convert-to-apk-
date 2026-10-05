package com.example.ui.components

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.util.InAppUpdateManager
import com.example.util.UpdateState

/**
 * Clean, modern in-app update banner for the Home screen.
 */
@Composable
fun InAppUpdateBanner(
    updateState: UpdateState,
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goldAccent = if (isDarkTheme) Color(0xFFF3C244) else Color(0xFFB45309)
    val emeraldAccent = Color(0xFF10B981)

    AnimatedVisibility(
        visible = updateState !is UpdateState.Idle && updateState !is UpdateState.UpToDate,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        when (updateState) {
            is UpdateState.UpdateAvailable -> {
                val title = when (selectedLanguage) {
                    "Hausa" -> "Sabon Update Ya Samu!"
                    "Yoruba" -> "Ìgbàgbé Titun Wà!"
                    "Igbo" -> "Mmelite Ọhụrụ Dị!"
                    "Arabic" -> "تحديث جديد متوفر!"
                    "French" -> "Nouvelle mise à jour disponible !"
                    "Spanish" -> "¡Nueva actualización disponible!"
                    "Urdu" -> "نیا اپ ڈیٹ دستیاب ہے!"
                    "Chinese" -> "发现新版本！"
                    else -> "New Update Available!"
                }
                val sub = when (selectedLanguage) {
                    "Hausa" -> "Sabunta Zakiru domin samun sabbin fasaloli da inganta aikin manhaja."
                    "Yoruba" -> "Ṣe imudojuiwọn Zakiru fun awọn ẹya tuntun ati iriri to dara julọ."
                    "Igbo" -> "Melite Zakiru maka atụmatụ ọhụrụ na ahụmịhe ka mma."
                    "Arabic" -> "قم بتحديث ذاكر المسلم للحصول على ميزات جديدة وأداء أفضل."
                    "French" -> "Mettez à jour Zakiru pour profiter des nouvelles fonctionnalités."
                    "Spanish" -> "Actualiza Zakiru para disfrutar de nuevas funciones y mejoras."
                    "Urdu" -> "نئی خصوصیات اور بہتر تجربے کے لیے ذاکر اپ ڈیٹ کریں۔"
                    "Chinese" -> "更新 Zakiru 以体验最新功能与性能优化。"
                    else -> "Update Zakiru to get the latest features and performance improvements."
                }
                val actionText = when (selectedLanguage) {
                    "Hausa" -> "Sabunta Yanzu"
                    "Yoruba" -> "Ṣe Imudojuiwọn"
                    "Igbo" -> "Melite Ugbu a"
                    "Arabic" -> "تحديث الآن"
                    "French" -> "Mettre à jour"
                    "Spanish" -> "Actualizar ahora"
                    "Urdu" -> "ابھی اپ ڈیٹ کریں"
                    "Chinese" -> "立即更新"
                    else -> "Update Now"
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF0D251D) else Color(0xFFE8F6F0)
                    ),
                    border = BorderStroke(1.2.dp, goldAccent.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    goldAccent.copy(alpha = 0.3f),
                                                    emeraldAccent.copy(alpha = 0.2f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SystemUpdate,
                                        contentDescription = null,
                                        tint = goldAccent,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = title,
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDarkTheme) Color.White else Color(0xFF064E3B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isDarkTheme) Color(0xFF1E3A2F) else Color(0xFFC7EADB)
                                        ) {
                                            Text(
                                                text = "Current: v${BuildConfig.VERSION_NAME}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isDarkTheme) Color(0xFFA7F3D0) else Color(0xFF047857),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = goldAccent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "New: Build ${updateState.availableVersionCode}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = goldAccent,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = if (isDarkTheme) Color(0xFFAEC4BE) else Color(0xFF557065),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = sub,
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp,
                            color = if (isDarkTheme) Color(0xFFD1E7DD) else Color(0xFF235542)
                        )

                        Button(
                            onClick = onStartUpdate,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = goldAccent,
                                contentColor = Color(0xFF1E1702)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = actionText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            is UpdateState.Downloading -> {
                val downloadingTitle = when (selectedLanguage) {
                    "Hausa" -> "Ana saukar da sabon update... (${updateState.percent}%)"
                    "Yoruba" -> "N ṣe igbasilẹ imudojuiwọn... (${updateState.percent}%)"
                    "Igbo" -> "Na-ebudata mmelite... (${updateState.percent}%)"
                    "Arabic" -> "جارٍ تنزيل التحديث... (${updateState.percent}%)"
                    "French" -> "Téléchargement de la mise à jour... (${updateState.percent}%)"
                    "Spanish" -> "Descargando actualización... (${updateState.percent}%)"
                    "Urdu" -> "اپ ڈیٹ ڈاؤن لوڈ ہو رہا ہے... (${updateState.percent}%)"
                    "Chinese" -> "正在下载更新... (${updateState.percent}%)"
                    else -> "Downloading update... (${updateState.percent}%)"
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF0F261E) else Color(0xFFE8F5E9)
                    ),
                    border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
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
                            CircularProgressIndicator(
                                progress = { updateState.percent / 100f },
                                modifier = Modifier.size(26.dp),
                                color = goldAccent,
                                strokeWidth = 3.dp,
                                trackColor = if (isDarkTheme) Color(0xFF1B3D34) else Color(0xFFC8E6C9)
                            )
                            Text(
                                text = downloadingTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else Color(0xFF1B3D2F),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        LinearProgressIndicator(
                            progress = { updateState.percent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = goldAccent,
                            trackColor = if (isDarkTheme) Color(0xFF163B30) else Color(0xFFC8E6C9)
                        )
                    }
                }
            }

            is UpdateState.Downloaded -> {
                val downloadedTitle = when (selectedLanguage) {
                    "Hausa" -> "🎉 An sauke sabon update!"
                    "Yoruba" -> "🎉 A ti ṣe igbasilẹ imudojuiwọn!"
                    "Igbo" -> "🎉 Ebudatala mmelite ọhụrụ!"
                    "Arabic" -> "🎉 تم تنزيل التحديث بنجاح!"
                    "French" -> "🎉 Mise à jour téléchargée !"
                    "Spanish" -> "🎉 ¡Actualización descargada!"
                    "Urdu" -> "🎉 نیا اپ ڈیٹ ڈاؤن لوڈ ہو گیا!"
                    "Chinese" -> "🎉 新版本已下载完成！"
                    else -> "🎉 Update ready to install!"
                }
                val downloadedSub = when (selectedLanguage) {
                    "Hausa" -> "Danna 'Sake Kunna' domin kammala sabuntawa."
                    "Yoruba" -> "Tẹ 'Tun Bẹrẹ' lati pari imudojuiwọn."
                    "Igbo" -> "Kpatụ 'Malitegharịa' iji wụnye mmelite ahụ."
                    "Arabic" -> "انقر على 'إعادة التشغيل' لإكمال التثبيت."
                    "French" -> "Appuyez sur 'Redémarrer' pour appliquer la mise à jour."
                    "Spanish" -> "Toca 'Reiniciar' para aplicar la actualización."
                    "Urdu" -> "اپ ڈیٹ مکمل کرنے کے لیے 'دوبارہ شروع کریں' پر ٹیپ کریں۔"
                    "Chinese" -> "点击“重启应用”以完成更新安装。"
                    else -> "Tap 'Restart' to finish installing the update."
                }
                val restartButtonText = when (selectedLanguage) {
                    "Hausa" -> "Sake Kunna & Sabunta"
                    "Yoruba" -> "Tun Bẹrẹ & Ṣe Imudojuiwọn"
                    "Igbo" -> "Malitegharịa & Melite"
                    "Arabic" -> "إعادة التشغيل والتثبيت"
                    "French" -> "Redémarrer et appliquer"
                    "Spanish" -> "Reiniciar e instalar"
                    "Urdu" -> "دوبارہ شروع کریں اور انسٹال کریں"
                    "Chinese" -> "重启并安装"
                    else -> "Restart & Install"
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF0A3326) else Color(0xFFDFF0E8)
                    ),
                    border = BorderStroke(1.5.dp, Color(0xFF32A873)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
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
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF32A873),
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = downloadedTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color.White else Color(0xFF0F3D2A)
                                )
                                Text(
                                    text = downloadedSub,
                                    fontSize = 12.sp,
                                    color = if (isDarkTheme) Color(0xFFD5E8DF) else Color(0xFF28543E)
                                )
                            }
                        }

                        Button(
                            onClick = onCompleteUpdate,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF32A873),
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = restartButtonText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            else -> {
                // Idle or Checking or UpToDate
            }
        }
    }
}

/**
 * Enhanced Settings In-App Update Tile with version badge and live progress bar.
 */
@Composable
fun SettingsInAppUpdateTile(
    updateState: UpdateState,
    selectedLanguage: String,
    isDarkTheme: Boolean,
    onCheckForUpdates: () -> Unit,
    onStartUpdate: () -> Unit,
    onCompleteUpdate: () -> Unit
) {
    val goldAccent = if (isDarkTheme) Color(0xFFF3C244) else Color(0xFFB45309)
    val emeraldAccent = Color(0xFF10B981)
    val cardBg = if (isDarkTheme) Color(0xFF112922) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val cardBorder = if (isDarkTheme) Color(0xFF1B4036) else MaterialTheme.colorScheme.outlineVariant

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBg,
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        goldAccent.copy(alpha = 0.25f),
                                        emeraldAccent.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SystemUpdate,
                            contentDescription = "In-App Updates",
                            tint = goldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val headerText = when (selectedLanguage) {
                                "Hausa" -> "Sabunta Manhaja"
                                "Yoruba" -> "Imudojuiwọn Manhaja"
                                "Igbo" -> "Mmelite Ngwa"
                                "Arabic" -> "تحديث التطبيق المباشر"
                                "French" -> "Mise à jour intégrée"
                                "Spanish" -> "Actualización de la app"
                                "Urdu" -> "ایپ اپ ڈیٹ"
                                "Chinese" -> "应用内更新"
                                else -> "In-App Update"
                            }
                            Text(
                                text = headerText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface
                            )

                            // App version badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDarkTheme) Color(0xFF1E3A2F) else Color(0xFFE2EFE9)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkTheme) Color(0xFFA7F3D0) else Color(0xFF047857),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }

                        val statusSubtitle = when (updateState) {
                            is UpdateState.Checking -> when (selectedLanguage) {
                                "Hausa" -> "Ana duba sabon tsari..."
                                "Yoruba" -> "N ṣayẹwo imudojuiwọn..."
                                "Igbo" -> "Na-elele mmelite..."
                                "Arabic" -> "جارٍ التحقق من التحديثات..."
                                "French" -> "Vérification des mises à jour..."
                                "Spanish" -> "Buscando actualizaciones..."
                                "Urdu" -> "اپ ڈیٹس چیک کیے جا رہے ہیں..."
                                "Chinese" -> "正在检查更新..."
                                else -> "Checking for updates..."
                            }
                            is UpdateState.UpdateAvailable -> when (selectedLanguage) {
                                "Hausa" -> "Akwai sabon tsari (Build ${updateState.availableVersionCode})"
                                "Yoruba" -> "Imudojuiwọn wa (Build ${updateState.availableVersionCode})"
                                "Igbo" -> "Mmelite dị (Build ${updateState.availableVersionCode})"
                                "Arabic" -> "تحديث متاح (الإصدار ${updateState.availableVersionCode})"
                                "French" -> "Mise à jour disponible (${updateState.availableVersionCode})"
                                "Spanish" -> "Actualización disponible (${updateState.availableVersionCode})"
                                "Urdu" -> "نیا اپ ڈیٹ دستیاب ہے (${updateState.availableVersionCode})"
                                "Chinese" -> "发现新版本 (Build ${updateState.availableVersionCode})"
                                else -> "Update available (Build ${updateState.availableVersionCode})"
                            }
                            is UpdateState.Downloading -> when (selectedLanguage) {
                                "Hausa" -> "Ana saukewa... ${updateState.percent}%"
                                "Yoruba" -> "N ṣe igbasilẹ... ${updateState.percent}%"
                                "Igbo" -> "Na-ebudata... ${updateState.percent}%"
                                "Arabic" -> "جارٍ التنزيل... ${updateState.percent}%"
                                "French" -> "Téléchargement... ${updateState.percent}%"
                                "Spanish" -> "Descargando... ${updateState.percent}%"
                                "Urdu" -> "ڈاؤن لوڈ ہو رہا ہے... ${updateState.percent}%"
                                "Chinese" -> "正在下载... ${updateState.percent}%"
                                else -> "Downloading... ${updateState.percent}%"
                            }
                            is UpdateState.Downloaded -> when (selectedLanguage) {
                                "Hausa" -> "An sauke! Danna don sake kunna manhaja"
                                "Yoruba" -> "A ti gba wọle! Tẹ lati fi sii"
                                "Igbo" -> "Ebudatala! Kpatụ ka ị wụnye"
                                "Arabic" -> "جاهز للتثبيت! انقر للتحديث"
                                "French" -> "Prêt ! Appuyez pour installer"
                                "Spanish" -> "¡Listo! Toca para reiniciar"
                                "Urdu" -> "تیار ہے! انسٹال کرنے کے لیے ٹیپ کریں"
                                "Chinese" -> "已就绪！点击重启安装"
                                else -> "Downloaded! Tap to restart"
                            }
                            is UpdateState.UpToDate -> when (selectedLanguage) {
                                "Hausa" -> "Manhajar na kan sabon tsari ✓"
                                "Yoruba" -> "Manhaja ti wa ni imudojuiwọn ✓"
                                "Igbo" -> "Ngwa dị ọhụrụ ✓"
                                "Arabic" -> "التطبيق محدث إلى آخر إصدار ✓"
                                "French" -> "L'application est à jour ✓"
                                "Spanish" -> "La aplicación está actualizada ✓"
                                "Urdu" -> "ایپ اپ ٹو ڈیٹ ہے ✓"
                                "Chinese" -> "已是最新版本 ✓"
                                else -> "App is up to date ✓"
                            }
                            is UpdateState.Error -> updateState.message
                            UpdateState.Idle -> when (selectedLanguage) {
                                "Hausa" -> "Duba ko akwai sabon tsari"
                                "Yoruba" -> "Ṣayẹwo imudojuiwọn"
                                "Igbo" -> "Lelee mmelite"
                                "Arabic" -> "التحقق من التحديثات"
                                "French" -> "Vérifier les mises à jour"
                                "Spanish" -> "Comprobar actualizaciones"
                                "Urdu" -> "اپ ڈیٹس چیک کریں"
                                "Chinese" -> "检查更新"
                                else -> "Check for updates"
                            }
                        }

                        Text(
                            text = statusSubtitle,
                            fontSize = 11.5.sp,
                            color = when (updateState) {
                                is UpdateState.Downloaded -> Color(0xFF32A873)
                                is UpdateState.UpdateAvailable -> goldAccent
                                is UpdateState.UpToDate -> Color(0xFF32A873)
                                else -> if (isDarkTheme) Color(0xFFAEC4BE) else MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                when (updateState) {
                    is UpdateState.Checking -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = goldAccent
                        )
                    }
                    is UpdateState.UpdateAvailable -> {
                        Button(
                            onClick = onStartUpdate,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = goldAccent,
                                contentColor = Color(0xFF1E1702)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Sabunta" else "Update",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    is UpdateState.Downloaded -> {
                        Button(
                            onClick = onCompleteUpdate,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF32A873),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Sake Kunna" else "Restart",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    is UpdateState.Downloading -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = goldAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${updateState.percent}%",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldAccent,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    else -> {
                        OutlinedButton(
                            onClick = onCheckForUpdates,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.7f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = goldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Duba"
                                    "Yoruba" -> "Ṣayẹwo"
                                    "Igbo" -> "Lelee"
                                    "Arabic" -> "فحص"
                                    "French" -> "Vérifier"
                                    "Spanish" -> "Buscar"
                                    "Urdu" -> "چیک کریں"
                                    "Chinese" -> "检查"
                                    else -> "Check"
                                },
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = goldAccent
                            )
                        }
                    }
                }
            }

            // Live progress bar when downloading
            if (updateState is UpdateState.Downloading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { updateState.percent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = goldAccent,
                        trackColor = if (isDarkTheme) Color(0xFF1B4036) else Color(0xFFC7EADB)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Ana saukewa daga Google Play..." else "Downloading from Google Play...",
                            fontSize = 10.5.sp,
                            color = if (isDarkTheme) Color(0xFFAEC4BE) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${updateState.percent}%",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = goldAccent
                        )
                    }
                }
            }
        }
    }
}
