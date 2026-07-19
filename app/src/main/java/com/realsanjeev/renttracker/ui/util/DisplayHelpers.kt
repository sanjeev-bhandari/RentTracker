package com.realsanjeev.renttracker.ui.util

import com.realsanjeev.renttracker.R
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.TenantStatus

fun TenantStatus.localizedStringId(): Int = when (this) {
    TenantStatus.PAID -> R.string.status_paid
    TenantStatus.PENDING -> R.string.status_pending
    TenantStatus.OVERDUE -> R.string.status_overdue
}

fun TenantStatus.displayName(): String = when (this) {
    TenantStatus.PAID -> "PAID"
    TenantStatus.PENDING -> "PENDING"
    TenantStatus.OVERDUE -> "OVERDUE"
}

fun TenantStatus.statusColorIndex(): Int = when (this) {
    TenantStatus.PAID -> 0
    TenantStatus.PENDING -> 1
    TenantStatus.OVERDUE -> 2
}

fun Tenant.avatarColorIndex(): Int {
    val hash = name.hashCode()
    return kotlin.math.abs(hash) % 4
}
