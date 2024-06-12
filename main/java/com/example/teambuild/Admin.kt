package com.example.teambuild
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Anotación de entidad para la base de datos
@Entity(tableName = "admin")
data class Admin( // Con data class ya se incluyen los getters y setters
    @PrimaryKey(autoGenerate = true) // Generación automática de la clave primaria
    var uid: Int?,
    @ColumnInfo(name="username") var usuario:String?,
    @ColumnInfo(name="password") var password:String?,
    )

