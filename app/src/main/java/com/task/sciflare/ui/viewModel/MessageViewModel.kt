package com.task.sciflare.ui.viewModel

import androidx.lifecycle.ViewModel
import com.task.sciflare.models.MessageModel

class MessageViewModel : ViewModel() {

    private lateinit var list: MessageModel

    fun bind(list: MessageModel) {
        this.list = list
    }

    fun getTitle() = list.title

    fun getMessage() = list.message

    fun getDateTime() = list.dateTime

    fun getStatus() = list.status
}