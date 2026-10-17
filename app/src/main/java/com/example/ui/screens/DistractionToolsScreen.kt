package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.components.SteadyCard
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent

private enum class ResetTool { GROUNDING, COLOR_HUNT, CATEGORY_SPRINT, BUBBLE_POP }

@Composable
fun DistractionToolsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var selectedTool by remember { mutableStateOf(ResetTool.GROUNDING) }
    val completedSteps = remember { mutableStateListOf<Int>() }
    var colorIndex by remember { mutableIntStateOf(0) }
    var foundObjects by remember { mutableIntStateOf(0) }
    var categoryIndex by remember { mutableIntStateOf(0) }
    var bubblePopState by remember { mutableStateOf(BubblePopGameState()) }

    val groundingSteps = listOf(
        "Notice 5 things you can see",
        "Notice 4 things you can physically feel",
        "Notice 3 sounds around you",
        "Notice 2 scents, if available",
        "Notice 1 taste or take one comfortable breath"
    )
    val colors = listOf("blue", "green", "red", "yellow", "white", "black", "brown", "purple")
    val categories = listOf(
        "Name 5 animals (real or fictional)",
        "Name 5 foods that are crunchy",
        "Name 5 things that are round",
        "Name 5 places you would like to visit",
        "Name 5 songs, films, or stories",
        "Name 5 objects you can see",
        "Name 5 things beginning with S"
    )

    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SagePrimaryDeep)
            }
            Column(Modifier.weight(1f)) {
                Text("Reset & Refocus", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Short activities for an anxious moment", style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
            Box(Modifier.size(42.dp).clip(CircleShape).background(com.example.ui.theme.MintSurface), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = SagePrimaryDeep)
            }
        }

        SteadyCard(Modifier.fillMaxWidth(), backgroundColor = com.example.ui.theme.BlueSurface, elevation = 0.dp) {
            Column(Modifier.padding(16.dp)) {
                Text("You don't have to make anxiety disappear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = SagePrimaryDeep)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Choose one brief activity to gently shift your attention. There is no score to pass, and it's okay if anxiety is still present afterwards.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToolChoice("Ground", selectedTool == ResetTool.GROUNDING, { selectedTool = ResetTool.GROUNDING; completedSteps.clear() }, Modifier.weight(1f))
                ToolChoice("Color hunt", selectedTool == ResetTool.COLOR_HUNT, { selectedTool = ResetTool.COLOR_HUNT }, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToolChoice("Word game", selectedTool == ResetTool.CATEGORY_SPRINT, { selectedTool = ResetTool.CATEGORY_SPRINT }, Modifier.weight(1f))
                ToolChoice("Bubble Pop", selectedTool == ResetTool.BUBBLE_POP, { selectedTool = ResetTool.BUBBLE_POP }, Modifier.weight(1f))
            }
        }

        when (selectedTool) {
            ResetTool.GROUNDING -> SteadyCard(Modifier.fillMaxWidth(), testTag = "reset_grounding_card") {
                Column(Modifier.padding(18.dp)) {
                    Text("5-senses grounding", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Take your time. Skip any step that doesn't suit your surroundings.", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    Spacer(Modifier.height(12.dp))
                    groundingSteps.forEachIndexed { index, step ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = completedSteps.contains(index),
                                onCheckedChange = { checked ->
                                    if (checked && !completedSteps.contains(index)) completedSteps.add(index)
                                    if (!checked) completedSteps.remove(index)
                                }
                            )
                            Text(step, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (completedSteps.size == groundingSteps.size) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SagePrimaryDeep)
                            Spacer(Modifier.width(8.dp))
                            Text("Activity complete. Return to your next step, even if some discomfort remains.", color = SagePrimaryDeep, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { completedSteps.clear() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Start again")
                    }
                }
            }

            ResetTool.COLOR_HUNT -> SteadyCard(Modifier.fillMaxWidth(), testTag = "reset_color_hunt_card") {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(64.dp).clip(CircleShape).background(com.example.ui.theme.PeachSurface), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Color hunt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Find 5 different objects that are", style = MaterialTheme.typography.bodyMedium, color = MutedText)
                    Text(colors[colorIndex].uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SagePrimaryDeep)
                    Text("$foundObjects of 5 spotted", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = { if (foundObjects < 5) foundObjects++ },
                        enabled = foundObjects < 5,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimaryDeep, contentColor = PureWhite)
                    ) { Text(if (foundObjects < 5) "I spotted one" else "Round complete") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { colorIndex = (colorIndex + 1) % colors.size; foundObjects = 0 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Try another color")
                    }
                    if (foundObjects == 5) {
                        Spacer(Modifier.height(8.dp))
                        Text("Nice noticing. You can stop here—there's no need to continue until you feel perfectly calm.", textAlign = TextAlign.Center, color = SagePrimaryDeep)
                    }
                }
            }

            ResetTool.BUBBLE_POP -> BubblePopGameCard(
                state = bubblePopState,
                onStateChange = { bubblePopState = it }
            )

            ResetTool.CATEGORY_SPRINT -> SteadyCard(Modifier.fillMaxWidth(), testTag = "reset_word_game_card") {
                Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Category sprint", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    Text("YOUR PROMPT", style = MaterialTheme.typography.labelMedium, color = MutedText)
                    Spacer(Modifier.height(6.dp))
                    Text(categories[categoryIndex], style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, color = SagePrimaryDeep)
                    Spacer(Modifier.height(8.dp))
                    Text("Think of answers or say them quietly. This is play, not a test.", textAlign = TextAlign.Center, color = MutedText)
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = { categoryIndex = (categoryIndex + 1) % categories.size },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary, contentColor = PureWhite)
                    ) { Text("Next challenge") }
                }
            }
        }

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SandAccent.copy(alpha = 0.35f))
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("A gentle reminder", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SagePrimaryDeep)
                Spacer(Modifier.height(5.dp))
                Text(
                    "Use these as brief attention breaks, not as a rule you must complete to cancel a thought or make anxiety reach zero. If you're doing planned exposure and response prevention (ERP), follow your agreed plan and return to it when ready.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun BubblePopGameCard(
    state: BubblePopGameState,
    onStateChange: (BubblePopGameState) -> Unit
) {
    val motion = rememberInfiniteTransition(label = "bubble_environment")
    val drift by motion.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 14000,
                easing = androidx.compose.animation.core.LinearEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "ambient_bubble_drift"
    )
    val pulse by motion.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.045f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 1900,
                easing = androidx.compose.animation.core.FastOutSlowInEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "target_bubble_breath"
    )

    SteadyCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "bubble_pop_game_card"
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                        .background(Color(0xFFE2F4F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Spa,
                        contentDescription = null,
                        tint = Color(0xFF286D70),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "A little bubble break",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Slow down. Notice. Pop when you're ready.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (state.isComplete) "You made space for a pause" else "Your gentle round",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SagePrimaryDeep
                )
                Text(
                    "${state.score.coerceAtMost(BubblePopGameState.ROUND_LENGTH)} / ${BubblePopGameState.ROUND_LENGTH}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MutedText
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                repeat(BubblePopGameState.ROUND_LENGTH) { index ->
                    Box(
                        modifier = Modifier.weight(1f).height(5.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (index < state.score) Color(0xFF63B9AE)
                                else Color(0xFFE3ECEB)
                            )
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF173D55))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                        .pointerInput(state.score, state.isComplete) {
                            detectTapGestures { tap ->
                                onStateChange(
                                    state.tap(
                                        tap.x,
                                        tap.y,
                                        size.width.toFloat(),
                                        size.height.toFloat()
                                    )
                                )
                            }
                        }
                        .testTag("bubble_pop_play_area")
                ) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF204F66),
                                Color(0xFF286B78),
                                Color(0xFF193E58)
                            ),
                            startY = 0f,
                            endY = size.height
                        )
                    )
                    // Soft, diffuse light gives the play area depth without visual clutter.
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF83D9C8).copy(alpha = 0.23f), Color.Transparent),
                            center = Offset(size.width * 0.18f, size.height * 0.16f),
                            radius = size.width * 0.66f
                        ),
                        radius = size.width * 0.66f,
                        center = Offset(size.width * 0.18f, size.height * 0.16f)
                    )

                    // Background bubbles float slowly; they are decorative and never count as targets.
                    val ambient = listOf(
                        Triple(0.13f, 0.24f, 7f),
                        Triple(0.84f, 0.18f, 10f),
                        Triple(0.72f, 0.77f, 6f),
                        Triple(0.24f, 0.86f, 9f),
                        Triple(0.91f, 0.53f, 5f),
                        Triple(0.43f, 0.12f, 4f),
                        Triple(0.53f, 0.91f, 6f)
                    )
                    ambient.forEachIndexed { index, bubble ->
                        val x = bubble.first * size.width
                        val y = ((bubble.second + drift * (if (index % 2 == 0) 0.18f else 0.12f)) % 1f) * size.height
                        val radius = bubble.third.dp.toPx()
                        drawCircle(
                            color = Color(0xFFBDEDE8).copy(alpha = 0.12f),
                            radius = radius * 1.3f,
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = Color(0xFFD8FFFA).copy(alpha = 0.25f),
                            radius = radius,
                            center = Offset(x, y),
                            style = Stroke(width = 1.1.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.48f),
                            radius = radius * 0.18f,
                            center = Offset(x - radius * 0.28f, y - radius * 0.30f)
                        )
                    }

                    if (!state.isComplete) {
                        val target = state.currentTarget(size.width, size.height)
                        val radius = target.radius * pulse

                        // A quiet halo makes the interactive bubble easy to find.
                        drawCircle(
                            color = Color(0xFF9CEBE0).copy(alpha = 0.12f),
                            radius = radius * 1.75f,
                            center = Offset(target.centerX, target.centerY)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFF7FFFF),
                                    Color(0xFFB7F4E8),
                                    Color(0xFF67CFC3),
                                    Color(0xFF378E9B)
                                ),
                                center = Offset(
                                    target.centerX - radius * 0.28f,
                                    target.centerY - radius * 0.34f
                                ),
                                radius = radius * 1.8f
                            ),
                            radius = radius,
                            center = Offset(target.centerX, target.centerY)
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.76f),
                            radius = radius * 0.82f,
                            center = Offset(target.centerX, target.centerY),
                            style = Stroke(width = 1.3.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.86f),
                            radius = radius * 0.20f,
                            center = Offset(
                                target.centerX - radius * 0.30f,
                                target.centerY - radius * 0.34f
                            )
                        )
                        drawCircle(
                            color = Color.White.copy(alpha = 0.38f),
                            radius = radius * 0.075f,
                            center = Offset(
                                target.centerX + radius * 0.32f,
                                target.centerY + radius * 0.24f
                            )
                        )
                    } else {
                        // A calm, simple completion glow rather than a flashing reward effect.
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF9DE7D6).copy(alpha = 0.32f), Color.Transparent),
                                center = Offset(size.width / 2f, size.height / 2f),
                                radius = size.width * 0.42f
                            ),
                            radius = size.width * 0.42f,
                            center = Offset(size.width / 2f, size.height / 2f)
                        )
                    }
                }

                if (state.isComplete) {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            Modifier.size(54.dp).clip(CircleShape).background(Color(0xFFB9EDE0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF205D62),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            "A pause, just for you",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "You can stop here. You don't need to feel perfectly calm.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE0F5F1),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (state.isComplete) {
                Button(
                    onClick = { onStateChange(state.restart()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SagePrimaryDeep,
                        contentColor = PureWhite
                    ),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Float through another round")
                }
            } else {
                Button(
                    onClick = {
                        val target = state.currentTarget(1000f, 1000f)
                        onStateChange(state.tap(target.centerX, target.centerY, 1000f, 1000f))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF286D70),
                        contentColor = PureWhite
                    ),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Text("Pop this bubble")
                }
                Text(
                    "No timer. No penalties. Tap the glowing bubble or use the button—go at your own pace.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                "A short attention break, not a way you must use to cancel a thought. If you're following a planned ERP exercise, return to your agreed plan when ready.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ToolChoice(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    if (selected) {
        Button(
            onClick = onClick, modifier = modifier.height(44.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SagePrimary, contentColor = PureWhite)
        ) { Text(label, style = MaterialTheme.typography.labelMedium) }
    } else {
        OutlinedButton(
            onClick = onClick, modifier = modifier.height(44.dp),
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
        ) { Text(label, style = MaterialTheme.typography.labelMedium) }
    }
}
