package com.kingkoval.leemchat.model

data class Message(
    var message: String = "",
    var senderUid: String = "",
    var timeMessage: Any? = null
)