package com.example.teambuild

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teambuild.databinding.ActivityChatListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatListBinding
    private lateinit var appDB: BaseDatos
    private lateinit var session: SessionManager
    private lateinit var name: String
    private lateinit var pass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Se reciben los datos de la sesion
        session = SessionManager(applicationContext)
        var user = session.getUserData()
        name = user.get(SessionManager.KEY_NAME).toString()
        pass = user.get(SessionManager.KEY_PASSWORD).toString()

        //Variable para guardar la conexion con la BD
        appDB = BaseDatos.getDB(this)


        val recyclerview = binding.listOfContacts
        recyclerview.layoutManager = GridLayoutManager(this,2)

        lifecycleScope.launch(Dispatchers.IO) {
            var arr = ArrayList<Empleado>()
            recyclerview.apply {
                // Se guarda la lista de empleados en una variable
                var empleados = appDB.empleadoDao().getAllEmp()
                //Filtro para no icluir al usuario que ha iniciado sesion
                empleados.forEach {
                    if (it.nombre != name) {
                        arr.add(it)
                    }
                }
                val adapter = ChatAdapter(this@ChatListActivity, arr)
                // Se añaden los datos de la variable a la vista de la pagina
                recyclerview.adapter = adapter
            }
        }


        binding.arrowBack.setOnClickListener{
            val i = Intent(this, SecondActivity::class.java)
            startActivity(i)
        }

    }
}