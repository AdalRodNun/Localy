package com.example.proyect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EscribirResenaActivity : AppCompatActivity() {
    lateinit var idServicio : String
    lateinit var reseñaTexto : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_escribir_resena)

        reseñaTexto = findViewById(R.id.textoReseña)

        idServicio = intent.getStringExtra("idServicio").toString()
    }

    fun agregarReseña (view : View) {
        var reseña = reseñaTexto.text.toString()

        if(reseña.isEmpty()){
            Toast.makeText(this, "Falta agregar la reseña", Toast.LENGTH_SHORT).show()
            return
        }

        var reseñasActuales : ArrayList<String>

        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == idServicio) {
                        reseñasActuales = documento.data["reseñas"] as ArrayList<String>

                        reseñasActuales.add(reseña)

                        Firebase.firestore.collection("servicios").document(documento.id).update(
                            mapOf("reseñas" to reseñasActuales)
                        )
                        break
                    }
                }
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
        finish()
    }
}