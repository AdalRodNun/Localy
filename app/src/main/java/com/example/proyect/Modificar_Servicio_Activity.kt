package com.example.proyect

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class Modificar_Servicio_Activity : AppCompatActivity() {
    lateinit var nombre : TextView
    lateinit var informacion : TextView
    lateinit var latitud : String
    lateinit var longitud : String
    lateinit var idServicio : String
    var productosData : ArrayList<Producto> = ArrayList()

    val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data

            latitud = data?.getStringExtra("latitud").toString()
            longitud = data?.getStringExtra("longitud").toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_servicio)

        nombre = findViewById(R.id.modificarNombreServicio)
        informacion = findViewById(R.id.modificarInformacionServicio)

        nombre.text = intent.getStringExtra("nombre")
        informacion.text = intent.getStringExtra("informacion")
        latitud = intent.getStringExtra("latitud")!!
        longitud = intent.getStringExtra("longitud")!!
        idServicio = intent.getStringExtra("idServicio")!!
    }

    fun modificarProductos(view : View){
        val intent = Intent(this, Modificar_Productos_Activity::class.java)
        intent.putExtra("idServicio", idServicio)
        startActivity(intent)
    }

    fun checkServicio(view : View) {
        if(nombre.text.toString().isEmpty() || informacion.text.toString().isEmpty()){
            Toast.makeText(this, "Falta agregar uno o mas campos", Toast.LENGTH_SHORT).show();
            return
        } else {
            modificarDatos()
        }
    }

    fun modificarDatos() {
        Firebase.firestore.collection("servicios").document(idServicio).update("nombre", nombre.text.toString())
        Firebase.firestore.collection("servicios").document(idServicio).update("informacion servicio", informacion.text.toString())
        //Firebase.firestore.collection("servicios").document(idServicio).update("productos", productos.text.toString())
        Firebase.firestore.collection("servicios").document(idServicio).update("latitud", latitud)
        Firebase.firestore.collection("servicios").document(idServicio).update("longitud", longitud)

        Toast.makeText(this, "Se ha modificado exitosamente", Toast.LENGTH_SHORT).show();
        finish()
    }

    fun seleccionarUbicacion(view: View){
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("latitud", latitud)
        intent.putExtra("longitud", longitud)
        activityResultLauncher.launch(intent)
    }
}