package com.example.otocheck.Models

data class EkspertizAramaModel(
    val sirketId: Int,
    val paketId: Int,
    val sirketAdi: String,
    val paketAdi: String,
    val konum: String,
    val fiyat: Double,
    val icerikOzet: String,
    var isExpanded: Boolean = false,
    var secilenSaat: String = "",
    val logoBase64: String? = null
)