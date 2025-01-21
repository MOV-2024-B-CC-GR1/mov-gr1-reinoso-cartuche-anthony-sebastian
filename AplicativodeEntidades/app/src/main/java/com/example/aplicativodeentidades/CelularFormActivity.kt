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

class CelularFormActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var celularId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_celular_form)

        dbHelper = DatabaseHelper(this)

        val etMarca = findViewById<EditText>(R.id.etMarca)
        val etModelo = findViewById<EditText>(R.id.etModelo)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val etFechaFabricacion = findViewById<EditText>(R.id.etFechaFabricacion)
        val cbEsSmartphone = findViewById<CheckBox>(R.id.cbEsSmartphone)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Verificar si es edición
        celularId = intent.getIntExtra("CELULAR_ID", -1)
        if (celularId != -1) {
            cargarDatosCelular(celularId, etMarca, etModelo, etPrecio, etFechaFabricacion, cbEsSmartphone)
        }

        findViewById<Button>(R.id.btnGuardarCelular).setOnClickListener {
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val fechaFabricacion = etFechaFabricacion.text.toString()
            val esSmartphone = cbEsSmartphone.isChecked

            if (marca.isBlank() || modelo.isBlank() || precio <= 0.0 || fechaFabricacion.isBlank()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (celularId == -1) {
                crearCelular(marca, modelo, precio, fechaFabricacion, esSmartphone)
            } else {
                actualizarCelular(celularId, marca, modelo, precio, fechaFabricacion, esSmartphone)
            }

            finish()
        }

    }

    private fun cargarDatosCelular(
        id: Int,
        etMarca: EditText,
        etModelo: EditText,
        etPrecio: EditText,
        etFechaFabricacion: EditText,
        cbEsSmartphone: CheckBox
    ) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Celular WHERE id = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            etMarca.setText(cursor.getString(1))
            etModelo.setText(cursor.getString(2))
            etPrecio.setText(cursor.getDouble(3).toString())
            etFechaFabricacion.setText(cursor.getString(4))
            cbEsSmartphone.isChecked = cursor.getInt(5) == 1
        }
        cursor.close()
    }

    private fun crearCelular(marca: String, modelo: String, precio: Double, fecha: String, esSmartphone: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("marca", marca)
            put("modelo", modelo)
            put("precio", precio)
            put("fechaFabricacion", fecha)
            put("esSmartphone", if (esSmartphone) 1 else 0)
        }
        db.insert("Celular", null, values)
        Toast.makeText(this, "Celular creado con éxito", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarCelular(id: Int, marca: String, modelo: String, precio: Double, fecha: String, esSmartphone: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("marca", marca)
            put("modelo", modelo)
            put("precio", precio)
            put("fechaFabricacion", fecha)
            put("esSmartphone", if (esSmartphone) 1 else 0)
        }
        db.update("Celular", values, "id = ?", arrayOf(id.toString()))
        Toast.makeText(this, "Celular actualizado con éxito", Toast.LENGTH_SHORT).show()
    }

}