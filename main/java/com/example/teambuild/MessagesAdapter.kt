package com.example.teambuild

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teambuild.databinding.LayoutReceiveBinding
import com.example.teambuild.databinding.LayoutSendBinding

class MessagesAdapter (val context: Context, messageList: List<Messages>?) : RecyclerView.Adapter<RecyclerView.ViewHolder?>(){

    lateinit var messageList : List<Messages>
    private lateinit var session: SessionManager
    val type_sent = 1
    val type_received = 2


    // Con esta funcion se establece la vista que se va a cargar, y en que lugar aparece cada mensaje
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return if (viewType == type_sent){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_send, parent, false)
            SentHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_receive, parent, false)
            ReceivedHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages = messageList[position]
        session = SessionManager(context)
        var user = session.getUserData()
        var name = user.get(SessionManager.KEY_NAME)
        var pass = user.get(SessionManager.KEY_PASSWORD)

        //Se establece si los mensajes son enviados o recibidos segun el usuario que haya iniciado sesion
        if(name == "admin" && pass == "1234"){
            return type_sent
        }else{
            if(messages.emisor.toString() == name){
                return type_sent
            }else return type_received
        }

    }

    // Esta funcion sustituye el contenido de la vista
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        if(viewHolder.itemViewType == type_sent) {
            val message = messageList[position]
            val holder = viewHolder as SentHolder
            holder.binding.mensaje.text = message.msg
        }else{
            val holder = viewHolder as ReceivedHolder
            holder.binding.mensaje.text = message.msg
        }
    }

    // Esta funcion devuelve el numero de elementos que componen la lista
    override fun getItemCount() = messageList.size


    inner class SentHolder(view: View): RecyclerView.ViewHolder(view){
        val binding: LayoutSendBinding = LayoutSendBinding.bind(view)
    }
    inner class ReceivedHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: LayoutReceiveBinding = LayoutReceiveBinding.bind(view)
    }

    init{
        if(messageList != null){
            this.messageList = messageList
        }
    }


}