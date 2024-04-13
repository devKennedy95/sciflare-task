package com.task.sciflare.ui.fragment

import android.content.ContentResolver
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.task.sciflare.ui.listener.OnItemClick
import com.task.sciflare.databinding.FragmentReceiverBinding
import com.task.sciflare.models.MessageModel
import com.task.sciflare.ui.BaseFragment
import com.task.sciflare.ui.adapter.SmsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ReceiverFragment : BaseFragment<FragmentReceiverBinding>(), OnItemClick<MessageModel> {

    private val messageList: ArrayList<MessageModel> = ArrayList()

    override fun onCreateViewBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentReceiverBinding.inflate(inflater, container, false)

    override fun onViewReady(binding: FragmentReceiverBinding, savedInstanceState: Bundle?) {

    }

    override fun onResume() {
        super.onResume()
        readSms()
    }

    private fun readSms() {
        try {
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
                    val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    if (body.contains("From TaskApp:")) {
                        val message = body.split(":")
                        if (message.isNotEmpty() && message.size >= 2) {
                            val decryptMsg = decrypt(message[1].trim())
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
                                message = "\t ${message[0]}: $decryptMsg",
                                dateTime = date
                            )
                            messageList.add(messageModel)
                        }
                    }
                } while (cursor.moveToNext())
            }
            cursor?.close()

            val smsListAdapter =
                SmsListAdapter(messageList, this@ReceiverFragment)
            if (hasBinging()) {
                getBinding().apply {
                    receiverList.adapter = smsListAdapter
                }
            }
        } catch (ex: Exception) {
            Log.d(">> Error", ex.toString())
        }

    }

    override fun onItemClick(t: MessageModel) {

    }
}