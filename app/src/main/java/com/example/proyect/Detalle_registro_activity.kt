package com.example.proyect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class Detalle_registro_activity : AppCompatActivity() {

    lateinit var registatrButton : Button
    lateinit var nombre_tienda : EditText
    lateinit var servicios_ofrecidos :EditText
    lateinit var informacion_servicio :EditText
    lateinit var altitud : EditText
    lateinit var longitud : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_registro)

        registatrButton = findViewById(R.id.registrar_button)
        nombre_tienda = findViewById(R.id.nombre_editText)
        servicios_ofrecidos = findViewById(R.id.servicios_ofrecidos_editText)
        informacion_servicio = findViewById(R.id.informacion_servicio_editText)
        altitud = findViewById(R.id.altitud_editText)
        longitud = findViewById(R.id.longitud_editText)
    }





    // Al darse click a este servicio, se debe de guardar la informacion a firebase y regresar a la actividad activity_registro_servicio
    fun registroServicio(){

    }
}