package com.example.otocheck.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.otocheck.Adapters.SirketRandevuAdapter
import com.example.otocheck.DatabaseBaglantı
import com.example.otocheck.R
import com.example.otocheck.databinding.FragmentTumRandevularBinding

class TumRandevular_Fragment : Fragment() {

    private var _binding: FragmentTumRandevularBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTumRandevularBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGeri.setOnClickListener {
            findNavController().popBackStack()
        }

        val sharedPref = requireActivity().getSharedPreferences("OtoCheckPrefs", Context.MODE_PRIVATE)
        val sirketId = sharedPref.getInt("KullaniciID", -1)

        if (sirketId != -1) {
            gelecekRandevulariYukle(sirketId)
        }
    }

    private fun gelecekRandevulariYukle(sirketId: Int) {
        Thread {

            val randevular = DatabaseBaglantı.getSirketGelecekRandevulari(sirketId)

            activity?.runOnUiThread {
                if (randevular.isEmpty()) {
                    binding.txtTumRandevularYok.visibility = View.VISIBLE
                    binding.rvTumRandevular.visibility = View.GONE
                } else {
                    binding.txtTumRandevularYok.visibility = View.GONE
                    binding.rvTumRandevular.visibility = View.VISIBLE

                    binding.rvTumRandevular.layoutManager = LinearLayoutManager(requireContext())


                    binding.rvTumRandevular.adapter =
                        SirketRandevuAdapter(randevular) { secilenRandevu ->


                            val bundle = Bundle().apply {
                                putInt("randevuId", secilenRandevu.randevuId)
                            }


                            findNavController().navigate(
                                R.id.action_tumRandevular_Fragment_to_raporOlusturAna_Fragment,
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