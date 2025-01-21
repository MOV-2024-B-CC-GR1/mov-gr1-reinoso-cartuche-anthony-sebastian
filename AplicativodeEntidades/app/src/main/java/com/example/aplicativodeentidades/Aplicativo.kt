package com.example.aplicativodeentidades

data class Aplicativo(
    val id: Int = 0,
    val nombre: String,
    val version: String,
    val pesoMb: Double,
    val esGratuito: Boolean,
    val categoria: String,
    val celularId: Int
)
