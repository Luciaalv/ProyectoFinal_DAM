package com.example.teambuild

import androidx.room.*

@Dao
interface ObraDao {

    @Query("SELECT * FROM obras")
    fun getAllWork(): List<Obra>

    @Delete
    suspend fun deleteWork(obra: Obra)

    @Update
    suspend fun updateWork(obra: Obra)

    @Query("UPDATE obras SET estado = :estado WHERE oid = :id")
    suspend fun updateStatus(estado: String, id: Int)

    @Query("UPDATE obras SET fotos = :fotos WHERE oid = :id")
    suspend fun updateImages(fotos: kotlin.String?, id: Int)

    @Query("SELECT fotos FROM obras WHERE oid = :id")
    suspend fun getImage(id: Int): String

    @Query("UPDATE obras SET comentarios = :notas WHERE oid = :id")
    suspend fun updateNotes(notas: String, id: Int)

    @Query("SELECT * FROM obras WHERE obra = :name AND direccion = :direccion")
    suspend fun getWork(name: String?, direccion: String?): Obra  // Con suspend se ejecutarán las funciones en segundo plano

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWork(obra: Obra)

}