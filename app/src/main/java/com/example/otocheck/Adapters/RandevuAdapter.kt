package com.example.otocheck.Adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.otocheck.Models.Randevu
import com.example.otocheck.R
import com.google.android.material.card.MaterialCardView

class RandevuAdapter(private val randevuListesi: List<Randevu>) : RecyclerView.Adapter<RandevuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBaslik: TextView = view.findViewById(R.id.txtRandevuBaslik)
        val txtTarih: TextView = view.findViewById(R.id.txtRandevuTarih)
        val btnYolTarifi: MaterialCardView = view.findViewById(R.id.btnYolTarifi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_randevu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val randevu = randevuListesi[position]
        holder.txtBaslik.text = randevu.sirketAdi


        holder.txtTarih.text = "${randevu.tarih}  |  Saat: ${randevu.saat}"

        holder.btnYolTarifi.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(randevu.adres)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            holder.itemView.context.startActivity(mapIntent)
        }
    }

    override fun getItemCount() = randevuListesi.size
}