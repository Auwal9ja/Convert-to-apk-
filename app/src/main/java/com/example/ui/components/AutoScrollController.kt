package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

/**
 * Pure Right-Edge Draggable Auto-Scroll Bar
 *
 * Exactly matches Quran reading apps:
 * - A clean vertical line track along the right side.
 * - A sleek pink/red circular knob that users can directly touch and drag up/down anytime.
 * - Dragging downwards increases the auto-scroll speed; dragging to the top or tapping pauses.
 * - Displays an animated hint/indicator to teach users to drag the knob down for hands-free reading.
 * - Displays a minimal floating speed badge (e.g., 1.5x) next to the knob during interaction.
 */
@Composable
fun AutoScrollSideBar(
    listState: LazyListState,
    selectedLanguage: String = "English",
    isDarkTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var thumbFraction by remember { mutableFloatStateOf(0.0f) } // 0.0f = Top (Paused), 1.0f = Bottom (Fastest)
    var trackHeightPx by remember { mutableFloatStateOf(1f) }
    var isUserDragging by remember { mutableStateOf(false) }
    var hasUserInteracted by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    // Animated subtle sliding finger cue to indicate dragging/scrolling down
    val infiniteTransition = rememberInfiniteTransition(label = "scroller_finger_cue")
    val fingerSlideOffset by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "finger_slide"
    )
    val fingerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "finger_alpha"
    )

    // Calculate current speed factor with deep focus on ultra-low slow-reading speeds (0.05x to 3.5x)
    val speedMultiplier = remember(thumbFraction, isPlaying) {
        if (!isPlaying || thumbFraction <= 0.02f) 0.0f
        else {
            val norm = ((thumbFraction - 0.02f) / 0.98f).coerceIn(0f, 1f)
            // Piecewise smooth power curve to give maximum resolution and fine-tuning to calm low speeds
            if (norm < 0.35f) {
                // 0.05x to 0.45x (Ultra-gentle tranquil reading speeds - Natsuwa Mode)
                val subNorm = norm / 0.35f
                0.05f + (subNorm * 0.40f)
            } else if (norm < 0.70f) {
                // 0.45x to 1.5x (Comfortable reading speeds)
                val subNorm = (norm - 0.35f) / 0.35f
                0.45f + (subNorm * 1.05f)
            } else {
                // 1.5x to 3.5x (Faster overview)
                val subNorm = (norm - 0.70f) / 0.30f
                1.50f + (subNorm * 2.00f)
            }
        }
    }

    // Convert speed to pixels scrolled per frame (~60fps) with ultra-fine sub-pixel precision
    val effectiveSpeedPx = remember(speedMultiplier, density) {
        if (speedMultiplier <= 0f) 0f
        else speedMultiplier * 0.75f * density.density
    }

    // Auto-scroll continuous coroutine loop with ultra-smooth delta delivery
    LaunchedEffect(isPlaying, effectiveSpeedPx) {
        if (isPlaying && effectiveSpeedPx > 0f) {
            var subPixelAccumulator = 0f
            while (isActive) {
                if (listState.canScrollForward) {
                    subPixelAccumulator += effectiveSpeedPx
                    if (subPixelAccumulator >= 0.25f) {
                        listState.dispatchRawDelta(subPixelAccumulator)
                        subPixelAccumulator = 0f
                    }
                    delay(16) // ~60fps smooth scrolling
                } else {
                    // Reached end of the list: pause and reset thumb
                    isPlaying = false
                    thumbFraction = 0.0f
                    break
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .padding(end = 4.dp, top = 32.dp, bottom = 32.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .width(44.dp)
                .fillMaxHeight(0.85f)
                .onSizeChanged { size ->
                    trackHeightPx = size.height.toFloat()
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            hasUserInteracted = true
                            isUserDragging = true
                            val fraction = (offset.y / trackHeightPx).coerceIn(0.0f, 1.0f)
                            if (fraction <= 0.06f) {
                                // Tap near the top pauses
                                isPlaying = false
                                thumbFraction = 0.0f
                            } else {
                                thumbFraction = fraction
                                isPlaying = true
                            }
                            tryAwaitRelease()
                            isUserDragging = false
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            hasUserInteracted = true
                            isUserDragging = true
                            val fraction = (offset.y / trackHeightPx).coerceIn(0.0f, 1.0f)
                            thumbFraction = fraction
                            isPlaying = fraction > 0.05f
                        },
                        onDragEnd = {
                            isUserDragging = false
                            if (thumbFraction <= 0.05f) {
                                isPlaying = false
                                thumbFraction = 0.0f
                            }
                        },
                        onDragCancel = {
                            isUserDragging = false
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            hasUserInteracted = true
                            isUserDragging = true
                            val fraction = (change.position.y / trackHeightPx).coerceIn(0.0f, 1.0f)
                            thumbFraction = fraction
                            isPlaying = fraction > 0.05f
                        }
                    )
                },
            contentAlignment = Alignment.TopEnd
        ) {
            // Slender Vertical Track Line along the right edge
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
                    .align(Alignment.CenterEnd)
                    .offset(x = (-9).dp)
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFF4081).copy(alpha = 0.35f),
                                Color(0xFFE91E63).copy(alpha = 0.75f),
                                Color(0xFFC2185B).copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Calculate knob vertical position
            val knobDiameterDp = 22.dp
            val knobDiameterPx = with(density) { knobDiameterDp.toPx() }
            val availableTravel = (trackHeightPx - knobDiameterPx).coerceAtLeast(0f)
            val thumbYOffsetPx = (thumbFraction * availableTravel).coerceAtLeast(0f)

            // Draggable Pink/Red Knob + Floating Speed Badge + Drag-down Indicator
            Row(
                modifier = Modifier
                    .offset { IntOffset(0, thumbYOffsetPx.roundToInt()) }
                    .align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // Sleek Speed Pill Badge (appears when dragging or scrolling)
                AnimatedVisibility(
                    visible = isUserDragging || isPlaying,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    val isSlowCalmMode = isPlaying && speedMultiplier <= 0.35f
                    val badgeBorderColor = when {
                        !isPlaying -> Color(0xFF888888)
                        isSlowCalmMode -> Color(0xFF10B981) // Emerald Green for peaceful slow reading (Natsuwa)
                        speedMultiplier <= 1.2f -> Color(0xFFD4AF37) // Gold for normal comfortable speed
                        else -> Color(0xFFE91E63) // Vibrant Pink for fast scrolling
                    }

                    val badgeTextColor = when {
                        !isPlaying -> Color(0xFF888888)
                        isSlowCalmMode -> Color(0xFF10B981)
                        speedMultiplier <= 1.2f -> Color(0xFFD4AF37)
                        else -> Color(0xFFE91E63)
                    }

                    val speedText = when {
                        !isPlaying -> "⏸"
                        speedMultiplier < 0.10f -> "%.2fx".format(speedMultiplier)
                        else -> "%.1fx".format(speedMultiplier)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) Color(0xFF11221A).copy(alpha = 0.94f)
                                else Color(0xFFFFFFFF).copy(alpha = 0.95f),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .clickable {
                                hasUserInteracted = true
                                // Tap badge to cycle through gentle presets or pause
                                if (!isPlaying || speedMultiplier > 1.2f) {
                                    // Start with tranquil slow reading mode (0.1x)
                                    thumbFraction = 0.08f
                                    isPlaying = true
                                } else if (speedMultiplier <= 0.35f) {
                                    // Step to comfortable reading (0.6x)
                                    thumbFraction = 0.40f
                                    isPlaying = true
                                } else {
                                    // Pause
                                    isPlaying = false
                                    thumbFraction = 0.0f
                                }
                            }
                            .border(
                                1.2.dp,
                                badgeBorderColor,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = speedText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeTextColor
                            )
                            if (isSlowCalmMode) {
                                Text(
                                    text = "🍃",
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                // Small Animated Finger Indicator indicating scroll down (Dan Karamin finger)
                AnimatedVisibility(
                    visible = !isPlaying && !isUserDragging && thumbFraction <= 0.02f,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                if (isDarkTheme) Color(0xFF162A20).copy(alpha = 0.94f)
                                else Color(0xFFFFFFFF).copy(alpha = 0.95f)
                            )
                            .border(
                                1.dp,
                                Color(0xFFE91E63).copy(alpha = 0.65f),
                                CircleShape
                            )
                            .clickable {
                                hasUserInteracted = true
                                thumbFraction = 0.10f
                                isPlaying = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.offset(y = fingerSlideOffset.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = "Scroll down",
                                tint = Color(0xFFE91E63).copy(alpha = fingerAlpha),
                                modifier = Modifier.size(13.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = Color(0xFFE91E63).copy(alpha = fingerAlpha),
                                modifier = Modifier
                                    .size(7.dp)
                                    .offset(y = (-2).dp)
                            )
                        }
                    }
                }

                // Pure Circular Pink/Red Draggable Knob (Directly matching user's image)
                Box(
                    modifier = Modifier
                        .size(knobDiameterDp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFFF5282),
                                    Color(0xFFE91E63),
                                    Color(0xFFC2185B)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle center icon/dot for grip visual cue
                    if (!isPlaying && thumbFraction <= 0.02f) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Drag down",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        )
                    }
                }
            }
        }
    }
}
