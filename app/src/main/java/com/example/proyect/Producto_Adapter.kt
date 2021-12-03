package com.example.proyect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class Producto_Adapter(private var productos : ArrayList<Producto>, private var listener : View.OnClickListener) : RecyclerView.Adapter<Producto_Adapter.ProductoViewHolder>() {

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var precioNombreText : TextView

        init {
            precioNombreText = itemView.findViewById(R.id.producto_precio_texto_view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.producto_row, parent, false)
        val dvh = ProductoViewHolder(view)

        view.setOnClickListener(listener)

        return dvh
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        holder.precioNombreText.text = productos[position].precioNombreProducto
    }

    override fun getItemCount(): Int {
        return productos.size
    }
}