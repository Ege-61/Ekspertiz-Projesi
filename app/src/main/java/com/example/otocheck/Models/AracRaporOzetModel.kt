package com.example.otocheck.Models

data class AracRaporOzetModel(
    val raporId: Int,
    val plaka: String,
    val saseNo: String,
    val marka: String,
    val model: String,
    val kilometre: Int,
    val raporTarihi: String,
    val sirketAdi: String,
    val paketAdi: String
)