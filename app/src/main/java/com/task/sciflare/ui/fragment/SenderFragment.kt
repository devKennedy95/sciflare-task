package com.task.sciflare.ui.fragment

import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.task.sciflare.ui.listener.OnItemClick
import com.task.sciflare.R
import com.task.sciflare.databinding.FragmentSenderBinding
import com.task.sciflare.extensions.hasSMSREADPermission
import com.task.sciflare.extensions.hasSMSSENDPermission
import com.task.sciflare.extensions.hideKeyboard
import com.task.sciflare.extensions.showAlert
import com.task.sciflare.extensions.showSnackBar
import com.task.sciflare.models.MessageModel
import com.task.sciflare.ui.BaseFragment
import com.task.sciflare.ui.adapter.SmsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class SenderFragment : BaseFragment<FragmentSenderBinding>(), OnItemClick<MessageModel> {
    private val messageList: ArrayList<MessageModel> = ArrayList()

    override fun onCreateViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentSenderBinding.inflate(inflater, container, false)

    override fun onViewReady(binding: FragmentSenderBinding, savedInstanceState: Bundle?) {
        getBinding().btnSend.setOnClickListener { view ->
            view.hideKeyboard()
            val message: String = getBinding().textMessage.text.toString()
            val toAddress: String = getBinding().toAddress.text.toString()
            if (toAddress.isEmpty()) {
                showSnackBar(view, getString(R.string.addressE))
            } else if (message.isEmpty()) {
                showSnackBar(view, getString(R.string.messageE))
            } else if (toAddress.isNotEmpty() && message.isNotEmpty()) {
                if (requireContext().hasSMSSENDPermission()) {
                    val encryptData = encrypt(message)
                    sendSMS(toAddress, "From TaskApp: $encryptData")
                } else {
                    requestSMSPermission(this, requestPermission) {
                        showSnackBar(view, "Try sending again!")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        readSms()
    }

    private fun sendSMS(phoneNumber: String, encryptData: String) {
        try {
            getBinding().textMessage.setText("")
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requireContext().getSystemService(SmsManager::class.java)
            } else {
                SmsManager.getDefault()
            }
            val parts: ArrayList<String> = smsManager.divideMessage(encryptData)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            requireContext().showAlert("Message sent successfully").show()
            readSms()
        } catch (ex: Exception) {
            Log.d(">> Error", ex.toString())
        }
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            readSms()
        }

    private fun readSms() {
        try {
            if (!requireContext().hasSMSREADPermission()) {
                requestSMSPermission(this, requestPermission) {
                    readSms()
                }
            } else {
                messageList.clear()
                val contentResolver: ContentResolver? = context?.contentResolver
                val cursor = contentResolver?.query(
                    Telephony.Sms.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val body =
                            cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                        if (body.contains("From TaskApp:")) {
                            val address =
                                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                            val timestampMillis =
                                cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                            val instant = Instant.ofEpochMilli(timestampMillis.toLong())
                            val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                            val formatter = DateTimeFormatter.ofPattern("dd-MM-yy hh:mm a")
                            val date = dateTime.format(formatter)
                            val messageModel = MessageModel(
                                title = "To : $address",
                                message = "\t $body",
                                dateTime = date
                            )
                            messageList.add(messageModel)
                        }
                    } while (cursor.moveToNext())
                }
                cursor?.close()

                val smsListAdapter =
                    SmsListAdapter(messageList, this@SenderFragment)
                if (hasBinging()) {
                    getBinding().apply {
                        senderList.adapter = smsListAdapter
                    }
                }
            }
        } catch (ex: Exception) {
            Log.d(">> Error", ex.toString())
        }
    }

    override fun onItemClick(t: MessageModel) {

    }
}