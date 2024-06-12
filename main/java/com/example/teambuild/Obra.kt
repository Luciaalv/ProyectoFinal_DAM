package com.example.teambuild

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName="obras")
data class Obra (
    @PrimaryKey(autoGenerate = true) // Generación automática de la clave primaria
    var oid: Int?,
    @ColumnInfo(name="obra") var nombre: String?,
    @ColumnInfo(name="encargado") var encargado:String?,
    @ColumnInfo(name="direccion") var direccion:String?,
    @ColumnInfo(name="equipo") var equipo: ArrayList<Empleado>,
    @ColumnInfo(name="estado") var estado: String?,
    @ColumnInfo(name="fotos") var fotos: String?,
    @ColumnInfo(name="comentarios") var comentarios: String?,
    @ColumnInfo(name="fecha") var fecha: String?,
    @ColumnInfo(name="archivos") var archivos: String?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
      /*  parcel.readParcelable(Empleado::class.java.classLoader), PARA TIPO EMPLEADO EN LUGAR DE STRING */
        parcel.readString(),
        parcel.readString(),
        arrayListOf<Empleado>().apply {
            parcel.readList(this, Empleado::class.java.classLoader)},
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    class ObjectConverter{
        @TypeConverter
        fun listToString(list: ArrayList<Empleado>): String {
            return Gson().toJson(list)        }
        @TypeConverter
        fun stringToList(stringList: String): ArrayList<Empleado> {
            val listType = object :
                TypeToken<List<Empleado?>?>() {}.type
            return Gson().fromJson(stringList, listType)
        }

        /* PARA TIPO EMPLEADO EN LUGAR DE STRING
        @TypeConverter
        fun objectToString(emp: Empleado): String {
            return Gson().toJson(emp)
        }
        @TypeConverter
        fun stringToObject(string: String) : Empleado{
            return Gson().fromJson(string, Empleado::class.java)
        }
        */
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(oid)
        parcel.writeString(nombre)
       /* parcel.writeParcelable(encargado, flags) PARA TIPO EMPLEADO EN LUGAR DE STRING */
        parcel.writeString(encargado)
        parcel.writeString(direccion)
        parcel.writeList(equipo)
        parcel.writeString(estado)
        parcel.writeString(fotos)
        parcel.writeString(comentarios)
        parcel.writeString(fecha)
        parcel.writeString(archivos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Obra> {
        override fun createFromParcel(parcel: Parcel): Obra {
            return Obra(parcel)
        }

        override fun newArray(size: Int): Array<Obra?> {
            return arrayOfNulls(size)
        }
    }
}