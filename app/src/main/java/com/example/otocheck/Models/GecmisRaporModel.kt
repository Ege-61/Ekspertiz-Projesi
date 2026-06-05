package com.example.otocheck.Models

data class GecmisRaporModel(
    val randevuId: Int,
    val raporId: Int,
    val sirketAdi: String,
    val paketAdi: String,
    val tarih: String,
    val fiyat: Double,
    val plaka: String,
    val marka: String,
    val model: String
)