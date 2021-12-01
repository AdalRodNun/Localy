package com.example.proyect

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class HOME_ACTIVITY : AppCompatActivity(), View.OnClickListener {
    lateinit var recyclerView: RecyclerView
    var serviciosData : ArrayList<Servicio> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerViewServicios)

        leerProductos()
    }

    fun reloadProductos(view: View){
        serviciosData.clear()
        leerProductos()
    }

    fun leerProductos(){
        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {

                    leerImagenProductos(documento.id, documento.data["nombre"].toString(), documento.data["usuario"].toString(),
                    documento.data["productos"].toString(), documento.data["informacion servicio"].toString(),
                    documento.data["latitud"].toString(), documento.data["longitud"].toString())
                }
                //iniciarRecycler()
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

                val adapter = Servicio_Adapter(serviciosData, this)
                var llm = LinearLayoutManager(this)
                llm.orientation = LinearLayoutManager.VERTICAL

                recyclerView.layoutManager = llm
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun iniciarRecycler(){

    }

    fun gotoMenu(view: View){
        val intent = Intent(this, MENU_ACTIVITY::class.java)
        startActivity(intent)
    }

    fun buscar(view: View){
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(row : View) {
        val position = recyclerView.getChildLayoutPosition(row)
        val intent = Intent(this, SERVICIO_ACTIVITY::class.java)
        intent.putExtra("nombre", serviciosData[position].nombreServicio.toString())
        intent.putExtra("productos", serviciosData[position].productos.toString())
        intent.putExtra("informacion", serviciosData[position].informacion.toString())
        intent.putExtra("latitud", serviciosData[position].latitud.toString())
        intent.putExtra("longitud", serviciosData[position].longitud.toString())
        //intent.putExtra("usuario", serviciosData[position].idusuario.toString())
        startActivity(intent)
    }
}