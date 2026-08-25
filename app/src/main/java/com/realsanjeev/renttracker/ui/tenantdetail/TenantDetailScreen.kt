package com.realsanjeev.renttracker.ui.tenantdetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.realsanjeev.renttracker.domain.model.PaymentRecord
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.TenantStatus
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.ui.recordpayment.RecordPaymentDialog
import com.realsanjeev.renttracker.ui.theme.Amber40
import com.realsanjeev.renttracker.ui.theme.Blue40
import com.realsanjeev.renttracker.ui.theme.Green40
import com.realsanjeev.renttracker.ui.util.Formatting
import com.realsanjeev.renttracker.ui.util.avatarColorIndex
import com.realsanjeev.renttracker.ui.util.localizedStringId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenantDetailScreen(
    onNavigateBack: () -> Unit,
    onEditTenant: (Long) -> Unit,
    viewModel: TenantDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showRecordPaymentDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val currentLanguage = context.resources.configuration.locales[0].language
    val useNepali = when (uiState.preferences.numeralPreference) {
        UserPreferences.NumeralPreference.NEPALI.value -> true
        UserPreferences.NumeralPreference.ENGLISH.value -> false
        else -> currentLanguage == "ne"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.tenant?.name ?: "Tenant Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    uiState.tenant?.let { tenant ->
                        IconButton(onClick = { onEditTenant(tenant.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit tenant")
                        }
                        IconButton(onClick = {
                            viewModel.deleteTenant(onDeleted = onNavigateBack)
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete tenant")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            uiState.tenant?.let { tenant ->
                ExtendedFloatingActionButton(
                    onClick = { showRecordPaymentDialog = true },
                    icon = { Icon(Icons.Default.AddCard, contentDescription = null) },
                    text = { Text("Record Payment") },
                    shape = RoundedCornerShape(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val tenant = uiState.tenant
            if (tenant == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tenant not found.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    // Tenant Overview Card
                    item {
                        TenantOverviewHeader(tenant, uiState.preferences, useNepali)
                    }

                    // Meter Reading & Details Card
                    item {
                        TenantMeterDetailsCard(tenant, uiState.preferences, useNepali)
                    }

                    // Payment History Header
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Payment History (${uiState.paymentHistory.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (uiState.paymentHistory.isEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No payments recorded yet.\nTap 'Record Payment' to add a receipt.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(uiState.paymentHistory, key = { it.id }) { record ->
                            PaymentReceiptItem(record, uiState.preferences, useNepali)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showRecordPaymentDialog && uiState.tenant != null) {
        RecordPaymentDialog(
            tenants = listOf(uiState.tenant!!),
            initialSelectedTenant = uiState.tenant,
            onDismiss = { showRecordPaymentDialog = false },
            onConfirmRecord = { record, updatedTenant ->
                viewModel.recordPayment(record, updatedTenant)
                showRecordPaymentDialog = false
            }
        )
    }
}

@Composable
private fun TenantOverviewHeader(
    tenant: Tenant,
    preferences: UserPreferences,
    useNepali: Boolean
) {
    val avatarColors = listOf(Blue40, Amber40, Color(0xFF7C3AED), Green40)
    val avatarColor = avatarColors[tenant.avatarColorIndex()]
    val status = tenant.calculatedStatus

    val (bgColor, textColor) = when (status) {
        TenantStatus.PAID -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        TenantStatus.PENDING -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        TenantStatus.OVERDUE -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tenant.initials,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tenant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tenant.propertyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = stringResource(status.localizedStringId()),
                        color = textColor,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Monthly Rent", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = Formatting.formatAmountLocalized(tenant.rentPay, preferences.currencySymbol, useNepali),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text("Next Due Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = Formatting.formatDateDisplay(tenant.paymentDate, preferences.calendarPreference, useNepali),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column {
                    Text("Advance Mode", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (tenant.isAdvancePaid) "1 Mo. Advance" else "Post-Paid",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun TenantMeterDetailsCard(
    tenant: Tenant,
    preferences: UserPreferences,
    useNepali: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = "Electricity Meter Status",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Last Reading", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${Formatting.formatNumber(tenant.electricityUnitLast, useNepali)} units",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text("Current Reading", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${Formatting.formatNumber(tenant.electricityUnitCurrent, useNepali)} units",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column {
                    Text("Rate / Unit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = Formatting.formatAmountLocalized(tenant.electricityRate, preferences.currencySymbol, useNepali),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentReceiptItem(
    record: PaymentRecord,
    preferences: UserPreferences,
    useNepali: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Payment on ${record.paymentDate}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = record.periodCovered,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rent: ${Formatting.formatAmountLocalized(record.rentAmount, preferences.currencySymbol, useNepali)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Electricity: ${Formatting.formatAmountLocalized(record.electricityAmount, preferences.currencySymbol, useNepali)} (${Formatting.formatNumber(record.electricityUnitCurrent - record.electricityUnitLast, useNepali)} units)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (record.note.isNotBlank()) {
                    Text(
                        text = "Note: ${record.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Text(
                    text = "Total Paid: ${Formatting.formatAmountLocalized(record.totalAmount, preferences.currencySymbol, useNepali)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
