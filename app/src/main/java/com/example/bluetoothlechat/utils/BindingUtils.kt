package com.example.bluetoothlechat.utils

import android.content.SharedPreferences
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import com.example.bluetoothlechat.R
import com.example.bluetoothlechat.models.UserModel
import com.example.bluetoothlechat.models.VoteOptionModel
import com.google.gson.Gson


/**
 * Property of TicketShala, Inc @ 2021 All Rights Reserved.
 */

class BindingUtils {

    companion object {

        @BindingAdapter(value = ["set_option_background"])
        @JvmStatic
        fun setOptionBackground(
            appCompatTextView: AppCompatTextView,
            voteOptionModel: VoteOptionModel
        ) {
            val prefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appCompatTextView.context)
            prefs.getString(Constants.USER, "")?.let { userString ->
                val userModel = Gson().fromJson(userString, UserModel::class.java)
                userModel?.let { finalUserModel ->
                    if (voteOptionModel.selected_users_id.contains(finalUserModel.id)) {
                        appCompatTextView.setBackgroundResource(R.drawable.bg_round_green_10sdp_filled)
                    } else {
                        if (voteOptionModel.selected) {
                            appCompatTextView.setBackgroundResource(R.drawable.bg_round_green_10sdp_filled)
                        } else {
                            appCompatTextView.setBackgroundResource(R.drawable.bg_border_gray_light_10sdp)
                        }
                    }
                }
            }
        }

        @BindingAdapter(value = ["set_option_text_color"])
        @JvmStatic
        fun setOptionTextColor(
            appCompatTextView: AppCompatTextView,
            voteOptionModel: VoteOptionModel
        ) {
            val prefs: SharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(appCompatTextView.context)
            prefs.getString(Constants.USER, "")?.let { userString ->
                val userModel = Gson().fromJson(userString, UserModel::class.java)
                userModel?.let { finalUserModel ->
                    if (voteOptionModel.selected_users_id.contains(finalUserModel.id)) {
                        appCompatTextView.setTextColor(
                            ContextCompat.getColor(
                                appCompatTextView.context,
                                R.color.white
                            )
                        )
                    } else {

                        if (voteOptionModel.selected) {
                            appCompatTextView.setTextColor(
                                ContextCompat.getColor(
                                    appCompatTextView.context,
                                    R.color.white
                                )
                            )
                        } else {
                            appCompatTextView.setTextColor(
                                ContextCompat.getColor(
                                    appCompatTextView.context,
                                    R.color.color_text_gray
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}