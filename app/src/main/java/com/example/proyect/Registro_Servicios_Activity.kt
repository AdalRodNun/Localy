package com.example.proyect

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Registro_Servicios_Activity : AppCompatActivity() {

    lateinit var addServiceButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_servicios)

        addServiceButton = findViewById(R.id.nuevo_servicio_button)
    }


    // Esta funcion te va allevar a la actividad donde se va a capturar los datos del nuevo servicio
    fun gotoDetalleServicio(){

    }
}