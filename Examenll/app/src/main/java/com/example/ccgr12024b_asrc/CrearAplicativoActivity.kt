package com.example.ccgr12024b_asrc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CrearAplicativoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var aplicativoId: Int? = null
    private var celularId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_aplicativo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        val editTextNombre = findViewById<EditText>(R.id.etNombreAplicativo)
        celularId = intent.getIntExtra("CELULAR_ID", -1)
        aplicativoId = intent.getIntExtra("APLICATIVO_ID", -1).takeIf { it != -1 }

        if (aplicativoId != null) {
            cargarAplicativo(aplicativoId!!, editTextNombre)
        }

        findViewById<Button>(R.id.btnGuardarAplicativo).setOnClickListener {
            val nombre = editTextNombre.text.toString()
            guardarAplicativo(nombre)
        }

    }

    private fun cargarAplicativo(id: Int, editText: EditText) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Aplicativo WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            editText.setText(cursor.getString(1))
        }
        cursor.close()
    }

    private fun guardarAplicativo(nombre: String) {
        val db = dbHelper.writableDatabase
        if (aplicativoId == null) {
            db.execSQL("INSERT INTO Aplicativo (nombre, celularId) VALUES (?, ?)", arrayOf(nombre, celularId))
        } else {
            db.execSQL("UPDATE Aplicativo SET nombre = ? WHERE id = ?", arrayOf(nombre, aplicativoId))
        }
        finish()
    }

}