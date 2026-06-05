package com.example.otocheck.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.otocheck.Adapters.AracRaporOzetAdapter
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.R
import com.example.otocheck.databinding.FragmentAramaSonuclariBinding

class AramaSonuclari_Fragment : Fragment() {

    private var _binding: FragmentAramaSonuclariBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAramaSonuclariBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aramaMetni = arguments?.getString("aramaMetni", "") ?: ""
        binding.txtBaslikPlaka.text = "$aramaMetni - Rapor Geçmişi"

        Thread {
            val sonuclar = DatabaseBaglantı.plakaVeyaSaseyeGoreRaporlariGetir(aramaMetni)

            activity?.runOnUiThread {
                if (sonuclar.isEmpty()) {
                    binding.txtAramaBos.visibility = View.VISIBLE
                    binding.rvAramaSonuclari.visibility = View.GONE
                } else {
                    binding.txtAramaBos.visibility = View.GONE
                    binding.rvAramaSonuclari.visibility = View.VISIBLE

                    binding.rvAramaSonuclari.adapter =
                        AracRaporOzetAdapter(sonuclar) { secilenRapor ->

                            // bir rapora tıklandığında detaylı görüntüleme ekranına gidiyoruz
                            val bundle = Bundle().apply {
                                putInt("raporId", secilenRapor.raporId)
                            }


                            findNavController().navigate(
                                R.id.action_aramaSonuclari_Fragment_to_raporDetay_Fragment,
                                bundle
                            )
                        }
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}