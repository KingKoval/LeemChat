package com.kingkoval.leemchat.model

import android.provider.ContactsContract

data class User(
    var profileImage: String = "",
    var name: String = "",
    var email: String = "",
    var uid: String = "",
)