package com.adikul.hrmaa

import android.content.Context
import android.content.SharedPreferences

abstract class SessionSharedPref {


    companion object obj{

        const val PREF_KEY_SESSION_NUM = "PREF_KEY_SESSION_NUM"
        const val SHARED_PREF_FILE = "com.adikul.hrmaa.SHARED_PREF_FILE"

        private fun getSharedPrefs(context : Context) : SharedPreferences{
            return context.getSharedPreferences( SHARED_PREF_FILE, Context.MODE_PRIVATE)
        }
        public fun getSessionNum( context : Context) : Int{
            val prefs = getSharedPrefs(context)
            val sess_num = prefs.getInt( PREF_KEY_SESSION_NUM, 0)
            return sess_num
        }

        public fun setSessionNum( context : Context, sess : Int) {
            val prefs = getSharedPrefs(context)
            val editor = prefs.edit()
            editor.putInt( PREF_KEY_SESSION_NUM, sess)
            editor.apply()
        }
    }
}