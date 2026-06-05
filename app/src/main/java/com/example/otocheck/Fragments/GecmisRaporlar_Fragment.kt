package com.example.otocheck.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.Adapters.GecmisRaporAdapter
import com.example.otocheck.R
import com.example.otocheck.databinding.FragmentGecmisRaporlarBinding

class GecmisRaporlar_Fragment : Fragment() {

    private var _binding: FragmentGecmisRaporlarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGecmisRaporlarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        val musteriId = sharedPref.getInt("KullaniciID", -1)

        if (musteriId != -1) {
            Thread {
                val gecmisRaporlar = DatabaseBaglantı.gecmisRaporlariGetir(musteriId)

                activity?.runOnUiThread {
                    if (gecmisRaporlar.isEmpty()) {
                        binding.txtGecmisRaporYok.visibility = View.VISIBLE
                        binding.rvGecmisRaporlar.visibility = View.GONE
                    } else {
                        binding.txtGecmisRaporYok.visibility = View.GONE
                        binding.rvGecmisRaporlar.visibility = View.VISIBLE

                        binding.rvGecmisRaporlar.layoutManager =
                            LinearLayoutManager(requireContext())
                        binding.rvGecmisRaporlar.adapter =
                            GecmisRaporAdapter(gecmisRaporlar) { secilenRapor ->


                                val bundle = Bundle().apply {
                                    putInt("raporId", secilenRapor.raporId)
                                }


                                findNavController().navigate(
                                    R.id.action_gecmisRaporlar_Fragment_to_raporDetay_Fragment,
                                    bundle
                                )
                            }
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