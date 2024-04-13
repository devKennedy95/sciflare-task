package com.task.sciflare.models

data class MessageModel(
    var title: String? = null,

    var message: String? = null,

    var dateTime: String? = null,

    var status: Boolean? = false,
)