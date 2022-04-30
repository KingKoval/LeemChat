package com.kingkoval.leemchat.model

data class Friend(
    val profileImage: String = "",
    val name: String = "",
    val uid: String = "",
    val time_last_sms: Long = 0
)

//public val comparator = Comparator<Friend>{
//    x1, x2 ->
//
//    return@Comparator x1.time_last_sms.compareTo(x2.time_last_sms)
//}