package com.example.teambuild


import androidx.room.*

@Dao
interface AdminDao {

    @Query("SELECT * FROM admin")
    fun getAllAdmins(): List<Admin>

    @Delete
    suspend fun deleteAdmin(usuario: Admin)

    @Query("SELECT * FROM admin WHERE username = :name AND password = :pass")
    suspend fun getUser(name: String, pass: String): Admin  // Con suspend se ejecutarán las funciones en segundo plano

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(usuario: Admin)

    // @Insert
   // suspend fun insertAdmin(vararg admin: Admin)
}