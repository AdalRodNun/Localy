package com.example.proyect

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MIS_SERVICIOS_ACTIVITY : AppCompatActivity(), View.OnClickListener {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : Servicio_Adapter
    lateinit var misServiciosIDs : ArrayList<String>
    var serviciosData : ArrayList<Servicio> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_servicios)

        recyclerView = findViewById(R.id.recyclerViewMios)

        leerProductos()
    }

    @Override
    override fun onResume() {
        super.onResume()
        leerProductos()
    }

    fun leerProductos(){
        serviciosData.clear()
        leerIDs()
        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(misServiciosIDs.contains(documento.id)){
                        leerImagenProductos(documento.id, documento.data["nombre"].toString())
                    }
                }

            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun leerIDs(){
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == idUsuario) {
                        misServiciosIDs = documento.data["mis servicios"] as ArrayList<String>
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE Menu", "Error al leer usuario: ${it.message}")
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
    }

    override fun onClick(row: View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, SERVICIO_ACTIVITY::class.java)
        intent.putExtra("id", serviciosData[position].idServicio)
        //intent.putExtra("usuario", serviciosData[position].idusuario.toString())
        startActivity(intent)
    }

    fun agregarServicio(view : View){
        val intent = Intent(this, Agregar_Servicio_Activity::class.java)
        startActivity(intent)
    }
}