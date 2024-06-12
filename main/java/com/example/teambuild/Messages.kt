package com.example.teambuild

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Anotación de entidad para la base de datos
@Entity(tableName = "mensajes")
data class Messages (
    @PrimaryKey(autoGenerate = true) var mid: Int? = null,
    @ColumnInfo(name="mensaje") var msg:String? = null,
    @ColumnInfo(name="emisor") var emisor:String? = null,
    @ColumnInfo(name="receptor") var receptor:String? = null,
    @ColumnInfo(name="hora") var time:Long? = 0,
)