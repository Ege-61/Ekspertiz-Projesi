package com.example.otocheck.Fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.Models.PaketModel
import com.example.otocheck.R
import com.example.otocheck.Adapters.SirketPaketleriAdapter
import com.example.otocheck.databinding.FragmentPaketDuzenleBinding

class PaketDuzenle_Fragment : Fragment() {

    private var _binding: FragmentPaketDuzenleBinding? = null
    private val binding get() = _binding!!

    private var aktifSirketId: Int = -1
    private var duzenlenenPaketId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaketDuzenleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGeri.setOnClickListener { findNavController().popBackStack() }

        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        aktifSirketId = sharedPref.getInt("KullaniciID", -1)

        if (aktifSirketId != -1) {
            paketleriYukle()
        }

        binding.btnPaketEkle.setOnClickListener {
            paketKaydetVeyaGuncelle()
        }
    }

    private fun paketleriYukle() {
        Thread {
            val paketler = DatabaseBaglantı.sirketPaketleriniGetir(aktifSirketId)
            activity?.runOnUiThread {
                if (paketler.isEmpty()) {
                    binding.txtPaketYok.visibility = View.VISIBLE
                    binding.rvSirketPaketleri.visibility = View.GONE
                } else {
                    binding.txtPaketYok.visibility = View.GONE
                    binding.rvSirketPaketleri.visibility = View.VISIBLE
                    binding.rvSirketPaketleri.layoutManager = LinearLayoutManager(requireContext())

                    binding.rvSirketPaketleri.adapter = SirketPaketleriAdapter(
                        paketListesi = paketler,
                        onSilClicked = { silinecekPaket -> paketSilOnay(silinecekPaket) },
                        onDuzenleClicked = { duzenlenecekPaket -> paketiFormaTasi(duzenlenecekPaket) }
                    )
                }
            }
        }.start()
    }

    // Düzenleye basınca paket yükleme kısmına bilgilerini yükleyip güncelleme moduna giriyor
    private fun paketiFormaTasi(paket: PaketModel) {
        duzenlenenPaketId = paket.paketId

        binding.etPaketAdi.setText(paket.paketAdi)
        binding.etPaketFiyati.setText(paket.fiyat.toString())

        binding.cbPaketMotor.isChecked = paket.motor
        binding.cbPaketMekanik.isChecked = paket.mekanik
        binding.cbPaketKaporta.isChecked = paket.kaporta
        binding.cbPaketAirbag.isChecked = paket.airbag
        binding.cbPaketOBD.isChecked = paket.obd


        binding.btnPaketEkle.text = "Paketi Güncelle"
        binding.btnPaketEkle.setBackgroundColor(Color.parseColor("#FF9800"))
    }

    private fun formuTemizle() {
        duzenlenenPaketId = null
        binding.etPaketAdi.text?.clear()
        binding.etPaketFiyati.text?.clear()
        binding.cbPaketMotor.isChecked = false
        binding.cbPaketMekanik.isChecked = false
        binding.cbPaketKaporta.isChecked = false
        binding.cbPaketAirbag.isChecked = false
        binding.cbPaketOBD.isChecked = false


        binding.btnPaketEkle.text = "Paketi Sisteme Ekle"
        binding.btnPaketEkle.setBackgroundColor(resources.getColor(R.color.ana_yesil, null))
    }

    private fun paketKaydetVeyaGuncelle() {
        val paketAdi = binding.etPaketAdi.text.toString().trim()
        val fiyatStr = binding.etPaketFiyati.text.toString().trim()

        if (paketAdi.isEmpty() || fiyatStr.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen paket adı ve fiyatını giriniz!", Toast.LENGTH_SHORT).show()
            return
        }

        val fiyat = fiyatStr.toDoubleOrNull()
        if (fiyat == null || fiyat <= 0) {
            Toast.makeText(requireContext(), "Geçerli bir fiyat giriniz!", Toast.LENGTH_SHORT).show()
            return
        }

        val motor = binding.cbPaketMotor.isChecked
        val mekanik = binding.cbPaketMekanik.isChecked
        val kaporta = binding.cbPaketKaporta.isChecked
        val airbag = binding.cbPaketAirbag.isChecked
        val obd = binding.cbPaketOBD.isChecked

        Thread {
            val basarili = if (duzenlenenPaketId == null) {

                DatabaseBaglantı.paketEkle(aktifSirketId, paketAdi, fiyat, motor, mekanik, kaporta, airbag, obd)
            } else {

                DatabaseBaglantı.paketGuncelle(duzenlenenPaketId!!, paketAdi, fiyat, motor, mekanik, kaporta, airbag, obd)
            }

            activity?.runOnUiThread {
                if (basarili) {
                    Toast.makeText(requireContext(), if (duzenlenenPaketId == null) "Paket eklendi!" else "Paket güncellendi!", Toast.LENGTH_SHORT).show()
                    formuTemizle()
                    paketleriYukle()
                } else {
                    Toast.makeText(requireContext(), "İşlem sırasında bir hata oluştu.", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun paketSil(paketId: Int) {
        Thread {
            val basarili = DatabaseBaglantı.paketSil(paketId)
            activity?.runOnUiThread {
                if (basarili) {
                    Toast.makeText(requireContext(), "Paket silindi.", Toast.LENGTH_SHORT).show()
                    paketleriYukle()
                } else {
                    Toast.makeText(requireContext(), "Bu pakete ait randevular olduğu için silinemiyor olabilir.", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
    private fun paketSilOnay(paket: PaketModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Paketi Sil")
            .setMessage("${paket.paketAdi} paketini silmek istediğinize emin misiniz? Bu işlem geri alınamaz.")
            .setPositiveButton("Evet, Sil") { _, _ -> paketSil(paket.paketId) }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}