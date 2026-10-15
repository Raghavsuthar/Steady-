package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SteadyDatabase
import com.example.data.model.ErpSession
import com.example.data.model.ImaginalScript
import com.example.data.model.LadderItem
import com.example.data.model.PersonalGoal
import com.example.data.model.ReminderSchedule
import com.example.data.model.RitualLog
import com.example.data.model.SettingsEntity
import com.example.data.model.StruggleEvent
import com.example.data.model.SudsCheckIn
import com.example.data.repository.SteadyRepository
import com.example.service.GeminiService
import com.example.service.SafetyChecker
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.example.service.ReminderReceiver
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SteadyTab {
    HOME,
    PRACTICE,
    TRACK,
    LEARN
}

enum class ActiveScreen {
    MAIN_TABS,
    ONBOARDING,
    STRUGGLE_FLOW,
    ERP_SESSION,
    IMAGINAL_SESSION,
    CLINICIAN_SUMMARY,
    HELP_SAFETY,
    SETTINGS
}

data class ErpActiveState(
    val ladderItem: LadderItem? = null,
    val step: Int = 1, // 1: Prep, 2: Active Exposure, 3: Completion
    val predictionText: String = "",
    val predictionBeliefStart: Int = 80,
    val startSuds: Int = 70,
    val totalSeconds: Int = 20 * 60,
    val remainingSeconds: Int = 20 * 60,
    val isRunning: Boolean = false,
    val checkIns: List<SudsCheckIn> = emptyList(),
    val isCheckInPopupVisible: Boolean = false,
    val tempCheckInSuds: Float = 70f,
    val endSuds: Int = 30,
    val predictionBeliefEnd: Int = 20,
    val learnedNote: String = "",
    val compulsionResisted: String = "",
    val alternativeAction: String = "",
    val elapsedSeconds: Int = 0,
    val highSudsSeconds: Int = 0 // tracks if >= 90 for 5+ min
)

data class StruggleFlowState(
    val step: Int = 1, // 1: Initial SUDS, 2: What's here, 3: Breathing, 4: Delay timer, 5: Re-rate & result
    val startSuds: Float = 75f,
    val struggleType: String = "RITUAL_URGE", // RITUAL_URGE, INTRUSIVE_THOUGHT, NOT_SURE
    val delayMinutes: Int = 10,
    val remainingDelaySeconds: Int = 10 * 60,
    val isTimerActive: Boolean = false,
    val endSuds: Float = 40f,
    val note: String = ""
)

class SteadyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SteadyRepository

    init {
        val database = SteadyDatabase.getDatabase(application, viewModelScope)
        repository = SteadyRepository(database.steadyDao())
    }

    val settings: StateFlow<SettingsEntity?> = repository.settingsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val ladderItems: StateFlow<List<LadderItem>> = repository.ladderItemsDesc
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val archivedLadderItems: StateFlow<List<LadderItem>> = repository.archivedLadderItemsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val sessions: StateFlow<List<ErpSession>> = repository.sessionsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val ritualLogs: StateFlow<List<RitualLog>> = repository.ritualLogsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val imaginalScripts: StateFlow<List<ImaginalScript>> = repository.imaginalScriptsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val struggleEvents: StateFlow<List<StruggleEvent>> = repository.struggleEventsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allGoals: StateFlow<List<PersonalGoal>> = repository.allGoalsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val activeGoals: StateFlow<List<PersonalGoal>> = repository.activeGoalsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val reminderSchedule: StateFlow<ReminderSchedule?> = repository.reminderScheduleFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Active Navigation Screen & Current Tab
    private val _currentTab = MutableStateFlow(SteadyTab.HOME)
    val currentTab: StateFlow<SteadyTab> = _currentTab.asStateFlow()

    private val _activeScreen = MutableStateFlow(ActiveScreen.MAIN_TABS)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    // Safety Trigger Overlay Alert
    private val _safetyAlertTriggered = MutableStateFlow(false)
    val safetyAlertTriggered: StateFlow<Boolean> = _safetyAlertTriggered.asStateFlow()

    // Struggle Flow State
    private val _struggleState = MutableStateFlow(StruggleFlowState())
    val struggleState: StateFlow<StruggleFlowState> = _struggleState.asStateFlow()
    private var struggleTimerJob: Job? = null

    // ERP Active Session State
    private val _erpState = MutableStateFlow(ErpActiveState())
    val erpState: StateFlow<ErpActiveState> = _erpState.asStateFlow()
    private var erpTimerJob: Job? = null

    // AI Coach State
    private val _aiCoachLoading = MutableStateFlow(false)
    val aiCoachLoading: StateFlow<Boolean> = _aiCoachLoading.asStateFlow()

    private val _aiCoachResult = MutableStateFlow<String?>(null)
    val aiCoachResult: StateFlow<String?> = _aiCoachResult.asStateFlow()

    // Toast message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun dismissToast() {
        _toastMessage.value = null
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun selectTab(tab: SteadyTab) {
        _currentTab.value = tab
    }

    fun navigateTo(screen: ActiveScreen) {
        _activeScreen.value = screen
    }

    fun handleBack() {
        if (_activeScreen.value != ActiveScreen.MAIN_TABS) {
            _activeScreen.value = ActiveScreen.MAIN_TABS
        }
    }

    fun triggerSafetyScreen() {
        _safetyAlertTriggered.value = false
        _activeScreen.value = ActiveScreen.HELP_SAFETY
    }

    fun dismissSafetyAlert() {
        _safetyAlertTriggered.value = false
    }

    // Safety text inspection using layered evaluator
    fun checkSafetyText(text: String) {
        val result = SafetyChecker.evaluate(text)
        if (result.isTriggered) {
            _safetyAlertTriggered.value = true
        }
    }

    // Settings actions
    fun updateUserName(name: String) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(current.copy(userName = name))
        }
    }

    fun toggleDarkMode(enableDark: Boolean?) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(current.copy(isDarkTheme = enableDark))
        }
    }

    fun toggleReducedMotion(reduced: Boolean) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(current.copy(reducedMotion = reduced))
        }
    }

    fun toggleLargeText(large: Boolean) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(current.copy(largeText = large))
        }
    }

    fun setReminderTime(time: String) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(current.copy(reminderTime = time))
            val parts = time.split(":")
            if (parts.size == 2) {
                val h = parts[0].trim().toIntOrNull() ?: 20
                val m = parts[1].trim().toIntOrNull() ?: 0
                updateReminderSchedule(
                    isEnabled = true,
                    hour = h,
                    minute = m,
                    weekdaysOnly = false,
                    includeWeekends = true
                )
            }
        }
    }

    fun updateReminderSchedule(
        isEnabled: Boolean,
        hour: Int,
        minute: Int,
        weekdaysOnly: Boolean,
        includeWeekends: Boolean
    ) {
        viewModelScope.launch {
            repository.updateReminderSchedule(
                ReminderSchedule(
                    id = 1,
                    isEnabled = isEnabled,
                    timeHour = hour,
                    timeMinute = minute,
                    weekdaysOnly = weekdaysOnly,
                    includeWeekends = includeWeekends
                )
            )
            val currentSettings = settings.value ?: SettingsEntity()
            val formattedTime = String.format("%02d:%02d", hour, minute)
            repository.updateSettings(
                currentSettings.copy(
                    allowNotifications = isEnabled,
                    reminderTime = formattedTime
                )
            )
            scheduleReminderAlarm(isEnabled, hour, minute, weekdaysOnly, includeWeekends)
            showToast(if (isEnabled) "Practice reminder set for $formattedTime" else "Reminders paused")
        }
    }

    private fun scheduleReminderAlarm(
        isEnabled: Boolean,
        hour: Int,
        minute: Int,
        weekdaysOnly: Boolean,
        includeWeekends: Boolean
    ) {
        try {
            val app = getApplication<Application>()
            val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val intent = ReminderReceiver.createReminderIntent(app, weekdaysOnly, includeWeekends)
            val pendingIntent = PendingIntent.getBroadcast(
                app,
                ReminderReceiver.NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (!isEnabled) {
                alarmManager.cancel(pendingIntent)
                return
            }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (_: Exception) {
            // Safe fallback
        }
    }

    fun setAiCoachEnabled(enabled: Boolean, consentGiven: Boolean = true) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(
                current.copy(
                    aiCoachEnabled = enabled,
                    aiCoachConsentAccepted = if (enabled) consentGiven else current.aiCoachConsentAccepted
                )
            )
        }
    }

    fun completeOnboarding(name: String, themes: List<String>, reminder: String) {
        viewModelScope.launch {
            val current = settings.value ?: SettingsEntity()
            repository.updateSettings(
                current.copy(
                    hasCompletedOnboarding = true,
                    userName = name,
                    ocdThemes = themes.joinToString(", "),
                    reminderTime = reminder
                )
            )
            _activeScreen.value = ActiveScreen.MAIN_TABS
            showToast("Welcome to Steady. Your private, local practice space is ready.")
        }
    }

    fun redoOnboarding() {
        _activeScreen.value = ActiveScreen.ONBOARDING
    }

    // ------------------------------------------------------------------------
    // PERSONAL GOALS & VALUES
    // ------------------------------------------------------------------------
    fun addPersonalGoal(title: String, description: String, category: String) {
        checkSafetyText(title)
        checkSafetyText(description)
        viewModelScope.launch {
            repository.addGoal(
                PersonalGoal(
                    title = title,
                    description = description,
                    category = category
                )
            )
            showToast("Personal goal added to your values journey")
        }
    }

    fun updatePersonalGoal(goal: PersonalGoal) {
        checkSafetyText(goal.title)
        checkSafetyText(goal.description)
        viewModelScope.launch {
            repository.updateGoal(goal)
            showToast("Personal goal updated")
        }
    }

    fun deletePersonalGoal(goal: PersonalGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            showToast("Goal removed")
        }
    }

    fun logGoalPracticeToday(goalId: Long) {
        viewModelScope.launch {
            repository.incrementGoalPractice(goalId)
            showToast("Goal practice logged! Valued action over avoidance.")
        }
    }

    // ------------------------------------------------------------------------
    // STRUGGLE FLOW (Immediate in-the-moment relief without reassurance)
    // ------------------------------------------------------------------------
    fun startStruggleFlow() {
        struggleTimerJob?.cancel()
        _struggleState.value = StruggleFlowState()
        _activeScreen.value = ActiveScreen.STRUGGLE_FLOW
    }

    fun updateStruggleStartSuds(suds: Float) {
        _struggleState.value = _struggleState.value.copy(startSuds = suds)
    }

    fun setStruggleType(type: String) {
        _struggleState.value = _struggleState.value.copy(struggleType = type, step = 3)
    }

    fun advanceStruggleStep(nextStep: Int) {
        _struggleState.value = _struggleState.value.copy(step = nextStep)
    }

    fun startDelayTimer(minutes: Int) {
        struggleTimerJob?.cancel()
        _struggleState.value = _struggleState.value.copy(
            delayMinutes = minutes,
            remainingDelaySeconds = minutes * 60,
            isTimerActive = true,
            step = 4
        )

        struggleTimerJob = viewModelScope.launch {
            while (_struggleState.value.remainingDelaySeconds > 0 && _struggleState.value.isTimerActive) {
                delay(1000L)
                val newRem = _struggleState.value.remainingDelaySeconds - 1
                _struggleState.value = _struggleState.value.copy(remainingDelaySeconds = newRem)
            }
            if (_struggleState.value.remainingDelaySeconds <= 0) {
                _struggleState.value = _struggleState.value.copy(step = 5, isTimerActive = false)
            }
        }
    }

    fun extendDelayTimer(extraMinutes: Int = 5) {
        val currentRem = _struggleState.value.remainingDelaySeconds
        _struggleState.value = _struggleState.value.copy(
            remainingDelaySeconds = currentRem + extraMinutes * 60
        )
    }

    fun updateStruggleEndSuds(suds: Float) {
        _struggleState.value = _struggleState.value.copy(endSuds = suds)
    }

    fun updateStruggleNote(note: String) {
        checkSafetyText(note)
        _struggleState.value = _struggleState.value.copy(note = note)
    }

    fun finishStruggleFlow() {
        struggleTimerJob?.cancel()
        val s = _struggleState.value
        viewModelScope.launch {
            repository.addStruggleEvent(
                StruggleEvent(
                    struggleType = s.struggleType,
                    startSuds = s.startSuds.toInt(),
                    endSuds = s.endSuds.toInt(),
                    delayMinutes = s.delayMinutes,
                    note = s.note
                )
            )
            _activeScreen.value = ActiveScreen.MAIN_TABS
            showToast("Struggle logged. Sitting with discomfort builds lasting neuroplastic tolerance.")
        }
    }

    // ------------------------------------------------------------------------
    // FEAR LADDER & ERP PRACTICE
    // ------------------------------------------------------------------------
    fun addOrUpdateLadderItem(
        id: Long = 0,
        title: String,
        category: String,
        baselineSuds: Int,
        notes: String,
        personalGoalId: Long? = null
    ) {
        checkSafetyText(title)
        checkSafetyText(notes)
        viewModelScope.launch {
            if (id == 0L) {
                repository.addLadderItem(
                    LadderItem(
                        title = title,
                        category = category,
                        baselineSuds = baselineSuds,
                        notes = notes,
                        status = "NOT_STARTED",
                        personalGoalId = personalGoalId
                    )
                )
                showToast("Situation added to Fear Ladder")
            } else {
                val existing = ladderItems.value.find { it.id == id }
                if (existing != null) {
                    repository.updateLadderItem(
                        existing.copy(
                            title = title,
                            category = category,
                            baselineSuds = baselineSuds,
                            notes = notes,
                            personalGoalId = personalGoalId
                        )
                    )
                    showToast("Ladder item updated")
                }
            }
        }
    }

    fun archiveLadderItem(item: LadderItem, isArchived: Boolean) {
        viewModelScope.launch {
            repository.archiveLadderItem(item, isArchived)
            showToast(if (isArchived) "Item archived" else "Item restored to active ladder")
        }
    }

    fun deleteLadderItem(item: LadderItem) {
        viewModelScope.launch {
            repository.deleteLadderItem(item)
            showToast("Item removed from Fear Ladder")
        }
    }

    fun seedExampleLadderItems() {
        viewModelScope.launch {
            val examples = listOf(
                LadderItem(title = "Touching public door handle & waiting 15 min before washing", category = "Contamination", baselineSuds = 75),
                LadderItem(title = "Leaving the house after checking the lock once", category = "Checking", baselineSuds = 65),
                LadderItem(title = "Writing feared thought on paper and reading it aloud", category = "Unwanted Thoughts", baselineSuds = 80),
                LadderItem(title = "Leaving kitchen counter slightly asymmetrical", category = "Symmetry / Order", baselineSuds = 45),
                LadderItem(title = "Sitting near sharp object while doing deep breathing", category = "Harm", baselineSuds = 70)
            )
            examples.forEach { repository.addLadderItem(it) }
            showToast("Example situations added to ladder")
        }
    }

    fun loadSamplePracticeData() {
        viewModelScope.launch {
            repository.loadSampleData()
            showToast("Sample practice data loaded for review.")
        }
    }

    // ------------------------------------------------------------------------
    // ACTIVE ERP SESSION
    // ------------------------------------------------------------------------
    fun startErpSession(ladderItem: LadderItem) {
        erpTimerJob?.cancel()
        _erpState.value = ErpActiveState(
            ladderItem = ladderItem,
            step = 1,
            predictionText = "",
            predictionBeliefStart = 80,
            startSuds = ladderItem.baselineSuds,
            totalSeconds = 20 * 60,
            remainingSeconds = 20 * 60,
            isRunning = false,
            checkIns = listOf(SudsCheckIn(0, ladderItem.baselineSuds)),
            tempCheckInSuds = ladderItem.baselineSuds.toFloat()
        )
        _activeScreen.value = ActiveScreen.ERP_SESSION
    }

    fun updateErpPrediction(text: String, belief: Int, preSuds: Int) {
        checkSafetyText(text)
        _erpState.value = _erpState.value.copy(
            predictionText = text,
            predictionBeliefStart = belief,
            startSuds = preSuds,
            tempCheckInSuds = preSuds.toFloat(),
            checkIns = listOf(SudsCheckIn(0, preSuds))
        )
    }

    fun beginActiveExposure(durationMinutes: Int = 20) {
        val seconds = durationMinutes * 60
        _erpState.value = _erpState.value.copy(
            step = 2,
            totalSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = true,
            elapsedSeconds = 0
        )

        launchErpTimer()
    }

    private fun launchErpTimer() {
        erpTimerJob?.cancel()
        erpTimerJob = viewModelScope.launch {
            while (_erpState.value.remainingSeconds > 0 && _erpState.value.isRunning) {
                delay(1000L)
                val cur = _erpState.value
                val newRem = cur.remainingSeconds - 1
                val newElapsed = cur.elapsedSeconds + 1

                val latestSuds = cur.checkIns.lastOrNull()?.suds ?: cur.startSuds
                val newHighSudsSec = if (latestSuds >= 90) cur.highSudsSeconds + 1 else 0

                // Clinical safety check: SUDS >= 90 for 5+ min
                if (newHighSudsSec >= 300) {
                    _safetyAlertTriggered.value = true
                }

                // Periodic check-in every 3 minutes (180 seconds)
                val triggerCheckIn = (newElapsed > 0 && newElapsed % 180 == 0 && !cur.isCheckInPopupVisible)

                _erpState.value = cur.copy(
                    remainingSeconds = newRem,
                    elapsedSeconds = newElapsed,
                    highSudsSeconds = newHighSudsSec,
                    isCheckInPopupVisible = if (triggerCheckIn) true else cur.isCheckInPopupVisible,
                    tempCheckInSuds = if (triggerCheckIn) latestSuds.toFloat() else cur.tempCheckInSuds
                )
            }

            if (_erpState.value.remainingSeconds <= 0) {
                finishActiveExposureTimer()
            }
        }
    }

    fun toggleErpPause() {
        val currentlyRunning = _erpState.value.isRunning
        if (currentlyRunning) {
            erpTimerJob?.cancel()
            _erpState.value = _erpState.value.copy(isRunning = false)
        } else {
            _erpState.value = _erpState.value.copy(isRunning = true)
            launchErpTimer()
        }
    }

    fun submitCheckInSuds(suds: Int) {
        val cur = _erpState.value
        val minute = (cur.elapsedSeconds / 60).coerceAtLeast(1)
        val updatedCheckIns = cur.checkIns + SudsCheckIn(minute, suds)

        _erpState.value = cur.copy(
            checkIns = updatedCheckIns,
            isCheckInPopupVisible = false
        )
    }

    fun endErpSessionEarly() {
        erpTimerJob?.cancel()
        finishActiveExposureTimer()
    }

    private fun finishActiveExposureTimer() {
        erpTimerJob?.cancel()
        val cur = _erpState.value
        val lastSuds = cur.checkIns.lastOrNull()?.suds ?: (cur.startSuds / 2)
        _erpState.value = cur.copy(
            step = 3,
            isRunning = false,
            endSuds = lastSuds
        )
    }

    fun completeErpSession(
        learnedNote: String,
        postBelief: Int,
        postSuds: Int,
        compulsionResisted: String = "",
        alternativeAction: String = ""
    ) {
        checkSafetyText(learnedNote)
        checkSafetyText(compulsionResisted)
        checkSafetyText(alternativeAction)
        val cur = _erpState.value
        val durationMin = (cur.elapsedSeconds / 60).coerceAtLeast(1)

        val checkInsSerialized = cur.checkIns.joinToString(",") { "${it.minuteOffset}:${it.suds}" }

        val session = ErpSession(
            ladderItemId = cur.ladderItem?.id,
            ladderItemTitle = cur.ladderItem?.title ?: "Free Exposure",
            sessionType = "IN_VIVO",
            predictionText = cur.predictionText,
            predictionBeliefStart = cur.predictionBeliefStart,
            startSuds = cur.startSuds,
            checkInsJson = checkInsSerialized,
            endSuds = postSuds,
            predictionBeliefEnd = postBelief,
            learnedNote = learnedNote,
            durationMinutes = durationMin,
            timestamp = System.currentTimeMillis(),
            compulsionResisted = compulsionResisted,
            alternativeAction = alternativeAction,
            wasCompleted = true
        )

        viewModelScope.launch {
            repository.recordSession(session)
            _activeScreen.value = ActiveScreen.MAIN_TABS
            showToast("Practice complete! You stayed with uncertainty and chose valued behavior over rituals.")
        }
    }

    // ------------------------------------------------------------------------
    // RITUAL LOGGING
    // ------------------------------------------------------------------------
    fun logRitual(
        description: String,
        trigger: String,
        durationMinutes: Int,
        outcome: String // RESISTED, DELAYED, PERFORMED
    ) {
        checkSafetyText(description)
        checkSafetyText(trigger)
        viewModelScope.launch {
            repository.addRitualLog(
                RitualLog(
                    description = description,
                    trigger = trigger,
                    durationMinutes = durationMinutes,
                    outcome = outcome
                )
            )
            val outcomeMsg = when (outcome) {
                "RESISTED" -> "Ritual resisted! Powerful step forward."
                "DELAYED" -> "Ritual delayed. Delaying weakens the OCD loop."
                else -> "Ritual logged with self-compassion. Awareness is progress."
            }
            showToast(outcomeMsg)
        }
    }

    // ------------------------------------------------------------------------
    // IMAGINAL EXPOSURE
    // ------------------------------------------------------------------------
    fun saveImaginalScript(title: String, text: String, audioPath: String? = null) {
        checkSafetyText(title)
        checkSafetyText(text)
        viewModelScope.launch {
            repository.addImaginalScript(
                ImaginalScript(
                    title = title,
                    text = text,
                    audioPath = audioPath
                )
            )
            showToast("Imaginal script saved")
        }
    }

    fun deleteImaginalScript(script: ImaginalScript) {
        viewModelScope.launch {
            repository.deleteImaginalScript(script)
            showToast("Script deleted")
        }
    }

    // ------------------------------------------------------------------------
    // AI COACH
    // ------------------------------------------------------------------------
    fun askAiCoach(prompt: String, itemType: String) {
        checkSafetyText(prompt)
        _aiCoachLoading.value = true
        _aiCoachResult.value = null

        viewModelScope.launch {
            val res = GeminiService.phraseExposureItem(prompt, itemType)
            _aiCoachLoading.value = false
            res.onSuccess {
                _aiCoachResult.value = it
            }.onFailure { err ->
                if (err.message == "CRISIS_DETECTED") {
                    _safetyAlertTriggered.value = true
                } else {
                    _aiCoachResult.value = "Remember: focus on the fear in the present tense, sit with the uncertainty, and make a plan to do nothing to neutralize it."
                }
            }
        }
    }

    fun clearAiCoachResult() {
        _aiCoachResult.value = null
    }

    // Clear all user data
    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            showToast("All data successfully cleared.")
        }
    }
}
