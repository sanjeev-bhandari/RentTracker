package com.realsanjeev.renttracker.ui.addtenant

import android.app.DatePickerDialog
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realsanjeev.renttracker.R
import com.realsanjeev.renttracker.domain.model.TenantStatus
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.ui.util.Formatting
import com.realsanjeev.renttracker.ui.util.NepaliCalendar
import com.realsanjeev.renttracker.ui.util.NepaliDatePickerDialog
import com.realsanjeev.renttracker.ui.util.localizedStringId
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTenantScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditTenantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val useBsCalendar = uiState.calendarPreference == UserPreferences.CalendarPreference.BS.value
    val currentLanguage = context.resources.configuration.locales[0].language
    val useNepaliNumerals = when (uiState.numeralPreference) {
        UserPreferences.NumeralPreference.NEPALI.value -> true
        UserPreferences.NumeralPreference.ENGLISH.value -> false
        else -> currentLanguage == "ne"
    }
    var showNepaliDatePickerForPayment by remember { mutableStateOf(false) }
    var showNepaliDatePickerForMoveIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddEditTenantEvent.Saved -> onNavigateBack()
                is AddEditTenantEvent.Deleted -> onNavigateBack()
                is AddEditTenantEvent.Error -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isEditing) stringResource(R.string.title_edit_tenant) else stringResource(R.string.title_add_tenant))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.delete() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete tenant")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. Tenant Info Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tenant Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = { Text(stringResource(R.string.label_tenant_name)) },
                        placeholder = { Text("e.g. Alex Sharma") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.propertyName,
                        onValueChange = viewModel::updatePropertyName,
                        label = { Text(stringResource(R.string.label_property)) },
                        placeholder = { Text("e.g. Sunset Heights Apt 3B") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 2. Billing & Move-in Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rent & Lease Agreement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = uiState.rentPay,
                        onValueChange = viewModel::updateRentPay,
                        label = { Text(stringResource(R.string.label_monthly_rent)) },
                        placeholder = { Text("2500") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Move-in Date
                    OutlinedTextField(
                        value = Formatting.formatDateDisplay(
                            uiState.moveInDate,
                            uiState.calendarPreference,
                            useNepaliNumerals = false
                        ),
                        onValueChange = {},
                        label = { Text("Move-in / Agreement Start Date") },
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (useBsCalendar) {
                                    showNepaliDatePickerForMoveIn = true
                                } else {
                                    val cal = Calendar.getInstance()
                                    val parts = uiState.moveInDate.split("-")
                                    if (parts.size == 3) {
                                        cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                                    }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            viewModel.updateMoveInDate(
                                                String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                                            )
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick move in date")
                        }
                    )

                    // Advance Payment Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Paid 1 Month Advance upon Move-in?",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (uiState.isAdvancePaid) "Paid 1st month on move-in. Next due is +1 month." else "Post-paid. Payment due after 1 month.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isAdvancePaid,
                            onCheckedChange = viewModel::updateIsAdvancePaid
                        )
                    }

                    // Next Due Date
                    OutlinedTextField(
                        value = Formatting.formatDateDisplay(
                            uiState.paymentDate,
                            uiState.calendarPreference,
                            useNepaliNumerals = false
                        ),
                        onValueChange = {},
                        label = { Text("Next Rent Due Date") },
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (useBsCalendar) {
                                    showNepaliDatePickerForPayment = true
                                } else {
                                    val cal = Calendar.getInstance()
                                    val parts = uiState.paymentDate.split("-")
                                    if (parts.size == 3) {
                                        cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                                    }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            viewModel.updatePaymentDate(
                                                String.format(java.util.Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                                            )
                                        },
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick next due date")
                        }
                    )
                }
            }

            // 3. Electricity Meter Details Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Electricity Meter Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = uiState.electricityUnitLast,
                        onValueChange = viewModel::updateElectricityUnitLast,
                        label = { Text("Initial / Last Reading") },
                        placeholder = { Text("1400") },
                        suffix = { Text("units") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = uiState.electricityRate,
                        onValueChange = viewModel::updateElectricityRate,
                        label = { Text(stringResource(R.string.label_rate_per_unit)) },
                        placeholder = { Text("15.0") },
                        prefix = { Text("Rs. ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 4. Initial Payment Status Section
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Current Status",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TenantStatus.entries.forEach { status ->
                            FilterChip(
                                selected = uiState.status == status,
                                onClick = { viewModel.updateStatus(status) },
                                label = { Text(stringResource(status.localizedStringId())) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.btn_cancel))
                }
                Button(
                    onClick = viewModel::save,
                    enabled = uiState.valid && !uiState.loading,
                    modifier = Modifier.weight(1.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp).width(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = if (uiState.isEditing) stringResource(R.string.btn_update) else stringResource(R.string.btn_save),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showNepaliDatePickerForPayment) {
        val initialBs = runCatching {
            NepaliCalendar.toBs(LocalDate.parse(uiState.paymentDate))
        }.getOrElse { NepaliCalendar.toBs(LocalDate.now()) }
        NepaliDatePickerDialog(
            initialDate = initialBs,
            useNepaliNumerals = useNepaliNumerals,
            onDateSelected = { bsDate ->
                viewModel.updatePaymentDate(NepaliCalendar.toAd(bsDate).toString())
                showNepaliDatePickerForPayment = false
            },
            onDismiss = { showNepaliDatePickerForPayment = false }
        )
    }

    if (showNepaliDatePickerForMoveIn) {
        val initialBs = runCatching {
            NepaliCalendar.toBs(LocalDate.parse(uiState.moveInDate))
        }.getOrElse { NepaliCalendar.toBs(LocalDate.now()) }
        NepaliDatePickerDialog(
            initialDate = initialBs,
            useNepaliNumerals = useNepaliNumerals,
            onDateSelected = { bsDate ->
                viewModel.updateMoveInDate(NepaliCalendar.toAd(bsDate).toString())
                showNepaliDatePickerForMoveIn = false
            },
            onDismiss = { showNepaliDatePickerForMoveIn = false }
        )
    }
}
