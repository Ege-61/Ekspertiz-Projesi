package com.example.otocheck.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.R
import com.example.otocheck.databinding.FragmentAracBilgileriBinding

class AracBilgileri_Fragment : Fragment() {

    private var _binding: FragmentAracBilgileriBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAracBilgileriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //geri butonu
        binding.btnAracGeri.setOnClickListener {
            findNavController().popBackStack()
        }


        binding.btnRandevuyuTamamla.setOnClickListener {
            val plaka = binding.etAracPlaka.text.toString()
            val marka = binding.etAracMarka.text.toString()
            val model = binding.etAracModel.text.toString()
            val yilStr = binding.etAracYil.text.toString()

            if (plaka.isEmpty() || marka.isEmpty() || model.isEmpty() || yilStr.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Lütfen tüm araç bilgilerini doldurun!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }


            val sirketId = arguments?.getInt("sirketId") ?: 0
            val paketId = arguments?.getInt("paketId") ?: 0
            val tarih = arguments?.getString("tarih") ?: ""
            val saat = arguments?.getString("saat") ?: ""
            val fiyat = arguments?.getDouble("fiyat") ?: 0.0

            //bu bilgileri çekiyoruz ana sayfa da da gerekli
            val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
            val musteriId = sharedPref.getInt("KullaniciID", -1)


            if (musteriId == -1) {
                Toast.makeText(requireContext(), "Oturum hatası! Lütfen tekrar giriş yapın.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Thread {
                val basariliMi = DatabaseBaglantı.randevuOlustur(
                    musteriId, sirketId, paketId, tarih, saat, fiyat,
                    plaka, marka, model, yilStr.toInt()
                )

                activity?.runOnUiThread {
                    if (basariliMi) {
                        Toast.makeText(
                            requireContext(),
                            "Randevunuz Başarıyla Oluşturuldu!",
                            Toast.LENGTH_LONG
                        ).show()

                        findNavController().navigate(R.id.action_aracBilgileri_Fragment_to_anaSayfaFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "com.example.otocheck.Models.Randevu oluşturulamadı. Lütfen tekrar deneyin.",
                            Toast.LENGTH_SHORT
                        ).show()
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