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
import com.example.otocheck.databinding.FragmentGirisBinding

class Giris_Fragment : Fragment() {

    private var _binding: FragmentGirisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGirisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        val isLogged = sharedPref.getBoolean("IsLogged", false)

        if (isLogged) {
            findNavController().navigate(R.id.action_giris_Fragment_to_anaSayfaFragment)
            return
        }

        binding.KayitOltxtButton.setOnClickListener {

            findNavController().navigate(R.id.action_giris_Fragment_to_kayit_Fragment)
        }


        binding.girisbutton.setOnClickListener {
            val email = binding.EmailEditText.text.toString().trim()
            val sifre = binding.PasswordEditText.text.toString().trim()

            if (email.isEmpty() || sifre.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldur!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val isKurumsal = binding.toggleGirisTipi.checkedButtonId == R.id.btnKurumsal

            Thread {

                val gelenID = DatabaseBaglantı.kullaniciGiris(email, sifre, isKurumsal)

                activity?.runOnUiThread {
                    if (gelenID > 0) {
                        Toast.makeText(requireContext(), "Giriş Başarılı!", Toast.LENGTH_LONG).show()

                        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()


                        editor.putInt("KullaniciID", gelenID)
                        editor.putBoolean("isKurumsal", isKurumsal)
                        editor.putBoolean("IsLogged", true)
                        editor.apply()

                        findNavController().navigate(R.id.action_giris_Fragment_to_anaSayfaFragment)
                    } else {
                        Toast.makeText(requireContext(), "Hatalı E-posta veya Şifre!", Toast.LENGTH_LONG).show()
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