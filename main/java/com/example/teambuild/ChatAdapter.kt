package com.example.teambuild

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatAdapter (val context: Context, private val empList: List<Empleado>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){
    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val cname: TextView = view.findViewById(R.id.contact_name)
        val cposition: TextView = view.findViewById(R.id.contact_position)
        val cprofile: ImageView = view.findViewById(R.id.perfil)
    }
    // Con esta funcion se establece la vista que se va a cargar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    // Esta funcion sustituye el contenido de la vista
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.cname.text = empList[position].nombre
        viewHolder.cposition.text = empList[position].cargo

        Glide.with(context).load(empList[position].perfil)
            .placeholder(R.drawable.avatar)
            .into(viewHolder.cprofile)

        viewHolder.itemView.setOnClickListener{
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("nombre", empList[position].nombre)
            intent.putExtra("perfil", empList[position].perfil.toString())
            context.startActivity(intent)
        }
    }

    // Esta funcion devuelve el numero de elementos que componen la lista
    override fun getItemCount() = empList.size
}