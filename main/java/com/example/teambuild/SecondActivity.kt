package com.example.teambuild

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teambuild.databinding.ActivitySecondBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var navView : NavigationView
    private lateinit var appDB: BaseDatos
    private lateinit var session: SessionManager
    private lateinit var recyclerView: RecyclerView
    private var myAdapter: WorkAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Vinculación de la clase SecondActivity con su hoja de layout
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Se aplica el contexto a la sesion
        session = SessionManager(applicationContext)
        var user = session.getUserData()
        var name = user.get(SessionManager.KEY_NAME)
        var pass = user.get(SessionManager.KEY_PASSWORD)

        //Variable para guardar la conexion con la BD
        appDB = BaseDatos.getDB(this)

        //RecyclerView para mostrar las obras de la BD
        recyclerView = findViewById<RecyclerView>(R.id.list_of_works)
        recyclerView.layoutManager = LinearLayoutManager(this)
        myAdapter = WorkAdapter()


        // Vinculación de la variable del layout de navegación con el propio layout establecido en el xml
        drawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        // Listener para abrir/cerrar la barra de navegación
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Opción para que aparezca siempre el icono de la barra de navegacion
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Asociamos la variable a la vista de navegacion
        navView = findViewById(R.id.navView)

        //Variables del menu lateral de navegacion
        val obra = navView.menu.findItem(R.id.add_wk_menu)
        val create_user = navView.menu.findItem(R.id.add_users_menu)
        val edit_user = navView.menu.findItem(R.id.edit_users_menu)
        val perfil_user = navView.menu.findItem(R.id.settings)
        val chat_menu = navView.menu.findItem(R.id.chat)

        //Si el usuario es admin, obtiene acceso a las secciones de crear y editar usuarios
        if(name == "admin" && pass == "1234"){
            create_user.setVisible(true)
            edit_user.setVisible(true)
            perfil_user.setVisible(false)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val emp = appDB.empleadoDao().getEmployee(name!!, pass!!)
            //Si el usuario es admin o un jefe, obtiene acceso a la seccion de crear obras
            if(name == "admin" && pass == "1234"){
                obra.setVisible(true)
                chat_menu.setVisible(false)
            }
            if(emp != null){
                if(emp.cargo == "Jefe de obra"){
                    obra.setVisible(true)
                }
                //Si el usuario no es jefe de obra o admin, se ocultan los botones de editar y eliminar
                if(emp.cargo != "Jefe de obra"){
                    myAdapter?.desactivar = true
                }
            }

        }
        navView.setNavigationItemSelectedListener {
           when (it.itemId) {
               R.id.home -> {
                   val i = Intent(this, SecondActivity::class.java)
                   startActivity(i)
               }
               R.id.settings -> {
                   val i = Intent(this, ProfileActivity::class.java)
                   startActivity(i)
               }
               R.id.add_wk_menu -> {
                   changeFragment(AddWork(), it.title.toString())
               }
               R.id.add_users_menu -> {
                   changeFragment(AddUsers(), it.title.toString())
               }
               R.id.edit_users_menu -> {
                    changeFragment(DisplayUsers(), it.title.toString())
               }
               R.id.chat -> {
                   val i = Intent(this, ChatListActivity::class.java)
                   startActivity(i)
               }
               R.id.logout -> {
                    session.logOut()
                    finish()
               }
           }
            true
        }

    }

    // Método para cerrar o abrir el menú de navegación
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        //Se aplica el contexto a la sesion
        session = SessionManager(applicationContext)
        var user = session.getUserData()
        var name = user.get(SessionManager.KEY_NAME)
        var pass = user.get(SessionManager.KEY_PASSWORD)

        lifecycleScope.launch(Dispatchers.IO) {
            recyclerView.apply {
                // Se guarda la lista de obras en una variable
                var obras = appDB.obraDao().getAllWork()
                var misObras = ArrayList<Obra>()
                var emp = appDB.empleadoDao().getEmployee(name!!, pass!!)
                //Si el usuario no es jefe ni admin, solo puede ver sus obras
                if(emp != null && emp.cargo != "Jefe de obra"){
                    obras.forEach{
                        for(e in it.equipo){
                            if(e.id == emp.id){
                                Log.e("contiene", "si")
                                misObras.add(it)
                            }
                        }
                    }
                    recyclerView.adapter = myAdapter
                    myAdapter?.setData(misObras)
                }else{
                    recyclerView.adapter = myAdapter
                    myAdapter?.setData(obras)
                }


                //Funcion para ver cada obra en detalle
                myAdapter?.onItemClick = {
                    val intent = Intent(context, DetailedWork::class.java)
                    intent.putExtra("obras", it)
                    startActivity(intent)
                }

                //Funcion para editar obras, viene de WorkAdapter
                myAdapter?.onClickEditListener {
                    val frag = AddWork()
                    val bundle = Bundle()
                    bundle.putParcelable("obra", it)
                    frag.arguments = bundle
                    val fragment = supportFragmentManager.beginTransaction()
                    fragment.replace(R.id.fragment_container, frag).addToBackStack(null)
                    fragment.commit()
                }

                //Funcion para eliminar obras, viene de WorkAdapter
                myAdapter?.onClickDeleteListener {
                    val builder = AlertDialog.Builder(this@SecondActivity)
                    builder.setMessage("¿Seguro que quieres borrar esta obra?")
                    builder.setPositiveButton("Sí"){p0, _ ->
                        //Se añade la corrutina para poder llamar a la función suspendida
                        GlobalScope.launch(Dispatchers.IO) {
                            appDB.obraDao().deleteWork(it)
                            val newData = appDB.obraDao().getAllWork()
                            myAdapter?.setData(newData)
                        }
                        val secondBuilder = AlertDialog.Builder(this@SecondActivity)
                        secondBuilder.setMessage("Registro eliminado")
                        secondBuilder.setPositiveButton("Vale") { _,_ -> }
                        secondBuilder.create().show()
                        myAdapter?.notifyDataSetChanged()
                        p0.dismiss()
                    }
                    builder.setNegativeButton("No"){p0,_ ->
                        p0.dismiss()
                    }
                    builder.create().show()
                }

            }
        }
    }

    private fun changeFragment(frag : Fragment, titulo : String){
        val fragment = supportFragmentManager.beginTransaction()
        fragment.replace(R.id.fragment_container, frag).addToBackStack(null)
        fragment.commit()
        drawerLayout.closeDrawer(GravityCompat.START)
        title = titulo
    }
}