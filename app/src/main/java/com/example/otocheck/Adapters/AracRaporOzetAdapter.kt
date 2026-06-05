package com.example.otocheck.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.otocheck.Models.AracRaporOzetModel
import com.example.otocheck.R

class AracRaporOzetAdapter(
    private val raporListesi: List<AracRaporOzetModel>,
    private val onItemClick: (AracRaporOzetModel) -> Unit
) : RecyclerView.Adapter<AracRaporOzetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtAracIsmi: TextView = view.findViewById(R.id.txtOzetAracIsmi)
        val txtTarih: TextView = view.findViewById(R.id.txtOzetTarih)
        val txtSirketPaket: TextView = view.findViewById(R.id.txtOzetSirketVePaket)
        val txtKm: TextView = view.findViewById(R.id.txtOzetKm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_arac_rapor_ozet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rapor = raporListesi[position]
        holder.txtAracIsmi.text = "${rapor.marka} ${rapor.model}"
        holder.txtTarih.text = rapor.raporTarihi
        holder.txtSirketPaket.text = "${rapor.sirketAdi} | ${rapor.paketAdi}"
        holder.txtKm.text = "Ekspertiz KM: %,d KM".format(rapor.kilometre)

        holder.itemView.setOnClickListener { onItemClick(rapor) }
    }

    override fun getItemCount() = raporListesi.size
}