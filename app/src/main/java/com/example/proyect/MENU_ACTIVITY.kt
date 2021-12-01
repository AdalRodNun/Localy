package com.example.proyect

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class MENU_ACTIVITY : AppCompatActivity(), View.OnClickListener {
    lateinit var recyclerView: RecyclerView
    var serviciosData : ArrayList<Servicio> = ArrayList()
    lateinit var adapter : Servicio_Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        recyclerView = findViewById(R.id.recyclerViewMios)

        leerProductos()

    }

    fun leerProductos(){
        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(FirebaseAuth.getInstance().currentUser?.uid.toString() == documento.data["usuario"].toString()){
                        leerImagenProductos(documento.id, documento.data["nombre"].toString(), documento.data["usuario"].toString(),
                                            documento.data["productos"].toString(), documento.data["informacion servicio"].toString(),
                                            documento.data["latitud"].toString(), documento.data["longitud"].toString())
                    }
                }

            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun leerImagenProductos(nombreImagen : String, nombre : String, usuario : String, productos : String, informacion : String, latitud : String, longitud : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesServicios/$nombreImagen")
        val localfile = File.createTempFile("imagenTemporal", "jpg")

        storageReference.getFile(localfile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                serviciosData.add(Servicio(nombre, bitmap, usuario, productos, informacion, latitud, longitud, nombreImagen))
                Log.d("FIREBASE", "Correctamente cargado")

                adapter = Servicio_Adapter(serviciosData, this)
                var llm = LinearLayoutManager(this)
                llm.orientation = LinearLayoutManager.VERTICAL

                recyclerView.layoutManager = llm
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    override fun onClick(row: View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, SERVICIO_ACTIVITY::class.java)
        intent.putExtra("nombre", serviciosData[position].nombreServicio.toString())
        intent.putExtra("productos", serviciosData[position].productos.toString())
        intent.putExtra("informacion", serviciosData[position].informacion.toString())
        intent.putExtra("latitud", serviciosData[position].latitud.toString())
        intent.putExtra("longitud", serviciosData[position].longitud.toString())
        intent.putExtra("usuario", serviciosData[position].idusuario.toString())
        intent.putExtra("id", serviciosData[position].idservicio.toString())
        startActivity(intent)
    }

    fun agregarServicio(view : View){
        val intent = Intent(this, Agregar_Servicio_Activity::class.java)
        startActivity(intent)
    }
}