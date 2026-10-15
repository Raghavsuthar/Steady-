package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ErpSession
import com.example.data.model.LadderItem
import com.example.data.model.RitualLog
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClinicianReportScreen(
    ladderItems: List<LadderItem>,
    sessions: List<ErpSession>,
    ritualLogs: List<RitualLog>,
    userName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var dateRangeDays by remember { mutableIntStateOf(30) } // 7, 30, 90

    // Section inclusion toggles
    var includeLadder by remember { mutableStateOf(true) }
    var includeSessions by remember { mutableStateOf(true) }
    var includeRituals by remember { mutableStateOf(true) }
    var includeLearnedNotes by remember { mutableStateOf(true) }

    val cutoff = System.currentTimeMillis() - dateRangeDays * 86_400_000L
    val filteredSessions = sessions.filter { it.timestamp >= cutoff }
    val filteredRituals = ritualLogs.filter { it.timestamp >= cutoff }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("clinician_report_back")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SagePrimaryDeep)
                }
                Text(
                    text = "Share with Clinician",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Privacy notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SandAccent.copy(alpha = 0.45f))
                    .padding(16.dp)
            ) {
                Text(
                    text = "You choose what to share. Nothing is sent automatically or uploaded to any server. This generates a clear one-page clinical summary.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Date Range Selection
            Text("Date Range", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(7, 30, 90).forEach { days ->
                    SteadySecondaryButton(
                        text = "Last $days Days",
                        onClick = { dateRangeDays = days },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    )
                }
            }

            // Section Toggles
            Text("Sections to Include", style = MaterialTheme.typography.labelLarge)
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    ReportToggleRow("Fear Ladder Hierarchy", includeLadder) { includeLadder = it }
                    ReportToggleRow("ERP Practice Sessions & Distress Drop", includeSessions) { includeSessions = it }
                    ReportToggleRow("Ritual Log Outcomes & Reclaimed Time", includeRituals) { includeRituals = it }
                    ReportToggleRow("Patient Inhibitory Learning Notes", includeLearnedNotes) { includeLearnedNotes = it }
                }
            }

            // Printable Preview Card
            Text("Report Preview", style = MaterialTheme.typography.labelLarge)
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = PureWhite,
                elevation = 1.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "STEADY ERP PRACTICE REPORT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SagePrimaryDeep,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Client: ${if (userName.isNotBlank()) userName else "Self-help Client"} • Date: ${SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date())} (Last $dateRangeDays days)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (includeLadder) {
                        Text("1. FEAR LADDER HIERARCHY", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        ladderItems.take(4).forEach { item ->
                            Text(
                                "• [SUDS ${item.baselineSuds}] ${item.title} (${item.status})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (includeSessions) {
                        Text("2. ERP SESSIONS SUMMARY", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Total Completed: ${filteredSessions.size} sessions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        val avgStart = if (filteredSessions.isNotEmpty()) filteredSessions.map { it.startSuds }.average().toInt() else 0
                        val avgEnd = if (filteredSessions.isNotEmpty()) filteredSessions.map { it.endSuds }.average().toInt() else 0
                        Text(
                            "Average Distress Reduction: SUDS $avgStart ➔ SUDS $avgEnd",
                            style = MaterialTheme.typography.bodySmall,
                            color = SagePrimaryDeep,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (includeRituals) {
                        Text("3. RITUAL MONITORING", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        val resisted = filteredRituals.count { it.outcome == "RESISTED" }
                        val delayed = filteredRituals.count { it.outcome == "DELAYED" }
                        val performed = filteredRituals.count { it.outcome == "PERFORMED" }
                        Text(
                            "Resisted: $resisted | Delayed: $delayed | Performed: $performed",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (includeLearnedNotes) {
                        Text("4. INHIBITORY LEARNING HIGHLIGHTS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        val notes = filteredSessions.mapNotNull { it.learnedNote.takeIf { n -> n.isNotBlank() } }.take(2)
                        if (notes.isNotEmpty()) {
                            notes.forEach { note ->
                                Text("• \"$note\"", style = MaterialTheme.typography.bodySmall, color = MutedText)
                            }
                        } else {
                            Text("• No notes recorded in this timeframe", style = MaterialTheme.typography.bodySmall, color = MutedText)
                        }
                    }
                }
            }

            // Share & Export Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SteadyPrimaryButton(
                    text = "Share Report",
                    onClick = {
                        val reportText = buildClinicianReportText(
                            userName = userName,
                            dateRangeDays = dateRangeDays,
                            includeLadder = includeLadder,
                            ladderItems = ladderItems,
                            includeSessions = includeSessions,
                            sessions = filteredSessions,
                            includeRituals = includeRituals,
                            rituals = filteredRituals,
                            includeLearnedNotes = includeLearnedNotes
                        )
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Steady ERP Clinician Summary - $userName")
                            putExtra(Intent.EXTRA_TEXT, reportText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Clinician Report"))
                    },
                    leadingIcon = Icons.Default.Share,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "share_clinician_report_button"
                )
            }
        }
    }
}

@Composable
private fun ReportToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = SagePrimary)
        )
    }
}

private fun buildClinicianReportText(
    userName: String,
    dateRangeDays: Int,
    includeLadder: Boolean,
    ladderItems: List<LadderItem>,
    includeSessions: Boolean,
    sessions: List<ErpSession>,
    includeRituals: Boolean,
    rituals: List<RitualLog>,
    includeLearnedNotes: Boolean
): String {
    val sb = StringBuilder()
    sb.appendLine("STEADY ERP CLINICIAN SUMMARY REPORT")
    sb.appendLine("Client: ${if (userName.isNotBlank()) userName else "Self-help Client"}")
    sb.appendLine("Timeframe: Last $dateRangeDays days")
    sb.appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())}")
    sb.appendLine("----------------------------------------")

    if (includeLadder) {
        sb.appendLine("\n1. FEAR LADDER HIERARCHY")
        ladderItems.forEach {
            sb.appendLine("- [SUDS ${it.baselineSuds}] ${it.title} (Status: ${it.status}, Sessions: ${it.sessionCount})")
        }
    }

    if (includeSessions) {
        sb.appendLine("\n2. ERP SESSIONS COMPLETED (${sessions.size})")
        val avgStart = if (sessions.isNotEmpty()) sessions.map { it.startSuds }.average().toInt() else 0
        val avgEnd = if (sessions.isNotEmpty()) sessions.map { it.endSuds }.average().toInt() else 0
        sb.appendLine("Average SUDS drop: $avgStart -> $avgEnd")
        sessions.take(5).forEach {
            sb.appendLine("- ${it.ladderItemTitle}: Start ${it.startSuds} -> End ${it.endSuds} (${it.durationMinutes} min)")
        }
    }

    if (includeRituals) {
        sb.appendLine("\n3. RITUAL MONITORING")
        val resisted = rituals.count { it.outcome == "RESISTED" }
        val delayed = rituals.count { it.outcome == "DELAYED" }
        val performed = rituals.count { it.outcome == "PERFORMED" }
        sb.appendLine("Resisted: $resisted | Delayed: $delayed | Performed: $performed")
    }

    if (includeLearnedNotes) {
        sb.appendLine("\n4. INHIBITORY LEARNING NOTES")
        sessions.mapNotNull { it.learnedNote.takeIf { n -> n.isNotBlank() } }.forEach {
            sb.appendLine("- \"$it\"")
        }
    }

    sb.appendLine("\n----------------------------------------")
    sb.appendLine("Note: Steady is an ERP self-help companion and does not offer clinical diagnosis or replacement for licensed care.")
    return sb.toString()
}
