package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.billing.BillingConstants
import com.example.billing.BillingManager
import com.example.billing.PremiumPlan
import com.example.ui.screens.openPrivacyPolicy

@Composable
fun SubscriptionDialog(
    selectedLanguage: String,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val billingManager = remember { BillingManager.getInstance(context) }

    val isAdsRemoved by billingManager.isAdsRemoved.collectAsStateWithLifecycle()
    val isConnecting by billingManager.isConnecting.collectAsStateWithLifecycle()
    val availablePlans by billingManager.availablePlans.collectAsStateWithLifecycle()
    val statusMessage by billingManager.billingStatusMessage.collectAsStateWithLifecycle()

    var selectedPlanId by remember { mutableStateOf<String?>(null) }
    var isRestoring by remember { mutableStateOf(false) }

    // Auto-select yearly or first plan when plans load
    LaunchedEffect(availablePlans) {
        if (selectedPlanId == null && availablePlans.isNotEmpty()) {
            val yearly = availablePlans.find { it.planKey.contains("yearly", ignoreCase = true) }
            selectedPlanId = yearly?.planKey ?: availablePlans.first().planKey
        }
    }

    // Refresh products on open
    LaunchedEffect(Unit) {
        billingManager.startBillingConnection()
    }

    // Show toast on status message update
    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            billingManager.clearStatusMessage()
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .testTag("subscription_dialog_card"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. HEADER WITH GOLDEN GRADIENT
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F5132), // Deep Islamic Emerald
                                    Color(0xFF198754),
                                    Color(0xFFB8860B)  // Dark Golden
                                )
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Crown Badge
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD4AF37).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAdsRemoved) Icons.Default.Verified else Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isAdsRemoved) {
                                when (selectedLanguage) {
                                    "Hausa" -> "🎉 Kana da Noor Premium!"
                                    "Yoruba" -> "🎉 O ni Noor Premium!"
                                    "Igbo" -> "🎉 Ị nwere Noor Premium!"
                                    else -> "🎉 Noor Premium Active!"
                                }
                            } else {
                                when (selectedLanguage) {
                                    "Hausa" -> "Cire Tallace-tallace (Noor Premium)"
                                    "Yoruba" -> "Yọ Àwọn Ìpolówó Kúrò"
                                    "Igbo" -> "Wepụ Mgbasa Ozi Niile"
                                    else -> "Remove All Ads & Support App"
                                }
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isAdsRemoved) {
                                when (selectedLanguage) {
                                    "Hausa" -> "Dukkan tallace-tallace an cire su a manhajarka. Muna godiya da gudunmawarka!"
                                    "Yoruba" -> "A ti yọ gbogbo ipolowo kuro. A dupẹ fun atilẹyin rẹ!"
                                    "Igbo" -> "E wepụla mgbasa ozi niile. Daalụ maka nkwado gị!"
                                    else -> "All ads are completely removed. Thank you for supporting this project!"
                                }
                            } else {
                                when (selectedLanguage) {
                                    "Hausa" -> "Kariyar zikiri cikin cikakkiyar natsuwa ba tare da wani katsewa na talla ba"
                                    "Yoruba" -> "Gbadun kika azkar laisi idalọwọduro ipolowo kankan"
                                    "Igbo" -> "Gụọ azkar na ekpere niile n'udo na-enweghị nkwụsị mgbasa ozi"
                                    else -> "Enjoy an uninterrupted spiritual journey with 100% ad-free experience"
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 2. CONTENT BODY
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isAdsRemoved) {
                        // ACTIVE PREMIUM CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = when (selectedLanguage) {
                                            "Hausa" -> "Manhaja Ad-Free ce 100%"
                                            "Yoruba" -> "100% Laisi Ipolowo"
                                            "Igbo" -> "100% Enweghị Mgbasa Ozi"
                                            else -> "100% Ad-Free Experience"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = when (selectedLanguage) {
                                        "Hausa" -> "Banners, bidiyoyi da hotunan talla ba za su sake fitowa ba a ko ina a cikin wannan manhaja."
                                        "Yoruba" -> "Gbogbo ipolowo ti wa ni piparẹ lori ẹrọ rẹ."
                                        "Igbo" -> "A gaghịzi egosipụta mgbasa ozi ọ bụla na ngwa gị."
                                        else -> "Banner, interstitial, and rewarded ads are permanently turned off on your account."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Button(
                                    onClick = {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions")).apply {
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            }
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Google Play Subscriptions", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors()
                                ) {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = when (selectedLanguage) {
                                            "Hausa" -> "Sarrafa Biyan Kuɗi a Google Play"
                                            "Yoruba" -> "Ṣakoso Ìforúkọsílẹ̀ lori Google Play"
                                            "Igbo" -> "Jikwaa Ndebanye aha na Google Play"
                                            else -> "Manage Subscription in Play Store"
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // BENEFITS LIST
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            BenefitRow(
                                icon = Icons.Default.Block,
                                title = when (selectedLanguage) {
                                    "Hausa" -> "Babu Wani Talla (100% Ad-Free)"
                                    "Yoruba" -> "Kò Sí Ìpolówó Kankan"
                                    "Igbo" -> "Enweghị Mgbasa Ozi Ọ Bụla"
                                    else -> "100% Ad-Free Experience"
                                },
                                description = when (selectedLanguage) {
                                    "Hausa" -> "Karatun Azkar, Salloli da Addu'o'i cikin natsuwa ba tare da wani talla ba."
                                    "Yoruba" -> "Ka gbogbo azkar ati adua laisi ipolowo tabi idalọwọduro kankan."
                                    "Igbo" -> "Gụọ azkar na ekpere niile n'udo na-enweghị mgbasa ozi ọ bụla."
                                    else -> "Zero banners, popups or interstitial interruptions throughout the app."
                                }
                            )

                            BenefitRow(
                                icon = Icons.Default.Speed,
                                title = when (selectedLanguage) {
                                    "Hausa" -> "Sauri & Sauƙin Aiki"
                                    "Yoruba" -> "Iyara & Irọrun"
                                    "Igbo" -> "Ọsọ & Ọrụ dị mfe"
                                    else -> "Faster Loading & Battery Saver"
                                },
                                description = when (selectedLanguage) {
                                    "Hausa" -> "Babu jinkirin saukar da tallace-tallace, yana kiyaye caji da data."
                                    "Yoruba" -> "Fi batiri ati data pamọ nitori ko si fifuye ipolowo."
                                    "Igbo" -> "Chekwaa batrị na data ebe ọ bụ na enweghị mgbasa ozi a na-ebugo."
                                    else -> "Smoother UI navigation, saves mobile internet data and device battery."
                                }
                            )

                            BenefitRow(
                                icon = Icons.Default.VolunteerActivism,
                                title = when (selectedLanguage) {
                                    "Hausa" -> "Sadaqah Jariyah & Bunkasawa"
                                    "Yoruba" -> "Sadaqah Jariyah & Atilẹyin"
                                    "Igbo" -> "Sadaqah Jariyah & Nkwado"
                                    else -> "Support Islamic Project Development"
                                },
                                description = when (selectedLanguage) {
                                    "Hausa" -> "Gudunmawarka na taimakawa wajen ci gaba da kula da wannan manhaja."
                                    "Yoruba" -> "Atilẹyin rẹ n ran wa lọwọ lati tẹsiwaju idagbasoke awọn ohun elo ẹsin."
                                    "Igbo" -> "Nkwado gị na-enyere aka ịnọgide na-arụ ọrụ na ngwa a na-akwụghị ụgwọ."
                                    else -> "Empower continuous updates, translations, and Islamic learning features."
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // SUBSCRIPTION TIERS (Weekly, Monthly, Yearly)
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "Zaɓi Tsarin da Kake So:"
                                "Yoruba" -> "Yan Eto ti O Fẹ:"
                                "Igbo" -> "Họrọ Atụmatụ Ị Chọrọ:"
                                else -> "Select Your Subscription Plan:"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (availablePlans.isEmpty()) {
                            // Fallback preview while connecting to Google Play Store
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                DummyPlanCard(
                                    title = when (selectedLanguage) {
                                        "Hausa" -> "Biyan Mako (Weekly)"
                                        "Yoruba" -> "Ìforúkọsílẹ̀ Ọ̀sẹ̀"
                                        "Igbo" -> "Ndebanye aha Kwa Izu"
                                        else -> "Weekly Subscription"
                                    },
                                    subtitle = when (selectedLanguage) {
                                        "Hausa" -> "Biyan kuɗi kowane mako • Gwaji mai sauƙi"
                                        "Yoruba" -> "Biya ni gbogbo ọsẹ • Idanwo irọrun"
                                        "Igbo" -> "Kwụọ ụgwọ kwa izu • Ule dị mfe"
                                        else -> "Billed weekly • Flexible short-term plan"
                                    },
                                    badge = "Flexible",
                                    price = if (isConnecting) "Connecting..." else "Available on Play Store",
                                    isSelected = selectedPlanId?.contains("weekly") == true,
                                    onClick = {
                                        billingManager.startBillingConnection()
                                    }
                                )

                                DummyPlanCard(
                                    title = when (selectedLanguage) {
                                        "Hausa" -> "Biyan Wata-wata (Monthly)"
                                        "Yoruba" -> "Ìforúkọsílẹ̀ Oṣooṣù"
                                        "Igbo" -> "Ndebanye aha Kwa Ọnwa"
                                        else -> "Monthly Subscription"
                                    },
                                    subtitle = when (selectedLanguage) {
                                        "Hausa" -> "Biyan kuɗi kowane wata • Za ka iya soke shi a ko yaushe"
                                        "Yoruba" -> "Biya ni gbogbo oṣu • Fagilee nigbakugba"
                                        "Igbo" -> "Kwụọ ụgwọ kwa ọnwa • Kagbuo oge ọ bụla"
                                        else -> "Billed monthly • Cancel anytime in Google Play"
                                    },
                                    badge = "Popular",
                                    price = if (isConnecting) "Connecting..." else "Available on Play Store",
                                    isSelected = selectedPlanId?.contains("monthly") == true,
                                    onClick = {
                                        billingManager.startBillingConnection()
                                    }
                                )

                                DummyPlanCard(
                                    title = when (selectedLanguage) {
                                        "Hausa" -> "Biyan Shekara (Yearly)"
                                        "Yoruba" -> "Ìforúkọsílẹ̀ Ọdọọdún"
                                        "Igbo" -> "Ndebanye aha Kwa Afọ"
                                        else -> "Yearly Subscription"
                                    },
                                    subtitle = when (selectedLanguage) {
                                        "Hausa" -> "Mafi arha • Biyan shekara guda cif (Rage 45%)"
                                        "Yoruba" -> "Ẹto ti o dara julọ fun gbogbo ọdun"
                                        "Igbo" -> "Nchekwa kacha mma maka afọ zuru oke"
                                        else -> "Best value • Full year ad-free access (Save 45%)"
                                    },
                                    badge = "★ BEST VALUE (SAVE 45%)",
                                    price = if (isConnecting) "Connecting..." else "Available on Play Store",
                                    isSelected = selectedPlanId == null || selectedPlanId?.contains("yearly") == true,
                                    onClick = {
                                        billingManager.startBillingConnection()
                                    }
                                )
                            }
                        } else {
                            // REAL GOOGLE PLAY STORE SUBSCRIPTION PRODUCTS
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                availablePlans.forEach { plan ->
                                    val isSelected = plan.planKey == selectedPlanId
                                    PlanCard(
                                        plan = plan,
                                        isSelected = isSelected,
                                        onClick = { selectedPlanId = plan.planKey }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // PRIMARY SUBSCRIBE BUTTON
                        Button(
                            onClick = {
                                val plan = availablePlans.find { it.planKey == selectedPlanId }
                                    ?: availablePlans.firstOrNull()

                                if (plan != null && activity != null) {
                                    val result = billingManager.launchPurchaseFlow(activity, plan)
                                    if (result.responseCode != com.android.billingclient.api.BillingClient.BillingResponseCode.OK) {
                                        Toast.makeText(context, result.debugMessage.ifBlank { "Could not launch Google Play Billing." }, Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    billingManager.startBillingConnection()
                                    Toast.makeText(
                                        context,
                                        if (selectedLanguage == "Hausa") "Ana haɗawa da Google Play... Don Allah sake dannawa." else "Connecting to Google Play... Please tap again in a moment.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("subscribe_now_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0F5132) // Emerald Green
                            )
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Ci gaba da Biya / Subscribe"
                                    "Yoruba" -> "Tẹsiwaju lati Sanwo"
                                    "Igbo" -> "Gaa n'ihu ịkwụ ụgwọ"
                                    else -> "Continue to Subscribe"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // RESTORE PURCHASES & LEGAL LINKS
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TextButton(
                            onClick = {
                                isRestoring = true
                                billingManager.restorePurchases { success, msg ->
                                    isRestoring = false
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            },
                            enabled = !isRestoring
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRestoring) {
                                    if (selectedLanguage == "Hausa") "Ana duba Google Play..." else "Checking Play Store..."
                                } else {
                                    when (selectedLanguage) {
                                        "Hausa" -> "Dawo da Tsohon Biyan Kuɗi (Restore Purchases)"
                                        "Yoruba" -> "Mu Ìsanwó Àtijọ́ Padà"
                                        "Igbo" -> "Weghachi Ịkwụ Ụgwọ Mbụ"
                                        else -> "Restore Existing Purchases"
                                    }
                                },
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = when (selectedLanguage) {
                                    "Hausa" -> "Manufar Tsare Sirri"
                                    "Yoruba" -> "Eto Aṣiri"
                                    "Igbo" -> "Iwu Nzuzo"
                                    else -> "Privacy Policy"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable { openPrivacyPolicy(context) }
                            )
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Google Play Terms",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/about/play-terms/")).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                        context.startActivity(intent)
                                    } catch (_: Exception) {}
                                }
                            )
                        }
                    }

                    // CLOSE BUTTON
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (selectedLanguage) {
                                "Hausa" -> "Rufe (Close)"
                                "Yoruba" -> "Paade"
                                "Igbo" -> "Mechie"
                                else -> "Close"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun PlanCard(
    plan: PremiumPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plan_card_${plan.productId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = plan.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        plan.badge?.let { b ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFD4AF37)
                            ) {
                                Text(
                                    text = b,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = plan.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = plan.formattedPrice,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DummyPlanCard(
    title: String,
    subtitle: String,
    badge: String?,
    price: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onClick
                )
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        badge?.let { b ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFD4AF37)
                            ) {
                                Text(
                                    text = b,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = price,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
