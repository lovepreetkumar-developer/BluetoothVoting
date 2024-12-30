package com.example.bluetoothlechat.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdminAllVotesModel(
    var admin_al_votes: MutableList<VoteModel> = mutableListOf()
) : Parcelable