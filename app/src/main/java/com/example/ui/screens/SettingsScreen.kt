package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MotionPhotosOff
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SettingsEntity
import com.example.ui.components.SteadyCard
import com.example.ui.components.SteadyConfirmDialog
import com.example.ui.components.SteadyGhostButton
import com.example.ui.components.SteadyPrimaryButton
import com.example.ui.components.SteadySecondaryButton
import com.example.ui.theme.DustyBlue
import com.example.ui.theme.MutedText
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SagePrimary
import com.example.ui.theme.SagePrimaryDeep
import com.example.ui.theme.SandAccent
import com.example.ui.theme.WarmAmber
import org.json.JSONObject

@Composable
fun SettingsScreen(
    settings: SettingsEntity,
    onUpdateName: (String) -> Unit,
    onToggleDark: (Boolean?) -> Unit,
    onToggleReducedMotion: (Boolean) -> Unit,
    onToggleLargeText: (Boolean) -> Unit,
    onUpdateReminder: (String) -> Unit,
    onToggleAiCoach: (Boolean, Boolean) -> Unit,
    onExportData: () -> Unit,
    onDeleteAllData: () -> Unit,
    onOpenClinicianReport: () -> Unit,
    onLoadDemoData: () -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showNameEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(settings.userName) }

    var showReminderDialog by remember { mutableStateOf(false) }
    var tempReminder by remember { mutableStateOf(settings.reminderTime) }

    var showAiConsentDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("settings_back")) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SagePrimaryDeep)
                }
                Text(
                    text = "Settings",
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
            // Profile & Preferences
            Text("Profile & Practice", style = MaterialTheme.typography.labelLarge, color = MutedText)
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingClickableRow(
                        icon = Icons.Default.Person,
                        title = "Preferred Name",
                        subtitle = if (settings.userName.isNotBlank()) settings.userName else "Not set",
                        onClick = {
                            tempName = settings.userName
                            showNameEditDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingClickableRow(
                        icon = Icons.Default.Notifications,
                        title = "Daily Practice Reminder",
                        subtitle = "${settings.reminderTime} daily",
                        onClick = {
                            tempReminder = settings.reminderTime
                            showReminderDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingClickableRow(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = when (settings.language) {
                            "hi" -> "Hindi (हिंदी)"
                            "gu" -> "Gujarati (ગુજરાતી)"
                            else -> "English"
                        },
                        onClick = { showLanguageDialog = true }
                    )
                }
            }

            // Share with Clinician Shortcut
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.45f),
                onClick = onOpenClinicianReport
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SagePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = PureWhite, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Share with my clinician",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Export printable summary report",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                    }
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = SagePrimaryDeep)
                }
            }

            // Accessibility & Appearance
            Text("Appearance & Accessibility", style = MaterialTheme.typography.labelLarge, color = MutedText)
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitchRow(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Theme",
                        subtitle = if (settings.isDarkTheme == true) "Enabled" else "Light / System",
                        checked = settings.isDarkTheme == true,
                        onCheckedChange = { onToggleDark(if (it) true else false) }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingSwitchRow(
                        icon = Icons.Default.MotionPhotosOff,
                        title = "Reduce Motion",
                        subtitle = "Replaces breathing animation with static text cues",
                        checked = settings.reducedMotion,
                        onCheckedChange = onToggleReducedMotion
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SettingSwitchRow(
                        icon = Icons.Default.FormatSize,
                        title = "Comfortable Text Size",
                        subtitle = "Slightly enlarged body text for relaxed reading",
                        checked = settings.largeText,
                        onCheckedChange = onToggleLargeText
                    )
                }
            }

            // Optional AI Coach (Gemini API, OFF by default, explicit consent required)
            Text("Optional AI Exposure Assistant", style = MaterialTheme.typography.labelLarge, color = MutedText)
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingSwitchRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "AI Exposure Coach (Gemini)",
                        subtitle = "Helps phrase situations and imaginal scripts in present tense. Never reassures.",
                        checked = settings.aiCoachEnabled,
                        onCheckedChange = { enable ->
                            if (enable) {
                                if (!settings.aiCoachConsentAccepted) {
                                    showAiConsentDialog = true
                                } else {
                                    onToggleAiCoach(true, true)
                                }
                            } else {
                                onToggleAiCoach(false, settings.aiCoachConsentAccepted)
                            }
                        }
                    )
                }
            }

            // Privacy & Data
            Text("Privacy & Storage", style = MaterialTheme.typography.labelLarge, color = MutedText)
            SteadyCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("100% Local Device Storage", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Steady has no user accounts, no analytics, and no cloud trackers. Your journals, ratings, and logs remain strictly on this device.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedText
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SteadySecondaryButton(
                        text = "Load Sample Practice Data",
                        onClick = onLoadDemoData,
                        leadingIcon = Icons.Default.AutoAwesome,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "load_sample_data_button"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SteadySecondaryButton(
                        text = "Export All Data (JSON)",
                        onClick = onExportData,
                        leadingIcon = Icons.Default.Download,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "export_json_button"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SteadyGhostButton(
                        text = "Delete All Data",
                        onClick = { showDeleteConfirmDialog = true },
                        leadingIcon = Icons.Default.DeleteForever,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "delete_all_data_button"
                    )
                }
            }

            // Clinical Disclaimer & About
            SteadyCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = SandAccent.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Clinical Guardrails & Disclaimer", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Steady is a self-help practice companion for Exposure and Response Prevention (ERP). It is not a diagnostic tool or a substitute for medical evaluation, diagnosis, or clinical therapy. In case of mental health crisis, call Tele-MANAS (14416) or emergency services (112).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Version 1.0 • Steady for Android",
                        style = MaterialTheme.typography.labelSmall,
                        color = SagePrimaryDeep
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Edit Name Dialog
        if (showNameEditDialog) {
            AlertDialog(
                onDismissRequest = { showNameEditDialog = false },
                title = { Text("Edit Preferred Name") },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        singleLine = true,
                        placeholder = { Text("Your name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateName(tempName)
                            showNameEditDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                    ) {
                        Text("Save", color = PureWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNameEditDialog = false }) {
                        Text("Cancel", color = MutedText)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Edit Reminder Dialog
        if (showReminderDialog) {
            AlertDialog(
                onDismissRequest = { showReminderDialog = false },
                title = { Text("Daily Practice Reminder Time") },
                text = {
                    OutlinedTextField(
                        value = tempReminder,
                        onValueChange = { tempReminder = it },
                        singleLine = true,
                        placeholder = { Text("e.g. 20:00") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateReminder(tempReminder)
                            showReminderDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                    ) {
                        Text("Save", color = PureWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReminderDialog = false }) {
                        Text("Cancel", color = MutedText)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // AI Coach Consent Dialog
        if (showAiConsentDialog) {
            AlertDialog(
                onDismissRequest = { showAiConsentDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SagePrimaryDeep)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enable AI Exposure Coach?")
                    }
                },
                text = {
                    Text(
                        text = "The AI Exposure Coach is powered by Gemini and acts exclusively as an ERP writing assistant to help phrase exposure hierarchy situations and imaginal scripts.\n\nClinical Guardrails:\n• It will NEVER provide reassurance.\n• It will NEVER confirm or deny feared outcomes.\n• It will NEVER access your local database unless you specifically request phrasing for a prompt.\n• It is NOT a therapist.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onToggleAiCoach(true, true)
                            showAiConsentDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary)
                    ) {
                        Text("I Accept & Enable", color = PureWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAiConsentDialog = false }) {
                        Text("Keep Disabled", color = MutedText)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Delete All Data Double Confirm Dialog
        if (showDeleteConfirmDialog) {
            SteadyConfirmDialog(
                title = "Delete all data?",
                message = "This will permanently erase all ladder items, recorded ERP sessions, ritual logs, and struggle events on this device. This action cannot be undone.",
                confirmText = "Delete Everything",
                dismissText = "Keep My Data",
                onConfirm = {
                    onDeleteAllData()
                    showDeleteConfirmDialog = false
                },
                onDismiss = { showDeleteConfirmDialog = false }
            )
        }

        // Language Structure Dialog (English, Hindi, Gujarati)
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = { Text("Select Language") },
                text = {
                    Column {
                        listOf("English" to "en", "Hindi (हिंदी - Coming soon)" to "hi", "Gujarati (ગુજરાતી - Coming soon)" to "gu").forEach { (name, code) ->
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showLanguageDialog = false }
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Close", color = SagePrimaryDeep)
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun SettingClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MutedText, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = SagePrimaryDeep, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MutedText)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = PureWhite,
                checkedTrackColor = SagePrimary
            )
        )
    }
}
