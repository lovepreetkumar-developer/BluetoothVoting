package com.example.bluetoothlechat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoteModel(
    var id: Int = 0,
    var vote_title: String = "",
    var admin_name: String = "Admin",
    var description: String = "",
    var voted_by_list: MutableList<Int> = mutableListOf(),
    var user_ids: MutableList<Int> = mutableListOf(),
    var options: MutableList<VoteOptionModel> = mutableListOf()
) : Parcelable