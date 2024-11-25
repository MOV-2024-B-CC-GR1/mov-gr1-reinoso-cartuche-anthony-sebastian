package org.example

import java.io.File

data class Aplicativo(
    val nombre: String,
    val version: String,
    val pesoMb: Double,
    val esGratuito: Boolean,
    val categoria: String
) {
    fun toFileString(): String = "$nombre,$version,$pesoMb,$esGratuito,$categoria"

    companion object {
        fun fromFileString(line: String): Aplicativo {
            val parts = line.split(",")
            return Aplicativo(
                parts[0],
                parts[1],
                parts[2].toDouble(),
                parts[3].toBoolean(),
                parts[4]
            )
        }
    }
}

data class Celular(
    var marca: String,
    var modelo: String,
    var precio: Double,
    var fechaFabricacion: String,
    var esSmartphone: Boolean,
    val aplicativos: MutableList<Aplicativo> = mutableListOf()
) {
    fun toFileString(): String {
        val apps = aplicativos.joinToString("|") { it.toFileString() }
        return "$marca,$modelo,$precio,$fechaFabricacion,$esSmartphone;$apps"
    }

    companion object {
        fun fromFileString(line: String): Celular {
            val parts = line.split(";")
            val celularData = parts[0].split(",")
            val aplicativos = if (parts.size > 1 && parts[1].isNotEmpty()) {
                parts[1].split("|").map { Aplicativo.fromFileString(it) }.toMutableList()
            } else {
                mutableListOf()
            }
            return Celular(
                celularData[0],
                celularData[1],
                celularData[2].toDouble(),
                celularData[3],
                celularData[4].toBoolean(),
                aplicativos
            )
        }
    }
}

class CRUDCelularAplicativo {

    private val archivo = "Datos.txt"

    // Guardar datos en archivo
    private fun guardarDatos(celulares: MutableList<Celular>) {
        val contenido = celulares.joinToString("\n") { it.toFileString() }
        File(archivo).writeText(contenido)
    }

    // Cargar datos desde archivo
    private fun cargarDatos(): MutableList<Celular> {
        return try {
            File(archivo).readLines().map { Celular.fromFileString(it) }.toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    // Crear un celular
    fun crearCelular() {
        val celulares = cargarDatos()

        println("Ingrese la marca del celular:")
        val marca = readLine() ?: ""

        println("Ingrese el modelo del celular:")
        val modelo = readLine() ?: ""

        println("Ingrese el precio del celular:")
        val precio = readLine()?.toDoubleOrNull() ?: 0.0

        println("Ingrese la fecha de fabricación (YYYY-MM-DD):")
        val fechaFabricacion = readLine() ?: ""

        println("¿Es un smartphone? (true/false):")
        val esSmartphone = readLine()?.toBoolean() ?: false

        val nuevoCelular = Celular(marca, modelo, precio, fechaFabricacion, esSmartphone)

        println("¿Cuántos aplicativos desea agregar?")
        val cantidad = readLine()?.toIntOrNull() ?: 0

        repeat(cantidad) {
            println("Ingrese el nombre del aplicativo:")
            val nombre = readLine() ?: ""

            println("Ingrese la versión del aplicativo:")
            val version = readLine() ?: ""

            println("Ingrese el peso en MB del aplicativo:")
            val pesoMb = readLine()?.toDoubleOrNull() ?: 0.0

            println("¿Es gratuito? (true/false):")
            val esGratuito = readLine()?.toBoolean() ?: false

            println("Ingrese la categoría del aplicativo:")
            val categoria = readLine() ?: ""

            val aplicativo = Aplicativo(nombre, version, pesoMb, esGratuito, categoria)
            nuevoCelular.aplicativos.add(aplicativo)
        }

        celulares.add(nuevoCelular)
        guardarDatos(celulares)
        println("Celular creado con éxito: $nuevoCelular")
    }

    // Leer todos los celulares
    fun leerCelulares() {
        val celulares = cargarDatos()
        if (celulares.isEmpty()) {
            println("No hay celulares registrados.")
        } else {
            celulares.forEach { println(it) }
        }
    }

    // Actualizar un celular
    fun actualizarCelular() {
        val celulares = cargarDatos()

        println("Ingrese el índice del celular a actualizar:")
        val index = readLine()?.toIntOrNull() ?: -1

        if (index in celulares.indices) {
            println("Ingrese la nueva marca del celular:")
            val marca = readLine() ?: ""

            println("Ingrese el nuevo modelo del celular:")
            val modelo = readLine() ?: ""

            println("Ingrese el nuevo precio del celular:")
            val precio = readLine()?.toDoubleOrNull() ?: 0.0

            println("Ingrese la nueva fecha de fabricación (YYYY-MM-DD):")
            val fechaFabricacion = readLine() ?: ""

            println("¿Es un smartphone? (true/false):")
            val esSmartphone = readLine()?.toBoolean() ?: false

            val celular = celulares[index]
            celular.marca = marca
            celular.modelo = modelo
            celular.precio = precio
            celular.fechaFabricacion = fechaFabricacion
            celular.esSmartphone = esSmartphone

            guardarDatos(celulares)
            println("Celular actualizado con éxito.")
        } else {
            println("Índice inválido.")
        }
    }

    // Eliminar un celular
    fun eliminarCelular() {
        val celulares = cargarDatos()

        println("Ingrese el índice del celular a eliminar:")
        val index = readLine()?.toIntOrNull() ?: -1

        if (index in celulares.indices) {
            val eliminado = celulares.removeAt(index)
            guardarDatos(celulares)
            println("Celular eliminado: $eliminado")
        } else {
            println("Índice inválido.")
        }
    }

    // Menú principal
    fun menu() {
        while (true) {
            println("\n--- MENÚ CRUD ---")
            println("1. Crear celular")
            println("2. Leer celulares")
            println("3. Actualizar celular")
            println("4. Eliminar celular")
            println("5. Salir")

            when (readLine()?.toIntOrNull()) {
                1 -> crearCelular()
                2 -> leerCelulares()
                3 -> actualizarCelular()
                4 -> eliminarCelular()
                5 -> {
                    println("Saliendo del programa...")
                    break
                }
                else -> println("Opción inválida.")
            }
        }
    }
}

fun main() {
    val crud = CRUDCelularAplicativo()
    crud.menu()
}
