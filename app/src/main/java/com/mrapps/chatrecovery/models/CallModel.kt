package com.mrapps.chatrecovery.models

data class CallModel (
    val id: Int = 0,
    val user: String = "",
    val callType: CallType,
    val callStatus: CallStatus,
    val time: Long = 0,
    val avatarFileName: String = ""
)

enum class CallType{
    AUDIO,
    VIDEO,
    UNKNOWN
}

enum class CallStatus{
    ONGOING,
    MISSED,
    INCOMING
}
