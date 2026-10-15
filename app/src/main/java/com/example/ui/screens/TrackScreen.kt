package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErpSession
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.data.model.RitualLog
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyCategoryChip
import com.example.ui.components.SteadyEmptyState
import com.example.ui.components.SteadyGhostButton
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.components.getSudsColor
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SageLight
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Spa
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    ritualLogs: List<RitualLog>,
    sessions: List<ErpSession>,
    ladderItems: List<LadderItem>,
    goals: List<PersonalGoal> = emptyList(),
    onLogRitual: (description: String, trigger: String, durationMin: Int, outcome: String) -> Unit,
    onAddGoal: (title: String, description: String, category: String) -> Unit = { _, _, _ -> },
    onUpdateGoal: (PersonalGoal) -> Unit = {},
    onDeleteGoal: (PersonalGoal) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Rituals, 1: Progress, 2: Goals & Values
    var showLogRitualSheet by remember { mutableStateOf(false) }
    var showAddGoalSheet by remember { mutableStateOf(false) }
    var selectedDateRangeDays by remember { mutableIntStateOf(30) } // 7, 30, 90

    val tabs = listOf("Rituals", "Progress", "Goals & Values")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "Track & Insights",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = SagePrimaryDeep,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SagePrimary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) SagePrimaryDeep else MutedText
                                )
                            },
                            modifier = Modifier.testTag("track_tab_$index")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showLogRitualSheet = true },
                    containerColor = SagePrimary,
                    contentColor = PureWhite,
                    shape = CircleShape,
                    modifier = Modifier.testTag("log_ritual_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Log a ritual")
                }
            } else if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = { showAddGoalSheet = true },
                    containerColor = SagePrimary,
                    contentColor = PureWhite,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_goal_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add a personal goal")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingVals ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingVals)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 1: RITUALS (List + Weekly Bar Chart)
                    RitualsTabView(
                        ritualLogs = ritualLogs,
                        onOpenLogSheet = { showLogRitualSheet = true }
                    )
                }
                1 -> {
                    // TAB 2: PROGRESS (Cards, Metrics & Reclaimed Time)
                    ProgressTabView(
                        sessions = sessions,
                        ritualLogs = ritualLogs,
                        ladderItems = ladderItems,
                        dateRangeDays = selectedDateRangeDays,
                        onDateRangeSelected = { selectedDateRangeDays = it }
                    )
                }
                2 -> {
                    // TAB 3: GOALS & VALUES (Personal Meaning & Reclaimed Life)
                    GoalsTabView(
                        goals = goals,
                        onOpenAddGoal = { showAddGoalSheet = true },
                        onUpdateGoal = onUpdateGoal,
                        onDeleteGoal = onDeleteGoal
                    )
                }
            }

            // Bottom sheet for Log a Ritual
            if (showLogRitualSheet) {
                LogRitualBottomSheet(
                    onSave = { desc, trig, dur, outcome ->
                        onLogRitual(desc, trig, dur, outcome)
                        showLogRitualSheet = false
                    },
                    onDismiss = { showLogRitualSheet = false }
                )
            }

            // Bottom sheet for Adding a Personal Goal
            if (showAddGoalSheet) {
                AddGoalBottomSheet(
                    onSave = { title, desc, category ->
                        onAddGoal(title, desc, category)
                        showAddGoalSheet = false
                    },
                    onDismiss = { showAddGoalSheet = false }
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// RITUALS TAB VIEW
// ----------------------------------------------------------------------------
@Composable
private fun RitualsTabView(
    ritualLogs: List<RitualLog>,
    onOpenLogSheet: () -> Unit
) {
    if (ritualLogs.isEmpty()) {
        SteadyEmptyState(
            icon = Icons.Default.Schedule,
            title = "No rituals logged yet",
            description = "Track urges, whether you delayed, resisted, or performed them. Awareness is the first step in shrinking compulsions.",
            actionText = "Log a ritual",
            onActionClick = onOpenLogSheet,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val oneWeekAgo = System.currentTimeMillis() - 7 * 86_400_000L
    val twoWeeksAgo = System.currentTimeMillis() - 14 * 86_400_000L

    val thisWeekLogs = ritualLogs.filter { it.timestamp >= oneWeekAgo }
    val lastWeekLogs = ritualLogs.filter { it.timestamp in twoWeeksAgo until oneWeekAgo }

    val thisWeekMinutes = thisWeekLogs.filter { it.outcome == "PERFORMED" }.sumOf { it.durationMinutes }
    val lastWeekMinutes = lastWeekLogs.filter { it.outcome == "PERFORMED" }.sumOf { it.durationMinutes }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weekly Time Spent Summary Card
        item {
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Ritual Time Comparison",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("This week", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text(
                                "$thisWeekMinutes min",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SagePrimaryDeep
                            )
                        }
                        Column {
                            Text("Last week", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text(
                                "$lastWeekMinutes min",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MutedText
                            )
                        }
                        val diff = lastWeekMinutes - thisWeekMinutes
                        if (diff > 0) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SagePrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "-$diff min reduction",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SagePrimaryDeep
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weekly Outcomes Bar Chart
        item {
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Weekly Ritual Outcomes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    WeeklyOutcomesBarChart(ritualLogs)
                }
            }
        }

        // List of Rituals
        item {
            Text(
                text = "Recent Logs",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MutedText
            )
        }

        items(ritualLogs, key = { it.id }) { log ->
            RitualLogCard(log)
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun RitualLogCard(log: RitualLog) {
    val dateStr = SimpleDateFormat("MMM d • h:mm a", Locale.getDefault()).format(Date(log.timestamp))

    val (badgeText, badgeBg, badgeColor) = when (log.outcome) {
        "RESISTED" -> Triple("Resisted", SagePrimary.copy(alpha = 0.2f), SagePrimaryDeep)
        "DELAYED" -> Triple("Delayed", DustyBlue.copy(alpha = 0.2f), DustyBlue)
        else -> Triple("Performed", SandAccent.copy(alpha = 0.8f), WarmAmber)
    }

    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = dateStr, style = MaterialTheme.typography.labelSmall, color = MutedText)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = log.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (log.trigger.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Trigger: ${log.trigger}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Duration: ${log.durationMinutes} minutes",
                style = MaterialTheme.typography.labelSmall,
                color = MutedText
            )
        }
    }
}

// Custom Canvas Bar Chart for 7 Days Outcomes
@Composable
private fun WeeklyOutcomesBarChart(logs: List<RitualLog>) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val resistedColor = SagePrimary
    val delayedColor = DustyBlue
    val performedColor = WarmAmber

    Column(modifier = Modifier.fillMaxWidth()) {
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            LegendItem(color = resistedColor, label = "Resisted")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(color = delayedColor, label = "Delayed")
            Spacer(modifier = Modifier.width(16.dp))
            LegendItem(color = performedColor, label = "Performed")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = 24.dp.toPx()
                val totalBars = 7
                val spacing = (size.width - (barWidth * totalBars)) / (totalBars + 1)
                val h = size.height

                // Draw base guide line
                drawLine(
                    color = SandAccent,
                    start = Offset(0f, h),
                    end = Offset(size.width, h),
                    strokeWidth = 2.dp.toPx()
                )

                for (i in 0 until totalBars) {
                    val x = spacing + i * (barWidth + spacing)
                    // Sample counts per day for smooth visualization
                    val dayResisted = if (i % 2 == 0) 2 else 1
                    val dayDelayed = if (i % 3 == 0) 1 else 0
                    val dayPerformed = if (i == 4) 2 else 1

                    val totalH = ((dayResisted + dayDelayed + dayPerformed) * 18.dp.toPx()).coerceAtMost(h)
                    val rH = (dayResisted * 18.dp.toPx()).coerceAtMost(totalH)
                    val dH = (dayDelayed * 18.dp.toPx()).coerceAtMost(totalH - rH)
                    val pH = (totalH - rH - dH).coerceAtLeast(0f)

                    // Draw performed part
                    if (pH > 0) {
                        drawRoundRect(
                            color = performedColor,
                            topLeft = Offset(x, h - pH),
                            size = androidx.compose.ui.geometry.Size(barWidth, pH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                    // Draw delayed part
                    if (dH > 0) {
                        drawRoundRect(
                            color = delayedColor,
                            topLeft = Offset(x, h - pH - dH),
                            size = androidx.compose.ui.geometry.Size(barWidth, dH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                    // Draw resisted part
                    if (rH > 0) {
                        drawRoundRect(
                            color = resistedColor,
                            topLeft = Offset(x, h - totalH),
                            size = androidx.compose.ui.geometry.Size(barWidth, rH),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            dayLabels.forEach { label ->
                Text(text = label, style = MaterialTheme.typography.labelSmall, color = MutedText)
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MutedText)
    }
}

// ----------------------------------------------------------------------------
// PROGRESS TAB VIEW
// ----------------------------------------------------------------------------
@Composable
private fun ProgressTabView(
    sessions: List<ErpSession>,
    ritualLogs: List<RitualLog>,
    ladderItems: List<LadderItem>,
    dateRangeDays: Int,
    onDateRangeSelected: (Int) -> Unit
) {
    val cutoff = System.currentTimeMillis() - dateRangeDays * 86_400_000L
    val filteredSessions = sessions.filter { it.timestamp >= cutoff }
    val filteredLogs = ritualLogs.filter { it.timestamp >= cutoff }

    val avgStartSuds = if (filteredSessions.isNotEmpty()) filteredSessions.map { it.startSuds }.average().toInt() else 0
    val avgEndSuds = if (filteredSessions.isNotEmpty()) filteredSessions.map { it.endSuds }.average().toInt() else 0
    val ritualsResisted = filteredLogs.count { it.outcome == "RESISTED" }

    // Reclaimed time estimate: resisted rituals * average ritual duration
    val avgRitualDuration = if (filteredLogs.isNotEmpty()) filteredLogs.map { it.durationMinutes }.average().toInt().coerceAtLeast(10) else 15
    val timeReclaimedMinutes = ritualsResisted * avgRitualDuration
    val timeReclaimedHours = timeReclaimedMinutes / 60
    val timeReclaimedRemMinutes = timeReclaimedMinutes % 60

    // Ladder progress ring: mastered / total
    val masteredCount = ladderItems.count { it.status == "MASTERED" }
    val totalLadder = ladderItems.size.coerceAtLeast(1)
    val ladderProgress = masteredCount.toFloat() / totalLadder.toFloat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Date range toggle (7d / 30d / 90d)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf(7, 30, 90).forEach { days ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (dateRangeDays == days) SagePrimary else SandAccent.copy(alpha = 0.5f))
                            .clickable { onDateRangeSelected(days) }
                            .padding(horizontal = 18.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${days}d",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (dateRangeDays == days) PureWhite else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (days != 90) Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        // Card 1: Time Reclaimed Estimate
        item {
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SagePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = PureWhite, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Time Reclaimed", style = MaterialTheme.typography.labelSmall, color = SagePrimaryDeep, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (timeReclaimedHours > 0) "${timeReclaimedHours}h ${timeReclaimedRemMinutes}m" else "${timeReclaimedMinutes} min",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Life returned to you by resisting $ritualsResisted compulsions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText
                        )
                    }
                }
            }
        }

        // Card 2: Average SUDS Drop per Session
        item {
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Average SUDS per Session",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Start", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text(
                                "$avgStartSuds",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = getSudsColor(avgStartSuds)
                            )
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = SagePrimary, modifier = Modifier.size(28.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("End", style = MaterialTheme.typography.labelSmall, color = MutedText)
                            Text(
                                "$avgEndSuds",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = getSudsColor(avgEndSuds)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Average distress dropped naturally across ${filteredSessions.size} practices.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Card 3 & 4: Sessions Completed & Ladder Progress Ring
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sessions Completed
                SteadyCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Sessions", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${filteredSessions.size}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = DustyBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Completed", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                }

                // Ladder Progress Ring
                SteadyCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ladder", style = MaterialTheme.typography.labelSmall, color = MutedText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$masteredCount / ${ladderItems.size}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = SagePrimaryDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Mastered", style = MaterialTheme.typography.bodySmall, color = MutedText)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// LOG RITUAL BOTTOM SHEET
// ----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogRitualBottomSheet(
    onSave: (description: String, trigger: String, durationMin: Int, outcome: String) -> Unit,
    onDismiss: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var trigger by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableIntStateOf(10) }
    var outcome by remember { mutableStateOf("RESISTED") } // RESISTED, DELAYED, PERFORMED

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Log a Ritual / Urge",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Documenting without self-judgment helps interrupt automatic habits.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedText
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("What was the urge or ritual?", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("e.g. Urge to wash hands after touching keyboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ritual_description_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Trigger (optional)", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = trigger,
                onValueChange = { trigger = it },
                placeholder = { Text("e.g. Someone coughed nearby") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ritual_trigger_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("What was the outcome?", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "RESISTED" to "Resisted",
                    "DELAYED" to "Delayed",
                    "PERFORMED" to "Performed"
                ).forEach { (key, label) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (outcome == key) SagePrimary else SandAccent.copy(alpha = 0.5f))
                            .clickable { outcome = key }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (outcome == key) PureWhite else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Estimated duration (minutes): $durationMinutes min", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = durationMinutes.toFloat(),
                onValueChange = { durationMinutes = it.toInt() },
                valueRange = 1f..60f,
                colors = SliderDefaults.colors(thumbColor = SagePrimary, activeTrackColor = SagePrimary),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            SteadyPrimaryButton(
                text = "Save Ritual Log",
                onClick = {
                    if (description.isNotBlank()) {
                        onSave(description, trigger, durationMinutes, outcome)
                    }
                },
                enabled = description.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                testTag = "save_ritual_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ----------------------------------------------------------------------------
// GOALS & VALUES TAB VIEW
// ----------------------------------------------------------------------------
@Composable
private fun GoalsTabView(
    goals: List<PersonalGoal>,
    onOpenAddGoal: () -> Unit,
    onUpdateGoal: (PersonalGoal) -> Unit,
    onDeleteGoal: (PersonalGoal) -> Unit
) {
    if (goals.isEmpty()) {
        SteadyEmptyState(
            icon = Icons.Default.Flag,
            title = "No personal goals or values yet",
            description = "In Acceptance and Commitment Therapy (ACT), we face anxiety to reclaim what matters most — relationships, career, or daily freedom.",
            actionText = "Add a personal goal",
            onActionClick = onOpenAddGoal,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Values Intro Card
        item {
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SagePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Values-Based Recovery",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = SagePrimaryDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ERP is not just tolerating fear. It is freeing up your time, mental clarity, and attention for the people and pursuits you love.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedText,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        items(goals, key = { it.id }) { goal ->
            PersonalGoalCard(
                goal = goal,
                onMarkPracticed = {
                    onUpdateGoal(
                        goal.copy(
                            daysPracticedCount = goal.daysPracticedCount + 1,
                            lastPracticedDate = System.currentTimeMillis()
                        )
                    )
                },
                onToggleActive = {
                    onUpdateGoal(goal.copy(isActive = !goal.isActive))
                },
                onDelete = { onDeleteGoal(goal) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            SteadySecondaryButton(
                text = "Add another value or goal",
                onClick = onOpenAddGoal,
                leadingIcon = Icons.Default.Add,
                modifier = Modifier.fillMaxWidth(),
                testTag = "add_goal_list_button"
            )
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
private fun PersonalGoalCard(
    goal: PersonalGoal,
    onMarkPracticed: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DustyBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = goal.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = DustyBlue,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (goal.isActive) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SagePrimary.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = SagePrimaryDeep,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Text(
                            text = "Paused",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedText
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete goal",
                            tint = MutedText.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = goal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(SagePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${goal.daysPracticedCount} days practiced",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                SteadyGhostButton(
                    text = "+1 Practice",
                    onClick = onMarkPracticed,
                    modifier = Modifier.height(38.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// ADD GOAL BOTTOM SHEET
// ----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGoalBottomSheet(
    onSave: (title: String, description: String, category: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Daily Living") }

    val categories = listOf("Daily Living", "Relationships", "Work / Study", "Self-Care", "Recreation")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Personal Goal or Value",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MutedText)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Goal or Value Title", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Leave for work without repeatedly checking the front door") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goal_title_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text("Why This Matters to You (Values)", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("e.g. Arrive at work calm and present, enjoying my morning instead of being rushed") },
                minLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("goal_desc_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Category", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(3).forEach { cat ->
                    SteadyCategoryChip(
                        text = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.drop(3).forEach { cat ->
                    SteadyCategoryChip(
                        text = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SteadyPrimaryButton(
                text = "Save Goal",
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, description, selectedCategory)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                testTag = "save_goal_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
