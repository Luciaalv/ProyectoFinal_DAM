package com.example.teambuild

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Context
import android.util.Log
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.ArrayList

class ImagesAdapter (val context: Context, val imgList: ArrayList<String>): RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    private var onClickDelete:((String) -> Unit)? = null

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var imgView: ImageView = view.findViewById(R.id.img)
        var delt: ImageButton = view.findViewById(R.id.delete_i_button)
    }

    // Con esta funcion se establece la vista que se va a cargar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.img_item, parent, false)
        return ViewHolder(view)
    }

    // Esta funcion sustituye el contenido de la vista
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context).load(imgList[position])
            .placeholder(R.drawable.avatar)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(viewHolder.imgView)

        //Se crea el listener para el boton de borrar
        viewHolder.delt.setOnClickListener{
            onClickDelete?.invoke(imgList[position])
        }
    }

    // Esta funcion devuelve el numero de elementos que componen la lista
    override fun getItemCount() = imgList.size

    fun setData(newList: ArrayList<String>) {
        imgList.apply{
            clear()
            addAll(newList)
        }
    }

    fun onClickDeleteListener(callback: (String) -> Unit) {
        this.onClickDelete = callback
    }

}