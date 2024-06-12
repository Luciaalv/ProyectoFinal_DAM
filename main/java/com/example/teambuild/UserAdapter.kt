package com.example.teambuild

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Collections.addAll

//Con esta clase se conectan el RecyclerView y los datos de la bd y se especifica donde van a aparecer
class UserAdapter() : RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private var empList = mutableListOf<Empleado>()
    private var onClickEdit:((Empleado) -> Unit)? = null
    private var onClickDelete:((Empleado) -> Unit)? = null

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.contact_name)
        val correo: TextView = view.findViewById(R.id.user_email)
        val puesto: TextView = view.findViewById(R.id.user_cargo)
        val editar: ImageButton = view.findViewById(R.id.edit_e_button)
        val borrar: ImageButton = view.findViewById(R.id.delete_e_button)
    }
    // Con esta funcion se establece la vista que se va a cargar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    // Esta funcion sustituye el contenido de la vista
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        var posicion = empList[position]
        viewHolder.textView.text = posicion.nombre
        viewHolder.correo.text = posicion.email
        viewHolder.puesto.text = posicion.cargo

        viewHolder.editar.setOnClickListener {
            onClickEdit?.invoke(posicion)
        }
        viewHolder.borrar.setOnClickListener {
            onClickDelete?.invoke(posicion)
        }
    }

    // Esta funcion devuelve el numero de elementos que componen la lista
    override fun getItemCount() = empList.size

    fun onClickEditListener(callback: (Empleado) -> Unit) {
        this.onClickEdit = callback
    }
    fun onClickDeleteListener(callback: (Empleado) -> Unit) {
        this.onClickDelete = callback
    }

    fun setData(newList: List<Empleado>) {
        empList.apply{
            clear()
            addAll(newList)
        }
    }



}


