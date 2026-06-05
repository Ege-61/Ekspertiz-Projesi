package com.example.otocheck.Models

data class RaporIcinVeriModel(
    val randevuId: Int,
    val plaka: String,
    val marka: String,
    val model: String,
    val yil: Int,
    val musteriAdi: String,
    val paketAdi: String,
    val motorVar: Boolean,
    val mekanikVar: Boolean,
    val kaportaVar: Boolean,
    val airbagVar: Boolean,
    val obdVar: Boolean
)