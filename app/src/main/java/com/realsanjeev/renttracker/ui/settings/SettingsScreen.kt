package com.realsanjeev.renttracker.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.ui.theme.Blue40
import com.realsanjeev.renttracker.ui.util.Formatting
import androidx.compose.ui.res.stringResource
import com.realsanjeev.renttracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onToggleLanguage: () -> Unit,
    onClearAllData: () -> Unit,
    isDarkMode: Boolean,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentLanguage = context.resources.configuration.locales[0].language
    val useNepali = when (preferences.numeralPreference) {
        UserPreferences.NumeralPreference.NEPALI.value -> true
        UserPreferences.NumeralPreference.ENGLISH.value -> false
        else -> currentLanguage == "ne"
    }

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showNumeralDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var showDueDayDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionHeader(stringResource(R.string.section_preferences))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    val notifMsg = stringResource(R.string.msg_notifications_enabled)
                    SettingsRow(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.row_notification),
                        subtitle = "Manage notification settings",
                        onClick = {
                            Toast.makeText(context, notifMsg, Toast.LENGTH_SHORT).show()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Default.DarkMode,
                        title = stringResource(R.string.row_dark_mode),
                        subtitle = if (isDarkMode) "Enabled" else "Disabled",
                        trailing = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onToggleDarkMode() }
                            )
                        }
                    )
                }
            }

            SettingsSectionHeader(stringResource(R.string.section_format))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.Payments,
                        title = stringResource(R.string.row_currency),
                        subtitle = preferences.currencySymbol.trim(),
                        onClick = { showCurrencyDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Default.Pin,
                        title = stringResource(R.string.row_numeral),
                        subtitle = when (preferences.numeralPreference) {
                            1 -> stringResource(R.string.numeral_english)
                            2 -> stringResource(R.string.numeral_nepali)
                            else -> stringResource(R.string.numeral_auto)
                        },
                        onClick = { showNumeralDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Default.Language,
                        title = stringResource(R.string.row_language),
                        subtitle = "Tap to switch",
                        onClick = { onToggleLanguage() }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Default.CalendarMonth,
                        title = stringResource(R.string.row_calendar),
                        subtitle = when (preferences.calendarPreference) {
                            UserPreferences.CalendarPreference.BS.value -> stringResource(R.string.calendar_bs)
                            else -> stringResource(R.string.calendar_ad)
                        },
                        onClick = { showCalendarDialog = true }
                    )
                }
            }

            SettingsSectionHeader(stringResource(R.string.section_defaults))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Default.ElectricBolt,
                        title = stringResource(R.string.row_rate),
                        subtitle = "${preferences.defaultElectricityRate}",
                        onClick = { showRateDialog = true }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Default.CalendarMonth,
                        title = stringResource(R.string.row_due_day),
                        subtitle = Formatting.ordinalSuffixLocalized(preferences.defaultDueDay, useNepali),
                        onClick = { showDueDayDialog = true }
                    )
                }
            }

            SettingsSectionHeader(stringResource(R.string.section_more))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    val shareText = stringResource(R.string.share_message)
                    SettingsRow(
                        icon = Icons.Default.Share,
                        title = stringResource(R.string.row_share),
                        subtitle = "Tell your friends",
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "RentTracker")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share"))
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    val privacyText = stringResource(R.string.privacy_policy_text)
                    SettingsRow(
                        icon = Icons.Default.Shield,
                        title = stringResource(R.string.row_privacy),
                        subtitle = "How we handle your data",
                        onClick = {
                            Toast.makeText(context, privacyText, Toast.LENGTH_LONG).show()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    val noAppText = stringResource(R.string.msg_no_app_found)
                    SettingsRow(
                        icon = Icons.Default.Email,
                        title = stringResource(R.string.row_contact),
                        subtitle = "Get help or give feedback",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@renttracker.app")
                                putExtra(Intent.EXTRA_SUBJECT, "RentTracker Support")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, noAppText, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = stringResource(R.string.row_clear_data),
                    subtitle = "Remove all tenants and reset settings",
                    iconTint = MaterialTheme.colorScheme.error,
                    onClick = { showClearDataDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    if (showCurrencyDialog) {
        val currencies = listOf(
            "रु. " to "Nepali Rupee (रु.)",
            "$ " to "US Dollar ($)",
            "₹ " to "Indian Rupee (₹)",
            "€ " to "Euro (€)",
            "£ " to "British Pound (£)"
        )
        SingleChoiceDialog(
            title = stringResource(R.string.row_currency),
            options = currencies.map { it.second },
            selectedIndex = currencies.indexOfFirst { it.first == preferences.currencySymbol }.coerceAtLeast(0),
            onSelect = { index ->
                viewModel.updateCurrency(currencies[index].first)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showNumeralDialog) {
        val options = listOf(
            stringResource(R.string.numeral_auto),
            stringResource(R.string.numeral_english),
            stringResource(R.string.numeral_nepali)
        )
        SingleChoiceDialog(
            title = stringResource(R.string.row_numeral),
            options = options,
            selectedIndex = preferences.numeralPreference,
            onSelect = { index ->
                viewModel.updateNumeralPreference(index)
                showNumeralDialog = false
            },
            onDismiss = { showNumeralDialog = false }
        )
    }

    if (showCalendarDialog) {
        val options = listOf(
            stringResource(R.string.calendar_ad),
            stringResource(R.string.calendar_bs)
        )
        SingleChoiceDialog(
            title = stringResource(R.string.row_calendar),
            options = options,
            selectedIndex = preferences.calendarPreference,
            onSelect = { index ->
                viewModel.updateCalendarPreference(index)
                showCalendarDialog = false
            },
            onDismiss = { showCalendarDialog = false }
        )
    }

    if (showRateDialog) {
        var rateText by remember { mutableStateOf(preferences.defaultElectricityRate.toString()) }
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            title = { Text(stringResource(R.string.row_rate)) },
            text = {
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it },
                    singleLine = true,
                    placeholder = { Text("15.0") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    rateText.toDoubleOrNull()?.let { viewModel.updateElectricityRate(it) }
                    showRateDialog = false
                }) { Text(stringResource(R.string.btn_save)) }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }

    if (showDueDayDialog) {
        val days = (1..31).map { it }
        val optionFormat = stringResource(R.string.due_day_option_format)
        SingleChoiceDialog(
            title = stringResource(R.string.row_due_day),
            options = days.map { String.format(java.util.Locale.US, optionFormat, Formatting.ordinalSuffixLocalized(it, useNepali)) },
            selectedIndex = preferences.defaultDueDay - 1,
            onSelect = { index ->
                viewModel.updateDueDay(index + 1)
                showDueDayDialog = false
            },
            onDismiss = { showDueDayDialog = false }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text(stringResource(R.string.row_clear_data)) },
            text = { Text(stringResource(R.string.msg_clear_data_confirm)) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(R.string.btn_clear), color = MaterialTheme.colorScheme.onError) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) { Text(stringResource(R.string.btn_cancel)) }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    trailing: @Composable (() -> Unit)? = null
) {
    val modifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing?.invoke()
    }
}

@Composable
private fun SingleChoiceDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.selectableGroup()) {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = index == selectedIndex,
                                onClick = { onSelect(index) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == selectedIndex,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
