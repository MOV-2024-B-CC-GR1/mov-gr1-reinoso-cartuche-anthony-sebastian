package com.example.ccgr12024b_asrc

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarAplicativoActivity : AppCompatActivity() {

    private var celularId: Int = -1
    private var aplicativoId: Int = -1
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvCelularInfo: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_aplicativo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        tvCelularInfo = findViewById(R.id.tvCelularInfo)

        val etNombre = findViewById<EditText>(R.id.etNombreAplicativo)
        val etPeso = findViewById<EditText>(R.id.etPesoAplicativo)
        val etversion = findViewById<EditText>(R.id.etVersionAplicativo)
        val etcategoria= findViewById<EditText>(R.id.etCategoriaAplicativo)
        val etgratis = findViewById<CheckBox>(R.id.cbesGratis)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarAplicativo)

        // Obtener el ID del celular y del aplicativo
        celularId = intent.getIntExtra("CELULAR_ID", -1)
        aplicativoId = intent.getIntExtra("APLICATIVO_ID", -1)

        if (celularId == -1) {
            Toast.makeText(this, "Error: no se encontró el celular.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        mostrarMarcaYModelo()

        // Si es edición, cargar datos del aplicativo
        if (aplicativoId != -1) {
            cargarDatosAplicativo(aplicativoId, etNombre, etPeso, etversion, etcategoria, etgratis)
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val version = etversion.text.toString()
            val categoria = etcategoria.text.toString()
            val tamanoMB = etPeso.text.toString().toDoubleOrNull()
            val esGratis = etgratis.isChecked

            if (nombre.isNotEmpty() && categoria.isNotEmpty() && version.isNotEmpty()) {
                val db = dbHelper.writableDatabase
                val valores = ContentValues().apply {
                    put("nombre", nombre)
                    put("version", version)
                    put("tamanoMB", tamanoMB)
                    put("categoria", categoria)
                    put("esgratis", esGratis)
                    put("celular_id", celularId)
                }

                if (aplicativoId == -1) {
                    // Agregar nuevo aplicativo
                    val resultado = db.insert("Aplicativo", null, valores)
                    if (resultado != -1L) {
                        Toast.makeText(this, "Aplicativo agregado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al guardar el aplicativo.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Editar aplicativo existente
                    val resultado = db.update("Aplicativo", valores, "id = ?", arrayOf(aplicativoId.toString()))
                    if (resultado > 0) {
                        Toast.makeText(this, "Aplicativo editado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al editar el aplicativo.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun mostrarMarcaYModelo() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT marca, modelo FROM Celular WHERE id = ?", arrayOf(celularId.toString()))
        if (cursor.moveToFirst()) {
            val marca = cursor.getString(0)
            val modelo = cursor.getString(1)
            tvCelularInfo.text = "$marca - $modelo"
        }
        cursor.close()
    }

    private fun cargarDatosAplicativo(
        aplicativoId: Int,
        etNombre: EditText,
        etPeso: EditText,
        etVersion: EditText,
        etCategoria: EditText,
        esgratis: CheckBox
    ) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT nombre, version, tamanoMB, categoria, esgratis FROM Aplicativo WHERE id = ?",
            arrayOf(aplicativoId.toString())
        )

        if (cursor.moveToFirst()) {
            etNombre.setText(cursor.getString(0))
            etVersion.setText(cursor.getString(1))
            etPeso.setText(cursor.getDouble(2).toString())
            etCategoria.setText(cursor.getString(3))
            esgratis.isChecked = cursor.getInt(4) == 1
        }
        cursor.close()
    }

}