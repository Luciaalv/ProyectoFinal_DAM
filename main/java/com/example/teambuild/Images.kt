package com.example.teambuild

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.teambuild.databinding.ActivityDetailedWorkBinding
import com.example.teambuild.databinding.ActivityImagesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

import java.util.Date
import kotlin.collections.ArrayList

class Images : AppCompatActivity() {

    private lateinit var binding: ActivityImagesBinding
    private lateinit var appDB: BaseDatos
    private var uri: Uri? = null
    private lateinit var path: String
    private lateinit var file: File
    private lateinit var obra: Obra

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





    }

    override fun onResume() {
        super.onResume()

        //Vinculación de la actividad Chat con su hoja de layout
        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Variable para guardar la conexion con la BD
        appDB = BaseDatos.getDB(this)

        //Se reciben los datos de la obra que se va a actualizar
        obra = intent.getParcelableExtra<Obra>("obra")!!

        var arr: ArrayList<String> = ArrayList()

        GlobalScope.launch(Dispatchers.IO) {
            var i = appDB.obraDao().getImage(obra!!.oid!!.toInt())
            if(i != "" && i != null){
                arr.add(i)
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.list_of_images)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var adapter = ImagesAdapter(this@Images,arr)
        recyclerView.adapter = adapter

        binding.arrowBack.setOnClickListener {
            finish()
        }

        binding.addImage.setOnClickListener {
            filePicker()
            adapter.notifyDataSetChanged()
        }


        //Funcion para eliminar obras, viene de WorkAdapter
        adapter.onClickDeleteListener {
            val builder = AlertDialog.Builder(this@Images)
            builder.setMessage("¿Seguro que quieres borrar esta imagen?")
            builder.setPositiveButton("Sí"){p0, _ ->
                //Se añade la corrutina para poder llamar a la función suspendida
                GlobalScope.launch(Dispatchers.IO) {
                    arr.remove(it)
                    appDB.obraDao().updateImages("", obra.oid!!.toInt())
                }
                val secondBuilder = AlertDialog.Builder(this@Images)
                secondBuilder.setMessage("Registro eliminado")
                secondBuilder.setPositiveButton("Vale") { _,_ -> }
                secondBuilder.create().show()
                adapter.notifyDataSetChanged()
                p0.dismiss()
            }
            builder.setNegativeButton("No"){p0,_ ->
                p0.dismiss()
            }
            builder.create().show()
        }

    }

    //Funcion que abre la bandeja de imagenes del dispositivo
    private fun filePicker(){
        var chooseFile = Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("image/*");
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(chooseFile, "Elige una imagen."),100);
    }

    @Deprecated("Obsoleto en Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 100 && resultCode == RESULT_OK && data != null){
            uri = data.data!!

            if (uri != null) {
                // Funcion para conseguir la ruta real del archivo en lugar de la ruta del filepicker
                path = getPathFromUri(uri!!)
                file = File(path)
                uri = Uri.fromFile(file)

                //Se establece la nueva foto y se actualiza la base de datos
                lifecycleScope.launch(Dispatchers.IO) {
                    appDB.obraDao().updateImages(uri.toString(), obra!!.oid!!.toInt())
                }
                val builder = AlertDialog.Builder(this@Images)
                builder.setMessage("Imagen agregada.")
                builder.setPositiveButton("Vale") { _,_ -> }
                builder.create().show()

            }

        }
        super.onActivityResult(requestCode, resultCode, data)

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