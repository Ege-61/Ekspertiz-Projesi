package com.example.otocheck.Fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.databinding.FragmentRaporOlusturGenelBinding

class RaporOlusturGenel_Fragment : Fragment() {

    private var _binding: FragmentRaporOlusturGenelBinding? = null
    private val binding get() = _binding!!
    private var gelenRandevuId: Int = -1
    private var gelenRaporId: Int = -1
    private var paketKaporta: Boolean = false
    private var paketMotor: Boolean = false
    private var paketMekanik: Boolean = false
    private var paketObd: Boolean = false
    private var paketAirbag: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRaporOlusturGenelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gelenRandevuId = arguments?.getInt("randevuId", -1) ?: -1
        gelenRaporId = arguments?.getInt("raporId", -1) ?: -1

        spinnerlariHazirla()

        if (gelenRandevuId != -1) {
            verileriVePaketiCek()
        }


        binding.headerKaporta.setOnClickListener { toggleSection(binding.contentKaporta, binding.ivArrowKaporta) }
        binding.headerMotor.setOnClickListener { toggleSection(binding.contentMotor, binding.ivArrowMotor) }
        binding.headerMekanik.setOnClickListener { toggleSection(binding.contentMekanik, binding.ivArrowMekanik) }
        binding.headerOBD.setOnClickListener { toggleSection(binding.contentOBD, binding.ivArrowOBD) }
        binding.headerAirbag.setOnClickListener { toggleSection(binding.contentAirbag, binding.ivArrowAirbag) }

        binding.btnTumRaporuKaydet.setOnClickListener {
            tumRaporuVeritabaninaGonder()
        }
    }

    private fun toggleSection(contentView: View, arrowView: View) {
        if (contentView.visibility == View.VISIBLE) {
            contentView.visibility = View.GONE
            arrowView.animate().rotation(0f).setDuration(200).start()
        } else {
            contentView.visibility = View.VISIBLE
            arrowView.animate().rotation(180f).setDuration(200).start()
        }
    }

    private fun spinnerlariHazirla() {

        val arrKprTavan = arrayOf("Orijinal", "Boyalı", "Lokal Boyalı", "Değişen", "Cam")
        val arrKprTampon = arrayOf("Orijinal", "Boyalı", "Lokal Boyalı", "Değişen", "Plastik")
        val arrKprStandart = arrayOf("Orijinal", "Boyalı", "Lokal Boyalı", "Değişen")
        val arrKprDirek = arrayOf("Orijinal", "Boyalı", "İşlemli")
        val arrKprSase = arrayOf("Orijinal", "İşlemli")

        val arrMtrSesi = arrayOf("Normal", "Sesli/İticili", "Anormal")
        val arrMtrUfleme = arrayOf("Yok", "Hafif Üfleme", "Duman Atıyor")
        val arrMtrSivi = arrayOf("Seviyeler Tam", "Eksik/Değişim Gerekli", "Özelliğini Yitirmiş")
        val arrMtrKacak = arrayOf("Yok", "Terleme Var", "Kaçak Var")
        val arrMtrTurbo = arrayOf("Sorunsuz", "Ses/Terleme Var", "Sorunlu/Bakım Şart")
        val arrMtrSogutma = arrayOf("Sorunsuz", "Su Kaçağı Var", "Klima Soğutmuyor")
        val arrMtrKayis = arrayOf("İyi Durumda", "Çatlak/Aşınmış", "Ses Yapıyor")
        val arrMtrSanziman = arrayOf("Sorunsuz", "Vuruntulu/Sarsıntılı", "Kaçak/Sorun Var")

        val arrMekanik = arrayOf("Çok İyi", "İyi", "Orta", "Kötü/Değişmeli")

        val arrObdKmOrj = arrayOf("Orijinal", "Düşürülmüş/Müdahaleli", "Tespit Edilemedi")
        val arrObdTramer = arrayOf("Hasar Kaydı Yok", "Çarpma/Maddi Hasar", "Ağır Hasar/Pert")
        val arrObdHata = arrayOf("Sorunsuz", "Geçmiş Hata Var", "Aktif Hata Var")
        val arrObdTcm = arrayOf("Sorunsuz", "Geçmiş Hata Var", "Aktif Hata Var", "Manuel/Yok")
        val arrObdAirbag = arrayOf("Sorunsuz", "Aktif Hata Var", "Direnç Atılmış/İşlemli")
        val arrObdElek = arrayOf("Sorunsuz", "Kısmi Arızalı", "Sorunlu")

        val arrAirbag = arrayOf("Orijinal", "Değişmiş", "Onarımlı/Kaplama", "Dirençli", "Patlak", "Donanımda Yok")
        val arrKemer = arrayOf("Orijinal", "Değişmiş", "Onarımlı/Dirençli", "Patlak")


        binding.spKprTavan.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKprTavan)
        binding.spKprOnTampon.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKprTampon)
        binding.spKprArkaTampon.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKprTampon)

        val kprStandartlar = listOf(binding.spKprKaput, binding.spKprBagaj, binding.spKprSolOnCamurluk, binding.spKprSagOnCamurluk, binding.spKprSolArkaCamurluk, binding.spKprSagArkaCamurluk, binding.spKprSolOnKapi, binding.spKprSagOnKapi, binding.spKprSolArkaKapi, binding.spKprSagArkaKapi, binding.spKprSolMarspiyel, binding.spKprSagMarspiyel)
        kprStandartlar.forEach { it.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKprStandart)
        }

        val kprDirekler = listOf(binding.spKprSolOnDirek, binding.spKprSagOnDirek, binding.spKprSolOrtaDirek, binding.spKprSagOrtaDirek, binding.spKprSolArkaDirek, binding.spKprSagArkaDirek)
        kprDirekler.forEach { it.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKprDirek)
        }

        val kprSaseler = listOf(binding.spKprSolOnSase, binding.spKprSagOnSase, binding.spKprSolArkaSase, binding.spKprSagArkaSase, binding.spKprSolPodye, binding.spKprSagPodye, binding.spKprBagajHavuzu)
        kprSaseler.forEach { it.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKprSase)
        }

        binding.spMtrSesi.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrSesi)
        binding.spMtrUfleme.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrUfleme)
        binding.spMtrSivi.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrSivi)
        binding.spMtrKacak.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrKacak)
        binding.spMtrTurbo.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrTurbo)
        binding.spMtrSogutma.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrSogutma)
        binding.spMtrKayis.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrKayis)
        binding.spMtrSanziman.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMtrSanziman)

        val mekSpinnerlar = listOf(binding.spMekSolAks, binding.spMekSagAks, binding.spMekDireksiyon, binding.spMekRot, binding.spMekOnBalata, binding.spMekArkaBalata, binding.spMekOnDisk, binding.spMekArkaDisk, binding.spMekSolOnSus, binding.spMekSagOnSus, binding.spMekSolArkSus, binding.spMekSagArkSus)
        mekSpinnerlar.forEach { it.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrMekanik)
        }

        binding.spObdKmOrj.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdKmOrj)
        binding.spObdTramer.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdTramer)
        binding.spObdEcu.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdHata)
        binding.spObdTcm.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdTcm)
        binding.spObdAbs.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdHata)
        binding.spObdAirbag.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdAirbag)
        binding.spObdElektronik.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrObdElek)

        val abgSpinnerlar = listOf(binding.spAbgSurucu, binding.spAbgYolcu, binding.spAbgSurucuDiz, binding.spAbgYolcuDiz, binding.spAbgSolPerde, binding.spAbgSagPerde, binding.spAbgSurucuKoltuk, binding.spAbgYolcuKoltuk)
        abgSpinnerlar.forEach { it.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrAirbag)
        }
        binding.spAbgKemer.adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, arrKemer)
    }

    private fun verileriVePaketiCek() {
        Thread {
            val veri = DatabaseBaglantı.getRaporIcinVeri(gelenRandevuId)
            activity?.runOnUiThread {
                if (veri != null) {
                    binding.etRaporPlaka.setText(veri.plaka)

                    paketKaporta = veri.kaportaVar
                    paketMotor = veri.motorVar
                    paketMekanik = veri.mekanikVar
                    paketObd = veri.obdVar
                    paketAirbag = veri.airbagVar

                    if (!paketKaporta) binding.cardKaporta.visibility = View.GONE
                    if (!paketMotor) binding.cardMotor.visibility = View.GONE
                    if (!paketMekanik) binding.cardMekanik.visibility = View.GONE
                    if (!paketObd) binding.cardOBD.visibility = View.GONE
                    if (!paketAirbag) binding.cardAirbag.visibility = View.GONE
                }
            }
        }.start()
    }

    private fun tumRaporuVeritabaninaGonder() {
        val saseNo = binding.etRaporSase.text.toString().trim()
        val kmStr = binding.etRaporKilometre.text.toString().trim()

        if (saseNo.length != 17 || kmStr.isEmpty()) {
            Toast.makeText(requireContext(), "Şase No (17 hane) ve KM boş geçilemez!", Toast.LENGTH_SHORT).show()
            return
        }
        val km = kmStr.toIntOrNull() ?: 0

        Thread {


            if (gelenRaporId != -1) {

                if (paketKaporta) {
                    DatabaseBaglantı.raporKaportaKaydet(gelenRaporId, binding.spKprTavan.selectedItem.toString(), binding.spKprOnTampon.selectedItem.toString(),
                        binding.spKprArkaTampon.selectedItem.toString(), binding.spKprKaput.selectedItem.toString(), binding.spKprBagaj.selectedItem.toString(),
                        binding.spKprSolOnCamurluk.selectedItem.toString(), binding.spKprSagOnCamurluk.selectedItem.toString(), binding.spKprSolArkaCamurluk.selectedItem.toString(),
                        binding.spKprSagArkaCamurluk.selectedItem.toString(), binding.spKprSolOnKapi.selectedItem.toString(), binding.spKprSagOnKapi.selectedItem.toString(),
                        binding.spKprSolArkaKapi.selectedItem.toString(), binding.spKprSagArkaKapi.selectedItem.toString(), binding.spKprSolMarspiyel.selectedItem.toString(),
                        binding.spKprSagMarspiyel.selectedItem.toString(), binding.spKprSolOnDirek.selectedItem.toString(), binding.spKprSagOnDirek.selectedItem.toString(),
                        binding.spKprSolOrtaDirek.selectedItem.toString(), binding.spKprSagOrtaDirek.selectedItem.toString(), binding.spKprSolArkaDirek.selectedItem.toString(),
                        binding.spKprSagArkaDirek.selectedItem.toString(), binding.spKprSolOnSase.selectedItem.toString(), binding.spKprSagOnSase.selectedItem.toString(),
                        binding.spKprSolArkaSase.selectedItem.toString(), binding.spKprSagArkaSase.selectedItem.toString(), binding.spKprSolPodye.selectedItem.toString(),
                        binding.spKprSagPodye.selectedItem.toString(), binding.spKprBagajHavuzu.selectedItem.toString(), binding.etKaportaNot.text.toString())
                }

                if (paketMotor) {
                    DatabaseBaglantı.raporMotorKaydet(gelenRaporId, binding.spMtrSesi.selectedItem.toString(), binding.spMtrUfleme.selectedItem.toString(),
                        binding.spMtrSivi.selectedItem.toString(), binding.spMtrKacak.selectedItem.toString(), binding.spMtrTurbo.selectedItem.toString(),
                        binding.spMtrSogutma.selectedItem.toString(), binding.spMtrKayis.selectedItem.toString(), binding.spMtrSanziman.selectedItem.toString(),
                        binding.etMotorNot.text.toString())
                }

                if (paketMekanik) {
                    DatabaseBaglantı.raporMekanikKaydet(gelenRaporId, binding.spMekSolAks.selectedItem.toString(), binding.spMekSagAks.selectedItem.toString(),
                        binding.spMekDireksiyon.selectedItem.toString(), binding.spMekRot.selectedItem.toString(), binding.spMekOnBalata.selectedItem.toString(),
                        binding.spMekArkaBalata.selectedItem.toString(), binding.spMekOnDisk.selectedItem.toString(), binding.spMekArkaDisk.selectedItem.toString(),
                        binding.spMekSolOnSus.selectedItem.toString(), binding.spMekSagOnSus.selectedItem.toString(), binding.spMekSolArkSus.selectedItem.toString(),
                        binding.spMekSagArkSus.selectedItem.toString(), binding.etMekanikNot.text.toString())
                }

                if (paketObd) {
                    val bKm = binding.etObdBeyinKM.text.toString().toIntOrNull() ?: km
                    val tTut = binding.etObdTramerTutari.text.toString().toDoubleOrNull() ?: 0.0
                    DatabaseBaglantı.raporObdKaydet(gelenRaporId, km, bKm, binding.spObdKmOrj.selectedItem.toString(), binding.spObdTramer.selectedItem.toString(),
                        tTut, binding.spObdEcu.selectedItem.toString(), binding.spObdTcm.selectedItem.toString(), binding.spObdAbs.selectedItem.toString(),
                        binding.spObdAirbag.selectedItem.toString(), binding.spObdElektronik.selectedItem.toString(), binding.etObdNot.text.toString())
                }

                if (paketAirbag) {
                    DatabaseBaglantı.raporAirbagKaydet(gelenRaporId, binding.spAbgSurucu.selectedItem.toString(), binding.spAbgYolcu.selectedItem.toString(),
                        binding.spAbgSurucuDiz.selectedItem.toString(), binding.spAbgYolcuDiz.selectedItem.toString(), binding.spAbgSolPerde.selectedItem.toString(),
                        binding.spAbgSagPerde.selectedItem.toString(), binding.spAbgSurucuKoltuk.selectedItem.toString(), binding.spAbgYolcuKoltuk.selectedItem.toString(),
                        binding.spAbgKemer.selectedItem.toString(), binding.etAirbagNot.text.toString())
                }


                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Tüm Seçili Raporlar Kaydedildi!", Toast.LENGTH_LONG).show()

                    findNavController().popBackStack(com.example.otocheck.R.id.anaSayfaFragment, false)
                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Hata: Geçerli bir Rapor ID bulunamadı!", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}