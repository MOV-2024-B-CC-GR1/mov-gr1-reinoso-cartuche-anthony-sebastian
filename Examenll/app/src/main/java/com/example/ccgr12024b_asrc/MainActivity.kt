package com.example.ccgr12024b_asrc

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listaCelulares: ListView
    private lateinit var celulares: MutableList<Celular>
    private lateinit var adapter: ArrayAdapter<String>


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        listaCelulares = findViewById(R.id.listaCelulares)

        celulares = obtenerCelulares()

        // Adaptador con los nombres de los celulares
        val nombres = celulares.map { "${it.marca} - ${it.modelo} (${it.precio}$)" }.toMutableList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listaCelulares.adapter = adapter

        findViewById<Button>(R.id.btnCrearCelular).setOnClickListener {
            val intent = Intent(this, AgregarCelularActivity::class.java)
            startActivity(intent)
        }

        listaCelulares.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpcionesCRUD(celulares[position])
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
                        fechaLanzamiento = cursor.getString(4),
                        disponible = cursor.getInt(5) == 1
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
            .setTitle("Opciones para ${celular.modelo}")
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
        val intent = Intent(this, AgregarCelularActivity::class.java)
        intent.putExtra("CELULAR_ID", celular.id)
        startActivity(intent)
    }

    private fun eliminarCelular(celular: Celular) {
        val db = dbHelper.writableDatabase
        db.delete("Celular", "id = ?", arrayOf(celular.id.toString()))
        celulares.remove(celular)
        adapter.remove("${celular.marca} - ${celular.modelo} (${celular.precio}$)")
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        cargarListaCelulares()
    }

    private fun cargarListaCelulares() {
        // Obtén la lista actualizada de celulares desde la base de datos
        celulares = obtenerCelulares()

        // Actualiza los datos del adaptador
        val nombres = celulares.map { "${it.marca} - ${it.modelo} (${it.precio}$)" }.toMutableList()
        adapter.clear()
        adapter.addAll(nombres)
        adapter.notifyDataSetChanged()
    }

    private fun verAplicativos(celular: Celular) {
        Log.d("MainActivity", "ID del celular seleccionado: ${celular.id}")

        val intent = Intent(this, ListaAplicativosActivity::class.java)
        intent.putExtra("MARCA", celular.marca)
        intent.putExtra("MODELO",celular.modelo )
        intent.putExtra("CELULAR_ID", celular.id)
        startActivity(intent)
    }

}