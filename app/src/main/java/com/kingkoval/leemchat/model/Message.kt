package com.kingkoval.leemchat.model

import android.annotation.SuppressLint
import android.util.Log
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

val dayOfMillisecond = 86400000
val weekOfMillisecond = 604800000

@SuppressLint("NewApi")
data class Message(
    var message: String = "",
    var senderUid: String = "",
    var timeMessage: Any? = null
) {
//    lateinit var messageKey: String

    constructor(
        message: String,
        senderUid: String,
        timeMessage: Any?,
        messageKey: String
    ) : this(
            message,
            senderUid,
            timeMessage
        ) {
//        this.messageKey = messageKey
    }

    fun convertTime(time: Long): ZonedDateTime {
        val i = Instant.ofEpochMilli(time)
        val zone = ZoneId.of(ZonedDateTime.now().zone.id)
        Log.i("ZONE123", zone.toString())

        return ZonedDateTime.ofInstant(i, zone)
    }


    fun formatForChat(date: ZonedDateTime): String{
        val now = ZonedDateTime.now().toInstant().toEpochMilli()

        return if (now - date.toInstant().toEpochMilli() < dayOfMillisecond)
            date.format(DateTimeFormatter.ofPattern("HH:mm"))
        else
            date.format(DateTimeFormatter.ofPattern("d LLL HH:mm"))

    }

    fun formatForUsersList(date: ZonedDateTime): String{
        val now = ZonedDateTime.now().toInstant().toEpochMilli()

        return if (now - date.toInstant().toEpochMilli() < dayOfMillisecond)
            date.format(DateTimeFormatter.ofPattern("HH:mm"))
        else if (now - date.toInstant().toEpochMilli() < weekOfMillisecond)
            date.format(DateTimeFormatter.ofPattern("EEE"))
        else
            date.format(DateTimeFormatter.ofPattern("d LLL"))

    }

}