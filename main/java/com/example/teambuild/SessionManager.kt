package com.example.teambuild

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SessionManager {
        var sp: SharedPreferences
        var editor: SharedPreferences.Editor
        var context: Context

        constructor(context: Context) {
                this.context = context
                sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                editor = sp.edit()
        }

        companion object{
                val PREF_NAME = "TeamBuildPref"
                val LOGIN = "loggedIn"
                val KEY_NAME = "name"
                val KEY_PASSWORD = "password"
        }

        fun createSession(name: String, password: String){
                editor.putBoolean(LOGIN, true)
                editor.putString(KEY_NAME, name)
                editor.putString(KEY_PASSWORD, password)
                editor.commit()
        }

        fun editSession(password: String){
                editor.putBoolean(LOGIN, true)
                editor.putString(KEY_PASSWORD, password)
                editor.commit()
        }

        fun checkLogin(){
                if(!this.loggedIn()){
                        val i = Intent(context, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(i)
                }
        }

        fun getUserData(): HashMap<String, String>{
                var user: Map<String, String> = HashMap<String, String>()
                (user as HashMap).put(KEY_NAME, sp.getString(KEY_NAME, null).toString())
                user.put(KEY_PASSWORD, sp.getString(KEY_PASSWORD, null).toString())
                return user
        }

        fun logOut(){
                //Se borran los datos de la sesion
                editor.clear()
                editor.commit()

                //Se redirecciona a la pagina de login
                val i = Intent(context, MainActivity::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
        }

        fun loggedIn(): Boolean{
                return sp.getBoolean(LOGIN, false)
        }
}

