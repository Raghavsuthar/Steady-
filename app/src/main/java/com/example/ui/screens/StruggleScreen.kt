package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SteadyBreathingCircle
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyCountdownRing
import com.example.ui.components.SteadyGhostButton
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadyProgressDots
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.components.SteadySudsSlider
import com.example.ui.components.getSudsColor
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.StruggleFlowState
import kotlinx.coroutines.delay

@Composable
fun StruggleScreen(
    state: StruggleFlowState,
    reducedMotion: Boolean,
    onUpdateStartSuds: (Float) -> Unit,
    onSetStruggleType: (String) -> Unit,
    onAdvanceStep: (Int) -> Unit,
    onStartDelayTimer: (Int) -> Unit,
    onExtendDelayTimer: (Int) -> Unit,
    onUpdateEndSuds: (Float) -> Unit,
    onUpdateNote: (String) -> Unit,
    onFinish: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SteadyProgressDots(
                    totalSteps = 5,
                    currentStep = state.step - 1
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("struggle_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit struggle flow",
                        tint = MutedText
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingVals ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            when (state.step) {
                1 -> {
                    // STEP 1: How intense does it feel right now?
                    Step1RateInitialSuds(
                        startSuds = state.startSuds,
                        onSudsChange = onUpdateStartSuds,
                        onContinue = { onAdvanceStep(2) }
                    )
                }
                2 -> {
                    // STEP 2: What's here?
                    Step2WhatsHere(
                        onSelectType = onSetStruggleType
                    )
                }
                3 -> {
                    // STEP 3: Breathing & Acceptance Mantras
                    Step3Breathing(
                        reducedMotion = reducedMotion,
                        onStayWithIt = { onAdvanceStep(5) },
                        onDelayRitual = { onAdvanceStep(4) }
                    )
                }
                4 -> {
                    // STEP 4: Delay Timer & Replacement ideas
                    Step4DelayTimer(
                        delayMinutes = state.delayMinutes,
                        remainingSeconds = state.remainingDelaySeconds,
                        isTimerActive = state.isTimerActive,
                        onStartTimer = onStartDelayTimer,
                        onExtendTimer = onExtendDelayTimer,
                        onProceedToRerate = { onAdvanceStep(5) }
                    )
                }
                5 -> {
                    // STEP 5: Re-rate SUDS & Result
                    Step5ReRateAndSummary(
                        startSuds = state.startSuds.toInt(),
                        endSuds = state.endSuds,
                        note = state.note,
                        onEndSudsChange = onUpdateEndSuds,
                        onNoteChange = onUpdateNote,
                        onFinish = onFinish
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 1
// ----------------------------------------------------------------------------
@Composable
private fun Step1RateInitialSuds(
    startSuds: Float,
    onSudsChange: (Float) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Pause for a moment.",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "How intense does it feel right now?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Take a breath. You don't have to push the feeling away, and you don't have to obey it.",
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(32.dp))

            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(20.dp)) {
                    SteadySudsSlider(
                        value = startSuds,
                        onValueChange = onSudsChange,
                        title = "Current distress level"
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            SteadyPrimaryButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
                testTag = "struggle_step1_continue"
            )
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 2
// ----------------------------------------------------------------------------
@Composable
private fun Step2WhatsHere(
    onSelectType: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Recognizing the experience",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "What is present right now?",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Naming it helps separate you from the OCD alarm.",
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Option A: An urge to do a ritual
            StruggleOptionCard(
                title = "An urge to do a ritual",
                description = "Washing, checking, repeating, asking for reassurance, or mental neutralizing.",
                icon = Icons.Default.PanTool,
                onClick = { onSelectType("RITUAL_URGE") },
                testTag = "struggle_type_ritual"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option B: An intrusive thought or image
            StruggleOptionCard(
                title = "An intrusive thought or image",
                description = "A sudden disturbing 'what if?', an image, or a feeling that feels intolerable.",
                icon = Icons.Default.Psychology,
                onClick = { onSelectType("INTRUSIVE_THOUGHT") },
                testTag = "struggle_type_thought"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Option C: Not sure
            StruggleOptionCard(
                title = "Not sure — just high anxiety",
                description = "A wave of unease or spike without a clear focal point.",
                icon = Icons.Default.Lightbulb,
                onClick = { onSelectType("NOT_SURE") },
                testTag = "struggle_type_notsure"
            )
        }
    }
}

@Composable
private fun StruggleOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    SteadyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        testTag = testTag
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(SandAccent.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SagePrimaryDeep,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 3
// ----------------------------------------------------------------------------
@Composable
private fun Step3Breathing(
    reducedMotion: Boolean,
    onStayWithIt: () -> Unit,
    onDelayRitual: () -> Unit
) {
    val rotatingMantras = remember {
        listOf(
            "This is OCD.",
            "You don't need to solve it.",
            "Let the feeling rise and fall.",
            "Doing nothing is allowed.",
            "Uncertainty is part of this."
        )
    }
    var mantraIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(12000L) // Rotates message gently
            mantraIndex = (mantraIndex + 1) % rotatingMantras.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sit with this moment",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Breathe and let it be.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            SteadyBreathingCircle(
                reducedMotion = reducedMotion,
                promptMessage = rotatingMantras[mantraIndex]
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(SandAccent.copy(alpha = 0.4f))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "“${rotatingMantras[mantraIndex]}”",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SagePrimaryDeep,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SteadyPrimaryButton(
                text = "I want to delay the ritual",
                onClick = onDelayRitual,
                modifier = Modifier.fillMaxWidth(),
                testTag = "struggle_delay_button"
            )

            SteadySecondaryButton(
                text = "I can stay with this feeling now",
                onClick = onStayWithIt,
                modifier = Modifier.fillMaxWidth(),
                testTag = "struggle_stay_button"
            )
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 4
// ----------------------------------------------------------------------------
@Composable
private fun Step4DelayTimer(
    delayMinutes: Int,
    remainingSeconds: Int,
    isTimerActive: Boolean,
    onStartTimer: (Int) -> Unit,
    onExtendTimer: (Int) -> Unit,
    onProceedToRerate: () -> Unit
) {
    var showSuggestions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Urge Surfing",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Delaying weakens the urge.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Delaying teaches your brain it can wait. Anxiety has a natural arc.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isTimerActive && remainingSeconds <= 0) {
                // Timer finished!
                Text(
                    text = "You did it! Time completed without doing the ritual.",
                    style = MaterialTheme.typography.titleMedium,
                    color = SagePrimaryDeep,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                SteadyCountdownRing(
                    remainingSeconds = remainingSeconds,
                    totalSeconds = (delayMinutes * 60).coerceAtLeast(1)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Duration selector buttons if timer not running yet
            if (!isTimerActive) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(5, 10, 15).forEach { min ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (delayMinutes == min) SagePrimary else SandAccent.copy(alpha = 0.6f))
                                .clickable { onStartTimer(min) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "$min min",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (delayMinutes == min) PureWhite else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Extend timer button
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SteadyGhostButton(
                        text = "+ 5 min more",
                        onClick = { onExtendTimer(5) },
                        leadingIcon = Icons.Default.Add,
                        testTag = "extend_delay_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Non-ritual replacement ideas
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showSuggestions = !showSuggestions }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "What could I do instead?",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SagePrimaryDeep
                        )
                        Text(
                            text = if (showSuggestions) "Hide" else "Show ideas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }

                    if (showSuggestions) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ReplacementActivityRow(Icons.AutoMirrored.Filled.DirectionsWalk, "Take a slow 5-minute walk without checking your phone")
                        Spacer(modifier = Modifier.height(8.dp))
                        ReplacementActivityRow(Icons.Default.WaterDrop, "Hold a cold glass of water and notice the physical temperature")
                        Spacer(modifier = Modifier.height(8.dp))
                        ReplacementActivityRow(Icons.Default.MusicNote, "Hum a simple tune aloud or listen to the room's ambient noise")
                        Spacer(modifier = Modifier.height(8.dp))
                        ReplacementActivityRow(Icons.Default.PanTool, "Do a simple unrelated physical task (fold one piece of laundry)")
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            SteadyPrimaryButton(
                text = "Re-rate my distress",
                onClick = onProceedToRerate,
                modifier = Modifier.fillMaxWidth(),
                testTag = "struggle_proceed_rerate"
            )
        }
    }
}

@Composable
private fun ReplacementActivityRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DustyBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ----------------------------------------------------------------------------
// STEP 5
// ----------------------------------------------------------------------------
@Composable
private fun Step5ReRateAndSummary(
    startSuds: Int,
    endSuds: Float,
    note: String,
    onEndSudsChange: (Float) -> Unit,
    onNoteChange: (String) -> Unit,
    onFinish: () -> Unit
) {
    val endSudsInt = endSuds.toInt()
    val dropped = endSudsInt < startSuds

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Reflection & Check-in",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "How does it feel now?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.padding(20.dp)) {
                    SteadySudsSlider(
                        value = endSuds,
                        onValueChange = onEndSudsChange,
                        title = "Distress after delaying / breathing"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Result Card Comparison
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.45f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Before", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text(
                                "$startSuds",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = getSudsColor(startSuds)
                            )
                        }

                        Text("➔", style = MaterialTheme.typography.titleLarge, color = MutedText)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Now", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text(
                                "$endSudsInt",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = getSudsColor(endSudsInt)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (dropped) {
                            "The feeling moved, and you didn't do the ritual. You taught your nervous system that you can tolerate uncertainty."
                        } else {
                            "Staying with it counts, even when the number didn't drop. Every second you withhold a compulsion weakens OCD."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = SagePrimaryDeep,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Optional Note
            Text(
                text = "Optional note (what did you learn?)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = { Text("e.g. The urge was loud for 4 minutes then softened...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("struggle_note_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )
        }

        Column(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)) {
            SteadyPrimaryButton(
                text = "Save and Return Home",
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                testTag = "struggle_save_finish"
            )
        }
    }
}
