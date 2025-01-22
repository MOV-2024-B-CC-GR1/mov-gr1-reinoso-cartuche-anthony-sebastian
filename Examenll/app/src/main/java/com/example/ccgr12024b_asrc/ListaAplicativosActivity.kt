package com.example.ccgr12024b_asrc

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaAplicativosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var aplicativos: MutableList<String>
    private var celularId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_aplicativos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listaAplicativos)
        dbHelper = DatabaseHelper(this)

        // Obtener el ID del celular enviado desde el MainActivity
        celularId = intent.getIntExtra("CELULAR_ID", -1).takeIf { it != -1 }

        if (celularId != null) {
            cargarAplicativos(celularId!!)
        } else {
            Toast.makeText(this, "Error al cargar los aplicativos", Toast.LENGTH_SHORT).show()
            finish() // Cierra la actividad si no se recibió un ID válido
        }

    }


    private fun cargarAplicativos(celularId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre, descripcion FROM Aplicativo WHERE celular_id = ?", arrayOf(celularId.toString()))

        aplicativos = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val descripcion = cursor.getString(1)
                aplicativos.add("$nombre: $descripcion")
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Mostrar los aplicativos en el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aplicativos)
        listView.adapter = adapter
    }


}