package com.example.teambuild

import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AddUsers : Fragment() {

    private lateinit var view: View
    private lateinit var appDB: BaseDatos
    private var emp: Empleado? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Primero se carga la vista del fragmento y la base de datos
        view = inflater.inflate(R.layout.fragment_add_users, container, false)
        appDB = BaseDatos.getDB(requireContext())

        //Se recogen los datos del empleado para actualizarlo
        var bundle = this.arguments
        if(bundle != null){
            emp = bundle?.getParcelable("empleado") as Empleado?
        }

        var boton = view.findViewById<Button>(R.id.add_users_button)

        //Dropdown menu para el puesto de trabajo
        val spinner = view.findViewById<Spinner>(R.id.dropdown_cargo)
        val cargos = resources.getStringArray(R.array.Puestos)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cargos)
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

        //Si existe el empleado, aparecen los datos. Si no, los campos estan vacios
        if(emp == null){
            boton.text = "Crear usuario"
        }else{
            boton.text = "Actualizar usuario"
            view.findViewById<EditText>(R.id.input_nombre).setText(emp?.nombre.toString())
            view.findViewById<EditText>(R.id.input_correo).setText(emp?.email.toString())
        }

        boton.setOnClickListener{
            var nombre = view.findViewById<EditText>(R.id.input_nombre).text.toString()
            var correo = view.findViewById<EditText>(R.id.input_correo).text.toString()
            var cargo = itemSelected
            val empleado = Empleado(null, nombre,correo,null, correo, null, cargo,null, null)

            if(nombre == "" || correo == "" || cargo == ""){
                Toast.makeText(activity, R.string.dataRequired, Toast.LENGTH_SHORT).show()
            }else if(emp == null){
                addEmployee(empleado)
            }else updateEmployee(empleado)
        }
        return view
    }

    // Función para añadir nuevos usuarios
    private fun addEmployee(empleado: Empleado){
            try {
                //Se añade la corrutina para poder llamar a la función suspendida
                GlobalScope.launch(Dispatchers.IO) {
                    appDB.empleadoDao().insertEmployee(empleado)
                }
                //Borrado de los datos en los campos de texto
                view.findViewById<TextView>(R.id.input_nombre).text = ""
                view.findViewById<TextView>(R.id.input_correo).text = ""
                Toast.makeText(context, "Se ha creado un nuevo usuario.", Toast.LENGTH_SHORT).show()

            }catch(e : SQLiteException){
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
            }
    }

    // Función para editar obras
    private fun updateEmployee(e: Empleado){

        e.id = emp?.id ?: 0

        try {
            //Se añade la corrutina para poder llamar a la función suspendida
            GlobalScope.launch(Dispatchers.IO) {
                appDB.empleadoDao().updateEmployee(e)
            }
            Toast.makeText(activity, "Se ha actualizado el empleado.", Toast.LENGTH_SHORT).show()

            //Vuelta a la pantalla de empleados
            val frag = DisplayUsers()
            val fragment = requireActivity().supportFragmentManager.beginTransaction()
            fragment.replace(R.id.fragment_container, frag).addToBackStack(null)
            fragment.commit()

        }catch(e : SQLiteException){
            Toast.makeText(activity, R.string.error, Toast.LENGTH_SHORT).show()
        }
    }



}