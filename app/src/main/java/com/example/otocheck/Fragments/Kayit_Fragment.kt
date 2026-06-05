package com.example.otocheck.Fragments

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.databinding.FragmentKayitBinding
import java.io.ByteArrayOutputStream

class Kayit_Fragment : Fragment() {

    private var _binding: FragmentKayitBinding? = null
    private val binding get() = _binding!!

    private var secilenLogoYolu: String = ""

    //bu kısım normalde galeri izni almamız falan gerekiyor ama bu kod ile o uzun uzun izni almak yerine direk galeriye kullanıcıyı eriştirebliyoruz
    private val galeriAcici = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            secilenLogoYolu = uri.toString()
            binding.SecilenLogoImageView.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKayitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Thread {
            val sehirler = DatabaseBaglantı.sehirleriGetir()

            activity?.runOnUiThread {
                if (sehirler.isNotEmpty()) {
                    //xml de seçiniz yazısı sseçilmiş gibi olduğu için koyu renk görünüyordu ve hoş değil bunu düzeltmek için yazılmış kod:
                    val sehirAdapter = object : ArrayAdapter<DatabaseBaglantı.Sehir>(requireContext(), R.layout.simple_spinner_dropdown_item, sehirler) {
                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent) as TextView
                            view.setTextColor(if (position == 0) Color.parseColor("#808080") else Color.BLACK)
                            return view
                        }
                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                            val view = super.getDropDownView(position, convertView, parent) as TextView
                            view.setTextColor(if (position == 0) Color.parseColor("#808080") else Color.BLACK)
                            return view
                        }
                    }

                    binding.spinnerSehir.adapter = sehirAdapter

                    binding.spinnerSehir.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val secilenSehir = sehirler[position]

                            Thread {
                                val ilceler = DatabaseBaglantı.ilceleriGetir(secilenSehir.id)
                                activity?.runOnUiThread {

                                    val ilceAdapter = object : ArrayAdapter<DatabaseBaglantı.Ilce>(requireContext(), R.layout.simple_spinner_dropdown_item, ilceler) {
                                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                            val view = super.getView(position, convertView, parent) as TextView
                                            view.setTextColor(if (position == 0) Color.parseColor("#808080") else Color.BLACK)
                                            return view
                                        }
                                        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                            val view = super.getDropDownView(position, convertView, parent) as TextView
                                            view.setTextColor(if (position == 0) Color.parseColor("#808080") else Color.BLACK)
                                            return view
                                        }
                                    }

                                    binding.spinnerIlce.adapter = ilceAdapter
                                }
                            }.start()
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }
        }.start()


        binding.GirisYaptxtButton.setOnClickListener {
            findNavController().navigate(com.example.otocheck.R.id.action_kayit_Fragment_to_giris_Fragment)
        }

        binding.btnLogoSec.setOnClickListener {
            galeriAcici.launch("image/*")
        }

        binding.toggleKayitTipi.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == com.example.otocheck.R.id.btnBireyselKayit) {
                    binding.bireyselAlanlar.visibility = View.VISIBLE
                    binding.kurumsalAlanlar.visibility = View.GONE
                } else if (checkedId == com.example.otocheck.R.id.btnKurumsalKayit) {
                    binding.bireyselAlanlar.visibility = View.GONE
                    binding.kurumsalAlanlar.visibility = View.VISIBLE
                }
            }
        }


        binding.kayitbutton.setOnClickListener {
            val isKurumsal = binding.toggleKayitTipi.checkedButtonId == com.example.otocheck.R.id.btnKurumsalKayit

            val telefon = binding.TelefonEditText.text.toString().trim()
            val email = binding.EmailEditText.text.toString().trim()
            val sifre = binding.PasswordEditText.text.toString().trim()

            var hataVar = false

            if (telefon.isEmpty() || telefon.length != 11 || !telefon.startsWith("0")) {
                binding.TelefonEditText.error = "Telefon 0 ile başlamalı ve 11 hane olmalı!"
                hataVar = true
            }
            if (email.isEmpty() || !email.contains("@")) {
                binding.EmailEditText.error = "Geçerli bir E-posta adresi giriniz!"
                hataVar = true
            }
            if (sifre.isEmpty() || sifre.length < 6) {
                binding.PasswordEditText.error = "Şifre en az 6 karakter olmalı!"
                hataVar = true
            }

            if (!isKurumsal) {
                val adSoyad = binding.AdSoyadEditText.text.toString().trim()
                if (adSoyad.isEmpty()) {
                    binding.AdSoyadEditText.error = "Ad Soyad boş bırakılamaz!"
                    hataVar = true
                }

                if (!hataVar) {
                    Thread {
                        val basariliMi = DatabaseBaglantı.musteriKayit(adSoyad, telefon, email, sifre)
                        activity?.runOnUiThread {
                            if (basariliMi) {
                                Toast.makeText(requireContext(), "Kayıt Başarılı!", Toast.LENGTH_LONG).show()
                                findNavController().navigate(com.example.otocheck.R.id.action_kayit_Fragment_to_giris_Fragment)
                            } else {
                                Toast.makeText(requireContext(), "Kayıt Başarısız! E-posta veya Telefon kullanılıyor olabilir.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }.start()
                }
            } else {
                val sirketAdi = binding.SirketAdiEditText.text.toString().trim()
                val adres = binding.AdresEditText.text.toString().trim()
                val vkn = binding.VknEditText.text.toString().trim()
                val tse = binding.TseEditText.text.toString().trim()

                val secilenSehirObj = binding.spinnerSehir.selectedItem as? DatabaseBaglantı.Sehir
                val secilenIlceObj = binding.spinnerIlce.selectedItem as? DatabaseBaglantı.Ilce

                val sehir = secilenSehirObj?.ad ?: ""
                val ilce = secilenIlceObj?.ad ?: ""

                if (secilenLogoYolu.isEmpty()) {
                    Toast.makeText(requireContext(), "Lütfen Şirket Logosu seçiniz!", Toast.LENGTH_SHORT).show()
                    hataVar = true
                }
                if (sirketAdi.isEmpty()) { binding.SirketAdiEditText.error = "Şirket Adı boş olamaz!"; hataVar = true }


                if (sehir == "Şehir Seçiniz..." || sehir.isEmpty()) {
                    val errorText = binding.spinnerSehir.selectedView as? TextView
                    errorText?.error = "Şehir seçimi zorunludur!"
                    hataVar = true
                }
                if (ilce == "İlçe Seçiniz..." || ilce.isEmpty()) {
                    val errorText = binding.spinnerIlce.selectedView as? TextView
                    errorText?.error = "İlçe seçimi zorunludur!"
                    hataVar = true
                }


                if (adres.isEmpty()) { binding.AdresEditText.error = "Adres boş olamaz!"; hataVar = true }
                if (vkn.isEmpty() || vkn.length != 11) { binding.VknEditText.error = "VKN 11 haneli olmalıdır!"; hataVar = true }
                if (tse.isEmpty()) { binding.TseEditText.error = "TSE No boş olamaz!"; hataVar = true }

                if (!hataVar) {
                    Thread {
                        val logoBase64Metni = uriToBase64(Uri.parse(secilenLogoYolu))
                        val basariliMi = DatabaseBaglantı.sirketKayit(sirketAdi, sehir, ilce, adres, vkn, tse, telefon, email, sifre, logoBase64Metni)
                        activity?.runOnUiThread {
                            if (basariliMi) {
                                Toast.makeText(requireContext(), "Kurumsal Kayıt Başarılı!", Toast.LENGTH_LONG).show()
                                findNavController().navigate(com.example.otocheck.R.id.action_kayit_Fragment_to_giris_Fragment)
                            } else {
                                Toast.makeText(requireContext(), "Kayıt Başarısız! Bilgileri kontrol edin.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }.start()
                }
            }
        }
    }

    private fun uriToBase64(uri: Uri): String {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val kucukBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("OtoCheckTest", "Base64 Çevirme Hatası: ${e.message}")
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}