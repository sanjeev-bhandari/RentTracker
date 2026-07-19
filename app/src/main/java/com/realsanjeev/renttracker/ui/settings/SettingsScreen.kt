package com.realsanjeev.renttracker.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
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

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showNumeralDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var showDueDayDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            SettingsSectionHeader("Preferences")

            SettingsRow(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Manage notification settings",
                onClick = {
                    Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
                }
            )

            SettingsRow(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                subtitle = if (isDarkMode) "Enabled" else "Disabled",
                trailing = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { onToggleDarkMode() }
                    )
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader("Format")

            SettingsRow(
                icon = Icons.Default.Payments,
                title = "Currency",
                subtitle = preferences.currencySymbol.trim(),
                onClick = { showCurrencyDialog = true }
            )

            SettingsRow(
                icon = Icons.Default.Pin,
                title = "Numeral System",
                subtitle = when (preferences.numeralPreference) {
                    1 -> "English"
                    2 -> "Nepali"
                    else -> "Auto"
                },
                onClick = { showNumeralDialog = true }
            )

            SettingsRow(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = "Tap to switch",
                onClick = { onToggleLanguage() }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader("Defaults")

            SettingsRow(
                icon = Icons.Default.ElectricBolt,
                title = "Default Electricity Rate",
                subtitle = "${preferences.defaultElectricityRate}",
                onClick = { showRateDialog = true }
            )

            SettingsRow(
                icon = Icons.Default.CalendarMonth,
                title = "Default Due Day",
                subtitle = Formatting.ordinalSuffix(preferences.defaultDueDay),
                onClick = { showDueDayDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionHeader("More")

            SettingsRow(
                icon = Icons.Default.Share,
                title = "Share App",
                subtitle = "Tell your friends",
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "RentTracker")
                        putExtra(Intent.EXTRA_TEXT, "Check out RentTracker - the easiest way to manage your rental properties!")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share"))
                }
            )

            SettingsRow(
                icon = Icons.Default.Shield,
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = {
                    Toast.makeText(context, "Your data stays on your device. We never collect or share your information.", Toast.LENGTH_LONG).show()
                }
            )

            SettingsRow(
                icon = Icons.Default.Email,
                title = "Contact Us",
                subtitle = "Get help or give feedback",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:support@renttracker.app")
                        putExtra(Intent.EXTRA_SUBJECT, "RentTracker Support")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Clear All Data",
                subtitle = "Remove all tenants and reset settings",
                iconTint = MaterialTheme.colorScheme.error,
                onClick = { showClearDataDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
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
            title = "Currency",
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
        val options = listOf("Auto (based on language)", "English (0-9)", "Nepali (०-९)")
        SingleChoiceDialog(
            title = "Numeral System",
            options = options,
            selectedIndex = preferences.numeralPreference,
            onSelect = { index ->
                viewModel.updateNumeralPreference(index)
                showNumeralDialog = false
            },
            onDismiss = { showNumeralDialog = false }
        )
    }

    if (showRateDialog) {
        var rateText by remember { mutableStateOf(preferences.defaultElectricityRate.toString()) }
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            title = { Text("Default Electricity Rate") },
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
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDueDayDialog) {
        val days = (1..31).map { it }
        SingleChoiceDialog(
            title = "Default Due Day",
            options = days.map { Formatting.ordinalSuffix(it) + " of month" },
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
            title = { Text("Clear All Data") },
            text = { Text("This will remove all tenants and reset settings. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearDataDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear", color = MaterialTheme.colorScheme.onError) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = Blue40,
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
