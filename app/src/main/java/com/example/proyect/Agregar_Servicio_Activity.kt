package com.example.proyect

import android.app.Activity
import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class Agregar_Servicio_Activity : AppCompatActivity() {

    lateinit var nombre : TextView
    lateinit var informacion : TextView
    lateinit var productos : TextView
    lateinit var imagen : ImageView
    lateinit var imagenUri : Uri
    lateinit var buscarImagen : ActivityResultLauncher<String>
    var imagenEmpty : Boolean = true
    var coordEmpty : Boolean = true
    lateinit var latitud : String
    lateinit var longitud : String

    val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
        if(result.resultCode == Activity.RESULT_OK) {

            val data: Intent? = result.data

            latitud = data?.getStringExtra("latitud").toString()
            longitud = data?.getStringExtra("longitud").toString()
            Log.wtf("DEBUG2", "${latitud}, ${longitud}")
            coordEmpty = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_servicio)

        nombre = findViewById(R.id.agregarNombreServicio)
        informacion = findViewById(R.id.agregarInformacionServicio)
        productos = findViewById(R.id.agregarProductosServicio)
        imagen = findViewById(R.id.agregarImagenServicio)

        buscarImagen = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) {
                imagenUri = it
                imagen.setImageURI(imagenUri)
                imagenEmpty = false
            }
        }
    }

    fun checkServicio(view : View) {
        if(nombre.text.toString().isEmpty() || informacion.text.toString().isEmpty() || productos.text.toString().isEmpty() || imagenEmpty || coordEmpty){
            Toast.makeText(this, "Falta agregar uno o mas campos", Toast.LENGTH_SHORT).show();
            return
        }

        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["nombre"] == nombre.text.toString()){
                        Toast.makeText(this, "Nombre de servicio ya existente", Toast.LENGTH_SHORT).show();
                        return@addOnSuccessListener
                    }
                }
                registrarDatos()
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun registrarDatos() {
        var idNuevo : String

        val servicio = hashMapOf(
            "nombre" to nombre.text.toString(),
            "informacion servicio" to informacion.text.toString(),
            "productos" to productos.text.toString(),
            "usuario" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "latitud" to latitud,
            "longitud" to longitud
        )

        Firebase.firestore.collection("servicios")
            .add(servicio)
            .addOnSuccessListener {

                Toast.makeText(this, "Servicio agregado", Toast.LENGTH_SHORT).show();
                registroExtra(it.id)
                registrarImagen(it.id)
                Log.d("FIREBASE", "id: ${it.id}")
                finish()
            }
            .addOnFailureListener {

                Toast.makeText(this, "Error al agregar el servcio", Toast.LENGTH_SHORT).show();
                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun seleccionarImagen(view : View){
        buscarImagen.launch("image/*")
    }

    fun registrarImagen(referenciaDocumento : String){
        val storageReference = FirebaseStorage.getInstance().getReference("imagenesServicios/$referenciaDocumento")
        storageReference.putFile(imagenUri)
            .addOnSuccessListener {

                Log.d("FIREBASE", "Correctamente cargado")
            }
            .addOnFailureListener {

                Log.e("FIREBASE", "exception: ${it.message}")
            }
    }

    fun registroExtra(idNuevo : String){
        var misServicios: ArrayList<String>
        var idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == idUsuario){
                        misServicios = documento.data["mis servicios"] as ArrayList<String>
                        misServicios.add(idNuevo)

                        Firebase.firestore.collection("usuarios").document(documento.id).update(
                            mapOf("mis servicios" to misServicios)
                        )
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Error al registrar servicio: ${it.message}")
            }
    }

    fun seleccionarUbicacion(view: View){
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("latitud", "20.737030")
        intent.putExtra("longitud", "-103.454188")
        activityResultLauncher.launch(intent)
    }
}