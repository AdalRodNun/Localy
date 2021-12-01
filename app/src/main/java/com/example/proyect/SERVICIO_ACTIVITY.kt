package com.example.proyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SERVICIO_ACTIVITY : AppCompatActivity() {
    lateinit var infoServicio: TextView
    lateinit var nombre : TextView
    lateinit var productos : TextView
    lateinit var latitud : String
    lateinit var longitud : String
    lateinit var editarBoton : AppCompatImageButton
    lateinit var borrarBoton : AppCompatImageButton
    lateinit var usuario : String
    lateinit var idservicio : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio)

        infoServicio = findViewById(R.id.info_text_1)
        nombre = findViewById(R.id.nombre_servicio_text)
        productos = findViewById(R.id.productos_text)
        editarBoton = findViewById(R.id.editButton)
        borrarBoton = findViewById(R.id.deleteButton)

        nombre.text = intent.getStringExtra("nombre")
        infoServicio.text = intent.getStringExtra("informacion")
        productos.text = intent.getStringExtra("productos")
        latitud = intent.getStringExtra("latitud").toString()
        longitud = intent.getStringExtra("longitud").toString()
        usuario = intent.getStringExtra("usuario").toString()
        idservicio = intent.getStringExtra("id").toString()

        if(FirebaseAuth.getInstance().currentUser?.uid.toString() != intent.getStringExtra("usuario")){
            editarBoton.visibility = View.GONE
            borrarBoton.visibility = View.GONE
        }
    }

    fun borrar(view: View){
        var id = intent.getStringExtra("id").toString()
        Firebase.firestore.collection("servicios").document(id)
            .delete()
            .addOnSuccessListener {
                Log.d("FIREBASE", "Documento borrado")
                Toast.makeText(this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error al borrar documento", e)
            }
    }

    fun editar(view: View){
        val intent = Intent(this, Modificar_Servicio_Activity::class.java)
        intent.putExtra("nombre", nombre.text.toString())
        intent.putExtra("informacion", infoServicio.text.toString())
        intent.putExtra("productos", productos.text.toString())
        intent.putExtra("latitud", latitud)
        intent.putExtra("longitud", longitud)
        intent.putExtra("id", idservicio)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
    }

    fun verMapa(view: View){
        val intent = Intent(this, MapsActivityVisualizador::class.java)

        intent.putExtra("nombre", nombre.text.toString())
        intent.putExtra("latitud", latitud)
        intent.putExtra("longitud", longitud)
        startActivity(intent)
    }
}