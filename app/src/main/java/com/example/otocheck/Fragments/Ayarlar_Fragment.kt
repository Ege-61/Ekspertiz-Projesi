package com.example.otocheck.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.R
import com.example.otocheck.databinding.FragmentAyarlarBinding

class Ayarlar_Fragment : Fragment() {

    private var _binding: FragmentAyarlarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAyarlarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("KullaniciID", -1)
        val isKurumsal = sharedPref.getBoolean("isKurumsal", false)

        // profil bilgilerini ekrana verme
        Thread {
            val profil = DatabaseBaglantı.kullaniciProfilBilgileriniGetir(userId, isKurumsal)

            activity?.runOnUiThread {
                if (profil.isNotEmpty()) {
                    binding.tvAyarlarIsim.text = profil["Isim"]
                    binding.tvAyarlarTelefon.text = profil["Telefon"]
                    binding.tvAyarlarMail.text = profil["Email"]
                } else {
                    binding.tvAyarlarIsim.text = "Kullanıcı Bulunamadı"
                    binding.tvAyarlarTelefon.text = "-"
                    binding.tvAyarlarMail.text = "-"
                }
            }
        }.start()

        //şifre ve bilgileri güncelleme sonra eklenicek!
        binding.btnBilgileriGuncelle.setOnClickListener {
            Toast.makeText(requireContext(), "Bu özellik şu an yapım aşamasındadır (v1.1 Güncellemesi).", Toast.LENGTH_SHORT).show()
        }

        binding.btnSifreDegistir.setOnClickListener {
            Toast.makeText(requireContext(), "Şifre değiştirme modülü güvenlik nedeniyle şu an bakımdadır.", Toast.LENGTH_SHORT).show()
        }


        //tema seçimi
        val secilenTema = sharedPref.getInt("TemaAyari", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)


        binding.rgTemaSecimi.setOnCheckedChangeListener(null)

        when (secilenTema) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.rgTemaSecimi.check(R.id.rbAcikMod)
            AppCompatDelegate.MODE_NIGHT_YES -> binding.rgTemaSecimi.check(R.id.rbKoyuMod)
            else -> binding.rgTemaSecimi.check(R.id.rbSistemTemasi)
        }

        binding.rgTemaSecimi.setOnCheckedChangeListener { _, checkedId ->
            val yeniTema = when (checkedId) {
                R.id.rbAcikMod -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.rbKoyuMod -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            if (yeniTema != secilenTema) {
                sharedPref.edit().putInt("TemaAyari", yeniTema).apply()
                AppCompatDelegate.setDefaultNightMode(yeniTema)
            }
        }
        //çıkış
        binding.btnAyarlarCikis.setOnClickListener {

            sharedPref.edit().clear().apply()
            Toast.makeText(requireContext(), "Başarıyla çıkış yapıldı.", Toast.LENGTH_SHORT).show()


            findNavController().navigate(R.id.action_ayarlar_Fragment_to_giris_Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}