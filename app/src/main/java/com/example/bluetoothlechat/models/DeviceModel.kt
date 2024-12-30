package com.example.bluetoothlechat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeviceModel(
    var name: String = "",
    var address: String = ""
) : Parcelable