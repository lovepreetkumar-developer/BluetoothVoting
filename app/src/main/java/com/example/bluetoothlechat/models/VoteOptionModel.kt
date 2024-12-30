package com.example.bluetoothlechat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoteOptionModel(
    val name: String = "",
    var selected: Boolean = false,
    var selected_users_id: MutableList<Int> = mutableListOf(),
    var need_to_view_count: Boolean = false
) : Parcelable