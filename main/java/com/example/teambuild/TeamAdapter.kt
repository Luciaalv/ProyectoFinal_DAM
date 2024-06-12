package com.example.teambuild

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TeamAdapter() : RecyclerView.Adapter<TeamAdapter.ViewHolder>(){

    private var teamList = mutableListOf<Empleado>()
    private var onClickDelete:((Empleado) -> Unit)? = null
    private var onClickAdd:((Empleado) -> Unit)? = null

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val tname: TextView = view.findViewById(R.id.user_team_name)
        val tmail: TextView = view.findViewById(R.id.user_team_mail)
        val addt: ImageButton = view.findViewById(R.id.btn_add_team)
        val delt: ImageButton = view.findViewById(R.id.btn_del_team)
    }
    // Con esta funcion se establece la vista que se va a cargar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.operario_item, parent, false)
        return ViewHolder(view)
    }

    // Esta funcion sustituye el contenido de la vista
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tname.text = teamList[position].nombre
        viewHolder.tmail.text = teamList[position].email

        //Se añaden los listeners para cada boton
        viewHolder.addt.setOnClickListener{
            onClickAdd?.invoke(teamList[position])
        }

        viewHolder.delt.setOnClickListener{
            onClickDelete?.invoke(teamList[position])
        }
    }

    // Esta funcion devuelve el numero de elementos que componen la lista
    override fun getItemCount() = teamList.size

    fun onClickAddListener(callback: (Empleado) -> Unit){
        this.onClickAdd = callback
    }
    fun onClickDeleteListener(callback: (Empleado) -> Unit){
        this.onClickDelete = callback
    }

    fun setData(newList: List<Empleado>) {
        teamList.apply{
            clear()
            addAll(newList)
        }
    }
}



