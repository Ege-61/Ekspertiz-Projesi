package com.example.otocheck.Models

data class SirketRandevuModel(
    val randevuId: Int,
    val sirketId: Int,
    val randevuTarihi: String,
    val randevuSaati: String,
    val paketAdi: String,
    val plaka: String,
    val marka: String,
    val model: String,
    val yil: Int,
    val durum: String
)