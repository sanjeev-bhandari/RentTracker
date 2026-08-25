package com.realsanjeev.renttracker.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.realsanjeev.renttracker.R
import java.time.LocalDate

@Composable
fun NepaliDatePickerDialog(
    initialDate: NepaliDate,
    useNepaliNumerals: Boolean,
    onDateSelected: (NepaliDate) -> Unit,
    onDismiss: () -> Unit
) {
    val todayBs = NepaliCalendar.toBs(LocalDate.now())
    val minYear = maxOf(1970, todayBs.year - 100)
    val maxYear = minOf(2250, todayBs.year + 10)

    var year by remember { mutableIntStateOf(initialDate.year.coerceIn(minYear, maxYear)) }
    var month by remember { mutableIntStateOf(initialDate.month.coerceIn(1, 12)) }
    val maxDay = NepaliCalendar.daysInMonth(year, month)
    var day by remember { mutableIntStateOf(initialDate.day.coerceIn(1, maxDay)) }

    var yearExpanded by remember { mutableStateOf(false) }
    var monthExpanded by remember { mutableStateOf(false) }
    var dayExpanded by remember { mutableStateOf(false) }

    fun digits(value: Int): String =
        if (useNepaliNumerals) Formatting.toNepaliDigits(value.toString()) else value.toString()

    val years = (minYear..maxYear).toList()
    val months = (1..12).map { it to "${digits(it)} (${NepaliCalendar.monthName(it)} / ${NepaliCalendar.monthNameNepali(it)})" }
    val days = (1..maxDay).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.bs_picker_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DateDropdown(
                        label = stringResource(R.string.bs_picker_year),
                        value = digits(year),
                        expanded = yearExpanded,
                        options = years.map(::digits),
                        onExpandedChange = { yearExpanded = it },
                        onSelect = { index ->
                            year = years[index]
                            val newMax = NepaliCalendar.daysInMonth(year, month)
                            if (day > newMax) day = newMax
                            yearExpanded = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    DateDropdown(
                        label = stringResource(R.string.bs_picker_month),
                        value = months.first { it.first == month }.second,
                        expanded = monthExpanded,
                        options = months.map { it.second },
                        onExpandedChange = { monthExpanded = it },
                        onSelect = { index ->
                            month = months[index].first
                            val newMax = NepaliCalendar.daysInMonth(year, month)
                            if (day > newMax) day = newMax
                            monthExpanded = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                DateDropdown(
                    label = stringResource(R.string.bs_picker_day),
                    value = digits(day),
                    expanded = dayExpanded,
                    options = days.map(::digits),
                    onExpandedChange = { dayExpanded = it },
                    onSelect = { index ->
                        day = days[index]
                        dayExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth(0.4f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDateSelected(NepaliDate(year, month, day)) }) {
                Text(stringResource(R.string.btn_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.btn_cancel)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    options: List<String>,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = { onSelect(index) }
                )
            }
        }
    }
}
