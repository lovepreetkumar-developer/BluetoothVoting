package com.example.bluetoothlechat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var id: Int = 0,
    var email: String = "",
    var login_type: Int = 0
) : Parcelable