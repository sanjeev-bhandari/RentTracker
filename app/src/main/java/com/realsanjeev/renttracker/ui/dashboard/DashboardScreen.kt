package com.realsanjeev.renttracker.ui.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.realsanjeev.renttracker.R
import com.realsanjeev.renttracker.domain.model.PaymentRecord
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.TenantStatus
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.ui.recordpayment.RecordPaymentDialog
import com.realsanjeev.renttracker.ui.theme.Amber40
import com.realsanjeev.renttracker.ui.theme.Blue40
import com.realsanjeev.renttracker.ui.theme.Green40
import com.realsanjeev.renttracker.ui.theme.Red40
import com.realsanjeev.renttracker.ui.util.Formatting
import com.realsanjeev.renttracker.ui.util.avatarColorIndex
import com.realsanjeev.renttracker.ui.util.localizedStringId
import com.realsanjeev.renttracker.ui.util.statusColorIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onLanguageToggle: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddTenant: () -> Unit,
    onTenantClick: (Tenant) -> Unit,
    onRecordPaymentConfirm: (PaymentRecord, Tenant) -> Unit,
    onSendReminder: () -> Unit,
    onDeleteTenant: (Tenant) -> Unit,
    onSeeAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLanguage = LocalContext.current.resources.configuration.locales[0].language
    val useNepali = when (uiState.preferences.numeralPreference) {
        UserPreferences.NumeralPreference.NEPALI.value -> true
        UserPreferences.NumeralPreference.ENGLISH.value -> false
        else -> currentLanguage == "ne"
    }

    var showRecordPaymentDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = onLanguageToggle) {
                        Icon(Icons.Default.Language, contentDescription = "Switch language")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Notifications, contentDescription = "Settings")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                item { GreetingSection(uiState.preferences, useNepali) }

                item { SummaryCard(uiState, useNepali) }

                item {
                    ActionChips(
                        onAddTenant = onAddTenant,
                        onRecordPayment = { showRecordPaymentDialog = true },
                        onSendReminder = onSendReminder
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.nav_tenants),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                if (uiState.tenants.isEmpty()) {
                    item {
                        DashboardEmptyState(onAddTenant = onAddTenant)
                    }
                } else {
                    items(
                        items = uiState.tenants,
                        key = { it.id }
                    ) { tenant ->
                        TenantCard(
                            tenant = tenant,
                            preferences = uiState.preferences,
                            useNepali = useNepali,
                            onClick = { onTenantClick(tenant) },
                            onLongClick = { onDeleteTenant(tenant) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showRecordPaymentDialog && uiState.tenants.isNotEmpty()) {
        RecordPaymentDialog(
            tenants = uiState.tenants,
            onDismiss = { showRecordPaymentDialog = false },
            onConfirmRecord = { record, updatedTenant ->
                onRecordPaymentConfirm(record, updatedTenant)
                showRecordPaymentDialog = false
            }
        )
    }
}

@Composable
private fun GreetingSection(preferences: UserPreferences, useNepali: Boolean) {
    Column(modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)) {
        Text(
            text = stringResource(R.string.greeting),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        val dayStr = if (useNepali) convertToNepaliDigitsStr("${preferences.defaultDueDay}") else preferences.defaultDueDay.toString()
        val formattedDay = if (useNepali) dayStr else "$dayStr${getOrdinalSuffix(preferences.defaultDueDay, false)}"
        Text(
            text = stringResource(R.string.rent_due_day_format, formattedDay),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SummaryCard(uiState: DashboardUiState, useNepali: Boolean) {
    val summary = uiState.summary
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Blue40,
                            Color(0xFF3B82F6)
                        )
                    )
                )
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.total_revenue),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "${if (useNepali) convertToNepaliDigitsStr("${summary.progressPercent}") else summary.progressPercent}% Collected",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = Formatting.formatAmountLocalized(
                        summary.totalRevenue,
                        uiState.preferences.currencySymbol,
                        useNepali
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    val progressValue = (summary.progressPercent / 100f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressValue)
                            .fillMaxHeight()
                            .background(Color.White.copy(alpha = 0.95f))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Collected: ${Formatting.formatAmountLocalized(summary.collected, uiState.preferences.currencySymbol, useNepali)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pending: ${Formatting.formatAmountLocalized(summary.pending, uiState.preferences.currencySymbol, useNepali)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionChips(
    onAddTenant: () -> Unit,
    onRecordPayment: () -> Unit,
    onSendReminder: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ActionChipItem(
            icon = Icons.Default.Add,
            label = stringResource(R.string.action_add_tenant),
            containerColor = Blue40,
            onClick = onAddTenant,
            modifier = Modifier.weight(1f)
        )
        ActionChipItem(
            icon = Icons.Default.AddCard,
            label = stringResource(R.string.action_record),
            containerColor = Green40,
            onClick = onRecordPayment,
            modifier = Modifier.weight(1f)
        )
        ActionChipItem(
            icon = Icons.Default.Send,
            label = stringResource(R.string.action_remind),
            containerColor = Amber40,
            onClick = onSendReminder,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionChipItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(containerColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = containerColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DashboardEmptyState(onAddTenant: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Tenants Added",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add your first tenant to start tracking revenue.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddTenant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Tenant")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TenantCard(
    tenant: Tenant,
    preferences: UserPreferences,
    useNepali: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val avatarColors = listOf(Blue40, Amber40, Color(0xFF7C3AED), Green40)
    val avatarColor = avatarColors[tenant.avatarColorIndex()]
    val status = tenant.calculatedStatus

    val statusColors = listOf(
        listOf(Green40, Color(0xFFD1FAE5)),
        listOf(Amber40, Color(0xFFFEF3C7)),
        listOf(Red40, Color(0xFFFEE2E2))
    )
    val statusColorPair = statusColors[status.statusColorIndex()]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tenant.initials,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tenant.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColorPair[1])
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(status.localizedStringId()),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColorPair[0],
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tenant.propertyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Formatting.formatAmountLocalized(tenant.totalAmount, preferences.currencySymbol, useNepali),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = Formatting.formatDateDisplay(tenant.paymentDate, preferences.calendarPreference, useNepali),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun getOrdinalSuffix(n: Int, useNepali: Boolean): String {
    if (useNepali) return ""
    if (n in 11..13) return "th"
    return when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

private fun convertToNepaliDigitsStr(input: String): String {
    val nepaliDigits = charArrayOf('०', '१', '२', '३', '४', '५', '६', '७', '८', '९')
    return input.map { ch ->
        if (ch in '0'..'9') nepaliDigits[ch - '0'] else ch
    }.joinToString("")
}
