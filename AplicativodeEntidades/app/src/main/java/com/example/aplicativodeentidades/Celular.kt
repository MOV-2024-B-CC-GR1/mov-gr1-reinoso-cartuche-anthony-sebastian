package com.example.aplicativodeentidades

data class Celular(
    val id: Int = 0,
    val marca: String,
    val modelo: String,
    val precio: Double,
    val fechaFabricacion: String,
    val esSmartphone: Boolean
)
