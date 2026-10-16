package com.example.ui.navigation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LadderItem
import com.example.ui.screens.AiCoachDialog
import com.example.ui.screens.ClinicianReportScreen
import com.example.ui.screens.DistractionToolsScreen
import com.example.ui.screens.ErpActiveSessionScreen
import com.example.ui.screens.HelpSafetyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.LogRitualBottomSheet
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StruggleScreen
import com.example.ui.screens.TrackScreen
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.SteadyTheme
import com.example.service.countDistinctPracticeDaysInCurrentMonth
import com.example.ui.theme.WarmAmber
import com.example.ui.viewmodel.ActiveScreen
import com.example.ui.viewmodel.SteadyTab
import com.example.ui.viewmodel.SteadyViewModel
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun SteadyApp(
    viewModel: SteadyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val ladderItems by viewModel.ladderItems.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val ritualLogs by viewModel.ritualLogs.collectAsState()
    val imaginalScripts by viewModel.imaginalScripts.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()
    val allGoals by viewModel.allGoals.collectAsState()

    val currentTab by viewModel.currentTab.collectAsState()
    val activeScreen by viewModel.activeScreen.collectAsState()
    val safetyAlertTriggered by viewModel.safetyAlertTriggered.collectAsState()
    val struggleState by viewModel.struggleState.collectAsState()
    val erpState by viewModel.erpState.collectAsState()

    val aiCoachLoading by viewModel.aiCoachLoading.collectAsState()
    val aiCoachResult by viewModel.aiCoachResult.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showAiCoachDialog by remember { mutableStateOf(false) }
    var aiCoachTargetText by remember { mutableStateOf("") }
    var showQuickLogRitualSheet by remember { mutableStateOf(false) }

    // System theme overrides
    val isDark = settings?.isDarkTheme ?: false

    SteadyTheme(darkTheme = isDark) {
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(toastMessage) {
            toastMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.dismissToast()
            }
        }

        // Automatic Safety Alert Overlay (tele-manas 14416 redirection)
        if (safetyAlertTriggered) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissSafetyAlert() },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = null, tint = SagePrimaryDeep)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("We Care About Your Safety")
                    }
                },
                text = {
                    Text(
                        text = "We noticed high distress or sensitive wording. Steady is a self-help practice companion and cannot replace emergency help. Would you like to view 24/7 crisis hotlines and grounding tools?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.triggerSafetyScreen() },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                    ) {
                        Text("View Crisis Support & Grounding", color = PureWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissSafetyAlert() }) {
                        Text("I am safe, continue", color = MutedText)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Handle System Back Press
        BackHandler(enabled = activeScreen != ActiveScreen.MAIN_TABS) {
            viewModel.handleBack()
        }

        // Check if Onboarding needs to be displayed
        val isFirstLaunch = settings != null && !settings!!.hasCompletedOnboarding

        if (isFirstLaunch || activeScreen == ActiveScreen.ONBOARDING) {
            OnboardingScreen(
                onComplete = { name, themes, reminder ->
                    viewModel.completeOnboarding(name, themes, reminder)
                }
            )
            return@SteadyTheme
        }

        // MAIN SCAFFOLD
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                // Show bottom tab bar only on main tabs
                if (activeScreen == ActiveScreen.MAIN_TABS) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        // Persistent Floating "I'm struggling" pill button above tab bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FloatingStrugglingPill(
                                onClick = { viewModel.startStruggleFlow() }
                            )
                        }

                        SteadyNavigationBar(
                            selectedTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                }
            },
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) { paddingVals ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingVals)
            ) {
                // Content Router
                when (activeScreen) {
                    ActiveScreen.MAIN_TABS -> {
                        when (currentTab) {
                            SteadyTab.HOME -> {
                                val daysThisMonth = calculateDaysPracticedThisMonth(sessions)
                                val lowestUnmastered = ladderItems
                                    .filter { it.status != "MASTERED" }
                                    .minByOrNull { it.baselineSuds }
                                val resistedCount = ritualLogs.count { it.outcome == "RESISTED" || it.outcome == "DELAYED" }

                                HomeScreen(
                                    userName = settings?.userName ?: "",
                                    suggestedLadderItem = lowestUnmastered,
                                    daysPracticedThisMonth = daysThisMonth,
                                    ritualsResistedCount = resistedCount,
                                    activeGoals = activeGoals,
                                    onStruggleClick = { viewModel.startStruggleFlow() },
                                    onStartPracticeClick = { viewModel.startErpSession(it) },
                                    onBuildLadderClick = { viewModel.selectTab(SteadyTab.PRACTICE) },
                                    onLogRitualClick = { showQuickLogRitualSheet = true },
                                    onOpenLadderClick = { viewModel.selectTab(SteadyTab.PRACTICE) },
                                    onOpenLearnClick = { viewModel.selectTab(SteadyTab.LEARN) },
                                    onOpenResetTools = { viewModel.navigateTo(ActiveScreen.DISTRACTION_TOOLS) },
                                    onOpenGoalsClick = { viewModel.selectTab(SteadyTab.TRACK) },
                                    onOpenHelpClick = { viewModel.navigateTo(ActiveScreen.HELP_SAFETY) }
                                )
                            }
                            SteadyTab.PRACTICE -> {
                                PracticeScreen(
                                    ladderItems = ladderItems,
                                    sessions = sessions,
                                    imaginalScripts = imaginalScripts,
                                    aiCoachEnabled = settings?.aiCoachEnabled == true,
                                    onStartErpSession = { viewModel.startErpSession(it) },
                                    onAddOrUpdateLadderItem = { id, title, cat, suds, notes ->
                                        viewModel.addOrUpdateLadderItem(id, title, cat, suds, notes)
                                    },
                                    onDeleteLadderItem = { viewModel.deleteLadderItem(it) },
                                    onSeedExamples = { viewModel.seedExampleLadderItems() },
                                    onSaveImaginalScript = { title, text, audio ->
                                        viewModel.saveImaginalScript(title, text, audio)
                                    },
                                    onDeleteImaginalScript = { viewModel.deleteImaginalScript(it) },
                                    onOpenAiCoach = { prompt ->
                                        aiCoachTargetText = prompt
                                        showAiCoachDialog = true
                                    }
                                )
                            }
                            SteadyTab.TRACK -> {
                                TrackScreen(
                                    ritualLogs = ritualLogs,
                                    sessions = sessions,
                                    ladderItems = ladderItems,
                                    goals = allGoals,
                                    onLogRitual = { desc, trig, dur, out ->
                                        viewModel.logRitual(desc, trig, dur, out)
                                    },
                                    onAddGoal = { title, desc, cat ->
                                        viewModel.addPersonalGoal(title, desc, cat)
                                    },
                                    onUpdateGoal = { goal ->
                                        viewModel.updatePersonalGoal(goal)
                                    },
                                    onDeleteGoal = { goal ->
                                        viewModel.deletePersonalGoal(goal)
                                    }
                                )
                            }
                            SteadyTab.LEARN -> {
                                LearnScreen()
                            }
                        }
                    }
                    ActiveScreen.STRUGGLE_FLOW -> {
                        StruggleScreen(
                            state = struggleState,
                            reducedMotion = settings?.reducedMotion == true,
                            onUpdateStartSuds = { viewModel.updateStruggleStartSuds(it) },
                            onSetStruggleType = { viewModel.setStruggleType(it) },
                            onAdvanceStep = { viewModel.advanceStruggleStep(it) },
                            onStartDelayTimer = { viewModel.startDelayTimer(it) },
                            onExtendDelayTimer = { viewModel.extendDelayTimer(it) },
                            onUpdateEndSuds = { viewModel.updateStruggleEndSuds(it) },
                            onUpdateNote = { viewModel.updateStruggleNote(it) },
                            onFinish = { viewModel.finishStruggleFlow() },
                            onClose = { viewModel.handleBack() }
                        )
                    }
                    ActiveScreen.ERP_SESSION -> {
                        ErpActiveSessionScreen(
                            state = erpState,
                            onUpdatePrediction = { text, belief, preSuds ->
                                viewModel.updateErpPrediction(text, belief, preSuds)
                            },
                            onBeginExposure = { duration ->
                                viewModel.beginActiveExposure(duration)
                            },
                            onTogglePause = { viewModel.toggleErpPause() },
                            onSubmitCheckInSuds = { viewModel.submitCheckInSuds(it) },
                            onEndEarly = { viewModel.endErpSessionEarly() },
                            onCompleteSession = { learnedNote, postBelief, postSuds ->
                                viewModel.completeErpSession(learnedNote, postBelief, postSuds)
                            },
                            onClose = { viewModel.handleBack() }
                        )
                    }
                    ActiveScreen.CLINICIAN_SUMMARY -> {
                        ClinicianReportScreen(
                            ladderItems = ladderItems,
                            sessions = sessions,
                            ritualLogs = ritualLogs,
                            userName = settings?.userName ?: "",
                            onBack = { viewModel.handleBack() }
                        )
                    }
                    ActiveScreen.HELP_SAFETY -> {
                        HelpSafetyScreen(
                            onBack = { viewModel.handleBack() }
                        )
                    }
                    ActiveScreen.DISTRACTION_TOOLS -> {
                        DistractionToolsScreen(
                            onBack = { viewModel.handleBack() }
                        )
                    }
                    ActiveScreen.SETTINGS -> {
                        SettingsScreen(
                            settings = settings ?: com.example.data.model.SettingsEntity(),
                            onUpdateName = { viewModel.updateUserName(it) },
                            onToggleDark = { viewModel.toggleDarkMode(it) },
                            onToggleReducedMotion = { viewModel.toggleReducedMotion(it) },
                            onToggleLargeText = { viewModel.toggleLargeText(it) },
                            onUpdateReminder = { viewModel.setReminderTime(it) },
                            onToggleAiCoach = { enabled, consent ->
                                viewModel.setAiCoachEnabled(enabled, consent)
                            },
                            onExportData = {
                                exportAllDataAsJson(context, ladderItems, sessions, ritualLogs)
                            },
                            onDeleteAllData = { viewModel.resetAllData() },
                            onOpenClinicianReport = { viewModel.navigateTo(ActiveScreen.CLINICIAN_SUMMARY) },
                            onLoadDemoData = { viewModel.loadSamplePracticeData() },
                            onBack = { viewModel.handleBack() }
                        )
                    }
                    ActiveScreen.IMAGINAL_SESSION -> {
                        // Handled in PracticeScreen tab
                    }
                    ActiveScreen.ONBOARDING -> {}
                }

                // Top Settings Action Icon on Main Tabs
                if (activeScreen == ActiveScreen.MAIN_TABS) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp, top = 4.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(ActiveScreen.SETTINGS) },
                            modifier = Modifier.testTag("app_settings_icon")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = SagePrimaryDeep
                            )
                        }
                    }
                }

                // AI Coach Dialog
                if (showAiCoachDialog) {
                    AiCoachDialog(
                        initialPrompt = aiCoachTargetText,
                        isLoading = aiCoachLoading,
                        result = aiCoachResult,
                        onAsk = { prompt, type ->
                            viewModel.askAiCoach(prompt, type)
                        },
                        onUseResult = { suggestion ->
                            // AiCoachDialog copies this into its editable prompt. Keep the dialog open and never auto-save.
                            aiCoachTargetText = suggestion
                            viewModel.clearAiCoachResult()
                        },
                        onDismiss = {
                            viewModel.clearAiCoachResult()
                            showAiCoachDialog = false
                        }
                    )
                }

                // Quick Log Ritual Sheet
                if (showQuickLogRitualSheet) {
                    LogRitualBottomSheet(
                        onSave = { desc, trig, dur, out ->
                            viewModel.logRitual(desc, trig, dur, out)
                            showQuickLogRitualSheet = false
                        },
                        onDismiss = { showQuickLogRitualSheet = false }
                    )
                }
            }
        }
    }
}

// Persistent Floating "I'm struggling" Pill Button
@Composable
private fun FloatingStrugglingPill(
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = SagePrimary,
        contentColor = PureWhite,
        shadowElevation = 4.dp,
        modifier = Modifier
            .height(46.dp)
            .testTag("persistent_struggling_pill")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "I'm struggling",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = PureWhite
            )
        }
    }
}

// Bottom Navigation Bar
@Composable
private fun SteadyNavigationBar(
    selectedTab: SteadyTab,
    onTabSelected: (SteadyTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == SteadyTab.HOME,
            onClick = { onTabSelected(SteadyTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SteadyTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SagePrimaryDeep,
                selectedTextColor = SagePrimaryDeep,
                indicatorColor = SandAccent.copy(alpha = 0.7f),
                unselectedIconColor = MutedText,
                unselectedTextColor = MutedText
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        NavigationBarItem(
            selected = selectedTab == SteadyTab.PRACTICE,
            onClick = { onTabSelected(SteadyTab.PRACTICE) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SteadyTab.PRACTICE) Icons.AutoMirrored.Filled.FormatListBulleted else Icons.AutoMirrored.Outlined.FormatListBulleted,
                    contentDescription = "Practice"
                )
            },
            label = { Text("Practice") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SagePrimaryDeep,
                selectedTextColor = SagePrimaryDeep,
                indicatorColor = SandAccent.copy(alpha = 0.7f),
                unselectedIconColor = MutedText,
                unselectedTextColor = MutedText
            ),
            modifier = Modifier.testTag("nav_tab_practice")
        )

        NavigationBarItem(
            selected = selectedTab == SteadyTab.TRACK,
            onClick = { onTabSelected(SteadyTab.TRACK) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SteadyTab.TRACK) Icons.Filled.Insights else Icons.Outlined.Insights,
                    contentDescription = "Track"
                )
            },
            label = { Text("Track") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SagePrimaryDeep,
                selectedTextColor = SagePrimaryDeep,
                indicatorColor = SandAccent.copy(alpha = 0.7f),
                unselectedIconColor = MutedText,
                unselectedTextColor = MutedText
            ),
            modifier = Modifier.testTag("nav_tab_track")
        )

        NavigationBarItem(
            selected = selectedTab == SteadyTab.LEARN,
            onClick = { onTabSelected(SteadyTab.LEARN) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == SteadyTab.LEARN) Icons.Filled.Book else Icons.Outlined.Book,
                    contentDescription = "Learn"
                )
            },
            label = { Text("Learn") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SagePrimaryDeep,
                selectedTextColor = SagePrimaryDeep,
                indicatorColor = SandAccent.copy(alpha = 0.7f),
                unselectedIconColor = MutedText,
                unselectedTextColor = MutedText
            ),
            modifier = Modifier.testTag("nav_tab_learn")
        )
    }
}

private fun calculateDaysPracticedThisMonth(sessions: List<com.example.data.model.ErpSession>): Int =
    countDistinctPracticeDaysInCurrentMonth(sessions.map { it.timestamp })

private fun exportAllDataAsJson(
    context: android.content.Context,
    ladderItems: List<com.example.data.model.LadderItem>,
    sessions: List<com.example.data.model.ErpSession>,
    rituals: List<com.example.data.model.RitualLog>
) {
    val root = JSONObject().apply {
        put("app", "Steady")
        put("exportedAt", System.currentTimeMillis())
        put("ladderItems", JSONArray().apply {
            ladderItems.forEach { item ->
                put(JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("category", item.category)
                    put("baselineSuds", item.baselineSuds)
                    put("status", item.status)
                    put("notes", item.notes)
                })
            }
        })
        put("sessions", JSONArray().apply {
            sessions.forEach { s ->
                put(JSONObject().apply {
                    put("id", s.id)
                    put("itemTitle", s.ladderItemTitle)
                    put("startSuds", s.startSuds)
                    put("endSuds", s.endSuds)
                    put("durationMinutes", s.durationMinutes)
                    put("timestamp", s.timestamp)
                })
            }
        })
        put("rituals", JSONArray().apply {
            rituals.forEach { r ->
                put(JSONObject().apply {
                    put("description", r.description)
                    put("outcome", r.outcome)
                    put("durationMinutes", r.durationMinutes)
                    put("timestamp", r.timestamp)
                })
            }
        })
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_SUBJECT, "Steady_Backup_${System.currentTimeMillis()}.json")
        putExtra(Intent.EXTRA_TEXT, root.toString(2))
    }
    context.startActivity(Intent.createChooser(shareIntent, "Export Steady Data"))
}
