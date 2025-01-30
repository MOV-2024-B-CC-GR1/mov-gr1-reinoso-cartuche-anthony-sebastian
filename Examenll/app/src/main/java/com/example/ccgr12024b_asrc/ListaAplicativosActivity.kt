package com.example.ccgr12024b_asrc

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListaAplicativosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private lateinit var aplicativos: MutableList<Pair<Int, String>> // Ahora incluye IDs
    private lateinit var marcaModeloTextView: TextView
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
        marcaModeloTextView = findViewById(R.id.marcaModeloTextView) // TextView para marca y modelo
        val btnAgregarAplicativo = findViewById<Button>(R.id.btnAgregarAplicativo)

        celularId = intent.getIntExtra("CELULAR_ID", -1)

        if (celularId == -1) {
            Toast.makeText(this, "Error: no se encontró el celular.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        mostrarMarcaYModelo()

        cargarAplicativos(celularId)

        btnAgregarAplicativo.setOnClickListener {
            val intent = Intent(this, AgregarAplicativoActivity::class.java)
            intent.putExtra("CELULAR_ID", celularId)
            startActivity(intent)
        }

        // Configurar el evento de mantener presionado en el ListView
        listView.setOnItemLongClickListener { _, _, position, _ ->
            mostrarOpciones(aplicativos[position].first, aplicativos[position].second)
            true
        }

    }

    private fun mostrarMarcaYModelo() {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT marca, modelo FROM Celular WHERE id = ?", arrayOf(celularId.toString()))
        if (cursor.moveToFirst()) {
            val marca = cursor.getString(0)
            val modelo = cursor.getString(1)
            marcaModeloTextView.text = "$marca - $modelo"
        }
        cursor.close()
    }


    private fun cargarAplicativos(celularId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT id, nombre, categoria FROM Aplicativo WHERE celular_id = ?", arrayOf(celularId.toString()))

        aplicativos = mutableListOf()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                val categoria = cursor.getString(2)
                aplicativos.add(id to "$nombre: $categoria")
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Mostrar los aplicativos en el ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, aplicativos.map { it.second })
        listView.adapter = adapter
    }

    private fun mostrarOpciones(aplicativoId: Int, aplicativoInfo: String) {
        val opciones = arrayOf("Editar", "Eliminar")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones para $aplicativoInfo")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> editarAplicativo(aplicativoId)
                1 -> eliminarAplicativo(aplicativoId)
            }
        }
        builder.show()
    }

    private fun editarAplicativo(aplicativoId: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT nombre, categoria, tamanoMB, version, esgratis FROM Aplicativo WHERE id = ?", arrayOf(aplicativoId.toString()))

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(0)
            val categoria = cursor.getString(1)
            val tamanoMB = cursor.getString(2)
            val version = cursor.getString(3)
            val esgratis = cursor.getInt(4) == 1

            cursor.close()

            // Abrir la actividad de agregar aplicativo con los datos existentes
            val intent = Intent(this, AgregarAplicativoActivity::class.java)
            intent.putExtra("APLICATIVO_ID", aplicativoId)
            intent.putExtra("NOMBRE", nombre)
            intent.putExtra("CATEGORIA", categoria)
            intent.putExtra("MB", tamanoMB)
            intent.putExtra("VERSION", version)
            intent.putExtra("ES_GRATIS", esgratis)
            intent.putExtra("CELULAR_ID", celularId)
            startActivity(intent)
        } else {
            cursor.close()
            Toast.makeText(this, "Error al cargar los datos del aplicativo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarAplicativo(aplicativoId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Aplicativo")
        builder.setMessage("¿Estás seguro de que quieres eliminar este aplicativo?")
        builder.setPositiveButton("Eliminar") { _, _ ->
            val db = dbHelper.writableDatabase
            db.execSQL("DELETE FROM Aplicativo WHERE id = ?", arrayOf(aplicativoId))
            cargarAplicativos(celularId)
            Toast.makeText(this, "Aplicativo eliminado correctamente", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        cargarAplicativos(celularId)
    }


}