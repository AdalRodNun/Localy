package com.example.proyect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class Servicio_Adapter(private var servicios :ArrayList<Servicio>, private var listener : View.OnClickListener) : RecyclerView.Adapter<Servicio_Adapter.ServicioViewHolder>() {

    class ServicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nombreText : TextView
        var servicioImage : ImageView

        init {
            nombreText = itemView.findViewById(R.id.textview_row_nombre)
            servicioImage = itemView.findViewById(R.id.image_row)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        val dvh = ServicioViewHolder(view)

        view.setOnClickListener(listener)

        return dvh
    }

    override fun onBindViewHolder(holder: ServicioViewHolder, position: Int) {
        holder.nombreText.text = servicios[position].nombreServicio
        holder.servicioImage.setImageBitmap(servicios[position].imagenServicioBitmap)
    }

    override fun getItemCount(): Int {
        return servicios.size
    }
}