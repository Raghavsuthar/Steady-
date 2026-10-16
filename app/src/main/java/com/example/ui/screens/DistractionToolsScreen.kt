package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

private enum class ResetTool { GROUNDING, COLOR_HUNT, CATEGORY_SPRINT }

@Composable
fun DistractionToolsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var selectedTool by remember { mutableStateOf(ResetTool.GROUNDING) }
    val completedSteps = remember { mutableStateListOf<Int>() }
    var colorIndex by remember { mutableIntStateOf(0) }
    var foundObjects by remember { mutableIntStateOf(0) }
    var categoryIndex by remember { mutableIntStateOf(0) }

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
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp),
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
            Box(Modifier.size(42.dp).clip(CircleShape).background(SandAccent.copy(alpha = 0.65f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Spa, contentDescription = null, tint = SagePrimaryDeep)
            }
        }

        SteadyCard(Modifier.fillMaxWidth(), backgroundColor = DustyBlue.copy(alpha = 0.12f), elevation = 0.dp) {
            Column(Modifier.padding(16.dp)) {
                Text("You don't have to make anxiety disappear", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = SagePrimaryDeep)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Choose one brief activity to gently shift your attention. There is no score to pass, and it's okay if anxiety is still present afterwards.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ToolChoice("Ground", selectedTool == ResetTool.GROUNDING, { selectedTool = ResetTool.GROUNDING; completedSteps.clear() }, Modifier.weight(1f))
            ToolChoice("Color hunt", selectedTool == ResetTool.COLOR_HUNT, { selectedTool = ResetTool.COLOR_HUNT }, Modifier.weight(1f))
            ToolChoice("Word game", selectedTool == ResetTool.CATEGORY_SPRINT, { selectedTool = ResetTool.CATEGORY_SPRINT }, Modifier.weight(1f))
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
                    Box(Modifier.size(64.dp).clip(CircleShape).background(SandAccent.copy(alpha = 0.55f)), contentAlignment = Alignment.Center) {
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
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary, contentColor = PureWhite)
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
