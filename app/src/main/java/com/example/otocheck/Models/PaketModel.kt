package com.example.otocheck.Models

data class PaketModel(
    val paketId: Int,
    val sirketId: Int,
    val paketAdi: String,
    val fiyat: Double,
    val motor: Boolean,
    val mekanik: Boolean,
    val kaporta: Boolean,
    val airbag: Boolean,
    val obd: Boolean
)