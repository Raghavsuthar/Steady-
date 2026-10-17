package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber

@Composable
fun HelpSafetyScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var groundingStep by remember { mutableIntStateOf(5) } // 5, 4, 3, 2, 1

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("help_safety_back")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SagePrimaryDeep)
                }
                Text(
                    text = "Help & Support",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingVals ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Calm, non-alarming banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SandAccent.copy(alpha = 0.5f))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SagePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PureWhite, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "You are not alone.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = ErpClinicalCopy.supportBanner,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
            }

            // TODO(clinician-review): Verify current Tele-MANAS contact details before release.
            // Crisis & Helpline 1: Tele-MANAS (14416)
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                testTag = "tele_manas_card"
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tele-MANAS Mental Health Helpline",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toll-free 24/7 mental health support (India)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "14416 / 1800-891-4416",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SagePrimaryDeep
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    SteadyPrimaryButton(
                        text = "Call",
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:14416"))
                            context.startActivity(dialIntent)
                        },
                        leadingIcon = Icons.Default.Call,
                        modifier = Modifier.height(48.dp),
                        testTag = "call_tele_manas_button"
                    )
                }
            }

            // Crisis & Emergency Services
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Immediate Emergency Services",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ErpClinicalCopy.crisisGuidance,
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    SteadySecondaryButton(
                        text = "Call 112",
                        onClick = {
                            val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(dialIntent)
                        },
                        leadingIcon = Icons.Default.LocalHospital,
                        modifier = Modifier.height(48.dp),
                        testTag = "call_emergency_button"
                    )
                }
            }

            // Clinician Note
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Contact Your Clinician",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "If you are working with an ERP therapist, psychiatrist, or counselor, consider texting or emailing them to schedule a check-in. They know your personal treatment plan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                }
            }

            // 5-4-3-2-1 Grounding Exercise
            Text(
                text = "5-4-3-2-1 Grounding Exercise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.35f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val (stepNumber, stepIcon, stepTitle, stepDescription) = when (groundingStep) {
                        5 -> Quad(5, Icons.Default.Visibility, "5 Things you can see", "Look around you right now. Spot 5 distinct objects (a shadow, a pen, a pattern on the floor).")
                        4 -> Quad(4, Icons.Default.PanTool, "4 Things you can feel", "Notice 4 physical textures (the fabric of your clothes, the surface under your feet, the temperature of your hands).")
                        3 -> Quad(3, Icons.Default.Hearing, "3 Things you can hear", "Listen closely for 3 sounds (traffic in the distance, a fan, the hum of a computer).")
                        2 -> Quad(2, Icons.Default.WbSunny, "2 Things you can smell", "Notice 2 scents in the air (soap, paper, fresh air) or take two deep sensory breaths.")
                        else -> Quad(1, Icons.Default.SelfImprovement, "1 Thing you can taste & 1 deep breath", "Notice any lingering taste in your mouth, unclench your jaw, and let your shoulders drop.")
                    }

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SagePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = stepIcon, contentDescription = null, tint = PureWhite, modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = stepTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SagePrimaryDeep,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stepDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (groundingStep > 1) {
                            SteadyPrimaryButton(
                                text = "Next Step (${groundingStep - 1})",
                                onClick = { groundingStep-- },
                                modifier = Modifier.height(48.dp)
                            )
                        } else {
                            SteadyPrimaryButton(
                                text = "Restart Grounding",
                                onClick = { groundingStep = 5 },
                                modifier = Modifier.height(48.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
