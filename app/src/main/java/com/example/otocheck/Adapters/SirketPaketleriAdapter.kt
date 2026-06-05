package com.example.otocheck.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.otocheck.Models.PaketModel
import com.example.otocheck.R
import java.text.NumberFormat
import java.util.Locale

class SirketPaketleriAdapter(
    private val paketListesi: List<PaketModel>,
    private val onSilClicked: (PaketModel) -> Unit,
    private val onDuzenleClicked: (PaketModel) -> Unit // YENİ EKLENDİ
) : RecyclerView.Adapter<SirketPaketleriAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMevcutPaketAdi: TextView = view.findViewById(R.id.tvMevcutPaketAdi)
        val tvMevcutPaketIcerik: TextView = view.findViewById(R.id.tvMevcutPaketIcerik)
        val tvMevcutPaketFiyat: TextView = view.findViewById(R.id.tvMevcutPaketFiyat)
        val btnPaketSil: Button = view.findViewById(R.id.btnPaketSil)
        val btnPaketDuzenle: Button = view.findViewById(R.id.btnPaketDuzenle) // YENİ EKLENDİ
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sirket_paket, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paket = paketListesi[position]

        holder.tvMevcutPaketAdi.text = paket.paketAdi

        val icerikListesi = mutableListOf<String>()
        if (paket.motor) icerikListesi.add("Motor")
        if (paket.mekanik) icerikListesi.add("Mekanik")
        if (paket.kaporta) icerikListesi.add("Kaporta")
        if (paket.airbag) icerikListesi.add("Airbag")
        if (paket.obd) icerikListesi.add("OBD")

        holder.tvMevcutPaketIcerik.text = if (icerikListesi.isEmpty()) "İçerik Belirtilmedi" else icerikListesi.joinToString(", ")

        val tlfFormati = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
        holder.tvMevcutPaketFiyat.text = tlfFormati.format(paket.fiyat)

        holder.btnPaketSil.setOnClickListener { onSilClicked(paket) }
        holder.btnPaketDuzenle.setOnClickListener { onDuzenleClicked(paket) }
    }

    override fun getItemCount(): Int = paketListesi.size
}