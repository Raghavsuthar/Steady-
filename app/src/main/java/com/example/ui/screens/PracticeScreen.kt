package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ErpSession
import com.example.data.model.ImaginalScript
import com.example.data.model.LadderItem
import com.example.data.model.SudsCheckIn
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyCategoryChip
import com.example.ui.components.SteadyConfirmDialog
import com.example.ui.components.SteadyCountdownRing
import com.example.ui.components.SteadyDistressLineChart
import com.example.ui.components.SteadyEmptyState
import com.example.ui.components.SteadyGhostButton
import com.example.ui.components.SteadyPrimaryButton
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    ladderItems: List<LadderItem>,
    sessions: List<ErpSession>,
    imaginalScripts: List<ImaginalScript>,
    aiCoachEnabled: Boolean,
    onStartErpSession: (LadderItem) -> Unit,
    onAddOrUpdateLadderItem: (id: Long, title: String, category: String, suds: Int, notes: String) -> Unit,
    onDeleteLadderItem: (LadderItem) -> Unit,
    onSeedExamples: () -> Unit,
    onSaveImaginalScript: (title: String, text: String, audioPath: String?) -> Unit,
    onDeleteImaginalScript: (ImaginalScript) -> Unit,
    onOpenAiCoach: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Fear Ladder, 1: Sessions, 2: Imaginal
    var showAddLadderSheet by remember { mutableStateOf(false) }
    var editingLadderItem by remember { mutableStateOf<LadderItem?>(null) }
    var itemToDelete by remember { mutableStateOf<LadderItem?>(null) }

    // Imaginal script dialog state
    var showAddImaginalDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Fear Ladder", "Sessions", "Imaginal")

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "Exposure & Response Prevention",
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
                            modifier = Modifier.testTag("practice_tab_$index")
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = {
                        editingLadderItem = null
                        showAddLadderSheet = true
                    },
                    containerColor = SagePrimary,
                    contentColor = PureWhite,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_ladder_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add exposure situation")
                }
            } else if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = { showAddImaginalDialog = true },
                    containerColor = SagePrimary,
                    contentColor = PureWhite,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_imaginal_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add imaginal script")
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
                    // TAB 1: FEAR LADDER
                    FearLadderView(
                        ladderItems = ladderItems,
                        onStartSession = onStartErpSession,
                        onEditItem = {
                            editingLadderItem = it
                            showAddLadderSheet = true
                        },
                        onDeleteItem = { itemToDelete = it },
                        onSeedExamples = onSeedExamples,
                        onAddNew = {
                            editingLadderItem = null
                            showAddLadderSheet = true
                        }
                    )
                }
                1 -> {
                    // TAB 2: SESSIONS HISTORY
                    SessionsHistoryView(
                        sessions = sessions,
                        onStartLadderPractice = { selectedTab = 0 }
                    )
                }
                2 -> {
                    // TAB 3: IMAGINAL EXPOSURE
                    ImaginalExposureView(
                        scripts = imaginalScripts,
                        onAddNewScript = { showAddImaginalDialog = true },
                        onDeleteScript = onDeleteImaginalScript,
                        onOpenAiCoach = onOpenAiCoach,
                        aiCoachEnabled = aiCoachEnabled
                    )
                }
            }

            // Bottom sheet for Add / Edit Ladder Item
            if (showAddLadderSheet) {
                AddEditLadderBottomSheet(
                    existingItem = editingLadderItem,
                    aiCoachEnabled = aiCoachEnabled,
                    onSave = { id, title, category, suds, notes ->
                        onAddOrUpdateLadderItem(id, title, category, suds, notes)
                        showAddLadderSheet = false
                    },
                    onDismiss = { showAddLadderSheet = false },
                    onOpenAiCoach = onOpenAiCoach
                )
            }

            // Confirm Delete Dialog
            itemToDelete?.let { item ->
                SteadyConfirmDialog(
                    title = "Remove Situation?",
                    message = "Are you sure you want to remove \"${item.title}\" from your ladder?",
                    confirmText = "Remove",
                    onConfirm = {
                        onDeleteLadderItem(item)
                        itemToDelete = null
                    },
                    onDismiss = { itemToDelete = null }
                )
            }

            // Add Imaginal Script Dialog
            if (showAddImaginalDialog) {
                AddImaginalScriptDialog(
                    aiCoachEnabled = aiCoachEnabled,
                    onSave = { title, text, audioPath ->
                        onSaveImaginalScript(title, text, audioPath)
                        showAddImaginalDialog = false
                    },
                    onDismiss = { showAddImaginalDialog = false },
                    onOpenAiCoach = onOpenAiCoach
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------
// FEAR LADDER VIEW
// ----------------------------------------------------------------------------
@Composable
private fun FearLadderView(
    ladderItems: List<LadderItem>,
    onStartSession: (LadderItem) -> Unit,
    onEditItem: (LadderItem) -> Unit,
    onDeleteItem: (LadderItem) -> Unit,
    onSeedExamples: () -> Unit,
    onAddNew: () -> Unit
) {
    if (ladderItems.isEmpty()) {
        SteadyEmptyState(
            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
            title = "Your Fear Ladder is empty",
            description = "A Fear Ladder organizes exposure situations from easiest to hardest. Practice facing them one step at a time without doing rituals.",
            actionText = "Start with examples",
            onActionClick = onSeedExamples,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    // Identify lowest-SUDS unmastered item as "Next Step"
    val nextStepItem = ladderItems
        .filter { it.status != "MASTERED" }
        .minByOrNull { it.baselineSuds }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Suggested Next Step Banner
        if (nextStepItem != null) {
            item {
                SteadyCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = SandAccent.copy(alpha = 0.5f),
                    elevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SagePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = PureWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Recommended Next Step",
                                style = MaterialTheme.typography.labelSmall,
                                color = SagePrimaryDeep,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = nextStepItem.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = "SUDS ${nextStepItem.baselineSuds} • Lowest unmastered",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedText
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        SteadyPrimaryButton(
                            text = "Practice",
                            onClick = { onStartSession(nextStepItem) },
                            modifier = Modifier.height(44.dp)
                        )
                    }
                }
            }
        }

        // Top bar summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${ladderItems.size} situations (sorted by distress)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MutedText
                )
                TextButton(onClick = onSeedExamples) {
                    Text("Add examples", color = SagePrimaryDeep, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        items(ladderItems, key = { it.id }) { item ->
            LadderItemCard(
                item = item,
                onStartPractice = { onStartSession(item) },
                onEdit = { onEditItem(item) },
                onDelete = { onDeleteItem(item) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(72.dp)) // Padding for FAB
        }
    }
}

@Composable
private fun LadderItemCard(
    item: LadderItem,
    onStartPractice: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val sudsColor = getSudsColor(item.baselineSuds)
    val sudsLabel = getSudsLabel(item.baselineSuds)

    SteadyCard(
        modifier = Modifier.fillMaxWidth(),
        testTag = "ladder_item_${item.id}"
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    SteadyCategoryChip(text = item.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    // Status Badge
                    val (statusText, statusBg, statusColor) = when (item.status) {
                        "MASTERED" -> Triple("Mastered", SagePrimary.copy(alpha = 0.2f), SagePrimaryDeep)
                        "IN_PROGRESS" -> Triple("In progress", DustyBlue.copy(alpha = 0.2f), DustyBlue)
                        else -> Triple("Not started", SandAccent.copy(alpha = 0.6f), MutedText)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // SUDS Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(sudsColor.copy(alpha = 0.18f))
                        .border(1.dp, sudsColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "SUDS ${item.baselineSuds}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = sudsColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${item.sessionCount} sessions",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                    if (item.lastSuds != null) {
                        Text(
                            text = " • Last SUDS: ${item.lastSuds}",
                            style = MaterialTheme.typography.labelSmall,
                            color = getSudsColor(item.lastSuds)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = MutedText, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MutedText, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    SteadyPrimaryButton(
                        text = "Practice",
                        onClick = onStartPractice,
                        modifier = Modifier.height(44.dp)
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// ADD / EDIT LADDER BOTTOM SHEET
// ----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLadderBottomSheet(
    existingItem: LadderItem?,
    aiCoachEnabled: Boolean,
    onSave: (id: Long, title: String, category: String, suds: Int, notes: String) -> Unit,
    onDismiss: () -> Unit,
    onOpenAiCoach: ((String) -> Unit)?
) {
    var title by remember { mutableStateOf(existingItem?.title ?: "") }
    var category by remember { mutableStateOf(existingItem?.category ?: "Contamination") }
    var suds by remember { mutableStateOf((existingItem?.baselineSuds ?: 50).toFloat()) }
    var notes by remember { mutableStateOf(existingItem?.notes ?: "") }

    val categories = listOf(
        "Contamination",
        "Checking",
        "Harm",
        "Symmetry / Order",
        "Unwanted Thoughts",
        "Taboo Themes",
        "Other"
    )

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingItem == null) "Add Exposure Situation" else "Edit Situation",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                if (aiCoachEnabled && onOpenAiCoach != null) {
                    TextButton(onClick = { onOpenAiCoach(title) }) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("AI Phrasing", color = SagePrimaryDeep, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Situation / Trigger", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Touching kitchen sponge without hand sanitizer for 10 min") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ladder_title_input"),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    SteadyCategoryChip(
                        text = cat,
                        isSelected = category == cat,
                        onClick = { category = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SteadySudsSlider(
                value = suds,
                onValueChange = { suds = it },
                title = "Baseline Distress (SUDS)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Response Prevention Instructions (Notes)", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("e.g. Do not ask anyone for reassurance. Sit with hands on lap.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ladder_notes_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = SandAccent
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            SteadyPrimaryButton(
                text = if (existingItem == null) "Add to Ladder" else "Save Changes",
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(existingItem?.id ?: 0L, title, category, suds.toInt(), notes)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                testTag = "save_ladder_item_button"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ----------------------------------------------------------------------------
// SESSIONS HISTORY VIEW
// ----------------------------------------------------------------------------
@Composable
private fun SessionsHistoryView(
    sessions: List<ErpSession>,
    onStartLadderPractice: () -> Unit
) {
    if (sessions.isEmpty()) {
        SteadyEmptyState(
            icon = Icons.Default.HourglassBottom,
            title = "No practice sessions yet",
            description = "Completed ERP sessions with distress ratings and expectancy violation notes will appear here.",
            actionText = "Start an ERP practice",
            onActionClick = onStartLadderPractice,
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
        items(sessions, key = { it.id }) { session ->
            SessionHistoryCard(session)
        }
    }
}

@Composable
private fun SessionHistoryCard(session: ErpSession) {
    val dateStr = SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(session.timestamp))

    // Parse checkins
    val checkIns = session.checkInsJson.split(",")
        .mapNotNull {
            val parts = it.split(":")
            if (parts.size == 2) {
                val m = parts[0].toIntOrNull() ?: 0
                val s = parts[1].toIntOrNull() ?: 0
                SudsCheckIn(m, s)
            } else null
        }

    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MutedText
                )
                Text(
                    text = "${session.durationMinutes} min",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = SagePrimaryDeep
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = session.ladderItemTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Start vs End SUDS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Start SUDS", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(
                        "${session.startSuds}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = getSudsColor(session.startSuds)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = SagePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("End SUDS", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(
                        "${session.endSuds}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = getSudsColor(session.endSuds)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Feared outcome", style = MaterialTheme.typography.labelSmall, color = MutedText)
                    Text(
                        "${session.predictionBeliefStart}% ➔ ${session.predictionBeliefEnd}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (checkIns.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                SteadyDistressLineChart(
                    checkIns = checkIns,
                    heightDp = 120.dp
                )
            }

            if (session.learnedNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SandAccent.copy(alpha = 0.35f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Learned: \"${session.learnedNote}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// IMAGINAL EXPOSURE VIEW
// ----------------------------------------------------------------------------
@Composable
private fun ImaginalExposureView(
    scripts: List<ImaginalScript>,
    onAddNewScript: () -> Unit,
    onDeleteScript: (ImaginalScript) -> Unit,
    onOpenAiCoach: ((String) -> Unit)?,
    aiCoachEnabled: Boolean
) {
    if (scripts.isEmpty()) {
        SteadyEmptyState(
            icon = Icons.Default.RecordVoiceOver,
            title = "No Imaginal Scripts",
            description = "Imaginal exposure involves writing or recording a narrative of your feared outcome in the present tense, sitting with the uncertainty without providing reassurance.",
            actionText = "Create an imaginal script",
            onActionClick = onAddNewScript,
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
        item {
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.4f),
                elevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Guidelines for Imaginal Exposure",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SagePrimaryDeep
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Describe the feared consequence in the present tense.\n• Do not add reassuring caveats or happy endings.\n• Read or listen repeatedly while allowing the anxiety to rise and fall.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        items(scripts, key = { it.id }) { script ->
            ImaginalScriptCard(
                script = script,
                onDelete = { onDeleteScript(script) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
private fun ImaginalScriptCard(
    script: ImaginalScript,
    onDelete: () -> Unit
) {
    var isReadingMode by remember { mutableStateOf(false) }

    SteadyCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = script.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete script", tint = MutedText, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = script.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isReadingMode) MaterialTheme.colorScheme.onSurface else MutedText,
                maxLines = if (isReadingMode) 50 else 3
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { isReadingMode = !isReadingMode }) {
                    Text(if (isReadingMode) "Show less" else "Read full script", color = SagePrimaryDeep)
                }

                if (script.audioPath != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DustyBlue.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = DustyBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Audio recorded", style = MaterialTheme.typography.labelSmall, color = DustyBlue)
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// ADD IMAGINAL SCRIPT DIALOG
// ----------------------------------------------------------------------------
@Composable
private fun AddImaginalScriptDialog(
    aiCoachEnabled: Boolean,
    onSave: (title: String, text: String, audioPath: String?) -> Unit,
    onDismiss: () -> Unit,
    onOpenAiCoach: ((String) -> Unit)?
) {
    var title by remember { mutableStateOf("") }
    var scriptText by remember { mutableStateOf("") }
    var hasSimulatedAudio by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("New Imaginal Script", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (aiCoachEnabled && onOpenAiCoach != null) {
                    TextButton(onClick = { onOpenAiCoach(scriptText) }) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("AI Coach", color = SagePrimaryDeep, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Title", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Forgetting to check the lock") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text("Feared Scenario (Present Tense)", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = scriptText,
                    onValueChange = { scriptText = it },
                    placeholder = { Text("I am walking down the street. I am unsure if the front door is locked. I feel the uncomfortable knot in my stomach...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Optional audio recording toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SandAccent.copy(alpha = 0.3f))
                        .clickable { hasSimulatedAudio = !hasSimulatedAudio }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasSimulatedAudio) Icons.Default.CheckCircle else Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (hasSimulatedAudio) SagePrimary else MutedText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasSimulatedAudio) "Voice recording attached" else "Attach audio voice recording",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && scriptText.isNotBlank()) {
                        onSave(title, scriptText, if (hasSimulatedAudio) "local_audio_track" else null)
                    }
                },
                enabled = title.isNotBlank() && scriptText.isNotBlank(),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = SagePrimary)
            ) {
                Text("Save Script", color = PureWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
