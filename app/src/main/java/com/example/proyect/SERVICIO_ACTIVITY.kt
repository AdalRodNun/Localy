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
    lateinit var idServicio : String
    lateinit var favoritoBoton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_servicio)

        infoServicio = findViewById(R.id.info_text_1)
        nombre = findViewById(R.id.nombre_servicio_text)
        productos = findViewById(R.id.productos_text)
        editarBoton = findViewById(R.id.editButton)
        borrarBoton = findViewById(R.id.deleteButton)
        favoritoBoton = findViewById(R.id.favoritoBoton)

        idServicio = intent.getStringExtra("id").toString()

        cargarDatos()
    }

    @Override
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    fun cargarDatos(){
        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == idServicio){
                        infoServicio.text = documento.data["informacion servicio"].toString()
                        nombre.text = documento.data["nombre"].toString()
                        productos.text = documento.data["productos"].toString()
                        latitud = documento.data["latitud"].toString()
                        longitud = documento.data["longitud"].toString()
                        usuario = documento.data["usuario"].toString()
                        cargarBoton()
                        break
                    }
                }

                if(FirebaseAuth.getInstance().currentUser?.uid.toString() != usuario){
                    editarBoton.visibility = View.GONE
                    borrarBoton.visibility = View.GONE
                }
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun borrarUsuarioID(view: View){
        var misServicios: ArrayList<String>
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == idUsuario) {
                        misServicios = documento.data["mis servicios"] as ArrayList<String>
                        misServicios.remove(idServicio)

                        Firebase.firestore.collection("usuarios").document(documento.id).update(
                            mapOf("mis servicios" to misServicios)
                        )

                        borrarDeFavoritos()
                        break
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Error al eliminar id servicio en usuario: ${it.message}")
            }
    }

    fun borrarDeFavoritos(){
        var serviciosFavoritos : ArrayList<String>
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    serviciosFavoritos = documento.data["servicios guardados"] as ArrayList<String>

                    if(serviciosFavoritos.contains(idServicio)){
                        serviciosFavoritos.remove(idServicio)

                        Firebase.firestore.collection("usuarios").document(documento.id).update(
                            mapOf("servicios guardados" to serviciosFavoritos)
                        )
                    }
                }
                borrar()
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Error al eliminar id servicio en usuario: ${it.message}")
            }
    }

    fun borrar() {
        Firebase.firestore.collection("servicios").document(idServicio)
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
        intent.putExtra("idServicio", idServicio)
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

    fun cargarBoton(){
        var serviciosFavoritos: ArrayList<String>
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == idUsuario) {
                        serviciosFavoritos = documento.data["servicios guardados"] as ArrayList<String>

                        if (serviciosFavoritos.contains(idServicio)) {
                            favoritoBoton.setText("Eliminar de favoritos")
                        } else {
                            favoritoBoton.setText("Añadir a favoritos")
                        }
                    }
                    break
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "error al leer usuarios: ${it.message}")
            }
    }

    fun agregarEliminarFavoritos(v : View){
        var serviciosFavoritos: ArrayList<String>
        val idUsuario = FirebaseAuth.getInstance().currentUser?.uid.toString()

        Firebase.firestore.collection("usuarios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.data["user id"] == idUsuario){
                        serviciosFavoritos = documento.data["servicios guardados"] as ArrayList<String>
                        if (!serviciosFavoritos.contains(idServicio)) {
                            serviciosFavoritos.add(idServicio)
                            favoritoBoton.setText("Eliminar de favoritos")

                            Toast.makeText(this, "Servicio guardado", Toast.LENGTH_SHORT).show()
                        } else {
                            serviciosFavoritos.remove(idServicio)
                            favoritoBoton.setText("Añadir a favoritos")

                            Toast.makeText(this, "Servicio eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        }

                        Firebase.firestore.collection("usuarios").document(documento.id).update(
                            mapOf("servicios guardados" to serviciosFavoritos)
                        )
                    }
                    break
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }
}