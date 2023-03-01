package com.example.wallpaperapplication.preferences

import android.content.Context
import android.content.SharedPreferences

@Suppress("UNCHECKED_CAST")
class PreferenceManager(context: Context) {
    var usersession: SharedPreferences
    var editor: SharedPreferences.Editor
    private val IS_LOGGED_IN = "IsLoggedIn"
    private val FIRST_RUN = "FirstRun"

    init {
        usersession = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        editor = usersession.edit()
    }

    fun set_FirstRun(firstRun: Boolean) {
        editor.putBoolean(FIRST_RUN, firstRun)
        editor.apply()
    }

    fun get_FirstRun(): Boolean {
        return usersession.getBoolean(FIRST_RUN, true)
    }

    fun store_interests(names:ArrayList<String>){

        val setInterests: MutableSet<String> = HashSet()
        setInterests.addAll(names)
        editor.putStringSet("interests", setInterests)
        editor.commit()
    }

    fun get_interests():ArrayList<String>{
        val set: MutableSet<String>? = usersession.getStringSet("interests", null)

        return set as ArrayList<String>
    }

    fun storeLoginStatus() {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.commit()
    }

    fun isLoggedIn(): Boolean {
        return usersession.getBoolean(IS_LOGGED_IN, false)
    }

    fun logout() {
        editor.clear()
        editor.commit()
    }
}