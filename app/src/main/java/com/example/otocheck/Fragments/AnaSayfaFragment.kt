package com.example.otocheck.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.R
import com.example.otocheck.Adapters.RandevuAdapter
import com.example.otocheck.Adapters.SirketRandevuAdapter
import com.example.otocheck.databinding.FragmentAnaSayfaBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnaSayfaFragment : Fragment() {

    private var _binding: FragmentAnaSayfaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnaSayfaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        val isKurumsal = sharedPref.getBoolean("isKurumsal", false)
        val userId = sharedPref.getInt("KullaniciID", -1)

        // hem şirketlerin hemde müşterilerin ana ekranını tek bir fragmenta sığdırdığımız için giriş yapan kişinin kim olduğunu öğrenip ona göre bir uı karşısına çıkıyor
        if (isKurumsal) {
            binding.panelMusteri.visibility = View.GONE
            binding.panelSirket.visibility = View.VISIBLE

            binding.btnSirketPaketler.setOnClickListener {
                findNavController().navigate(R.id.action_anaSayfaFragment_to_paketDuzenle_Fragment)
            }

            binding.btnSirketRandevular.setOnClickListener {
                findNavController().navigate(R.id.action_anaSayfaFragment_to_tumRandevular_Fragment)
            }

            binding.btnSirketYeniRapor.setOnClickListener {

                findNavController().navigate(R.id.action_anaSayfaFragment_to_tumRandevular_Fragment)
            }


            binding.btnSirketIstatistik.setOnClickListener {
                findNavController().navigate(R.id.action_anaSayfaFragment_to_sirketIstatistik_Fragment)
            }

            //günün randevularını ekrana veriyor
            sirketRandevulariniYukle(userId)

        } else {
            // müşteri kısmı
            binding.panelMusteri.visibility = View.VISIBLE
            binding.panelSirket.visibility = View.GONE

            randevulariYukle(userId)
        }


        Thread {
            val isim = DatabaseBaglantı.kullaniciIsmiGetir(userId, isKurumsal)
            activity?.runOnUiThread {
                binding.KarsilamaText.text = "$isim"
            }
        }.start()


        binding.btnSolMenu.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menu.add("Ayarlar")
            popup.menu.add("ÇIKIŞ YAP")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "ÇIKIŞ YAP" -> {
                        sharedPref.edit().clear().apply()
                        Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_giris_Fragment)
                        true
                    }
                    "Ayarlar" -> {
                        // İŞTE BURAYI AKTİF ETTİK!
                        findNavController().navigate(R.id.action_anaSayfaFragment_to_ayarlar_Fragment)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }


        binding.btnAramaYap.setOnClickListener {
            val aramaMetni = binding.AramaEditText.text.toString().trim()

            if (aramaMetni.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen Plaka veya Şase No girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //burada yazılan metni alıp ona göre listeyi veriyoruz
            val bundle = Bundle().apply {
                putString("aramaMetni", aramaMetni)
            }
            findNavController().navigate(R.id.action_anaSayfaFragment_to_aramaSonuclari_Fragment, bundle)
        }

        binding.btnMusteriGecmis.setOnClickListener {
            findNavController().navigate(R.id.action_anaSayfaFragment_to_gecmisRaporlar_Fragment)
        }


        binding.btnMusteriRandevuAl.setOnClickListener {
            findNavController().navigate(R.id.action_anaSayfaFragment_to_randevuAlma_Fragment)
        }
    }
    // BUGÜNÜN RANDEVULARINI ÇEKEN FONKSİYON
    private fun sirketRandevulariniYukle(sirketId: Int) {

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val bugunTarih = sdf.format(Date())


        Thread {
            val bugunkuRandevular = DatabaseBaglantı.getSirketBugununRandevulari(sirketId, bugunTarih)


            activity?.runOnUiThread {
                //randevu yoksa textview ekrana geliyor ama eğer varsa var olan randevular gözüküyor
                if (bugunkuRandevular.isEmpty()) {

                    binding.txtSirketRandevuYok.visibility = View.VISIBLE
                    binding.rvSirketRandevular.visibility = View.GONE
                } else {

                    binding.txtSirketRandevuYok.visibility = View.GONE
                    binding.rvSirketRandevular.visibility = View.VISIBLE

                    binding.rvSirketRandevular.layoutManager = LinearLayoutManager(requireContext())

                    binding.rvSirketRandevular.adapter =
                        SirketRandevuAdapter(bugunkuRandevular) { secilenRandevu ->


                            //rapor doldurmaya giderken randevu ıd yi yanımıza alıyoruz
                            val bundle = Bundle().apply {
                                putInt("randevuId", secilenRandevu.randevuId)
                            }


                            findNavController().navigate(
                                R.id.action_anaSayfaFragment_to_raporOlusturAna_Fragment,
                                bundle
                            )
                        }
                }
            }
        }.start()
    }

    private fun randevulariYukle(userId: Int) {
        Thread {
            val randevuListesi = DatabaseBaglantı.randevulariGetir(userId)

            activity?.runOnUiThread {
                if (randevuListesi.isEmpty()) {
                    binding.txtMusteriRandevuYok.visibility = View.VISIBLE
                    binding.rvMusteriRandevular.visibility = View.GONE
                } else {
                    binding.txtMusteriRandevuYok.visibility = View.GONE
                    binding.rvMusteriRandevular.visibility = View.VISIBLE

                    binding.rvMusteriRandevular.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.rvMusteriRandevular.adapter = RandevuAdapter(randevuListesi)
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}