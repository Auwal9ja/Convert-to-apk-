package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.AppLocalizer
import com.example.data.local.DuaDatabase
import com.example.data.local.DuaEntity
import com.example.data.local.DuaReferenceLocalization
import com.example.data.local.DuaTranslationLocalization
import com.example.receiver.PrayerWidgetProvider
import com.example.util.WidgetContentManager
import com.example.util.WidgetItem

/**
 * Full-featured Dialog for browsing and selecting ANY Dua, Azkar, or Surah
 * from the entire database to display on the Home Screen Widget.
 */
@Composable
fun SelectDuaForWidgetDialog(
    selectedLanguage: String,
    onItemSelected: (WidgetItem) -> Unit = {},
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val db = remember { DuaDatabase.getDatabase(context) }
    val allDuas by db.duaDao().getAllDuas().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }
    val currentSelectedId = remember { WidgetContentManager.getSelectedCustomId(context) }

    // Categories list with count
    val categories = remember(allDuas) {
        listOf(null) + allDuas.map { it.category }.distinct()
    }

    // Filtered Duas based on search query and category tab
    val filteredDuas = remember(allDuas, searchQuery, selectedCategoryFilter) {
        allDuas.filter { dua ->
            val matchesCategory = selectedCategoryFilter == null || dua.category == selectedCategoryFilter
            val q = searchQuery.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                    dua.title.lowercase().contains(q) ||
                    dua.arabic.contains(q) ||
                    dua.translation.lowercase().contains(q) ||
                    dua.translationHausa.lowercase().contains(q) ||
                    dua.category.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = if (isDark) Color(0xFF071F28) else Color(0xFFF8FAFC),
            border = BorderStroke(1.2.dp, if (isDark) Color(0xFF135B6E) else Color(0xFFCBD5E1)),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Widgets,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (selectedLanguage == "Hausa") "Zaɓi Addu'a ko Azkar na Widget" else "Select Dua / Azkar for Widget",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF0F261E)
                            )
                            Text(
                                text = if (selectedLanguage == "Hausa") "Dukkan addu'o'i da azkar (${allDuas.size})" else "All Duas & Azkar (${allDuas.size} items)",
                                fontSize = 11.5.sp,
                                color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                        )
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = if (selectedLanguage == "Hausa") "Bincika addu'a, kalma, ko zikiri..." else "Search Dua, keyword, or Arabic...",
                            fontSize = 12.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = if (isDark) Color(0xFF185465) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_dua_for_widget")
                )

                // Category Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        val label = if (cat == null) {
                            if (selectedLanguage == "Hausa") "Duka (${allDuas.size})" else "All (${allDuas.size})"
                        } else {
                            AppLocalizer.getCategoryName(cat, selectedLanguage)
                        }

                        Surface(
                            onClick = { selectedCategoryFilter = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) {
                                if (isDark) Color(0xFF0D423A) else Color(0xFFD1FAE5)
                            } else {
                                if (isDark) Color(0xFF0A2934) else Color(0xFFE2E8F0)
                            },
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Color(0xFF10B981) else if (isDark) Color(0xFF164756) else Color(0xFFCBD5E1)
                            )
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) {
                                    if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46)
                                } else {
                                    if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Results count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedLanguage == "Hausa") "${filteredDuas.size} Addu'o'i da aka samu:" else "Found ${filteredDuas.size} items:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )

                    Text(
                        text = if (selectedLanguage == "Hausa") "Danna don saitawa a Widget 📱" else "Tap to set on Widget 📱",
                        fontSize = 11.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                    )
                }

                // Scrollable List of Duas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredDuas, key = { it.id }) { dua ->
                        val isCurrentlySelected = currentSelectedId == "db_${dua.id}"
                        DuaWidgetPickerCard(
                            dua = dua,
                            isSelected = isCurrentlySelected,
                            selectedLanguage = selectedLanguage,
                            isDark = isDark,
                            onSelect = {
                                val item = WidgetContentManager.setCustomDuaEntity(context, dua, selectedLanguage)
                                PrayerWidgetProvider.updateAllWidgets(context)
                                Toast.makeText(
                                    context,
                                    if (selectedLanguage == "Hausa") "An saita \"${dua.title}\" a widget ɗin allon waya! 📱" else "Set \"${dua.title}\" on Home Screen Widget! 📱",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onItemSelected(item)
                                onDismissRequest()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual Card showing Dua details and 1-tap select button.
 */
@Composable
fun DuaWidgetPickerCard(
    dua: DuaEntity,
    isSelected: Boolean,
    selectedLanguage: String,
    isDark: Boolean,
    onSelect: () -> Unit
) {
    val displayTitle = AppLocalizer.getDuaTitle(dua.id, dua.title, selectedLanguage)
    val translatedText = DuaTranslationLocalization.getLocalizedTranslation(
        dua.id,
        selectedLanguage,
        dua.translation,
        dua.translationHausa,
        dua.translationYoruba,
        dua.translationIgbo
    )
    val localizedRef = DuaReferenceLocalization.getLocalizedReference(dua.id, selectedLanguage) ?: dua.reference

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) {
            if (isDark) Color(0xFF0C3831) else Color(0xFFE6F8F0)
        } else {
            if (isDark) Color(0xFF092530) else Color.White
        },
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) Color(0xFF10B981) else if (isDark) Color(0xFF144755) else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dua_picker_item_${dua.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Category & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isDark) Color(0xFF0E3844) else Color(0xFFE0F2FE)
                ) {
                    Text(
                        text = AppLocalizer.getCategoryName(dua.category, selectedLanguage),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                    )
                }

                if (isSelected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (selectedLanguage == "Hausa") "Yana kan Widget ✓" else "Active on Widget ✓",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }

            // Title
            Text(
                text = displayTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF0F261E),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Arabic Snippet
            Text(
                text = dua.arabic,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDE68A),
                textAlign = TextAlign.Right,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            // Translation Snippet
            Text(
                text = translatedText,
                fontSize = 11.sp,
                color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Reference & Select prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (localizedRef.isNotBlank()) "★ $localizedRef" else "★ Hisnul Muslim",
                    fontSize = 9.5.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFF059669) else if (isDark) Color(0xFF124350) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFF10B981) else Color(0xFF236A7D))
                ) {
                    Text(
                        text = if (isSelected) {
                            if (selectedLanguage == "Hausa") "Zaɓaɓɓe ✓" else "Selected ✓"
                        } else {
                            if (selectedLanguage == "Hausa") "Zaɓa a Widget ➔" else "Set on Widget ➔"
                        },
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else if (isDark) Color(0xFFA7F3D0) else Color(0xFF065F46),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
