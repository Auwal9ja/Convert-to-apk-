package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.local.AppLocalizer
import kotlin.math.*

// Coordinates of the Holy Kaaba in Makkah
const val KAABA_LATITUDE = 21.422487
const val KAABA_LONGITUDE = 39.826206

/**
 * Calculates the forward azimuth (bearing) from user coordinate to Holy Kaaba (0° - 360°)
 */
fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
    val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
    val kaabaLngRad = Math.toRadians(KAABA_LONGITUDE)
    val userLatRad = Math.toRadians(userLat)
    val userLngRad = Math.toRadians(userLng)

    val dLng = kaabaLngRad - userLngRad
    val y = sin(dLng) * cos(kaabaLatRad)
    val x = cos(userLatRad) * sin(kaabaLatRad) - sin(userLatRad) * cos(kaabaLatRad) * cos(dLng)

    val initialBearing = Math.toDegrees(atan2(y, x))
    return ((initialBearing + 360) % 360).toFloat()
}

/**
 * Calculates distance from user to Kaaba in kilometers using Haversine formula
 */
fun calculateDistanceToKaaba(userLat: Double, userLng: Double): Int {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(KAABA_LATITUDE - userLat)
    val dLng = Math.toRadians(KAABA_LONGITUDE - userLng)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(userLat)) * cos(Math.toRadians(KAABA_LATITUDE)) *
            sin(dLng / 2) * sin(dLng / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return (earthRadiusKm * c).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaCompassScreen(
    selectedLanguage: String,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // User Location State
    val initialLoc = remember { com.example.util.PrayerTimeManager.getSelectedLocation(context) }
    val initialCity = remember { com.example.util.PrayerTimeManager.getCityName(context) }
    val initialCountry = remember { com.example.util.PrayerTimeManager.getCountryName(context) }
    var currentLat by remember { mutableDoubleStateOf(initialLoc.first) }
    var currentLng by remember { mutableDoubleStateOf(initialLoc.second) }
    var locationName by remember { mutableStateOf(if (com.example.util.PrayerTimeManager.isLocationSet(context)) "$initialCity, $initialCountry" else if (selectedLanguage == "Hausa") "Wurin Da Kake" else "Current Location") }
    var isGpsActive by remember { mutableStateOf(false) }

    // Compass Sensor State
    var rawAzimuth by remember { mutableFloatStateOf(0f) }
    var smoothedAzimuth by remember { mutableFloatStateOf(0f) }
    var hasSensor by remember { mutableStateOf(true) }
    var sensorAccuracy by remember { mutableIntStateOf(SensorManager.SENSOR_STATUS_ACCURACY_HIGH) }
    var lastVibrationTime by remember { mutableLongStateOf(0L) }

    // Qibla Bearing Calculation
    val qiblaBearing = remember(currentLat, currentLng) {
        calculateQiblaBearing(currentLat, currentLng)
    }
    val distanceToKaaba = remember(currentLat, currentLng) {
        calculateDistanceToKaaba(currentLat, currentLng)
    }

    // Difference between phone orientation and Qibla bearing
    val angleDiff = remember(smoothedAzimuth, qiblaBearing) {
        var diff = (qiblaBearing - smoothedAzimuth) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        diff
    }

    val isFacingQibla = abs(angleDiff) <= 3.0f

    // Trigger subtle haptic buzz when accurately aligned
    LaunchedEffect(isFacingQibla) {
        if (isFacingQibla) {
            val now = System.currentTimeMillis()
            if (now - lastVibrationTime > 1500) { // Limit vibration intervals
                lastVibrationTime = now
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                        vibratorManager?.defaultVibrator?.vibrate(
                            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            @Suppress("DEPRECATION")
                            vibrator?.vibrate(100)
                        }
                    }
                } catch (_: Exception) { }
            }
        }
    }

    // Sensor Listener Setup
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (rotationSensor == null && (accelSensor == null || magnetSensor == null)) {
            hasSensor = false
        }

        val rMat = FloatArray(9)
        val orientation = FloatArray(3)
        val lastAccel = FloatArray(3)
        val lastMagnet = FloatArray(3)
        var accelSet = false
        var magnetSet = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rMat, event.values)
                    SensorManager.getOrientation(rMat, orientation)
                    val azimuthInRadians = orientation[0]
                    val azimuthInDegrees = ((Math.toDegrees(azimuthInRadians.toDouble()) + 360) % 360).toFloat()
                    rawAzimuth = azimuthInDegrees

                    // Shortest-arc exponential smoothing
                    var delta = (rawAzimuth - smoothedAzimuth) % 360f
                    if (delta > 180f) delta -= 360f
                    if (delta < -180f) delta += 360f
                    smoothedAzimuth = (smoothedAzimuth + delta * 0.20f + 360f) % 360f
                } else {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        System.arraycopy(event.values, 0, lastAccel, 0, event.values.size)
                        accelSet = true
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        System.arraycopy(event.values, 0, lastMagnet, 0, event.values.size)
                        magnetSet = true
                    }

                    if (accelSet && magnetSet) {
                        val success = SensorManager.getRotationMatrix(rMat, null, lastAccel, lastMagnet)
                        if (success) {
                            SensorManager.getOrientation(rMat, orientation)
                            val azimuthInRadians = orientation[0]
                            val azimuthInDegrees = ((Math.toDegrees(azimuthInRadians.toDouble()) + 360) % 360).toFloat()
                            rawAzimuth = azimuthInDegrees

                            var delta = (rawAzimuth - smoothedAzimuth) % 360f
                            if (delta > 180f) delta -= 360f
                            if (delta < -180f) delta += 360f
                            smoothedAzimuth = (smoothedAzimuth + delta * 0.20f + 360f) % 360f
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                sensorAccuracy = accuracy
            }
        }

        rotationSensor?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        } ?: run {
            accelSensor?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magnetSensor?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val detectGps: () -> Unit = {
        isGpsActive = true
        com.example.util.PrayerTimeManager.tryDetectGpsLocation(context) { city, country, lat, lng ->
            currentLat = lat
            currentLng = lng
            locationName = if (country.isNotBlank()) "$city, $country" else city
            isGpsActive = true
        }
    }

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            detectGps()
        }
    }

    // Automatically detect GPS location on start if permission already granted
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            detectGps()
        }
    }

    val animatedDialRotation by animateFloatAsState(
        targetValue = -smoothedAzimuth,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "dialRotation"
    )

    val primaryGreen = MaterialTheme.colorScheme.primary
    val goldAccent = Color(0xFFD4AF37)
    val alignBorderColor by animateColorAsState(
        targetValue = if (isFacingQibla) Color(0xFF10B981) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        label = "borderColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Kaaba Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0D7A68), Color(0xFF04221C))
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_kaaba_qibla_banner_1789824192395),
                contentDescription = "Holy Kaaba",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x55000000), Color(0xCC06251E))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = "الْقِبْلَةُ الْمُشَرَّفَةُ",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = AppLocalizer.getString("qibla", selectedLanguage) + " • " + AppLocalizer.getString("towards_kaaba", selectedLanguage),
                    color = Color(0xFFFDE68A),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Location & Header Bar
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isGpsActive) Color(0xFF10B981).copy(alpha = 0.15f) else goldAccent.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isGpsActive) Icons.Default.GpsFixed else Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = if (isGpsActive) Color(0xFF10B981) else goldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = AppLocalizer.getString("location_services", selectedLanguage),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = locationName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Button(
                    onClick = {
                        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        if (hasFine || hasCoarse) {
                            detectGps()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("qibla_gps_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "GPS",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (selectedLanguage == "Hausa") "Sabunta GPS" else "Refresh GPS",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direction & Alignment Status Banner
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isFacingQibla) Color(0xFF0F5132) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = if (isFacingQibla) 2.dp else 1.dp,
                    color = alignBorderColor,
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isFacingQibla) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocalizer.getString("facing_qibla", selectedLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                } else {
                    val turnText = if (angleDiff > 0) {
                        "${AppLocalizer.getString("turn_right", selectedLanguage)} ${angleDiff.toInt()}°"
                    } else {
                        "${AppLocalizer.getString("turn_left", selectedLanguage)} ${abs(angleDiff.toInt())}°"
                    }
                    Icon(
                        imageVector = if (angleDiff > 0) Icons.Default.ArrowForward else Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = goldAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = turnText,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Islamic Compass Dial
        Box(
            modifier = Modifier
                .size(310.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glowing Background Aura when Aligned
            if (isFacingQibla) {
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF10B981).copy(alpha = 0.35f),
                                    Color(0xFF10B981).copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Fixed Compass Base Canvas (Outer Ring + Cardinal Directions)
            val dialBackgroundColor = if (isDarkTheme) Color(0xFF0E221E) else Color(0xFFFFFFFF)
            val outerRingColor = if (isDarkTheme) Color(0xFF1B3D34) else Color(0xFFE2E8F0)
            val tickColor = if (isDarkTheme) Color(0xFF8BA69F) else Color(0xFF64748B)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(elevation = 6.dp, shape = CircleShape)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.minDimension / 2f - 6.dp.toPx()

                // Draw Background Disc
                drawCircle(
                    color = dialBackgroundColor,
                    radius = radius,
                    center = center
                )

                // Draw Outer Golden Rim
                drawCircle(
                    color = outerRingColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw 360 Degree Ticks (Rotated dynamically with dial)
                rotate(degrees = animatedDialRotation, pivot = center) {
                    for (degree in 0 until 360 step 5) {
                        val isMajor = degree % 30 == 0
                        val isCardinal = degree % 90 == 0
                        val tickLength = if (isCardinal) 14.dp.toPx() else if (isMajor) 9.dp.toPx() else 5.dp.toPx()
                        val tickWidth = if (isCardinal) 2.5.dp.toPx() else if (isMajor) 1.8.dp.toPx() else 1.dp.toPx()
                        val strokeColor = if (degree == 0) Color(0xFFEF4444) else if (isMajor) goldAccent else tickColor

                        val angleRad = Math.toRadians(degree.toDouble() - 90.0)
                        val startX = center.x + (radius - tickLength) * cos(angleRad).toFloat()
                        val startY = center.y + (radius - tickLength) * sin(angleRad).toFloat()
                        val endX = center.x + radius * cos(angleRad).toFloat()
                        val endY = center.y + radius * sin(angleRad).toFloat()

                        drawLine(
                            color = strokeColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = tickWidth,
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw Cardinal Markers N, E, S, W
                    drawCardinalNeedle(center, radius, isDarkTheme)

                    // Draw Qibla Pointer Marker on Dial
                    drawQiblaIndicator(center, radius, qiblaBearing, isFacingQibla)
                }

                // Inner Decorative Islamic Geometric Ring
                drawCircle(
                    color = goldAccent.copy(alpha = 0.35f),
                    radius = radius * 0.45f,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            // Central Kaaba & Degree Readout
            Surface(
                shape = CircleShape,
                color = if (isFacingQibla) Color(0xFF0F5132) else MaterialTheme.colorScheme.primaryContainer,
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isFacingQibla) Color(0xFF34D399) else goldAccent),
                shadowElevation = 4.dp,
                modifier = Modifier.size(80.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "Kaaba",
                        tint = if (isFacingQibla) Color.White else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "${smoothedAzimuth.toInt()}°",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = if (isFacingQibla) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Real-Time Metrics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = AppLocalizer.getString("qibla_angle", selectedLanguage),
                value = "${String.format("%.1f", qiblaBearing)}°",
                subtitle = getCompassDirectionName(qiblaBearing),
                icon = Icons.Default.Explore,
                accentColor = goldAccent,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = AppLocalizer.getString("distance_to_kaaba", selectedLanguage),
                value = "$distanceToKaaba km",
                subtitle = AppLocalizer.getString("towards_kaaba", selectedLanguage),
                icon = Icons.Default.NearMe,
                accentColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calibration Tip & Sensor Guide
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = AppLocalizer.getString("calibrate_compass_hint", selectedLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Draws the North / South needle and cardinal markers
 */
private fun DrawScope.drawCardinalNeedle(center: Offset, radius: Float, isDarkTheme: Boolean) {
    val needleRadius = radius * 0.78f

    // North Arrow (Red)
    val northPath = Path().apply {
        moveTo(center.x, center.y - needleRadius)
        lineTo(center.x - 7.dp.toPx(), center.y - radius * 0.45f)
        lineTo(center.x + 7.dp.toPx(), center.y - radius * 0.45f)
        close()
    }
    drawPath(northPath, Color(0xFFEF4444), style = Fill)

    // South Arrow (Gray / Dim)
    val southPath = Path().apply {
        moveTo(center.x, center.y + needleRadius)
        lineTo(center.x - 6.dp.toPx(), center.y + radius * 0.45f)
        lineTo(center.x + 6.dp.toPx(), center.y + radius * 0.45f)
        close()
    }
    val southColor = if (isDarkTheme) Color(0xFF475569) else Color(0xFF94A3B8)
    drawPath(southPath, southColor, style = Fill)
}

/**
 * Draws a prominent golden Kaaba marker indicator at the exact Qibla bearing on the dial
 */
private fun DrawScope.drawQiblaIndicator(
    center: Offset,
    radius: Float,
    qiblaBearing: Float,
    isAligned: Boolean
) {
    val angleRad = Math.toRadians(qiblaBearing.toDouble() - 90.0)
    val markerDistance = radius * 0.86f

    val markerCenterX = center.x + markerDistance * cos(angleRad).toFloat()
    val markerCenterY = center.y + markerDistance * sin(angleRad).toFloat()
    val markerCenter = Offset(markerCenterX, markerCenterY)

    // Glow circle around Kaaba marker
    drawCircle(
        color = if (isAligned) Color(0xFF10B981) else Color(0xFFD4AF37),
        radius = 16.dp.toPx(),
        center = markerCenter,
        style = Fill
    )

    drawCircle(
        color = Color.White,
        radius = 16.dp.toPx(),
        center = markerCenter,
        style = Stroke(width = 2.dp.toPx())
    )

    // Kaaba Cube Symbol (Square representation)
    val cubeSize = 12.dp.toPx()
    val topLeft = Offset(markerCenterX - cubeSize / 2f, markerCenterY - cubeSize / 2f)

    drawRect(
        color = Color(0xFF111827),
        topLeft = topLeft,
        size = androidx.compose.ui.geometry.Size(cubeSize, cubeSize),
        style = Fill
    )

    // Golden Kiswah line
    drawLine(
        color = Color(0xFFFBBF24),
        start = Offset(topLeft.x, topLeft.y + cubeSize * 0.35f),
        end = Offset(topLeft.x + cubeSize, topLeft.y + cubeSize * 0.35f),
        strokeWidth = 1.5.dp.toPx()
    )
}

/**
 * Converts degree bearing into human-readable compass cardinal text (e.g. "ENE", "NE")
 */
fun getCompassDirectionName(degree: Float): String {
    val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = (((degree + 11.25f) % 360) / 22.5f).toInt()
    return directions[index.coerceIn(0, directions.size - 1)]
}
