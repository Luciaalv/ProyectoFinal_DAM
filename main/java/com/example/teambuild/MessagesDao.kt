package com.example.teambuild

import androidx.room.*

@Dao
interface MessagesDao {

    @Query("SELECT * FROM mensajes")
    fun getAllMessages(): List<Messages>

    @Delete
    suspend fun deleteMsgs(msj: Messages)

    @Query("SELECT * FROM mensajes WHERE emisor = :sender AND receptor = :receiver OR emisor = :receiver AND receptor = :sender")
    suspend fun getMsg(sender: String, receiver: String): MutableList<Messages>  // Con suspend se ejecutarán las funciones en segundo plano

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMsg(msg: Messages)

    @Query("SELECT EXISTS (SELECT 1 FROM mensajes WHERE emisor = :sender AND receptor = :receiver)")
    fun msgExists(sender: String, receiver: String): Boolean

}