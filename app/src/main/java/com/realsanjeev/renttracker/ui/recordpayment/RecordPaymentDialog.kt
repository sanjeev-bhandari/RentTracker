package com.realsanjeev.renttracker.ui.recordpayment

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.realsanjeev.renttracker.domain.model.PaymentRecord
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.TenantStatus
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentDialog(
    tenants: List<Tenant>,
    initialSelectedTenant: Tenant? = null,
    onDismiss: () -> Unit,
    onConfirmRecord: (PaymentRecord, Tenant) -> Unit
) {
    if (tenants.isEmpty()) return

    var selectedTenant by remember { mutableStateOf(initialSelectedTenant ?: tenants.first()) }
    var tenantDropdownExpanded by remember { mutableStateOf(false) }

    var paymentDate by remember(selectedTenant) {
        mutableStateOf(LocalDate.now().toString())
    }
    var rentAmount by remember(selectedTenant) {
        mutableStateOf(selectedTenant.rentPay.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() })
    }
    var unitLast by remember(selectedTenant) {
        mutableStateOf(selectedTenant.electricityUnitCurrent.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() })
    }
    var unitCurrent by remember(selectedTenant) {
        mutableStateOf("")
    }
    var rate by remember(selectedTenant) {
        mutableStateOf(selectedTenant.electricityRate.let { if (it == it.toLong().toDouble()) it.toLong().toString() else it.toString() })
    }
    var note by remember { mutableStateOf("") }

    val context = LocalContext.current

    val lastNum = unitLast.toDoubleOrNull() ?: 0.0
    val currentNum = unitCurrent.toDoubleOrNull() ?: lastNum
    val unitsUsed = kotlin.math.max(0.0, currentNum - lastNum)
    val rateNum = rate.toDoubleOrNull() ?: 0.0
    val electricityCost = unitsUsed * rateNum
    val rentNum = rentAmount.toDoubleOrNull() ?: 0.0
    val totalAmount = rentNum + electricityCost

    val isFormValid = rentNum > 0 && currentNum >= lastNum

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Record Payment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Select Tenant Dropdown (if initialSelectedTenant == null or multiple choices available)
                ExposedDropdownMenuBox(
                    expanded = tenantDropdownExpanded,
                    onExpandedChange = { tenantDropdownExpanded = !tenantDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = "${selectedTenant.name} (${selectedTenant.propertyName})",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tenant") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tenantDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = tenantDropdownExpanded,
                        onDismissRequest = { tenantDropdownExpanded = false }
                    ) {
                        tenants.forEach { tenant ->
                            DropdownMenuItem(
                                text = { Text("${tenant.name} (${tenant.propertyName})") },
                                onClick = {
                                    selectedTenant = tenant
                                    tenantDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Payment Date
                OutlinedTextField(
                    value = paymentDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Date") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, yr, mo, dy ->
                                    paymentDate = String.format(Locale.US, "%04d-%02d-%02d", yr, mo + 1, dy)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick date")
                        }
                    }
                )

                // Rent Amount
                OutlinedTextField(
                    value = rentAmount,
                    onValueChange = { rentAmount = it },
                    label = { Text("Rent Amount") },
                    prefix = { Text("Rs. ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Meter Readings (Last vs Current)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = unitLast,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Last Reading") },
                        suffix = { Text("units") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = unitCurrent,
                        onValueChange = { unitCurrent = it },
                        label = { Text("Current Reading") },
                        suffix = { Text("units") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Rate per unit
                OutlinedTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = { Text("Rate per Unit") },
                    prefix = { Text("Rs. ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Summary Card with live total calculation preview
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Calculation Preview",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Electricity Units: ${String.format(Locale.US, "%.1f", unitsUsed)} units",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Electricity Cost: Rs. ${String.format(Locale.US, "%.2f", electricityCost)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Total Payable: Rs. ${String.format(Locale.US, "%.2f", totalAmount)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note / Description (Optional)") },
                    placeholder = { Text("e.g. Paid via eSewa / Cash") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val period = try {
                                LocalDate.parse(paymentDate).format(DateTimeFormatter.ofPattern("MMM yyyy"))
                            } catch (e: Exception) {
                                paymentDate
                            }

                            val record = PaymentRecord(
                                tenantId = selectedTenant.id,
                                paymentDate = paymentDate,
                                periodCovered = period,
                                rentAmount = rentNum,
                                electricityUnitLast = lastNum,
                                electricityUnitCurrent = currentNum,
                                electricityRate = rateNum,
                                electricityAmount = electricityCost,
                                totalAmount = totalAmount,
                                note = note
                            )

                            // Calculate next due date by advancing 1 month
                            val nextDueDate = try {
                                LocalDate.parse(selectedTenant.paymentDate).plusMonths(1).toString()
                            } catch (e: Exception) {
                                LocalDate.now().plusMonths(1).toString()
                            }

                            val updatedTenant = selectedTenant.copy(
                                electricityUnitLast = currentNum,
                                electricityUnitCurrent = currentNum,
                                paymentDate = nextDueDate,
                                status = TenantStatus.PAID
                            )

                            onConfirmRecord(record, updatedTenant)
                        },
                        enabled = isFormValid,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Record")
                    }
                }
            }
        }
    }
}
