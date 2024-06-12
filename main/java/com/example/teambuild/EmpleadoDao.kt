package com.example.teambuild

import androidx.room.*

@Dao
interface EmpleadoDao {

    @Query("SELECT * FROM empleados")
    fun getAllEmp(): List<Empleado>

    @Update
    suspend fun updateEmployee(usuario: Empleado)

    @Delete
    suspend fun deleteEmployee(usuario: Empleado)

    @Query("SELECT * FROM empleados WHERE usuario = :name AND password = :pass")
    suspend fun getEmployee(name: String, pass: String): Empleado  // Con suspend se ejecutarán las funciones en segundo plano

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEmployee(usuario: Empleado)

    @Query("UPDATE empleados SET perfil = :perfil WHERE usuario = :name")
    suspend fun updateProfile(perfil: String, name: String)



    @Query("SELECT EXISTS (SELECT 1 FROM empleados WHERE usuario = :name AND password = :pass)")
    suspend fun userExists(name: String, pass: String): Boolean

}