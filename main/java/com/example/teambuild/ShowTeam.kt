package com.example.teambuild

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.teambuild.databinding.ActivityShowTeamBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShowTeam : AppCompatActivity() {

    private lateinit var binding: ActivityShowTeamBinding
    private lateinit var appDB: BaseDatos
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Vinculación de la actividad Chat con su hoja de layout
        binding = ActivityShowTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Se aplica el contexto a la sesion
        session = SessionManager(applicationContext)
        var user = session.getUserData()
        var name = user.get(SessionManager.KEY_NAME)
        var pass = user.get(SessionManager.KEY_PASSWORD)

        //Asignacion de la BD
        appDB = BaseDatos.getDB(this)

        lifecycleScope.launch(Dispatchers.IO) {
            val emp = appDB.empleadoDao().getEmployee(name!!, pass!!)
            //Si el usuario es admin o un jefe, obtiene acceso a la seccion de crear obras
            if(emp != null){
                if(emp.cargo != "Jefe de obra"){
                    binding.addAlb.visibility = View.GONE
                    binding.addPrimera.visibility = View.GONE
                    binding.addSegunda.visibility = View.GONE
                    binding.addPeones.visibility = View.GONE
                }
            }

        }



        binding.arrowBack.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()

        val obras = intent.getParcelableExtra<Obra>("obra")

        val albNames = ArrayList<String>()
        val ofi1Names = ArrayList<String>()
        val ofi2Names = ArrayList<String>()
        val peonNames = ArrayList<String>()

        lifecycleScope.launch(Dispatchers.IO) {
            val o = appDB.obraDao().getWork(obras?.nombre, obras?.direccion)
            if(o.equipo != null && !o.equipo.isEmpty()){
                o.equipo.forEach{
                    if(it.cargo == "Albañil"){
                        albNames.add(it.nombre!!)
                    }else if(it.cargo == "Oficial 1"){
                        ofi1Names.add(it.nombre!!)
                    }else if(it.cargo == "Oficial 2"){
                        ofi2Names.add(it.nombre!!)
                    }else if(it.cargo == "Peón"){
                        peonNames.add(it.nombre!!)
                    }
                }
                runOnUiThread {
                    binding.detTeamInput.text =  albNames.joinToString ( ", ", "", "", 5, "..." )
                    binding.detPrimeraInput.text =  ofi1Names.joinToString ( ", ", "", "", 5, "..." )
                    binding.detSegundaInput.text =  ofi2Names.joinToString ( ", ", "", "", 5, "..." )
                    binding.detPeonInput.text =  peonNames.joinToString ( ", ", "", "", 5, "..." )
                }
            }
        }


        binding.addAlb.setOnClickListener{
            newIntent("alb", obras!!)
        }
        binding.addPrimera.setOnClickListener{
            newIntent("ofi1", obras!!)
        }
        binding.addSegunda.setOnClickListener{
            newIntent("ofi2", obras!!)
        }
        binding.addPeones.setOnClickListener{
            newIntent("peon", obras!!)
        }

    }

    private fun newIntent(tipo: String, obra: Obra){
        val i = Intent(this, AddTeam::class.java)
        i.putExtra("obra", obra)
        i.putExtra("tipo", tipo)
        startActivity(i)
    }


}