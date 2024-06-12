package com.example.teambuild

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.room.Delete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class AddWork : Fragment() {

    private lateinit var view: View
    private lateinit var appDB: BaseDatos
    private var obra: Obra? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Primero se carga la vista del fragmento y la base de datos
        view = inflater.inflate(R.layout.fragment_add_work, container, false)
        appDB = BaseDatos.getDB(requireContext())


        //Se recogen los datos de la obra para actualizarla
        var bundle = this.arguments
        if(bundle != null){
            obra = bundle?.getParcelable("obra") as Obra?
        }

        var boton = view.findViewById<Button>(R.id.add_work_button)

        //Dropdown menu para elegir al encargado
        val spinner = view.findViewById<Spinner>(R.id.dropdown_encargado)
        val arrJefes: MutableList<String> = ArrayList()
        arrJefes.add(0, "Elige un encargado")
        try {
            //Se añade la corrutina para poder llamar a la función suspendida
            GlobalScope.launch(Dispatchers.IO) {
                var eList = appDB.empleadoDao().getAllEmp()
                eList.forEach { e ->
                    if(e.cargo == "Jefe de obra"){
                        arrJefes.add(e.nombre!!)
                    }
                }
            }
        }catch(e : SQLiteException){
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
        }
        //Adaptador para el dropdrown menu
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, arrJefes)
        spinner.adapter = adapter

        var itemSelected: String? = null

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long){
                itemSelected = parent!!.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Datos del calendario
        val datePicker = view.findViewById<DatePicker>(R.id.date_picker)
        val today = Calendar.getInstance()
        var fecha = today.get(Calendar.DAY_OF_MONTH).toString() + "/" + today.get(Calendar.MONTH).toString() + "/" + today.get(Calendar.YEAR).toString()
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ) { view, year, month, day ->
            val month = month + 1
            fecha = "$day/$month/$year"
        }

        //Si existe la obra, aparecen los datos. Si no, los campos estan vacios
        if(obra == null){
            boton.text = "Crear obra"
        }else{
            boton.text = "Actualizar obra"
            view.findViewById<EditText>(R.id.input_nombre_obra).setText(obra?.nombre.toString())
            view.findViewById<EditText>(R.id.input_direccion).setText(obra?.direccion.toString())
        }


        //Listener para el boton de aceptar
        boton.setOnClickListener{
            //Vinculacion de los campos de texto que se enviaran al pulsar aceptar
            var nombre = view.findViewById<EditText>(R.id.input_nombre_obra).text.toString()
            var direccion = view.findViewById<EditText>(R.id.input_direccion).text.toString()
            var encargado = itemSelected
            var mifecha = fecha
            if(nombre == "" || direccion == "" || encargado == arrJefes[0]){
                Toast.makeText(activity, R.string.dataRequired, Toast.LENGTH_SHORT).show()
            }else if(obra == null){
                var new_obra = Obra(null, nombre, encargado, direccion, ArrayList<Empleado>(), "Nuevo", null,null, mifecha, null)
                addWork(new_obra)
            }else {
                var upd_obra = Obra(null, nombre, encargado, direccion, ArrayList<Empleado>(), "Actualizado", null,null, mifecha, null)
                updateWork(upd_obra)
            }

        }
        return view
    }

    // Función para añadir nuevas obras
    private fun addWork(obra: Obra){
        try {
            //Se añade la corrutina para poder llamar a la función suspendida
            GlobalScope.launch(Dispatchers.IO) {
                    appDB.obraDao().insertWork(obra)
            }
            Toast.makeText(activity, "Se ha creado una nueva obra.", Toast.LENGTH_SHORT).show()

            //Vuelta a la pantalla de obras
            val i = Intent(requireContext(), SecondActivity::class.java)
            startActivity(i)

        }catch(e : SQLiteException){
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }
    // Función para editar obras
    private fun updateWork(o: Obra){

        o.oid = obra?.oid ?: 0

        try {
            //Se añade la corrutina para poder llamar a la función suspendida
            GlobalScope.launch(Dispatchers.IO) {
                appDB.obraDao().updateWork(o)
            }
            Toast.makeText(activity, "Se ha actualizado la obra.", Toast.LENGTH_SHORT).show()

            //Vuelta a la pantalla de obras
            val i = Intent(requireContext(), SecondActivity::class.java)
            startActivity(i)

        }catch(e : SQLiteException){
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }

}