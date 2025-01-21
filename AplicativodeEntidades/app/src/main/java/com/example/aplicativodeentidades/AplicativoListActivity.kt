package com.example.aplicativodeentidades

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AplicativoListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var aplicativos: MutableList<Aplicativo>
    private lateinit var adapter: ArrayAdapter<String>
    private var celularId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.lvAplicativos)

        celularId = intent.getIntExtra("CELULAR_ID", -1)
        aplicativos = obtenerAplicativos()

        setContentView(R.layout.activity_aplicativo_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del adaptador
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            aplicativos.map { "${it.nombre} - ${it.version}" }
        )
        listView.adapter = adapter

        findViewById<Button>(R.id.btnCrearAplicativo).setOnClickListener {
            val intent = Intent(this, AplicativoFormActivity::class.java)
            intent.putExtra("CELULAR_ID", celularId)
            startActivity(intent)
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val aplicativo = aplicativos[position]
            mostrarOpcionesCRUD(aplicativo)
            true
        }

    }

    private fun obtenerAplicativos(): MutableList<Aplicativo> {
        val lista = mutableListOf<Aplicativo>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Aplicativo WHERE celularId = ?", arrayOf(celularId.toString()))
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Aplicativo(
                        id = cursor.getInt(0),
                        nombre = cursor.getString(1),
                        version = cursor.getString(2),
                        pesoMb = cursor.getDouble(3),
                        esGratuito = cursor.getInt(4) == 1,
                        categoria = cursor.getString(5),
                        celularId = cursor.getInt(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    private fun mostrarOpcionesCRUD(aplicativo: Aplicativo) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarAplicativo(aplicativo)
                    1 -> eliminarAplicativo(aplicativo)
                }
            }
            .show()
    }

    private fun editarAplicativo(aplicativo: Aplicativo) {
        val intent = Intent(this, AplicativoFormActivity::class.java)
        intent.putExtra("APLICATIVO_ID", aplicativo.id)
        startActivity(intent)
    }

    private fun eliminarAplicativo(aplicativo: Aplicativo) {
        val db = dbHelper.writableDatabase
        db.delete("Aplicativo", "id = ?", arrayOf(aplicativo.id.toString()))
        aplicativos.remove(aplicativo)
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        aplicativos.clear()
        aplicativos.addAll(obtenerAplicativos())
        adapter.notifyDataSetChanged()
    }

}