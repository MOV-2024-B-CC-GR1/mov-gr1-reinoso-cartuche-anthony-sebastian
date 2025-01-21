package com.example.aplicativodeentidades

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

class CelularListActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var celulares: MutableList<Celular>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_celular_list)

        dbHelper = DatabaseHelper(this)
        listView = findViewById(R.id.lvCelulares)
        celulares = obtenerCelulares()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configuración del adaptador
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            celulares.map { "${it.marca} - ${it.modelo}" }
        )
        listView.adapter = adapter

        // Crear un nuevo celular
        findViewById<Button>(R.id.btnCrearCelular).setOnClickListener {
            startActivity(Intent(this, CelularFormActivity::class.java))
        }

        // Mostrar opciones CRUD al hacer click prolongado
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val celular = celulares[position]
            mostrarOpcionesCRUD(celular)
            true
        }
    }

    private fun obtenerCelulares(): MutableList<Celular> {
        val lista = mutableListOf<Celular>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Celular", null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(
                    Celular(
                        id = cursor.getInt(0),
                        marca = cursor.getString(1),
                        modelo = cursor.getString(2),
                        precio = cursor.getDouble(3),
                        fechaFabricacion = cursor.getString(4),
                        esSmartphone = cursor.getInt(5) == 1
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    private fun mostrarOpcionesCRUD(celular: Celular) {
        val opciones = arrayOf("Editar", "Eliminar", "Ver Aplicativos")
        AlertDialog.Builder(this)
            .setTitle("Opciones")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarCelular(celular)
                    1 -> eliminarCelular(celular)
                    2 -> verAplicativos(celular)
                }
            }
            .show()
    }

    private fun editarCelular(celular: Celular) {
        val intent = Intent(this, CelularFormActivity::class.java)
        intent.putExtra("CELULAR_ID", celular.id)
        startActivity(intent)
    }

    private fun eliminarCelular(celular: Celular) {
        val db = dbHelper.writableDatabase
        db.delete("Celular", "id = ?", arrayOf(celular.id.toString()))
        celulares.remove(celular)
        adapter.notifyDataSetChanged()
    }

    private fun verAplicativos(celular: Celular) {
        val intent = Intent(this, AplicativoListActivity::class.java)
        intent.putExtra("CELULAR_ID", celular.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        celulares.clear()
        celulares.addAll(obtenerCelulares())
        adapter.notifyDataSetChanged()
    }

}