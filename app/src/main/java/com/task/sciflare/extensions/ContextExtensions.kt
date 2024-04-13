package com.task.sciflare.extensions

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.task.sciflare.R

fun Context.hasPermission(permission: String): Boolean =
    checkSelfPermission(permission) == PERMISSION_GRANTED

fun Context.showAlert(
    message: String,
) = showAlert(message) { dialog, _ ->
    dialog.dismiss()
}

fun Context.showAlert(
    message: String,
    listener: DialogInterface.OnClickListener
) = AlertDialog.Builder(this).apply {
    setTitle(this@showAlert.getString(R.string.app_name))
    setMessage(message)
    setCancelable(false)
    setPositiveButton(getString(R.string.ok), listener)
}

//show SnackBar
fun showSnackBar(view: View, msg: String) {
    Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
}


