package com.example.otocheck.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.databinding.FragmentRaporDetayBinding

class RaporDetay_Fragment : Fragment() {

    private var _binding: FragmentRaporDetayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRaporDetayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val raporId = arguments?.getInt("raporId", -1) ?: -1

        if (raporId != -1) {
            verileriYukle(raporId)
        } else {
            Toast.makeText(requireContext(), "Hata: Rapor ID geçersiz!", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }


        binding.headerKaporta.setOnClickListener { toggle(binding.contentKaporta, binding.ivArrowKaporta) }
        binding.headerMotor.setOnClickListener { toggle(binding.contentMotor, binding.ivArrowMotor) }
        binding.headerMekanik.setOnClickListener { toggle(binding.contentMekanik, binding.ivArrowMekanik) }
        binding.headerOBD.setOnClickListener { toggle(binding.contentOBD, binding.ivArrowOBD) }
        binding.headerAirbag.setOnClickListener { toggle(binding.contentAirbag, binding.ivArrowAirbag) }

        binding.btnGeri.setOnClickListener { findNavController().popBackStack() }
    }



    private fun toggle(view: View, arrow: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
            arrow.animate().rotation(0f).setDuration(200).start()
        } else {
            view.visibility = View.VISIBLE
            arrow.animate().rotation(180f).setDuration(200).start()
        }
    }

    //bu fonksiyon rapor detayını görüntülerken sıkıntılı durumları daha dikkat şekilde gösterecek şekilde göstermeye yarıyor bu kısımda yapay zekayı kullandım.
    private fun renklendir(textView: TextView, metin: String?) {
        val durum = metin ?: "Test Edilmedi"
        textView.text = durum


        val kelime = durum.lowercase()

        when {

            kelime.contains("donanımda yok") || kelime.contains("manuel/yok") ||
                    kelime.contains("tespit edilemedi") -> {
                textView.setTextColor(Color.parseColor("#9E9E9E")) // Gri
            }


            kelime.contains("lokal") || kelime.contains("terleme") ||
                    kelime.contains("hafif") || kelime.contains("orta") ||
                    kelime.contains("çarpma") || kelime.contains("geçmiş") ||
                    kelime.contains("kısmi") -> {
                textView.setTextColor(Color.parseColor("#FF9800"))
            }


            kelime.contains("boyalı") -> {
                textView.setTextColor(Color.parseColor("#2196F3"))
            }


            kelime.contains("orijinal") || kelime.contains("sorunsuz") ||
                    kelime.contains("normal") || kelime.contains("iyi") ||
                    kelime.contains("tam") || kelime.contains("hasar kaydı yok") ||
                    kelime == "yok" -> {
                textView.setTextColor(Color.parseColor("#4CAF50"))
            }


            kelime.contains("değiş") || kelime.contains("işlem") || kelime.contains("patlak") ||
                    kelime.contains("direnç") || kelime.contains("kötü") || kelime.contains("onarım") ||
                    kelime.contains("eksik") || kelime.contains("kaçak") || kelime.contains("sorunlu") ||
                    kelime.contains("çatlak") || kelime.contains("ses") || kelime.contains("vuruntu") ||
                    kelime.contains("düşürülmüş") || kelime.contains("ağır") || kelime.contains("pert") ||
                    kelime.contains("aktif hata") || kelime.contains("duman") || kelime.contains("anormal") ||
                    kelime.contains("yitirmiş") || kelime.contains("soğutmuyor") -> {
                textView.setTextColor(Color.parseColor("#F44336"))
            }


            else -> {
                textView.setTextColor(Color.parseColor("#9E9E9E")) // Gri
            }
        }
    }
    //bu fonksiyon database den çekilen tüm verileri textview lere bağlıyor.
    private fun verileriYukle(raporId: Int) {
        Thread {
            val veri = DatabaseBaglantı.raporDetaylariniGetir(raporId)
            activity?.runOnUiThread {
                if (veri.isNotEmpty()) {

                    binding.txtDetayArac.text = "${veri["Marka"]} ${veri["Model"]}"
                    binding.txtDetayPlaka.text = veri["Plaka"]
                    binding.txtDetayKmSase.text = "${veri["Kilometre"]} KM | Şase: ${veri["SaseNo"]}"
                    binding.txtDetayTarih.text = "Rapor Tarihi: ${veri["RaporTarihi"]}"


                    // Dış Kaporta
                    renklendir(binding.tvKprTavan, veri["Tavan"])
                    renklendir(binding.tvKprOnTampon, veri["OnTampon"])
                    renklendir(binding.tvKprArkaTampon, veri["ArkaTampon"])
                    renklendir(binding.tvKprKaput, veri["Kaput"])
                    renklendir(binding.tvKprBagaj, veri["Bagaj"])
                    renklendir(binding.tvKprSolOnCamurluk, veri["SolOnCamurluk"])
                    renklendir(binding.tvKprSagOnCamurluk, veri["SagOnCamurluk"])
                    renklendir(binding.tvKprSolArkaCamurluk, veri["SolArkaCamurluk"])
                    renklendir(binding.tvKprSagArkaCamurluk, veri["SagArkaCamurluk"])
                    renklendir(binding.tvKprSolOnKapi, veri["SolOnKapi"])
                    renklendir(binding.tvKprSagOnKapi, veri["SagOnKapi"])
                    renklendir(binding.tvKprSolArkaKapi, veri["SolArkaKapi"])
                    renklendir(binding.tvKprSagArkaKapi, veri["SagArkaKapi"])
                    renklendir(binding.tvKprSolMarspiyel, veri["SolMarspiyel"])
                    renklendir(binding.tvKprSagMarspiyel, veri["SagMarspiyel"])

                    // Direkler
                    renklendir(binding.tvKprSolOnDirek, veri["SolOnDirek"])
                    renklendir(binding.tvKprSagOnDirek, veri["SagOnDirek"])
                    renklendir(binding.tvKprSolOrtaDirek, veri["SolOrtaDirek"])
                    renklendir(binding.tvKprSagOrtaDirek, veri["SagOrtaDirek"])
                    renklendir(binding.tvKprSolArkaDirek, veri["SolArkaDirek"])
                    renklendir(binding.tvKprSagArkaDirek, veri["SagArkaDirek"])

                    // Şase
                    renklendir(binding.tvKprSolOnSase, veri["SolOnSase"])
                    renklendir(binding.tvKprSagOnSase, veri["SagOnSase"])
                    renklendir(binding.tvKprSolArkaSase, veri["SolArkaSase"])
                    renklendir(binding.tvKprSagArkaSase, veri["SagArkaSase"])
                    renklendir(binding.tvKprSolPodye, veri["SolPodye"])
                    renklendir(binding.tvKprSagPodye, veri["SagPodye"])
                    renklendir(binding.tvKprBagajHavuzu, veri["BagajHavuzu"])

                    //Not
                    binding.tvKprNot.text = "Usta Notu: ${veri["KaportaNot"]}"


                    renklendir(binding.tvMtrSesi, veri["MotorSesi"])
                    renklendir(binding.tvMtrUfleme, veri["UflemeVeDuman"])
                    renklendir(binding.tvMtrSivi, veri["YagVeSiviSeviyeleri"])
                    renklendir(binding.tvMtrKacak, veri["MotorYagKacaklari"])
                    renklendir(binding.tvMtrTurbo, veri["TurboVeYakıtSistemi"])
                    renklendir(binding.tvMtrSogutma, veri["SogutmaVeKlima"])
                    renklendir(binding.tvMtrKayis, veri["KayisVeKasnaklar"])
                    renklendir(binding.tvMtrSanziman, veri["SanzimanVeAktarma"])

                    binding.tvMtrNot.text = "Usta Notu: ${veri["MotorNot"]}"


                    renklendir(binding.tvMekSolAks, veri["SolAks"])
                    renklendir(binding.tvMekSagAks, veri["SagAks"])
                    renklendir(binding.tvMekDireksiyon, veri["DireksiyonKutusu"])
                    renklendir(binding.tvMekRot, veri["RotBaslari"])
                    renklendir(binding.tvMekOnBalata, veri["OnBalatalar"])
                    renklendir(binding.tvMekArkaBalata, veri["ArkaBalatalar"])
                    renklendir(binding.tvMekOnDisk, veri["OnDiskler"])
                    renklendir(binding.tvMekArkaDisk, veri["ArkaDiskler"])
                    renklendir(binding.tvMekSolOnSus, veri["SolOnSuspansiyon"])
                    renklendir(binding.tvMekSagOnSus, veri["SagOnSuspansiyon"])
                    renklendir(binding.tvMekSolArkaSus, veri["SolArkaSuspansiyon"])
                    renklendir(binding.tvMekSagArkaSus, veri["SagArkaSuspansiyon"])

                    binding.tvMekNot.text = "Usta Notu: ${veri["MekanikNot"]}"


                    binding.tvObdGostergeKm.text = "${veri["GostergeKilometre"]} KM"
                    binding.tvObdBeyinKm.text = "${veri["BeyinKilometre"]} KM"
                    binding.tvObdTramerTutari.text = "${veri["TramerTutari"]} TL"


                    renklendir(binding.tvObdKmOrj, veri["KmOrijinalligi"])
                    renklendir(binding.tvObdTramerDurumu, veri["TramerDurumu"])
                    renklendir(binding.tvObdMotorBeyni, veri["MotorBeyni_ECU"])
                    renklendir(binding.tvObdSanzimanBeyni, veri["SanzimanBeyni_TCM"])
                    renklendir(binding.tvObdAbsEsp, veri["ABS_ESP_Sistemi"])
                    renklendir(binding.tvObdAirbagSistemi, veri["Airbag_Sistemi"])
                    renklendir(binding.tvObdElektronik, veri["ElektronikAksamlar"])

                    binding.tvObdNot.text = "Usta Notu: ${veri["ObdNot"]}"


                    renklendir(binding.tvAbgSurucu, veri["SurucuAirbag"])
                    renklendir(binding.tvAbgYolcu, veri["YolcuAirbag"])
                    renklendir(binding.tvAbgSurucuDiz, veri["SurucuDizAirbag"])
                    renklendir(binding.tvAbgYolcuDiz, veri["YolcuDizAirbag"])
                    renklendir(binding.tvAbgSolPerde, veri["SolPerdeAirbag"])
                    renklendir(binding.tvAbgSagPerde, veri["SagPerdeAirbag"])
                    renklendir(binding.tvAbgSurucuKoltuk, veri["SurucuKoltukAirbag"])
                    renklendir(binding.tvAbgYolcuKoltuk, veri["YolcuKoltukAirbag"])
                    renklendir(binding.tvAbgKemer, veri["EmniyetKemerleri"])

                    binding.tvAbgNot.text = "Usta Notu: ${veri["AirbagNot"]}"
                }
                else {

                    binding.txtDetayArac.text = "Veri Çekilemedi!"
                    Toast.makeText(requireContext(), "SQL Okuma Yetkisi Yok veya Rapor Boş!", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}