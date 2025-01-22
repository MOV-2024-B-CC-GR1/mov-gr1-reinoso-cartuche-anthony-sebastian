package com.example.ccgr12024b_asrc

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaAplicativosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var aplicativos: MutableList<String>
    private var celularId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_aplicativos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.listaAplicativos)
        val btnAgregarAplicativo = findViewById<Button>(R.id.btnAgregarAplicativo)


        celularId = intent.getIntExtra("CELULAR_ID", -1)

        if (celularId == -1) {
            Toast.makeText(this, "Error: no se encontró el celular.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        cargarAplicativos(celularId)

        btnAgregarAplicativo.setOnClickListener {
            val intent = Intent(this, AgregarAplicativoActivity::class.java)
            intent.putExtra("CELULAR_ID", celularId)
            startActivity(intent)
        }

    }


    private fun cargarAplicativos(celularId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre, categoria FROM Aplicativo WHERE celular_id = ?", arrayOf(celularId.toString()))


        aplicativos = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(0)
                val categoria  = cursor.getString(1)
                aplicativos.add("$nombre: $categoria")
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Mostrar los aplicativos en el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aplicativos)
        listView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        cargarAplicativos(celularId)
    }


}