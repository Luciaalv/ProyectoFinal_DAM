package com.example.teambuild


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.teambuild.databinding.ActivityDetailedWorkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedWork : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedWorkBinding
    private lateinit var appDB: BaseDatos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Vinculación de la actividad con su hoja de layout
        binding = ActivityDetailedWorkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDB = BaseDatos.getDB(this)

        val obras = intent.getParcelableExtra<Obra>("obras")
        if(obras != null){
            val nombre : TextView = findViewById(R.id.det_obra_input)
            val direccion : TextView = findViewById(R.id.det_direccion_input)
            val encargado : TextView = findViewById(R.id.det_encargado_input)
            val fecha : TextView = findViewById(R.id.det_fecha_input)
            val notas : EditText = findViewById(R.id.det_notas_input)

            nombre.text = obras.nombre
            direccion.text = obras.direccion
            encargado.text = obras.encargado
            fecha.text = obras.fecha
            binding.detNotasInput.setText(obras.comentarios)

            binding.detNotasInput.setOnFocusChangeListener { view, foco ->
                if(!foco){
                    //Se guardan los comentarios en la BD cuando el usuario toca fuera de la caja de texto
                    GlobalScope.launch(Dispatchers.IO) {
                        appDB.obraDao().updateNotes(notas.text.toString(), obras?.oid!!.toInt())
                    }
                }
            }
        }

        //Dropdown menu para el estado de la obra
        val spinner = binding.detEstadoDropdown
        val estados = resources.getStringArray(R.array.Estados)
        estados[0] = obras?.estado
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, estados)
        spinner.adapter = adapter
        var itemSelected: String?

        var selection = 0
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long){
                itemSelected = parent!!.getItemAtPosition(position).toString()
                estados[0]= "Elige un estado"
                
                //Si se elige un estado distinto al anterior, se actualiza la BD
                if(++selection > 1){
                    GlobalScope.launch(Dispatchers.IO) {
                        appDB.obraDao().updateStatus(itemSelected!!, obras?.oid!!.toInt())
                    }
                    Toast.makeText(this@DetailedWork, "Se ha actualizado la obra.", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.arrowBack.setOnClickListener {
            val i = Intent(this, SecondActivity::class.java)
            startActivity(i)
        }

        binding.detFotosInput.setOnClickListener {
            val i = Intent(this, Images::class.java)
            i.putExtra("obra", obras)
            startActivity(i)
        }

        binding.detEquipoInput.setOnClickListener {
            val i = Intent(this, ShowTeam::class.java)
            i.putExtra("obra", obras)
            startActivity(i)
        }
    }

   override fun onBackPressed() {
       super.onBackPressed()
       val i = Intent(this, SecondActivity::class.java)
       startActivity(i)
   }


}