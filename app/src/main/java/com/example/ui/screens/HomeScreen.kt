package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.getSudsColor
import com.example.ui.components.getSudsLabel
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    suggestedLadderItem: LadderItem?,
    daysPracticedThisMonth: Int,
    ritualsResistedCount: Int,
    activeGoals: List<PersonalGoal>,
    onStruggleClick: () -> Unit,
    onStartPracticeClick: (LadderItem) -> Unit,
    onBuildLadderClick: () -> Unit,
    onLogRitualClick: () -> Unit,
    onOpenLadderClick: () -> Unit,
    onOpenLearnClick: () -> Unit,
    onOpenGoalsClick: () -> Unit,
    onOpenHelpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = rememberGreeting()
    val dateString = rememberFormattedDate()
    var isDailyInsightVisible by remember { mutableStateOf(true) }

    val dailyIntentions = remember {
        listOf(
            "“I don't need to answer the doubt right now. Doing nothing is allowed.”",
            "“Uncertainty is uncomfortable, but it is not dangerous.”",
            "“I can feel anxiety in my body and still choose what matters to me.”",
            "“Every time I delay a ritual, my brain learns it can wait.”"
        )
    }
    val todayIntention = remember { dailyIntentions[Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % dailyIntentions.size] }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Greeting & Nonjudgmental Daily Intention
        Column {
            Text(
                text = dateString.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MutedText,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (userName.isNotBlank()) "$greeting, $userName" else greeting,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = todayIntention,
                style = MaterialTheme.typography.bodyMedium,
                color = SagePrimaryDeep,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }

        // Hero Card: Big Sage Button "I'm struggling right now" (Urgent in-the-moment relief)
        SteadyCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = SandAccent.copy(alpha = 0.45f),
            elevation = 0.dp,
            testTag = "hero_struggle_card"
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = SagePrimaryDeep,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Feeling the urge or intrusive loop?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pause before doing a compulsion. Delay the ritual and let your distress come down naturally without reassurance.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedText,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                SteadyPrimaryButton(
                    text = "I'm struggling right now",
                    onClick = onStruggleClick,
                    leadingIcon = Icons.Default.SelfImprovement,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "home_struggle_button"
                )
            }
        }

        // Today's Practice Card (ERP Next Step)
        SteadyCard(
            modifier = Modifier.fillMaxWidth(),
            testTag = "todays_practice_card"
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Next Planned Practice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SagePrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Fear Ladder",
                            style = MaterialTheme.typography.labelSmall,
                            color = SagePrimaryDeep,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (suggestedLadderItem != null) {
                    val sudsColor = getSudsColor(suggestedLadderItem.baselineSuds)
                    val sudsLabel = getSudsLabel(suggestedLadderItem.baselineSuds)

                    Text(
                        text = suggestedLadderItem.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(sudsColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SUDS ${suggestedLadderItem.baselineSuds} ($sudsLabel)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MutedText
                            )
                        }

                        SteadyPrimaryButton(
                            text = "Start practice",
                            onClick = { onStartPracticeClick(suggestedLadderItem) },
                            leadingIcon = Icons.Default.PlayArrow,
                            modifier = Modifier.height(48.dp),
                            testTag = "start_suggested_practice_button"
                        )
                    }
                } else {
                    Text(
                        text = "Your Fear Ladder is ready for your first situation. Add an item you wish to face to begin your journey.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedText
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SteadyPrimaryButton(
                        text = "Build your ladder",
                        onClick = onBuildLadderClick,
                        leadingIcon = Icons.Default.Add,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "build_ladder_button"
                    )
                }
            }
        }

        // NEW: Personal Goals & Values Card
        val topGoal = activeGoals.firstOrNull()
        SteadyCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenGoalsClick,
            testTag = "home_goals_card"
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DustyBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null, tint = DustyBlue, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Values & Meaningful Goals",
                            style = MaterialTheme.typography.labelSmall,
                            color = DustyBlue,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = topGoal?.title ?: "Define what you are reclaiming life for",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        Text(
                            text = if (topGoal != null) "${topGoal.category} • ${topGoal.daysPracticedCount} practices" else "Align your ERP practice with what matters",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
            }
        }

        // Compact Progress Summary (Compassionate; no streaks to break!)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Practices Completed
            SteadyCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Practiced", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$daysPracticedThisMonth days",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SagePrimaryDeep
                    )
                    Text("this month", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }

            // Rituals Resisted / Delayed
            SteadyCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Compulsions", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$ritualsResistedCount resisted",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DustyBlue
                    )
                    Text("or delayed", style = MaterialTheme.typography.bodySmall, color = MutedText)
                }
            }
        }

        // Dismissible Daily Educational Insight
        AnimatedVisibility(visible = isDailyInsightVisible) {
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = SagePrimaryDeep,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Daily Clinical Insight",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = SagePrimaryDeep
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "When intrusive thoughts spike, notice the urge to rationalize or check. You don't have to figure out what the thought 'means' — doing nothing breaks the loop.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                    IconButton(
                        onClick = { isDailyInsightVisible = false },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Dismiss", tint = MutedText, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Quick Navigation Tiles
        Text(
            text = "Quick tools",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickToolTile(
                title = "Log ritual",
                subtitle = "Track urge & outcome",
                icon = Icons.Default.EditNote,
                onClick = onLogRitualClick,
                modifier = Modifier.weight(1f),
                testTag = "quick_tile_log_ritual"
            )
            QuickToolTile(
                title = "Fear Ladder",
                subtitle = "View hierarchy",
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                onClick = onOpenLadderClick,
                modifier = Modifier.weight(1f),
                testTag = "quick_tile_ladder"
            )
            QuickToolTile(
                title = "Learn",
                subtitle = "ERP insights",
                icon = Icons.Default.Book,
                onClick = onOpenLearnClick,
                modifier = Modifier.weight(1f),
                testTag = "quick_tile_learn"
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Small Footer Link: Need help? Support & crisis lines
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onOpenHelpClick,
                modifier = Modifier.testTag("footer_help_link")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SagePrimaryDeep,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Need help? Support & crisis lines (Tele-MANAS 14416)",
                    style = MaterialTheme.typography.labelMedium,
                    color = SagePrimaryDeep
                )
            }
        }
    }
}

@Composable
private fun QuickToolTile(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    SteadyCard(
        modifier = modifier.height(115.dp),
        onClick = onClick,
        testTag = testTag
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SandAccent.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SagePrimaryDeep,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 4..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
}

@Composable
private fun rememberFormattedDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return sdf.format(Date())
}
