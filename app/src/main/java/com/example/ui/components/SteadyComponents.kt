package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SudsCheckIn
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.SudsCalm
import com.example.ui.theme.SudsHigh
import com.example.ui.theme.SudsIntense
import com.example.ui.theme.SudsMild
import com.example.ui.theme.SudsModerate
import com.example.ui.theme.WarmAmber
import kotlin.math.roundToInt

// Helper to determine SUDS color band
fun getSudsColor(suds: Int): Color {
    return when {
        suds <= 20 -> SudsCalm
        suds <= 40 -> SudsMild
        suds <= 60 -> SudsModerate
        suds <= 80 -> SudsHigh
        else -> SudsIntense // Warm Amber, never red!
    }
}

fun getSudsLabel(suds: Int): String {
    return when {
        suds <= 20 -> "Calm"
        suds <= 40 -> "Mild"
        suds <= 60 -> "Moderate"
        suds <= 80 -> "High"
        else -> "Intense"
    }
}

// 1. BUTTONS (Primary / Secondary / Ghost)
@Composable
fun SteadyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SagePrimary,
            contentColor = PureWhite,
            disabledContainerColor = SagePrimary.copy(alpha = 0.4f),
            disabledContentColor = PureWhite.copy(alpha = 0.7f)
        ),
        modifier = modifier
            .height(56.dp)
            .testTag(testTag)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun SteadySecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String = "secondary_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = SandAccent.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = SandAccent.copy(alpha = 0.2f)
        ),
        modifier = modifier
            .height(56.dp)
            .testTag(testTag)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SteadyGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String = "ghost_button"
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .height(56.dp)
            .testTag(testTag)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = SagePrimaryDeep,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = SagePrimaryDeep,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// 2. STEADY CARD (Radius 20dp, soft shadow)
@Composable
fun SteadyCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    testTag: String = "steady_card",
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    Surface(
        shape = shape,
        color = backgroundColor,
        tonalElevation = elevation,
        shadowElevation = elevation,
        modifier = modifier
            .clip(shape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .testTag(testTag)
            .border(1.dp, SandAccent.copy(alpha = 0.5f), shape)
    ) {
        content()
    }
}

// 3. SUDS SLIDER (0-100 with live bubble and 5 soft color bands)
@Composable
fun SteadySudsSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Distress (SUDS: 0 - 100)",
    testTag: String = "suds_slider"
) {
    val intVal = value.roundToInt().coerceIn(0, 100)
    val currentColor = getSudsColor(intVal)
    val currentLabel = getSudsLabel(intVal)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Live value bubble
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(currentColor.copy(alpha = 0.18f))
                    .border(1.dp, currentColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$intVal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = currentColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• $currentLabel",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = currentColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Visual color bar gradient track preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(SudsCalm, SudsMild, SudsModerate, SudsHigh, SudsIntense)
                    )
                )
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            steps = 99,
            colors = SliderDefaults.colors(
                thumbColor = currentColor,
                activeTrackColor = currentColor.copy(alpha = 0.8f),
                inactiveTrackColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("${testTag}_control")
        )

        // Band labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Calm\n0", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.Center)
            Text("Mild\n25", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.Center)
            Text("Moderate\n50", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.Center)
            Text("High\n75", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.Center)
            Text("Intense\n100", style = MaterialTheme.typography.labelSmall, color = MutedText, textAlign = TextAlign.Center)
        }
    }
}

// 4. BREATHING CIRCLE (4s in / 6s out, with reduced motion fallback)
@Composable
fun SteadyBreathingCircle(
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier,
    promptMessage: String = "Let the feeling rise and fall."
) {
    if (reducedMotion) {
        // Static text cues when reduced motion is preferred
        Box(
            modifier = modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(SagePrimary.copy(alpha = 0.15f))
                .border(2.dp, SagePrimary, CircleShape)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Breathe with awareness",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SagePrimaryDeep,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Inhale 4s  •  Exhale 6s",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = promptMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "breathing")
        // Total cycle: 10,000ms = 4000ms inhale (expands to 1.0f) + 6000ms exhale (contracts to 0.7f)
        val phaseProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 10000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "breathing_phase"
        )

        val isInhale = phaseProgress < 0.4f
        val animatedScale = if (isInhale) {
            val t = phaseProgress / 0.4f
            0.65f + 0.35f * FastOutSlowInEasing.transform(t)
        } else {
            val t = (phaseProgress - 0.4f) / 0.6f
            1.0f - 0.35f * FastOutSlowInEasing.transform(t)
        }

        Box(
            modifier = modifier
                .size(250.dp)
                .testTag("breathing_circle_container"),
            contentAlignment = Alignment.Center
        ) {
            // Calming outer halo
            Box(
                modifier = Modifier
                    .size((250 * animatedScale).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                DustyBlue.copy(alpha = 0.25f),
                                SagePrimary.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Core breathing circle
            Box(
                modifier = Modifier
                    .size((190 * animatedScale).dp)
                    .clip(CircleShape)
                    .background(SagePrimary.copy(alpha = 0.22f))
                    .border(2.dp, SagePrimary, CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isInhale) "Inhale (4s)" else "Exhale (6s)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SagePrimaryDeep,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = promptMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 5. COUNTDOWN RING (MM:SS with Canvas circle)
@Composable
fun SteadyCountdownRing(
    remainingSeconds: Int,
    totalSeconds: Int,
    modifier: Modifier = Modifier,
    color: Color = SagePrimary
) {
    val progress = if (totalSeconds > 0) {
        remainingSeconds.toFloat() / totalSeconds.toFloat()
    } else 0f

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .size(190.dp)
            .testTag("countdown_ring"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 12.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Background ring track
            drawCircle(
                color = SandAccent.copy(alpha = 0.6f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active progress arc
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeFormatted,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Delaying the ritual",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )
        }
    }
}

// 6. DISTRESS LINE CHART (Smooth curve distress chart over check-in minutes)
@Composable
fun SteadyDistressLineChart(
    checkIns: List<SudsCheckIn>,
    modifier: Modifier = Modifier,
    lineColor: Color = SagePrimaryDeep,
    heightDp: Dp = 180.dp
) {
    if (checkIns.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(heightDp)
                .clip(RoundedCornerShape(16.dp))
                .background(SandAccent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text("No check-in distress points yet", style = MaterialTheme.typography.bodySmall, color = MutedText)
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightDp)
                .clip(RoundedCornerShape(16.dp))
                .background(SandAccent.copy(alpha = 0.2f))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw horizontal guide lines (0, 25, 50, 75, 100)
                val guideSteps = listOf(0, 25, 50, 75, 100)
                guideSteps.forEach { step ->
                    val y = h - (step / 100f) * h
                    drawLine(
                        color = SandAccent.copy(alpha = 0.8f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val maxMin = (checkIns.maxOfOrNull { it.minuteOffset } ?: 15).coerceAtLeast(1)

                // Build points
                val points = checkIns.map { checkIn ->
                    val x = (checkIn.minuteOffset.toFloat() / maxMin.toFloat()) * w
                    val y = h - (checkIn.suds.toFloat() / 100f) * h
                    Offset(x, y)
                }

                if (points.size >= 2) {
                    val linePath = Path()
                    val areaPath = Path()

                    linePath.moveTo(points.first().x, points.first().y)
                    areaPath.moveTo(points.first().x, h)
                    areaPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        val cx = (p0.x + p1.x) / 2
                        linePath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        areaPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                    }

                    areaPath.lineTo(points.last().x, h)
                    areaPath.close()

                    // Draw soft area fill
                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            listOf(
                                lineColor.copy(alpha = 0.35f),
                                lineColor.copy(alpha = 0.05f)
                            )
                        )
                    )

                    // Draw smooth line
                    drawPath(
                        path = linePath,
                        color = lineColor,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Draw circles at data points
                points.forEachIndexed { idx, point ->
                    val sudsVal = checkIns[idx].suds
                    val dotColor = getSudsColor(sudsVal)
                    drawCircle(
                        color = PureWhite,
                        radius = 6.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = dotColor,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val startMin = checkIns.firstOrNull()?.minuteOffset ?: 0
            val endMin = checkIns.lastOrNull()?.minuteOffset ?: 15
            Text("${startMin}m", style = MaterialTheme.typography.labelSmall, color = MutedText)
            Text("Distress over time (minutes)", style = MaterialTheme.typography.labelSmall, color = MutedText)
            Text("${endMin}m", style = MaterialTheme.typography.labelSmall, color = MutedText)
        }
    }
}

// 7. CHIP (Category tag)
@Composable
fun SteadyCategoryChip(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val bg = if (isSelected) SagePrimary else SandAccent.copy(alpha = 0.6f)
    val textColor = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}

// 8. EMPTY STATE (Friendly icon + message + action)
@Composable
fun SteadyEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SandAccent.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SagePrimaryDeep,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MutedText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(18.dp))
            SteadySecondaryButton(
                text = actionText,
                onClick = onActionClick,
                testTag = "empty_state_action"
            )
        }
    }
}

// 9. PROGRESS DOTS
@Composable
fun SteadyProgressDots(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            val isActive = i == currentStep
            Box(
                modifier = Modifier
                    .size(if (isActive) 24.dp else 8.dp, 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) SagePrimary else SandAccent)
            )
        }
    }
}

// 10. CONFIRM DIALOG (No red buttons; warm, supportive)
@Composable
fun SteadyConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmAmber, contentColor = PureWhite)
            ) {
                Text(confirmText, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText, color = MutedText)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
