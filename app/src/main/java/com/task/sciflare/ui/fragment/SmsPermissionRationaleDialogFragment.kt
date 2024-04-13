package com.task.sciflare.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.task.sciflare.R

class SmsPermissionRationaleDialogFragment : DialogFragment() {

    companion object {
        const val RESULT_OK = "result_ok"
        const val RESULT_CANCELLED = "result_cancelled"
        const val TAG: String = ">> Location"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val message = R.string.sms_permission_rationale_message
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.need_permission)
            .setMessage(message)
            .setPositiveButton(R.string.configuration) { _, _ ->
                setFragmentResult(RESULT_OK, bundleOf())
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                setFragmentResult(RESULT_CANCELLED, bundleOf())
                dialog.dismiss()
            }
            .create()
    }
}
