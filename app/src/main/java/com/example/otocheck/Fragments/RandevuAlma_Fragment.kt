package com.example.otocheck.Fragments

import android.R
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.Adapters.Randevu_Alma_Adapter
import com.example.otocheck.databinding.FragmentRandevuAlmaBinding
import java.util.Calendar

class RandevuAlma_Fragment : Fragment() {

    private var _binding: FragmentRandevuAlmaBinding? = null
    private val binding get() = _binding!!

    private var secilenTarih = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandevuAlmaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGeri.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.tvTarihSec.setOnClickListener {
            val c = Calendar.getInstance()
            val datePicker = DatePickerDialog(requireContext(), { _, yil, ay, gun ->
                val formatliAy = if (ay + 1 < 10) "0${ay + 1}" else "${ay + 1}"
                val formatliGun = if (gun < 10) "0$gun" else "$gun"

                secilenTarih = "$yil-$formatliAy-$formatliGun"
                binding.tvTarihSec.text = "$formatliGun/$formatliAy/$yil"
                binding.tvTarihSec.setTextColor(Color.BLACK)
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }


        Thread {
            val sehirler = DatabaseBaglantı.sehirleriGetir()
            activity?.runOnUiThread {
                if (sehirler.isNotEmpty()) {
                    val sehirAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_dropdown_item,
                        sehirler
                    )
                    binding.spinnerAramaSehir.adapter = sehirAdapter

                    binding.spinnerAramaSehir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            Thread {
                                val ilceler = DatabaseBaglantı.ilceleriGetir(sehirler[position].id)
                                activity?.runOnUiThread {
                                    val ilceAdapter = ArrayAdapter(
                                        requireContext(),
                                        R.layout.simple_spinner_dropdown_item,
                                        ilceler
                                    )
                                    binding.spinnerAramaIlce.adapter = ilceAdapter
                                }
                            }.start()
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }
        }.start()


        binding.btnFiltreleUygula.setOnClickListener {
            if (secilenTarih.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen önce bir com.example.otocheck.Models.Randevu Tarihi seçiniz!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val secilenSehirAd = binding.spinnerAramaSehir.selectedItem?.toString() ?: ""
            val secilenIlceAd = binding.spinnerAramaIlce.selectedItem?.toString() ?: ""
            val maxFiyat = binding.etAramaMaxFiyat.text.toString().toDoubleOrNull()


            val motorIstiyor = binding.cbMotor.isChecked
            val mekanikIstiyor = binding.cbMekanik.isChecked
            val kaportaIstiyor = binding.cbKaporta.isChecked
            val airbagIstiyor = binding.cbAirbag.isChecked
            val obdIstiyor = binding.cbOBD.isChecked

            val siralamaOlcutu = binding.spinnerSiralama.selectedItem?.toString() ?: "Fiyat (Artan)"

            Thread {
                val gercekSonucListesi = DatabaseBaglantı.ekspertizFiltrele(
                    secilenSehirAd,
                    secilenIlceAd,
                    maxFiyat,
                    motorIstiyor,
                    mekanikIstiyor, // SQL Prosedürüne Gönderiliyor
                    kaportaIstiyor,
                    airbagIstiyor,
                    obdIstiyor,
                    siralamaOlcutu
                )
                Log.d("OtoCheckTest", "Gelen paket sayısı: ${gercekSonucListesi.size}")
                activity?.runOnUiThread {
                    binding.tvSonucBaslik.visibility = View.VISIBLE
                    binding.rvAramaSonuclari.layoutManager = LinearLayoutManager(requireContext())

                    binding.rvAramaSonuclari.adapter = Randevu_Alma_Adapter(
                        gercekSonucListesi,
                        secilenTarih
                    ) { sId, pId, tarih, saat, fiyat ->
                        val bundle = Bundle().apply {
                            putInt("sirketId", sId)
                            putInt("paketId", pId)
                            putString("tarih", tarih)
                            putString("saat", saat)
                            putDouble("fiyat", fiyat)
                        }
                        findNavController().navigate(
                            com.example.otocheck.R.id.action_randevuAlma_Fragment_to_aracBilgileri_Fragment,
                            bundle
                        )
                    }

                    if (gercekSonucListesi.isEmpty()) {
                        Toast.makeText(requireContext(), "Kriterlere uygun paket bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}