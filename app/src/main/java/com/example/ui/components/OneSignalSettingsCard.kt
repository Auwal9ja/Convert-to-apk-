package com.example.ui.components

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.receiver.OneSignalHelper

@Composable
fun OneSignalSettingsCard(
    context: Context = LocalContext.current,
    selectedLanguage: String = "English",
    modifier: Modifier = Modifier
) {
    val isHausa = selectedLanguage.equals("Hausa", ignoreCase = true)
    val clipboardManager = LocalClipboardManager.current

    var currentAppId by remember { mutableStateOf(OneSignalHelper.getEffectiveAppId(context)) }
    var isSdkInit by remember { mutableStateOf(OneSignalHelper.isSdkInitialized()) }
    var subscriptionId by remember { mutableStateOf(OneSignalHelper.getSubscriptionId()) }
    var isOptedIn by remember { mutableStateOf(OneSignalHelper.isOptedIn()) }
    var showAppIdDialog by remember { mutableStateOf(false) }
    var inputAppId by remember { mutableStateOf(if (OneSignalHelper.isConfigured(context)) currentAppId else "") }
    var showGuide by remember { mutableStateOf(false) }

    val hasSystemNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Toast.makeText(context, if (isHausa) "An bada izinin sanarwa!" else "Notification permission granted!", Toast.LENGTH_SHORT).show()
            OneSignalHelper.requestPushPermission()
        } else {
            Toast.makeText(context, if (isHausa) "Ba a ba da izini ba." else "Notification permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
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
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = if (isHausa) "Sanarwar OneSignal (Push Notifications)" else "OneSignal Push Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHausa) "Sarrafa sanarwa da sakonnin zikiri kai tsaye" else "Receive live push reminders & global broadcasts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            // Configuration Status Row
            val isConfigured = OneSignalHelper.isConfigured(context)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isConfigured) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isConfigured) {
                                if (isHausa) "OneSignal App ID: An Saita ✓" else "OneSignal App ID: Configured ✓"
                            } else {
                                if (isHausa) "OneSignal App ID: Ba a saita ba tukuna ⚠️" else "OneSignal App ID: Not Configured ⚠️"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isConfigured) Color(0xFF1B5E20) else Color(0xFFE65100)
                        )
                        if (isConfigured) {
                            Text(
                                text = currentAppId,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showAppIdDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isConfigured) (if (isHausa) "Sauya" else "Change") else (if (isHausa) "Saita ID" else "Set ID"),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Subscription / Player ID Row
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isHausa) "Lambar Na'ura (Subscription ID / Player ID):" else "Device Subscription ID (Player ID):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (!subscriptionId.isNullOrBlank()) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(subscriptionId ?: ""))
                                    Toast.makeText(
                                        context,
                                        if (isHausa) "An kwafi Subscription ID!" else "Subscription ID copied to clipboard!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Player ID",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (!subscriptionId.isNullOrBlank()) {
                        Text(
                            text = subscriptionId ?: "",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            text = if (!isConfigured) {
                                if (isHausa) "Saita OneSignal App ID domin samun lambar na'ura." else "Set OneSignal App ID to register this device."
                            } else {
                                if (isHausa) "Ana haɗawa da OneSignal server..." else "Connecting to OneSignal server..."
                            },
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // System Permission & Opt-in toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isHausa) "Izinin Sanarwa (System Permission)" else "Notification Permission",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (hasSystemNotificationPermission) {
                            if (isHausa) "An ba da izini a waya ✓" else "Granted by system ✓"
                        } else {
                            if (isHausa) "Ba a ba da izini ba ⚠️" else "Disabled in system settings ⚠️"
                        },
                        fontSize = 11.sp,
                        color = if (hasSystemNotificationPermission) Color(0xFF2E7D32) else Color(0xFFC62828)
                    )
                }

                if (!hasSystemNotificationPermission) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                try {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = if (isHausa) "Kunna" else "Enable", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Test & Diagnostic Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        OneSignalHelper.sendTestLocalNotification(
                            context = context,
                            title = if (isHausa) "Noor Zikir - Gwajin Sanarwa" else "Noor Zikir - Notification Test",
                            message = if (isHausa) "Sanarwa na aiki lafiya! Wannan gwaji ne na tabbatar da sautin da fitowar sanarwa." else "Test notification received! Your device is fully ready for live reminders."
                        )
                        Toast.makeText(
                            context,
                            if (isHausa) "An tura sanarwar gwaji zuwa allon waya!" else "Test notification triggered! Check your status bar.",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHausa) "Gwada Sanarwa" else "Test Notification",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = {
                        subscriptionId = OneSignalHelper.getSubscriptionId()
                        isSdkInit = OneSignalHelper.isSdkInitialized()
                        isOptedIn = OneSignalHelper.isOptedIn()
                        currentAppId = OneSignalHelper.getEffectiveAppId(context)
                        Toast.makeText(context, if (isHausa) "An sabunta bayani!" else "Status refreshed!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Status")
                }
            }

            // Expandable Troubleshooting Guide
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGuide = !showGuide }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isHausa) "Yadda zaka saita OneSignal Dashboard (Koyarwa) ℹ️" else "OneSignal Dashboard Setup Checklist ℹ️",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (showGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = showGuide) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isHausa) "Matakan karɓar sanarwa daga OneSignal:" else "Why notifications might not arrive & how to fix:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHausa)
                                "1. Shigar da OneSignal App ID na ainihi a maɓallin 'Saita ID' da ke sama.\n" +
                                "2. A OneSignal Dashboard > Settings > Platforms > Google Android (FCM), saita Firebase Cloud Messaging (FCM Service Account JSON).\n" +
                                "3. A OneSignal Dashboard > Messages > New Push, tura saƙon zikiri. Zai sauka a duk wayoyin da suka sauke manhajar!"
                            else
                                "1. Enter your real OneSignal App ID using the 'Set ID' button above.\n" +
                                "2. In OneSignal Dashboard > Settings > Platforms > Google Android (FCM), upload your Firebase Service Account JSON (FCM V1).\n" +
                                "3. Ensure Notification Permission is enabled in Android.\n" +
                                "4. In OneSignal Dashboard > Messages > New Push, send your message to all subscribers.",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // App ID Input Dialog
    if (showAppIdDialog) {
        AlertDialog(
            onDismissRequest = { showAppIdDialog = false },
            title = {
                Text(
                    text = if (isHausa) "Shigar da OneSignal App ID" else "Configure OneSignal App ID",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isHausa)
                            "Sanya OneSignal App ID (misali: 12345678-abcd-1234-abcd-123456789abc) daga shafin OneSignal Dashboard na manhajarka:"
                        else
                            "Enter your OneSignal App ID (e.g., 12345678-abcd-1234-abcd-123456789abc) from your OneSignal dashboard settings:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = inputAppId,
                        onValueChange = { inputAppId = it },
                        placeholder = { Text("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = inputAppId.trim()
                        if (trimmed.length >= 16) {
                            OneSignalHelper.saveCustomAppId(context, trimmed)
                            currentAppId = trimmed
                            isSdkInit = OneSignalHelper.isSdkInitialized()
                            subscriptionId = OneSignalHelper.getSubscriptionId()
                            showAppIdDialog = false
                            Toast.makeText(
                                context,
                                if (isHausa) "An saita OneSignal App ID cikin nasara!" else "OneSignal App ID saved and initialized successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                if (isHausa) "Da fatan a shigar da ingantaccen OneSignal App ID." else "Please enter a valid OneSignal App ID (minimum 16 chars).",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text(if (isHausa) "Ajiye & Fara Aiki" else "Save & Initialize")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppIdDialog = false }) {
                    Text(if (isHausa) "Soke" else "Cancel")
                }
            }
        )
    }
}
