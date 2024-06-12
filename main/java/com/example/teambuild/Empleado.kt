package com.example.teambuild
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "empleados")
data class Empleado( // Con data class ya se incluyen los getters y setters
    @PrimaryKey(autoGenerate = true) // Generación automática de la clave primaria
    var id: Int?,
    @ColumnInfo(name="usuario") var nombre: String?,
    @ColumnInfo(name="password") var password:String?,
    @ColumnInfo(name="telefono") var telefono:Int?,
    @ColumnInfo(name="email") var email:String?,
    @ColumnInfo(name="ciudad") var ciudad:String?,
    @ColumnInfo(name="cargo") var cargo:String?,
    @ColumnInfo(name="equipo") var equipo:String?,
    @ColumnInfo(name="perfil") var perfil:String?

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(nombre)
        parcel.writeString(password)
        parcel.writeValue(telefono)
        parcel.writeString(email)
        parcel.writeString(ciudad)
        parcel.writeString(cargo)
        parcel.writeString(equipo)
        parcel.writeString(perfil)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Empleado> {
        override fun createFromParcel(parcel: Parcel): Empleado {
            return Empleado(parcel)
        }

        override fun newArray(size: Int): Array<Empleado?> {
            return arrayOfNulls(size)
        }
    }
}