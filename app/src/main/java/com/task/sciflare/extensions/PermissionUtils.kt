package com.task.sciflare.extensions

import android.Manifest.permission.*
import android.content.Context

fun Context.hasSMSREADPermission(): Boolean {
    return this.hasPermission(READ_SMS)
}

fun Context.hasSMSSENDPermission(): Boolean {
    return this.hasPermission(SEND_SMS)
}

fun smsPermissionArray() = arrayOf(READ_SMS, SEND_SMS)