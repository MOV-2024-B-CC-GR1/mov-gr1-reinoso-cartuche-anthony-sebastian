package com.example.aplicativodeentidades

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AplicativoFormActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var aplicativoId: Int = -1
    private var celularId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dbHelper = DatabaseHelper(this)

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etVersion = findViewById<EditText>(R.id.etVersion)
        val etPesoMb = findViewById<EditText>(R.id.etPesoMb)
        val cbEsGratuito = findViewById<CheckBox>(R.id.cbEsGratuito)
        val etCategoria = findViewById<EditText>(R.id.etCategoria)


        setContentView(R.layout.activity_aplicativo_form)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener IDs desde el intent
        aplicativoId = intent.getIntExtra("APLICATIVO_ID", -1)
        celularId = intent.getIntExtra("CELULAR_ID", -1)


        if (aplicativoId != -1) {
            cargarDatosAplicativo(aplicativoId, etNombre, etVersion, etPesoMb, cbEsGratuito, etCategoria)
        }

        findViewById<Button>(R.id.btnGuardarAplicativo).setOnClickListener {
            val nombre = etNombre.text.toString()
            val version = etVersion.text.toString()
            val pesoMb = etPesoMb.text.toString().toDoubleOrNull() ?: 0.0
            val esGratuito = cbEsGratuito.isChecked
            val categoria = etCategoria.text.toString()

            if (nombre.isBlank() || version.isBlank() || pesoMb <= 0.0 || categoria.isBlank()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (aplicativoId == -1) {
                crearAplicativo(nombre, version, pesoMb, esGratuito, categoria, celularId)
            } else {
                actualizarAplicativo(aplicativoId, nombre, version, pesoMb, esGratuito, categoria)
            }

            finish()
        }

    }

    private fun cargarDatosAplicativo(
        id: Int,
        etNombre: EditText,
        etVersion: EditText,
        etPesoMb: EditText,
        cbEsGratuito: CheckBox,
        etCategoria: EditText
    ) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Aplicativo WHERE id = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            etNombre.setText(cursor.getString(1))
            etVersion.setText(cursor.getString(2))
            etPesoMb.setText(cursor.getDouble(3).toString())
            cbEsGratuito.isChecked = cursor.getInt(4) == 1
            etCategoria.setText(cursor.getString(5))
        }
        cursor.close()
    }

    private fun crearAplicativo(
        nombre: String,
        version: String,
        pesoMb: Double,
        esGratuito: Boolean,
        categoria: String,
        celularId: Int
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("version", version)
            put("pesoMb", pesoMb)
            put("esGratuito", if (esGratuito) 1 else 0)
            put("categoria", categoria)
            put("celularId", celularId)
        }
        db.insert("Aplicativo", null, values)
        Toast.makeText(this, "Aplicativo creado con éxito", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarAplicativo(
        id: Int,
        nombre: String,
        version: String,
        pesoMb: Double,
        esGratuito: Boolean,
        categoria: String
    ) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("version", version)
            put("pesoMb", pesoMb)
            put("esGratuito", if (esGratuito) 1 else 0)
            put("categoria", categoria)
        }
        db.update("Aplicativo", values, "id = ?", arrayOf(id.toString()))
        Toast.makeText(this, "Aplicativo actualizado con éxito", Toast.LENGTH_SHORT).show()
    }


}