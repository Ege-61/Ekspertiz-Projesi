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
import com.example.otocheck.databinding.FragmentRaporOlusturAnaBinding

class RaporOlusturAna_Fragment : Fragment() {

    private var _binding: FragmentRaporOlusturAnaBinding? = null
    private val binding get() = _binding!!
    private var gelenRandevuId: Int = -1
    private var aracYili: Int = 2000
    private var paketMotor: Boolean = false
    private var paketMekanik: Boolean = false
    private var paketKaporta: Boolean = false
    private var paketAirbag: Boolean = false
    private var paketObd: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRaporOlusturAnaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        gelenRandevuId = arguments?.getInt("randevuId", -1) ?: -1

        spinnerlariDoldur()

        if (gelenRandevuId != -1) {
            aracBilgileriniOtomatikDoldur()
        } else {
            Toast.makeText(requireContext(), "Hata: com.example.otocheck.Models.Randevu bulunamadı!", Toast.LENGTH_SHORT).show()
            binding.tvPaketBilgi.text = "com.example.otocheck.Models.Randevu Hatası - ID Alınamadı"
        }

        binding.btnRaporAnaKaydet.setOnClickListener {
            anaRaporuSistemeKaydet()
        }
    }

    private fun spinnerlariDoldur() {
        val yakitTipleri = arrayOf("Benzin", "Dizel", "LPG", "Hibrit", "Elektrik")
        val vitesTipleri = arrayOf("Manuel", "Otomatik", "Yarı Otomatik")

        val yakitAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, yakitTipleri)
        val vitesAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, vitesTipleri)

        binding.spinnerYakit.adapter = yakitAdapter
        binding.spinnerVites.adapter = vitesAdapter
    }

    private fun aracBilgileriniOtomatikDoldur() {
        Thread {

            val veri = DatabaseBaglantı.getRaporIcinVeri(gelenRandevuId)

            activity?.runOnUiThread {
                if (veri != null) {
                    //randevu oluştururken alınan veriileri ekrana aktarıyoruz
                    binding.etRaporPlaka.setText(veri.plaka)
                    binding.etRaporMarka.setText(veri.marka)
                    binding.etRaporModel.setText(veri.model)

                    binding.tvPaketBilgi.text = "Müşteri: ${veri.musteriAdi}\nSeçilen Paket: ${veri.paketAdi}"


                    aracYili = veri.yil
                    paketMotor = veri.motorVar
                    paketMekanik = veri.mekanikVar
                    paketKaporta = veri.kaportaVar
                    paketAirbag = veri.airbagVar
                    paketObd = veri.obdVar

                } else {
                    Toast.makeText(requireContext(), "Araç bilgileri SQL'den çekilemedi!", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun anaRaporuSistemeKaydet() {
        val plaka = binding.etRaporPlaka.text.toString()
        val marka = binding.etRaporMarka.text.toString()
        val model = binding.etRaporModel.text.toString()

        val saseNo = binding.etRaporSase.text.toString().trim()
        val kmStr = binding.etRaporKilometre.text.toString().trim()
        val yakit = binding.spinnerYakit.selectedItem.toString()
        val vites = binding.spinnerVites.selectedItem.toString()


        if (saseNo.length != 17) {
            Toast.makeText(requireContext(), "Şase numarası tam 17 hane olmalıdır!", Toast.LENGTH_SHORT).show()
            return
        }
        if (kmStr.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen güncel kilometreyi giriniz!", Toast.LENGTH_SHORT).show()
            return
        }

        val km = kmStr.toIntOrNull() ?: 0


        Thread {

            val olusanRaporId = DatabaseBaglantı.raporAnaKaydet(
                gelenRandevuId, plaka, saseNo, km, marka, model, aracYili, yakit, vites
            )

            activity?.runOnUiThread {
                if (olusanRaporId != -1) {
                    Toast.makeText(requireContext(), "Ana Rapor Oluşturuldu! (ID: $olusanRaporId)", Toast.LENGTH_SHORT).show()


                    val bundle = Bundle().apply {
                        putInt("randevuId", gelenRandevuId)
                        putInt("raporId", olusanRaporId)
                    }


                    findNavController().navigate(com.example.otocheck.R.id.action_raporOlusturAna_Fragment_to_raporOlusturGenel_Fragment, bundle)

                } else {
                    Toast.makeText(requireContext(), "SQL Kayıt Hatası! Bilgileri kontrol edin.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}