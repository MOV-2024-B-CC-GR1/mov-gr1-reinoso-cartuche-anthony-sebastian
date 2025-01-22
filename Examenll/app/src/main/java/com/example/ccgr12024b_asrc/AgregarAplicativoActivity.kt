package com.example.ccgr12024b_asrc

import android.annotation.SuppressLint
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

class AgregarAplicativoActivity : AppCompatActivity() {

    private var celularId: Int = -1
    private lateinit var dbHelper: DatabaseHelper

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

        val etNombre = findViewById<EditText>(R.id.etNombreAplicativo)
        val etPeso = findViewById<EditText>(R.id.etPesoAplicativo)
        val etversion = findViewById<EditText>(R.id.etVersionAplicativo)
        val etcategoria= findViewById<EditText>(R.id.etCategoriaAplicativo)
        val etgratis = findViewById<CheckBox>(R.id.cbesGratis)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarAplicativo)

        celularId = intent.getIntExtra("CELULAR_ID", -1)

        if (celularId == -1) {
            Toast.makeText(this, "Error: no se encontró el celular.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString()
            val version = etversion.text.toString()
            val categoria = etcategoria.text.toString()
            val tamanoMB = etPeso.text.toString().toDoubleOrNull()
            val esgratis = etgratis.isChecked

            if (nombre.isNotEmpty() && categoria.isNotEmpty() && version.isNotEmpty()) {
                val db = dbHelper.writableDatabase
                val valores = ContentValues().apply {
                    put("nombre", nombre)
                    put("version", version)
                    put("tamanoMB",tamanoMB)
                    put("categoria", categoria)
                    put("esgratis", esgratis)
                    put("celular_id", celularId)
                }

                val resultado = db.insert("Aplicativo", null, valores)
                if (resultado != -1L) {
                    Toast.makeText(this, "Aplicativo agregado correctamente", Toast.LENGTH_SHORT).show()
                    finish() // Regresar a la lista de aplicativos
                } else {
                    Toast.makeText(this, "Error al guardar el aplicativo.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}