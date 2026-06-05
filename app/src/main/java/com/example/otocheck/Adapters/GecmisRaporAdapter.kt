package com.example.otocheck.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.otocheck.Models.GecmisRaporModel
import com.example.otocheck.R

class GecmisRaporAdapter(
    private val raporListesi: List<GecmisRaporModel>,
    private val onInceleClicked: (GecmisRaporModel) -> Unit
) : RecyclerView.Adapter<GecmisRaporAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtArac: TextView = view.findViewById(R.id.txtGecmisArac)
        val txtPlaka: TextView = view.findViewById(R.id.txtGecmisPlaka)
        val txtSirket: TextView = view.findViewById(R.id.txtGecmisSirket)
        val txtTarihPaket: TextView = view.findViewById(R.id.txtGecmisTarihPaket)
        val btnIncele: Button = view.findViewById(R.id.btnRaporuIncele)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gecmis_rapor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rapor = raporListesi[position]
        holder.txtArac.text = "${rapor.marka} ${rapor.model}"
        holder.txtPlaka.text = rapor.plaka
        holder.txtSirket.text = rapor.sirketAdi
        holder.txtTarihPaket.text = "${rapor.tarih} | ${rapor.paketAdi}"

        holder.btnIncele.setOnClickListener { onInceleClicked(rapor) }
    }

    override fun getItemCount() = raporListesi.size
}