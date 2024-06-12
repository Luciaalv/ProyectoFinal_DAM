package com.example.teambuild

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Con esta clase se conectan el RecyclerView y los datos de la bd y se especifica donde van a aparecer
class WorkAdapter() : RecyclerView.Adapter<WorkAdapter.ViewHolder>(){

    private var workList = mutableListOf<Obra>()
    var onItemClick : ((Obra) -> Unit)? = null
    private var onClickDelete:((Obra) -> Unit)? = null
    private var onClickEdit:((Obra) -> Unit)? = null
    var desactivar: Boolean = false

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val nombreObra: TextView = view.findViewById(R.id.work_name)
        val direcObra: TextView = view.findViewById(R.id.work_address)
        val fechaObra: TextView = view.findViewById(R.id.obra_fecha)
        val estadoObra: TextView = view.findViewById(R.id.obra_estado)
        val borrarObra: ImageView = view.findViewById(R.id.delete_w_button)
        val editarObra: ImageView = view.findViewById(R.id.edit_w_button)
    }
    // Con esta funcion se establece la vista que se va a cargar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.work_item, parent, false)
        return ViewHolder(view)
    }

    // Esta funcion sustituye el contenido de la vista
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.nombreObra.text = workList[position].nombre
        viewHolder.direcObra.text = workList[position].direccion
        viewHolder.fechaObra.text = workList[position].fecha
        viewHolder.estadoObra.text = workList[position].estado

        if(desactivar){
            viewHolder.editarObra.visibility = View.GONE
            viewHolder.borrarObra.visibility = View.GONE
        }

        viewHolder.itemView.setOnClickListener{
            onItemClick?.invoke(workList[position])
        }

        viewHolder.borrarObra.setOnClickListener{
            onClickDelete?.invoke(workList[position])
        }

        viewHolder.editarObra.setOnClickListener{
            onClickEdit?.invoke(workList[position])
        }
    }

    // Esta funcion devuelve el numero de elementos que componen la lista
    override fun getItemCount() = workList.size


    fun onClickDeleteListener(callback: (Obra) -> Unit) {
        this.onClickDelete = callback
    }

    fun onClickEditListener(callback: (Obra) -> Unit) {
        this.onClickEdit = callback
    }

    fun setData(newList: List<Obra>) {
        workList.apply{
            clear()
            addAll(newList)
        }
    }
}