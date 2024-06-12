package com.example.teambuild

import android.content.Context
import androidx.room.*

@Database(
    entities=[Admin::class, Empleado::class, Obra::class, Messages::class],  //Clases que formarán parte de la base de datos
    version = 1,
    exportSchema = false
)

@TypeConverters(Obra.ObjectConverter::class)

abstract class BaseDatos:RoomDatabase() {

    abstract fun adminDao():AdminDao
    abstract fun empleadoDao():EmpleadoDao
    abstract fun obraDao():ObraDao
    abstract fun mensajesDao():MessagesDao

    companion object {
        @Volatile
        private var INSTANCE: BaseDatos? = null

        fun getDB(context: Context): BaseDatos{
           val tempInstance = INSTANCE
            if (tempInstance != null){ //Si existe la instancia de la bd, se recupera
                return tempInstance
            }
            synchronized(this){ //Si no existe la instancia, se crea
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatos::class.java, "base_datos"
                ).build()
                INSTANCE = db
                return db

            }
        }

    }
}