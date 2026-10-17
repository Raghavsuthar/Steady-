package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SteadyCategoryChip
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadyProgressDots
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (name: String, themes: List<String>, reminder: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var stepIndex by remember { mutableIntStateOf(0) } // 0: Loop diagram, 1: ERP concept, 2: Therapy support, 3: Consent & Setup
    var consentAccepted by remember { mutableStateOf(false) }
    var preferredName by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf("20:00") }

    val allThemes = listOf(
        "Contamination",
        "Checking",
        "Harm OCD",
        "Symmetry / Order",
        "Unwanted Thoughts",
        "Taboo Themes",
        "Health Anxiety",
        "Other"
    )
    val selectedThemes = remember { mutableStateListOf("Contamination", "Checking") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STEADY",
                    style = MaterialTheme.typography.labelLarge,
                    color = SagePrimaryDeep,
                    letterSpacing = 2.sp
                )
                SteadyProgressDots(
                    totalSteps = 4,
                    currentStep = stepIndex
                )
                if (stepIndex < 3) {
                    TextButton(onClick = { stepIndex = 3 }) {
                        Text("Skip", color = MutedText, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (stepIndex) {
                0 -> {
                    // Slide 1: OCD is a loop
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        OcdCycleIllustration()
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "OCD is a loop, not a character flaw.",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "An intrusive thought sparks anxiety. A compulsion brings brief relief, but it teaches your brain the thought was dangerous — making the loop stronger.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MutedText
                        )
                    }
                }
                1 -> {
                    // Slide 2: ERP breaks the loop
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        ErpBreakIllustration()
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "ERP breaks the loop.",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = ErpClinicalCopy.onboardingExplanation,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MutedText
                        )
                    }
                }
                2 -> {
                    // Slide 3: Steady supports therapy
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(CircleShape)
                                .background(SandAccent.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = SagePrimaryDeep,
                                modifier = Modifier.size(72.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = "Steady supports therapy. It doesn't replace it.",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Steady is a self-help practice tool. We never give reassurance, diagnose conditions, or replace professional care. You are always in control of your pace.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MutedText
                        )
                    }
                }
                3 -> {
                    // Consent & 3 Quick Setup Questions
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Welcome to Steady",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Let's set up your private, local practice space.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedText
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Disclaimer & Consent Checkbox
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SandAccent.copy(alpha = 0.35f))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.Top) {
                                Checkbox(
                                    checked = consentAccepted,
                                    onCheckedChange = { consentAccepted = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SagePrimary,
                                        uncheckedColor = MutedText
                                    ),
                                    modifier = Modifier.testTag("consent_checkbox")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "I understand that Steady is a self-help tool, not a diagnostic service or substitute for medical therapy. All data stays strictly on this device.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question 1: Preferred Name
                        Text(
                            text = "What should we call you? (optional)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = preferredName,
                            onValueChange = { preferredName = it },
                            placeholder = { Text("Your preferred name") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                unfocusedBorderColor = SandAccent
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question 2: OCD Themes
                        Text(
                            text = "Main OCD themes (optional)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            allThemes.forEach { theme ->
                                val isSelected = selectedThemes.contains(theme)
                                SteadyCategoryChip(
                                    text = theme,
                                    isSelected = isSelected,
                                    onClick = {
                                        if (isSelected) selectedThemes.remove(theme)
                                        else selectedThemes.add(theme)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Question 3: Daily reminder time
                        Text(
                            text = "Daily gentle reminder time (optional)",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = reminderTime,
                            onValueChange = { reminderTime = it },
                            placeholder = { Text("e.g. 20:00") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("onboarding_reminder_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SagePrimary,
                                unfocusedBorderColor = SandAccent
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom action button
            if (stepIndex < 3) {
                SteadyPrimaryButton(
                    text = "Continue",
                    onClick = { stepIndex++ },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_continue_button"
                )
            } else {
                SteadyPrimaryButton(
                    text = "Begin Practice",
                    onClick = {
                        onComplete(preferredName, selectedThemes.toList(), reminderTime)
                    },
                    enabled = consentAccepted,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_begin_button"
                )
            }
        }
    }
}

// Visual Circular Diagram for OCD Loop
@Composable
fun OcdCycleIllustration() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .testTag("ocd_loop_diagram"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 6.dp.toPx()
            val radius = (size.minDimension - stroke) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Loop arc with arrow
            drawCircle(
                color = SandAccent,
                radius = radius,
                center = center,
                style = Stroke(width = stroke)
            )

            // Obsession quadrant
            drawArc(
                color = WarmAmber,
                startAngle = 180f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = stroke + 2, cap = StrokeCap.Round)
            )

            // Compulsion quadrant
            drawArc(
                color = DustyBlue,
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = stroke + 2, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Obsession", style = MaterialTheme.typography.labelSmall, color = WarmAmber, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("↺ The Loop ↻", style = MaterialTheme.typography.titleMedium, color = SagePrimaryDeep, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Compulsion", style = MaterialTheme.typography.labelSmall, color = DustyBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ErpBreakIllustration() {
    Box(
        modifier = Modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 8.dp.toPx()
            val radius = (size.minDimension - stroke) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Broken open circle - breaking the loop
            drawArc(
                color = SagePrimary,
                startAngle = 30f,
                sweepAngle = 300f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = SagePrimary,
                modifier = Modifier.size(42.dp)
            )
            Text(
                text = "Resist the ritual",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = SagePrimaryDeep
            )
        }
    }
}
