package com.kingkoval.leemchat

import android.provider.ContactsContract

data class User(
    var profileImage: String = "",
    var name: String = "",
    var email: String = "",
    var uid: String = ""
)