package com.example.teambuild

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.teambuild.databinding.ActivityChatBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class Chat : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var appDB: BaseDatos
    private lateinit var session: SessionManager
    var messages: MutableList<Messages>? = null
   // var msgEmisor: String? = null
   // var msgReceptor: String? = null
    var userEmisor: String? = null
    var userReceptor: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Vinculación de la actividad Chat con su hoja de layout
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Asignacion de la BD
        appDB = BaseDatos.getDB(this)

        //Datos de la sesion
        session = SessionManager(applicationContext)
        var user = session.getUserData()
        var name = user.get(SessionManager.KEY_NAME)
        var pass = user.get(SessionManager.KEY_PASSWORD)

        //Se reciben los datos del contacto, viene de ChatAdapter, que vincula esta clase con ChatFragment
        val nombreChat = intent.getStringExtra("nombre")
        val perfilChat = intent.getStringExtra("perfil")

        binding.contactName.text = nombreChat

        Glide.with(this@Chat).load(perfilChat)
            .placeholder(R.drawable.avatar)
            .into(binding.profile)

        binding.arrowBackChat.setOnClickListener {
            finish()
        }

        userReceptor = nombreChat

        if(name == "admin" && pass == "1234"){
            userEmisor = "1"
        }else {
            userEmisor = name
        }

       // msgEmisor = userEmisor + userReceptor
       // msgReceptor = userReceptor + userEmisor

        messages = ArrayList()

        //Se vinculan los datos de los usuarios con la vista del chat
        val recyclerview = findViewById<RecyclerView>(R.id.chatView)
        recyclerview.layoutManager = LinearLayoutManager(this)


        lifecycleScope.launch(Dispatchers.IO) {
            val msgExists = appDB.mensajesDao().msgExists(userEmisor!!, userReceptor!!)
            if(msgExists){
                messages = appDB.mensajesDao().getMsg(userEmisor!!, userReceptor!!)
            }

            //Si existen mensajes entre usuarios, se agregan al adaptador
            val adapter = MessagesAdapter(this@Chat, messages)

            //Se vinculan los datos del adaptador con el recyclerview
            recyclerview.adapter = adapter

            binding.send.setOnClickListener {
                val msg = binding!!.typeChat.text.toString()
                val date = Date()
                val txt = Messages(null, msg, userEmisor, userReceptor, date.time)
                lifecycleScope.launch(Dispatchers.IO) {
                    appDB.mensajesDao().insertMsg(txt)
                    val newData = appDB.mensajesDao().getMsg(userEmisor!!, userReceptor!!)
                    messages?.clear()
                    messages?.addAll(newData)
                }
                binding.typeChat.setText("")
                adapter.notifyDataSetChanged()
            }

        }



    }
}