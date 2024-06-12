package com.example.teambuild

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.teambuild.databinding.ActivityProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var appDB: BaseDatos
    private lateinit var session: SessionManager
    private var uri: Uri? = null
    private lateinit var path: String
    private lateinit var file: File
    private lateinit var name: String
    private lateinit var pass: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Se reciben los datos de la sesion
        session = SessionManager(applicationContext)
        var user = session.getUserData()
        name = user.get(SessionManager.KEY_NAME).toString()
        pass = user.get(SessionManager.KEY_PASSWORD).toString()

        //Variable para guardar la conexion con la BD
        appDB = BaseDatos.getDB(this)

        //Variables de cada elemento del formulario
        var update = binding.profileUpdateBtn
        var nombre = binding.profileName
        var email = binding.profileEmail
        var city = binding.profileCiudad
        var telf = binding.profileTelefono
        var pwd = binding.profilePassword
        var profile = binding.perfil


        //Se buscan los datos en la BD y se vinculan a las variables
        GlobalScope.launch(Dispatchers.IO) {
            var emp = appDB.empleadoDao().getEmployee(name!!, pass!!)
            if(emp.ciudad != null && emp.ciudad != ""){
                city.setText(emp.ciudad)
            }else city.hint
            nombre.text = emp.nombre
            email.text = emp.email
            if(emp.telefono != null){
                telf.setText(emp.telefono.toString())
            }else telf.hint
        }

        update.setOnClickListener{
            updateData(city.text.toString(), telf.text.toString(), pwd.text.toString(), session)
        }

        profile.setOnClickListener {
            filePicker()
        }

        binding.arrowBack.setOnClickListener{
            val i = Intent(this, SecondActivity::class.java)
            startActivity(i)
        }

    }


    override fun onResume() {
        super.onResume()

        lifecycleScope.launch(Dispatchers.IO) {
            var e = appDB.empleadoDao().getEmployee(name, pass)
            var f = e.perfil
            runOnUiThread {
                Glide.with(this@ProfileActivity)
                    .load(f)
                    .placeholder(R.drawable.avatar)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.perfil)
            }
        }
    }

    private fun filePicker(){
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        chooseFile.setType("image/*")
        startActivityForResult(chooseFile,100)
    }

    @Deprecated("Obsoleto en Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            uri = data.data!!

            if (uri != null) {
                // Funcion para conseguir la ruta real del archivo en lugar de la ruta del filepicker
                path = getPathFromUri(uri!!)
                file = File(path)
                uri = Uri.fromFile(file)

                //Se establece la nueva foto y se actualiza la base de datos
                lifecycleScope.launch(Dispatchers.IO) {
                    appDB.empleadoDao().updateProfile(uri.toString(), name)
                }
                Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT)
                    .show()

                Glide.with(this@ProfileActivity)
                    .load(uri)
                    .placeholder(R.drawable.avatar)
                    .transform(CircleCrop())
                    .into(binding.perfil)
            }

        }

    }


    private fun updateData(city: String, phone: String, pswd: String, session: SessionManager){
        lifecycleScope.launch(Dispatchers.IO) {
            var emp = appDB.empleadoDao().getEmployee(name, pass)
            emp.ciudad = city

            if(phone == null || phone == ""){
                emp.telefono = null
            }else emp.telefono = phone.toInt()

            if(emp.password != pswd && pswd != ""){
                emp.password = pswd
                session.editSession(pswd)
            }
            appDB.empleadoDao().updateEmployee(emp)
        }
        Toast.makeText(this, R.string.data_updated, Toast.LENGTH_SHORT).show()
    }

    private fun getPathFromUri(myUri: Uri): String {
        var res = ""
        val imgs = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = this.contentResolver?.query(myUri, imgs, null, null, null)
        if(cursor!!.moveToFirst()){
            val colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(colIndex)
        }
        cursor.close()
        return res
    }

}