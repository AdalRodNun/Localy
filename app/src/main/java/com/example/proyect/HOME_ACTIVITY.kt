package com.example.proyect

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class HOME_ACTIVITY : AppCompatActivity(), View.OnClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : Servicio_Adapter
    lateinit var botonMenu : AppCompatImageButton
    var serviciosData : ArrayList<Servicio> = ArrayList()
    var bandera : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        botonMenu = findViewById(R.id.opciones_button)
        recyclerView = findViewById(R.id.recyclerViewServicios)

        leerProductos()
    }

    @Override
    override fun onResume() {
        super.onResume()
        if (bandera) {
            leerProductos()
        }
    }

    fun leerProductos(){
        serviciosData.clear()
        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    leerImagenProductos(documento.id, documento.data["nombre"].toString())
                }
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun leerImagenProductos(nombreImagen : String, nombreServicio : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesServicios/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                serviciosData.add(Servicio(nombreServicio, nombreImagen, bitmap))
                Log.d("FIREBASE", "Correctamente cargado")

                iniciarRecycler()
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun iniciarRecycler(){
        adapter = Servicio_Adapter(serviciosData, this)
        var llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        if(!bandera){
            bandera = true
        }
    }

    fun buscar(view: View){

    }

    fun popupMenu (view: View){
        val popupMenu = PopupMenu(this, botonMenu)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            val idActividad = item.title.toString()

            when (idActividad) {
                "Mis servicios" -> {
                    val intent = Intent(this, MIS_SERVICIOS_ACTIVITY::class.java)
                    startActivity(intent)
                }
                "Servicios guardados" -> {
                    val intent = Intent(this, SERVICIOS_GUARDADOS_ACTIVITY::class.java)
                    startActivity(intent)
                }
                "Cerrar sesión" -> {
                    Firebase.auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        popupMenu.show()
    }

    override fun onClick(row : View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, SERVICIO_ACTIVITY::class.java)
        intent.putExtra("id", serviciosData[position].idServicio)
        //intent.putExtra("usuario", serviciosData[position].idusuario.toString())
        startActivity(intent)
    }
}