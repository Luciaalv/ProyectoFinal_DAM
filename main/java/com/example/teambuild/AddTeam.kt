package com.example.teambuild

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teambuild.databinding.ActivityAddTeamBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import android.app.AlertDialog
import android.content.Intent
import kotlinx.coroutines.launch



class AddTeam : AppCompatActivity() {

    private lateinit var binding: ActivityAddTeamBinding
    private lateinit var appDB: BaseDatos
    private lateinit var recyclerView: RecyclerView
    private var myAdapter: TeamAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        //Vinculación de la actividad Chat con su hoja de layout
        binding = ActivityAddTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Conexion con la BD
        appDB = BaseDatos.getDB(this)

        //RecyclerView para mostrar las obras de la BD
        recyclerView = findViewById<RecyclerView>(R.id.list_of_workers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        myAdapter = TeamAdapter()

        binding.arrowBack.setOnClickListener {
            finish()
        }

        val obra = intent.getParcelableExtra<Obra>("obra")
        val tipo = intent.getStringExtra("tipo")

        lifecycleScope.launch(Dispatchers.IO) {

            val alb = ArrayList<Empleado>()
            val ofi1 = ArrayList<Empleado>()
            val ofi2 = ArrayList<Empleado>()
            val peon = ArrayList<Empleado>()

            recyclerView.apply {
                // Se guarda la lista de empleados en una variable
                var  team = appDB.empleadoDao().getAllEmp()
                //Se recorre con un bucle para buscar el puesto en concreto y se guardan los empleados en arrays
                team.forEach{
                    if(it.cargo == "Albañil"){
                        alb.add(it)
                    }else if (it.cargo == "Oficial 1"){
                        ofi1.add(it)
                    }else if (it.cargo == "Oficial 2"){
                        ofi2.add(it)
                    }else if(it.cargo == "Peón"){
                        peon.add(it)
                    }
                }

                // Se añaden los datos de la variable a la vista de la pagina
                recyclerView.adapter = myAdapter

                if(!alb.isEmpty() && tipo == "alb"){
                    myAdapter?.setData(alb)
                }
                if(!ofi1.isEmpty() && tipo == "ofi1"){
                    myAdapter?.setData(ofi1)
                }
                if(!ofi2.isEmpty() && tipo == "ofi2"){
                    myAdapter?.setData(ofi2)
                }
                if(!peon.isEmpty() && tipo == "peon"){
                    myAdapter?.setData(peon)
                }


                //Funcion para agregar miembros al equipo, viene de TeamAdapter
                myAdapter?.onClickAddListener {
                    val builder = AlertDialog.Builder(this@AddTeam)
                    GlobalScope.launch(Dispatchers.IO) {
                        val o = appDB.obraDao().getWork(obra?.nombre, obra?.direccion)
                        for (e in o.equipo){
                            if(it.id == e.id){
                                runOnUiThread {
                                    builder.setMessage("Ya pertenece al equipo.")
                                    builder.create().show()
                                }
                                return@launch
                            }
                        }
                        o.equipo.add(it)
                        appDB.obraDao().updateWork(o)
                        runOnUiThread {
                            builder.setMessage("Se ha añadido el miembro al equipo.")
                            builder.create().show()
                        }
                    }

                }

                //Funcion para eliminar miembros del equipo, viene de TeamAdapter
                myAdapter?.onClickDeleteListener {
                    val builder = AlertDialog.Builder(this@AddTeam)
                    builder.setMessage("¿Seguro que quieres eliminar a este miembro del equipo?")
                    builder.setPositiveButton("Sí"){p0, _ ->
                        //Se añade la corrutina para poder llamar a la función suspendida
                        GlobalScope.launch(Dispatchers.IO) {
                            val o = appDB.obraDao().getWork(obra?.nombre, obra?.direccion)
                            o!!.equipo.remove(it)
                            appDB.obraDao().updateWork(o)
                        }
                        val secondBuilder = AlertDialog.Builder(this@AddTeam)
                        secondBuilder.setMessage("Miembro eliminado")
                        secondBuilder.setPositiveButton("Vale") { _,_ -> }
                        secondBuilder.create().show()
                        myAdapter?.notifyDataSetChanged()
                        p0.dismiss()
                    }
                    builder.setNegativeButton("No"){p0,_ ->
                        p0.dismiss()
                    }
                    builder.create().show()
                }

            }
        }
    }


}
