package com.example.teambuild

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.teambuild.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appDB: BaseDatos
    private  lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Vinculación de la actividad MainActivity con su hoja de layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appDB = BaseDatos.getDB(this)

        //Si la sesion esta guardada, se accedera directamente
        //De lo contrario, habra que iniciar sesion manualmente
        session = SessionManager(applicationContext)
        if(session.loggedIn()){
            val i = Intent(applicationContext, SecondActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        //Listener para el botón de login
        binding.login.setOnClickListener{
            login()
        }
    }

    //Función que permitirá o denegará el inicio de sesión
    private fun login(){
        //Se guardan en variables los datos de los dos campos de texto
        val username = binding.usernameInput.text.toString()
        val passwd = binding.passwordInput.text.toString()

        if(username == "" || passwd == "") {
            Toast.makeText(this, R.string.dataRequired, Toast.LENGTH_SHORT).show()
        }else{
            try {
                //Se añade la corrutina para poder llamar a las funciones suspendidas
                GlobalScope.launch(Dispatchers.IO) {
                    var checkUser = appDB.empleadoDao().userExists(username, passwd)

                    if(username == "admin" && passwd == "1234"){
                        // Se otorga acceso a la aplicación
                        session.createSession(username, passwd)
                        val mainPage = Intent(this@MainActivity, SecondActivity::class.java)
                        startActivity(mainPage)

                    }else if(checkUser){
                            session.createSession(username, passwd)
                            val mainPage = Intent(this@MainActivity, SecondActivity::class.java)
                            startActivity(mainPage)

                    }else{
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity, R.string.user_not_found, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            //Borrado de los datos en los campos de texto
            binding.usernameInput.text.clear()
            binding.passwordInput.text.clear()

            }catch(e : SQLiteException){ //Manejo de posible error al iniciar sesion
                Toast.makeText(this@MainActivity, R.string.user_not_found, Toast.LENGTH_SHORT).show()
            }
        }
    }


}