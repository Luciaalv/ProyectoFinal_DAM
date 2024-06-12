package com.example.teambuild

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DisplayUsers : Fragment(){

    private lateinit var view: View
    private lateinit var appDB: BaseDatos
    private lateinit var recyclerView: RecyclerView
    private var newAdapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = inflater.inflate(R.layout.fragment_display_users, container, false)
        appDB = BaseDatos.getDB(requireContext())

        recyclerView = view.findViewById(R.id.list_of_users)
        newAdapter = UserAdapter()

        GlobalScope.launch(Dispatchers.IO) {
            // Se guarda la lista de empleados en una variable
            val empleados = appDB.empleadoDao().getAllEmp()

            recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = newAdapter
                newAdapter?.setData(empleados)

                //Funcion para editar empleados, viene de UserAdapter
                newAdapter?.onClickEditListener {
                    val frag = AddUsers()
                    val bundle = Bundle()
                    bundle.putParcelable("empleado", it)
                    frag.arguments = bundle
                    val fragment = requireActivity().supportFragmentManager.beginTransaction()
                    fragment.replace(R.id.fragment_container, frag).addToBackStack(null)
                    fragment.commit()
                }

                //Funcion para eliminar empleados, viene de UserAdapter
                newAdapter?.onClickDeleteListener {
                        val builder = AlertDialog.Builder(activity)
                        builder.setMessage("¿Seguro que quieres borrar este usuario?")
                        builder.setPositiveButton("Sí"){p0, _ ->
                            //Se añade la corrutina para poder llamar a la función suspendida
                            GlobalScope.launch(Dispatchers.IO) {
                                appDB.empleadoDao().deleteEmployee(it)
                                val newData = appDB.empleadoDao().getAllEmp()
                                newAdapter?.setData(newData)
                            }
                            val secondBuilder = AlertDialog.Builder(activity)
                            secondBuilder.setMessage("Registro eliminado")
                            secondBuilder.setPositiveButton("Vale") { _,_ -> }
                            secondBuilder.create().show()
                            newAdapter?.notifyDataSetChanged()
                            p0.dismiss()
                        }
                        builder.setNegativeButton("No"){p0,_ ->
                            p0.dismiss()
                        }
                        builder.create().show()
                    }

            }
        }

        return view
    }


}
