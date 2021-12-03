package com.example.proyect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class Modificar_Productos_Activity : AppCompatActivity(),  View.OnClickListener {
    lateinit var recyclerViewProductos: RecyclerView
    lateinit var adapterProductos : Producto_Adapter
    lateinit var idServicio : String
    lateinit var productoTexto : EditText
    lateinit var seleccionado : String
    lateinit var productoSeleccionadoText : TextView
    var productosData : ArrayList<Producto> = ArrayList()
    var productosDataString : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar_productos)

        recyclerViewProductos = findViewById(R.id.recyclerViewProductosModificar)
        productoTexto = findViewById(R.id.productoText)
        productoSeleccionadoText = findViewById(R.id.seleccionaProductoText)

        idServicio = intent.getStringExtra("idServicio").toString()

        cargarProductos()
    }

    fun cargarProductos(){
        productosData.clear()
        var productosActuales : ArrayList<String>

        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == idServicio){
                        productosActuales = documento.data["productos"] as ArrayList<String>

                        for(producto in productosActuales){
                            productosData.add(Producto(producto))
                            productosDataString.add(producto)
                            Log.d("FIREBASE", "Correctamente cargado")
                            iniciarRecycler()
                        }
                        break
                    }
                }
            }
            .addOnFailureListener() {
                Log.e("FIRESTORE", "error al leer servicios: ${it.message}")
            }
    }

    fun iniciarRecycler(){
        adapterProductos = Producto_Adapter(productosData, this)
        var llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL

        recyclerViewProductos.layoutManager = llm
        recyclerViewProductos.adapter = adapterProductos

    }

    fun agregarProducto (view : View) {
        var producto = productoTexto.text.toString()

        if(producto.isEmpty()){
            Toast.makeText(this, "Falta agregar el producto", Toast.LENGTH_SHORT).show()
            return
        }

        productosData.add(Producto(producto))
        productosDataString.add(producto)

        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == idServicio){
                        Firebase.firestore.collection("servicios").document(documento.id).update(
                            mapOf("productos" to productosDataString)
                        )
                        break
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Error al registrar servicio: ${it.message}")
            }

        iniciarRecycler()
    }

    fun eliminarProducto(view : View){
        if(seleccionado == null){
            Toast.makeText(this, "Falta seleccionar el producto", Toast.LENGTH_SHORT).show()
            return
        }

        productosData.remove(Producto(seleccionado))
        productosDataString.remove(seleccionado)

        Firebase.firestore.collection("servicios")
            .get()
            .addOnSuccessListener {
                for (documento in it) {
                    if(documento.id == idServicio){
                        Firebase.firestore.collection("servicios").document(documento.id).update(
                            mapOf("productos" to productosDataString)
                        )
                        break
                    }
                }
            }
            .addOnFailureListener {
                Log.e("FIRESTORE", "Error al registrar servicio: ${it.message}")
            }

        iniciarRecycler()
    }

    override fun onClick(row : View) {
        val position = recyclerViewProductos.getChildLayoutPosition(row)
        seleccionado = productosData[position].precioNombreProducto
        productoSeleccionadoText.setText(seleccionado)
    }
}