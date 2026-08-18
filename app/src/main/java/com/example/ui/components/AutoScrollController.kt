package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

    val density = LocalDensity.current

    // Calculate current speed factor: from 0.0x to 4.5x
    val speedMultiplier = remember(thumbFraction, isPlaying) {
        if (!isPlaying || thumbFraction <= 0.03f) 0.0f
        else 0.4f + (thumbFraction * 4.1f) // Range: 0.4x to 4.5x
    }

    // Convert speed to pixels scrolled per frame (~60fps)
    val effectiveSpeedPx = remember(speedMultiplier, density) {
        if (speedMultiplier <= 0f) 0f
        else speedMultiplier * 1.5f * density.density
    }

    // Auto-scroll continuous coroutine loop
    LaunchedEffect(isPlaying, effectiveSpeedPx) {
        if (isPlaying && effectiveSpeedPx > 0f) {
            while (isActive) {
                if (listState.canScrollForward) {
                    listState.dispatchRawDelta(effectiveSpeedPx)
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

            // Draggable Pink/Red Knob + Floating Speed Badge
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
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDarkTheme) Color(0xFF16251E).copy(alpha = 0.92f)
                                else Color(0xFFFFFFFF).copy(alpha = 0.95f),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .padding(end = 6.dp)
                            .border(
                                1.dp,
                                if (isPlaying) Color(0xFFE91E63) else Color(0xFFD4AF37),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Text(
                            text = if (isPlaying) "%.1fx".format(speedMultiplier) else "⏸",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPlaying) Color(0xFFE91E63) else Color(0xFF888888),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
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
                    // Subtle center white dot for grip visual cue
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
