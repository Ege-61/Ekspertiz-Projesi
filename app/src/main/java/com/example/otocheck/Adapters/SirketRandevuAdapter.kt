package com.example.otocheck.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.otocheck.R
import com.example.otocheck.Models.SirketRandevuModel

class SirketRandevuAdapter(
    private val randevuListesi: List<SirketRandevuModel>,
    private val onRaporDoldurClicked: (SirketRandevuModel) -> Unit
) : RecyclerView.Adapter<SirketRandevuAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRandevuSaati: TextView = view.findViewById(R.id.tvRandevuSaati)
        val tvPaketAdi: TextView = view.findViewById(R.id.tvPaketAdi)
        val tvAracPlaka: TextView = view.findViewById(R.id.tvAracPlaka)
        val tvAracDetay: TextView = view.findViewById(R.id.tvAracDetay)
        val btnRaporDoldur: Button = view.findViewById(R.id.btnRaporDoldur)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sirket_randevu, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val randevu = randevuListesi[position]


        val formatliTarih = try {
            val parcalar = randevu.randevuTarihi.split("-")
            "${parcalar[2]}/${parcalar[1]}"
        } catch (e: Exception) {
            randevu.randevuTarihi
        }

        holder.tvRandevuSaati.text = "${randevu.randevuSaati} | $formatliTarih"

        holder.tvPaketAdi.text = randevu.paketAdi
        holder.tvAracPlaka.text = randevu.plaka
        holder.tvAracDetay.text = "${randevu.marka} ${randevu.model} (${randevu.yil})"

        holder.btnRaporDoldur.setOnClickListener {
            onRaporDoldurClicked(randevu)
        }
    }

    override fun getItemCount(): Int = randevuListesi.size
}