package com.example.proyect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class Resena_Adapter(private var reseñas : ArrayList<Reseña>, private var listener : View.OnClickListener) : RecyclerView.Adapter<Resena_Adapter.ReseñaViewHolder>() {

    class ReseñaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var reseñaText : TextView

        init {
            reseñaText = itemView.findViewById(R.id.resenaTextoRV)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReseñaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.resena_row, parent, false)
        val dvh = ReseñaViewHolder(view)

        view.setOnClickListener(listener)

        return dvh
    }

    override fun onBindViewHolder(holder: ReseñaViewHolder, position: Int) {
        holder.reseñaText.text = reseñas[position].reseñaTexto
    }

    override fun getItemCount(): Int {
        return reseñas.size
    }
}