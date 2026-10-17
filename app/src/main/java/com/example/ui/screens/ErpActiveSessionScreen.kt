package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyConfirmDialog
import com.example.ui.components.SteadyCountdownRing
import com.example.ui.components.SteadyDistressLineChart
import com.example.ui.components.SteadyGhostButton
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadyProgressDots
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.components.SteadySudsSlider
import com.example.ui.components.getSudsColor
import com.example.ui.components.getSudsLabel
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.ErpActiveState

@Composable
fun ErpActiveSessionScreen(
    state: ErpActiveState,
    onUpdatePrediction: (predictionText: String, beliefStart: Int, preSuds: Int) -> Unit,
    onBeginExposure: (durationMinutes: Int) -> Unit,
    onTogglePause: () -> Unit,
    onSubmitCheckInSuds: (Int) -> Unit,
    onEndEarly: () -> Unit,
    onCompleteSession: (learnedNote: String, postBelief: Int, postSuds: Int) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEndEarlyConfirm by remember { mutableStateOf(false) }

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
                    totalSteps = 3,
                    currentStep = state.step - 1
                )
                IconButton(
                    onClick = {
                        if (state.step == 2) {
                            showEndEarlyConfirm = true
                        } else {
                            onClose()
                        }
                    },
                    modifier = Modifier.testTag("erp_session_close")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Exit session", tint = MutedText)
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
                    // STEP 1: PREPARATION & EXPECTANCY PREDICTION
                    ErpPrepStep(
                        itemTitle = state.ladderItem?.title ?: "Exposure Situation",
                        itemNotes = state.ladderItem?.notes ?: "",
                        initialBaselineSuds = state.startSuds,
                        onBegin = { text, belief, suds, duration ->
                            onUpdatePrediction(text, belief, suds)
                            onBeginExposure(duration)
                        }
                    )
                }
                2 -> {
                    // STEP 2: ACTIVE EXPOSURE (Timer, check-in dialog, chart, pause)
                    ErpActiveExposureStep(
                        state = state,
                        onTogglePause = onTogglePause,
                        onEndEarly = { showEndEarlyConfirm = true }
                    )
                }
                3 -> {
                    // STEP 3: COMPLETION & EXPECTANCY VIOLATION SUMMARY
                    ErpCompletionStep(
                        state = state,
                        onComplete = onCompleteSession
                    )
                }
            }

            // Periodic 3-Minute Check-in Dialog
            if (state.isCheckInPopupVisible) {
                ErpCheckInPopup(
                    currentTempSuds = state.tempCheckInSuds,
                    onSubmit = onSubmitCheckInSuds
                )
            }

            // Confirm End Early Dialog
            if (showEndEarlyConfirm) {
                SteadyConfirmDialog(
                    title = "End session early?",
                    message = ErpClinicalCopy.endEarlyMessage,
                    confirmText = "End and Reflect",
                    dismissText = "Continue Exposure",
                    onConfirm = {
                        showEndEarlyConfirm = false
                        onEndEarly()
                    },
                    onDismiss = { showEndEarlyConfirm = false }
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 1: PREPARATION
// ----------------------------------------------------------------------------
@Composable
private fun ErpPrepStep(
    itemTitle: String,
    itemNotes: String,
    initialBaselineSuds: Int,
    onBegin: (predictionText: String, belief: Int, preSuds: Int, duration: Int) -> Unit
) {
    var predictionText by remember { mutableStateOf("") }
    var beliefPercent by remember { mutableFloatStateOf(80f) }
    var preSuds by remember { mutableFloatStateOf(initialBaselineSuds.toFloat()) }
    var selectedDuration by remember { mutableIntStateOf(20) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "ERP Preparation",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = itemTitle,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Clinical Instructions Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SandAccent.copy(alpha = 0.45f))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SagePrimaryDeep,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = ErpClinicalCopy.prepInstruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (itemNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reminder: $itemNotes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prediction: What do you fear will happen?
            Text(
                text = "What do you fear will happen if you skip the ritual?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = predictionText,
                onValueChange = { predictionText = it },
                placeholder = { Text("e.g. The anxiety will be unbearable and I won't be able to sleep...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("erp_prediction_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Belief Rating (0 - 100%)
            Text(
                text = "How strongly do you believe this right now? (${beliefPercent.toInt()}%)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = beliefPercent,
                onValueChange = { beliefPercent = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(thumbColor = DustyBlue, activeTrackColor = DustyBlue),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Starting SUDS
            SteadySudsSlider(
                value = preSuds,
                onValueChange = { preSuds = it },
                title = "Current baseline distress before starting"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer length (15, 20, 30 min)
            Text(
                text = "Session length",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(15, 20, 30).forEach { dur ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedDuration == dur) SagePrimary else SandAccent.copy(alpha = 0.5f))
                            .border(1.dp, if (selectedDuration == dur) SagePrimary else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { selectedDuration = dur }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "$dur min",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDuration == dur) PureWhite else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)) {
            SteadyPrimaryButton(
                text = "Begin Exposure",
                onClick = {
                    onBegin(predictionText, beliefPercent.toInt(), preSuds.toInt(), selectedDuration)
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "erp_begin_button"
            )
        }
    }
}

// ----------------------------------------------------------------------------
// STEP 2: ACTIVE EXPOSURE
// ----------------------------------------------------------------------------
@Composable
private fun ErpActiveExposureStep(
    state: ErpActiveState,
    onTogglePause: () -> Unit,
    onEndEarly: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Persistent soft reminder
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SagePrimary.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = ErpClinicalCopy.activeReminder,
                    style = MaterialTheme.typography.labelMedium,
                    color = SagePrimaryDeep,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = state.ladderItem?.title ?: "Exposure Situation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Countdown Timer Ring
            SteadyCountdownRing(
                remainingSeconds = state.remainingSeconds,
                totalSeconds = state.totalSeconds,
                color = if (state.isRunning) SagePrimary else DustyBlue
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Distress Line Chart (Live over checkins)
            SteadyDistressLineChart(
                checkIns = state.checkIns,
                heightDp = 140.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val latestSuds = state.checkIns.lastOrNull()?.suds ?: state.startSuds
            Text(
                text = "Current SUDS: $latestSuds (${getSudsLabel(latestSuds)})",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = getSudsColor(latestSuds)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SteadyPrimaryButton(
                text = if (state.isRunning) "Pause" else "Resume",
                onClick = onTogglePause,
                leadingIcon = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                modifier = Modifier.fillMaxWidth(),
                testTag = "erp_pause_resume_button"
            )

            SteadySecondaryButton(
                text = "End early (with kindness)",
                onClick = onEndEarly,
                modifier = Modifier.fillMaxWidth(),
                testTag = "erp_end_early_button"
            )
        }
    }
}

// ----------------------------------------------------------------------------
// CHECK-IN POPUP (Every 3 minutes)
// ----------------------------------------------------------------------------
@Composable
private fun ErpCheckInPopup(
    currentTempSuds: Float,
    onSubmit: (Int) -> Unit
) {
    var checkInVal by remember { mutableFloatStateOf(currentTempSuds) }

    AlertDialog(
        onDismissRequest = { /* forces rating */ },
        title = {
            Text(
                text = "3-Minute Check-in",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Notice where your distress is right now without judgment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText
                )
                Spacer(modifier = Modifier.height(16.dp))
                SteadySudsSlider(
                    value = checkInVal,
                    onValueChange = { checkInVal = it },
                    title = "Current SUDS"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(checkInVal.toInt()) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
            ) {
                Text("Save and Continue", color = PureWhite, fontWeight = FontWeight.SemiBold)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// ----------------------------------------------------------------------------
// STEP 3: COMPLETION & EXPECTANCY VIOLATION
// ----------------------------------------------------------------------------
@Composable
private fun ErpCompletionStep(
    state: ErpActiveState,
    onComplete: (learnedNote: String, postBelief: Int, postSuds: Int) -> Unit
) {
    var postSuds by remember { mutableFloatStateOf(state.endSuds.toFloat()) }
    var postBelief by remember { mutableFloatStateOf(25f) }
    var learnedNote by remember { mutableStateOf("") }

    val startSuds = state.startSuds
    val dropped = postSuds.toInt() < startSuds

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Practice Reflection",
                style = MaterialTheme.typography.labelLarge,
                color = SagePrimaryDeep
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You practiced facing uncertainty.",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ErpClinicalCopy.completionSummary,
                style = MaterialTheme.typography.bodyLarge,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Distress Chart
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Session Distress Arc",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SteadyDistressLineChart(
                        checkIns = state.checkIns,
                        heightDp = 130.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Final SUDS rating
            SteadySudsSlider(
                value = postSuds,
                onValueChange = { postSuds = it },
                title = "Final Distress (SUDS)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recompute belief rating (Expectancy Violation)
            Text(
                text = "How strongly do you believe your feared prediction now? (${postBelief.toInt()}%)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (state.predictionText.isNotBlank()) {
                Text(
                    text = "Feared prediction: \"${state.predictionText}\" (was ${state.predictionBeliefStart}%)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }
            Slider(
                value = postBelief,
                onValueChange = { postBelief = it },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(thumbColor = DustyBlue, activeTrackColor = DustyBlue),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // What actually happened note
            Text(
                text = "What actually happened? (Inhibitory Learning Note)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = learnedNote,
                onValueChange = { learnedNote = it },
                placeholder = { Text(ErpClinicalCopy.learningNotePlaceholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("erp_learned_note_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )
        }

        Column(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)) {
            SteadyPrimaryButton(
                text = "Save & Finish Practice",
                onClick = {
                    onComplete(learnedNote, postBelief.toInt(), postSuds.toInt())
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "erp_complete_save_button"
            )
        }
    }
}
