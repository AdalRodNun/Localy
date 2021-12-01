package com.example.proyect

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Servicio(val nombreServicio : String, val imagenServicioBitmap : Bitmap, val idusuario : String,
                    val productos : String, val informacion : String, val latitud : String, val longitud : String,
                    val idservicio : String)