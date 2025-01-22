package com.example.ccgr12024b_asrc

data class Aplicativo(
    val id: Int,
    val nombre: String,
    val version: String,
    val tamanoMB: Double,
    val esgratis: Boolean,
    val categoria: String,
    val celularId: Int // Relación con el celular
)
