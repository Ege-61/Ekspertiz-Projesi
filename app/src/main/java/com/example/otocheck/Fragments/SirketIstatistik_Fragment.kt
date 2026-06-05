package com.example.otocheck.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.databinding.FragmentSirketIstatistikBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SirketIstatistik_Fragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSirketIstatistikBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSirketIstatistikBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        val sirketId = sharedPref.getInt("KullaniciID", -1)

        if (sirketId != -1) {
            istatistikleriYukle(sirketId)
        } else {
            Toast.makeText(requireContext(), "Hata: Şirket bilgisi bulunamadı!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun istatistikleriYukle(sirketId: Int) {
        Thread {

            val istatistikler = DatabaseBaglantı.sirketIstatistikGetir(sirketId)

            activity?.runOnUiThread {
                if (istatistikler.isNotEmpty()) {

                    binding.tvBugunRandevu.text = istatistikler["BugunRandevu"]
                    binding.tvYarinRandevu.text = istatistikler["YarinRandevu"]


                    binding.tvGunlukKazanc.text = "${istatistikler["GunlukKazanc"]} ₺"

                    binding.tvEnCokSatanPaket.text = istatistikler["EnCokSatanPaket"]
                } else {
                    Toast.makeText(requireContext(), "İstatistikler yüklenemedi.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}