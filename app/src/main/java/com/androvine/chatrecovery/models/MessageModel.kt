package com.androvine.chatrecovery.models

data class MessageModel (
    val id: Int = 0,
    val user: String = "",
    val message: String = "",
    val time: Long = 0,
    val avatarFileName: String = "",
    val messageSummary: String = "",
)