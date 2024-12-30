package com.example.bluetoothlechat.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.bluetoothlechat.models.UserModel
import com.example.bluetoothlechat.models.VoteModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PreferenceProvider(context: Context) {

    private val appContext = context.applicationContext

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun clear(): Boolean {
        return preferences.edit().clear().commit()
    }

    fun setAdminVotesList(list: MutableList<VoteModel>) {
        val gson = Gson()
        val json = gson.toJson(list)
        return preferences.edit().putString(
            Constants.ADMIN_VOTES_LIST,
            json
        ).apply()
    }

    fun getAdminVotesList(): MutableList<VoteModel>? {
        val arrayItems: MutableList<VoteModel>?
        val serializedObject: String? = preferences.getString(Constants.ADMIN_VOTES_LIST, null)
        val gson = Gson()
        val type: Type = object : TypeToken<MutableList<VoteModel>>() {}.type
        arrayItems = gson.fromJson(serializedObject, type)
        return arrayItems
    }

    fun setUserVotesList(list: MutableList<VoteModel>) {
        val gson = Gson()
        val json = gson.toJson(list)
        return preferences.edit().putString(
            Constants.USER_VOTES_LIST,
            json
        ).apply()
    }

    fun getUserVotesList(): MutableList<VoteModel>? {
        val arrayItems: MutableList<VoteModel>?
        val serializedObject: String? = preferences.getString(Constants.USER_VOTES_LIST, null)
        val gson = Gson()
        val type: Type = object : TypeToken<MutableList<VoteModel>>() {}.type
        arrayItems = gson.fromJson(serializedObject, type)
        return arrayItems
    }

    fun setUser(userModel: UserModel?): Boolean {
        return preferences.edit().putString(
            Constants.USER, Gson().toJson(userModel)
        ).commit()
    }

    fun getUser(): UserModel? {
        return try {
            val s: String = preferences.getString(Constants.USER, null)
                ?: return null
            Gson().fromJson(s, UserModel::class.java)
        } catch (e: Exception) {
            null
        }
    }
}