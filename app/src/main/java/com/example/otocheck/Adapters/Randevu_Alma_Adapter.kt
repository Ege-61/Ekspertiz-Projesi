package com.example.otocheck.Adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.Models.EkspertizAramaModel
import com.example.otocheck.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Randevu_Alma_Adapter(
    private val liste: List<EkspertizAramaModel>,
    private val secilenTarih: String,
    private val onIlerleClicked: (sirketId: Int, paketId: Int, tarih: String, saat: String, fiyat: Double) -> Unit
) : RecyclerView.Adapter<Randevu_Alma_Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layoutVitrin: View = view.findViewById(R.id.layoutVitrin)
        val layoutGizliDetay: View = view.findViewById(R.id.layoutGizliDetay)
        val tvSirketAdi: TextView = view.findViewById(R.id.tvSirketAdi)
        val tvPaketAdi: TextView = view.findViewById(R.id.tvPaketAdi)
        val tvKonum: TextView = view.findViewById(R.id.tvKonum)
        val tvPaketFiyati: TextView = view.findViewById(R.id.tvPaketFiyati)
        val tvPaketIcerikOzet: TextView = view.findViewById(R.id.tvPaketIcerikOzet)
        val imgGecisOku: ImageView = view.findViewById(R.id.imgGecisOku)
        val rvSaatler: RecyclerView = view.findViewById(R.id.rvSaatler)
        val btnRandevuOnay: Button = view.findViewById(R.id.btnRandevuOnay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_randevu_bilgi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = liste[position]

        holder.tvSirketAdi.text = item.sirketAdi
        holder.tvPaketAdi.text = item.paketAdi
        holder.tvKonum.text = item.konum

        val tlfFormati = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
        holder.tvPaketFiyati.text = tlfFormati.format(item.fiyat)
        holder.tvPaketIcerikOzet.text = item.icerikOzet

        val isExpanded = item.isExpanded
        holder.layoutGizliDetay.visibility = if (isExpanded) View.VISIBLE else View.GONE
        holder.imgGecisOku.rotation = if (isExpanded) 180f else 0f

        holder.layoutVitrin.setOnClickListener {
            val guncelPozisyon = holder.bindingAdapterPosition
            if (guncelPozisyon != RecyclerView.NO_POSITION) {
                liste[guncelPozisyon].isExpanded = !liste[guncelPozisyon].isExpanded
                notifyItemChanged(guncelPozisyon)
            }
        }

        if (isExpanded) {
            holder.rvSaatler.layoutManager =
                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            val tumSaatler = listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00")

            Thread {

                val doluSaatler = DatabaseBaglantı.getDoluSaatler(item.sirketId, secilenTarih)
                var bosSaatler = tumSaatler.filter { it !in doluSaatler }


                var secilenGunBugunMu = false
                try {

                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val dateSecilen = sdf.parse(secilenTarih)

                    val calSecilen = Calendar.getInstance()
                    if (dateSecilen != null) calSecilen.time = dateSecilen

                    val calBugun = Calendar.getInstance()

                    if (calSecilen.get(Calendar.YEAR) == calBugun.get(Calendar.YEAR) &&
                        calSecilen.get(Calendar.DAY_OF_YEAR) == calBugun.get(Calendar.DAY_OF_YEAR)) {
                        secilenGunBugunMu = true
                    }
                } catch (e: Exception) {

                }


                if (secilenGunBugunMu) {
                    val calBugun = Calendar.getInstance()
                    val simdikiSaat = calBugun.get(Calendar.HOUR_OF_DAY)
                    val simdikiDakika = calBugun.get(Calendar.MINUTE)

                    bosSaatler = bosSaatler.filter { saatStr ->
                        val parcalar = saatStr.split(":")
                        val hedefSaat = parcalar[0].toInt()
                        val hedefDakika = parcalar[1].toInt()

                        if (hedefSaat > simdikiSaat) {
                            true
                        } else if (hedefSaat == simdikiSaat) {
                            hedefDakika > simdikiDakika
                        } else {
                            false
                        }
                    }
                }

                holder.itemView.post {
                    holder.rvSaatler.adapter = SaatAdapter(bosSaatler, item) { secilenSaat ->
                        item.secilenSaat = secilenSaat
                        holder.btnRandevuOnay.isEnabled = true
                        holder.btnRandevuOnay.text = "$secilenSaat Saatine Onayla"
                    }

                    if (item.secilenSaat.isNotEmpty()) {
                        holder.btnRandevuOnay.isEnabled = true
                        holder.btnRandevuOnay.text = "${item.secilenSaat} Saatine Onayla"
                    } else {

                        holder.btnRandevuOnay.isEnabled = false
                        holder.btnRandevuOnay.text = "Onaylamak İçin Saat Seçin"
                    }
                }
            }.start()

            holder.btnRandevuOnay.setOnClickListener {
                if (item.secilenSaat.isNotEmpty()) {
                    onIlerleClicked(
                        item.sirketId,
                        item.paketId,
                        secilenTarih,
                        item.secilenSaat,
                        item.fiyat
                    )
                } else {
                    Toast.makeText(holder.itemView.context, "Lütfen bir saat seçin!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if (!item.logoBase64.isNullOrEmpty()) {
            try {
                val temizBase64 = if (item.logoBase64.contains(",")) {
                    item.logoBase64.substring(item.logoBase64.indexOf(",") + 1)
                } else {
                    item.logoBase64
                }
                val imageBytes = Base64.decode(temizBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.itemView.findViewById<ImageView>(R.id.imgSirketLogo).setImageBitmap(decodedImage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int = liste.size

    //saat adapter ı da buraya ekledim bu kısım randevu alırken kullandığımız bir kısım
    class SaatAdapter(
        private val saatler: List<String>,
        private val model: EkspertizAramaModel,
        val onSaatSecildi: (String) -> Unit
    ) : RecyclerView.Adapter<SaatAdapter.SaatViewHolder>() {

        class SaatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvSaat: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaatViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            val layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(15, 0, 15, 0)
            view.layoutParams = layoutParams
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.gravity = Gravity.CENTER
            textView.setPadding(40, 20, 40, 20)
            return SaatViewHolder(view)
        }

        override fun onBindViewHolder(holder: SaatViewHolder, position: Int) {
            val anlikSaat = saatler[position]
            holder.tvSaat.text = anlikSaat

            if (anlikSaat == model.secilenSaat) {
                holder.tvSaat.setBackgroundColor(Color.parseColor("#4CAF50"))
                holder.tvSaat.setTextColor(Color.WHITE)
            } else {
                holder.tvSaat.setBackgroundColor(Color.parseColor("#E0E0E0"))
                holder.tvSaat.setTextColor(Color.BLACK)
            }

            holder.tvSaat.setOnClickListener {
                model.secilenSaat = anlikSaat
                notifyDataSetChanged()
                onSaatSecildi(anlikSaat)
            }
        }
        override fun getItemCount(): Int = saatler.size
    }
}