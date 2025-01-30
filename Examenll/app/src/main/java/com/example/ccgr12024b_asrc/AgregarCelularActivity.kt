package com.example.ccgr12024b_asrc

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarCelularActivity : AppCompatActivity() {

    private var celularId: Int? = null
    private lateinit var etMarca: EditText
    private lateinit var etModelo: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etFechaLanzamiento: EditText
    private lateinit var cbDisponible: CheckBox
    private lateinit var btnGuardar: Button
    private lateinit var btnVerFabricante: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_celular)

        // Inicializar vistas
        etMarca = findViewById(R.id.etMarca)
        etModelo = findViewById(R.id.etModelo)
        etPrecio = findViewById(R.id.etPrecio)
        etFechaLanzamiento = findViewById(R.id.etFechaLanzamiento)
        cbDisponible = findViewById(R.id.cbDisponible)
        btnGuardar = findViewById(R.id.btnGuardarCelular)
        btnVerFabricante = findViewById(R.id.verFabricante)

        dbHelper = DatabaseHelper(this)

        // Detectar si estamos en modo edición
        celularId = intent.getIntExtra("CELULAR_ID", -1).takeIf { it != -1 }

        if (celularId != null) {
            // Modo edición: cargar datos
            cargarDatosCelular(celularId!!)
            verificarCamposLlenos()
        }


        // Inicialmente ocultar el botón de "Ver Fabricante"
        btnVerFabricante.visibility = Button.GONE

        // Agregar TextWatcher a los campos de texto
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                verificarCamposLlenos()
            }
        }

        // Asignar el TextWatcher a los campos de texto
        etMarca.addTextChangedListener(textWatcher)
        etModelo.addTextChangedListener(textWatcher)
        etPrecio.addTextChangedListener(textWatcher)
        etFechaLanzamiento.addTextChangedListener(textWatcher)


        btnGuardar.setOnClickListener {
            val marca = etMarca.text.toString()
            val modelo = etModelo.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull()
            val fechaLanzamiento = etFechaLanzamiento.text.toString()
            val disponible = cbDisponible.isChecked

            if (marca.isNotEmpty() && modelo.isNotEmpty() && precio != null && fechaLanzamiento.isNotEmpty()) {
                if (celularId != null) {
                    // Editar el celular
                    actualizarCelular(celularId!!)
                } else {
                    // Crear un nuevo celular
                    agregarCelular(marca, modelo, precio, fechaLanzamiento, disponible)
                }
            } else {
                Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         // Ir al Maps
        val botonGoogleMaps = findViewById<Button>(R.id.verFabricante)
        botonGoogleMaps.setOnClickListener {
            val intent = Intent(this, GGoogleMaps::class.java)
            startActivity(intent)
        }

    }

    private fun cargarDatosCelular(id: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Celular WHERE id = ?", arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            val marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"))
            val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            val fechaLanzamiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaLanzamiento"))
            val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1

            // Mostrar los datos en los campos de texto
            etMarca.setText(marca)
            etModelo.setText(modelo)
            etPrecio.setText(precio.toString())
            etFechaLanzamiento.setText(fechaLanzamiento)
            cbDisponible.isChecked = disponible

            // Verificar si los campos están llenos después de cargar los datos
            verificarCamposLlenos()
        } else {
            Toast.makeText(this, "No se encontraron datos para el celular", Toast.LENGTH_SHORT).show()
        }
        cursor.close()
    }

    private fun verificarCamposLlenos() {
        val marca = etMarca.text.toString().trim()
        val modelo = etModelo.text.toString().trim()
        val precio = etPrecio.text.toString().trim()
        val fechaLanzamiento = etFechaLanzamiento.text.toString().trim()

        if (marca.isNotEmpty() && modelo.isNotEmpty() && precio.isNotEmpty() && fechaLanzamiento.isNotEmpty()) {
            btnVerFabricante.visibility = Button.VISIBLE
            println("Todos los campos están llenos. Botón visible.")
        } else {
            btnVerFabricante.visibility = Button.GONE
            println("Faltan campos por llenar. Botón oculto.")
        }
        Handler(Looper.getMainLooper()).postDelayed({
            verificarCamposLlenos()
        }, 500) // Retardo de 500 ms
    }



    private fun actualizarCelular(id: Int) {
        val db = dbHelper.writableDatabase

        val marca = etMarca.text.toString()
        val modelo = etModelo.text.toString()
        val precio = etPrecio.text.toString().toDouble()
        val fechaLanzamiento = etFechaLanzamiento.text.toString()
        val disponible = if (cbDisponible.isChecked) 1 else 0

        val valores = ContentValues().apply {
            put("marca", marca)
            put("modelo", modelo)
            put("precio", precio)
            put("fechaLanzamiento", fechaLanzamiento)
            put("disponible", disponible)
        }

        val filasActualizadas = db.update("Celular", valores, "id = ?", arrayOf(id.toString()))

        if (filasActualizadas > 0) {
            Toast.makeText(this, "Celular actualizado correctamente", Toast.LENGTH_SHORT).show()
            finish() // Regresar al MainActivity
        } else {
            Toast.makeText(this, "Error al actualizar el celular", Toast.LENGTH_SHORT).show()
        }
    }

    private fun agregarCelular(
        marca: String,
        modelo: String,
        precio: Double,
        fechaLanzamiento: String,
        disponible: Boolean
    ) {
        val db = dbHelper.writableDatabase

        val valores = ContentValues().apply {
            put("marca", marca)
            put("modelo", modelo)
            put("precio", precio)
            put("fechaLanzamiento", fechaLanzamiento)
            put("disponible", if (disponible) 1 else 0)
        }

        val resultado = db.insert("Celular", null, valores)
        if (resultado != -1L) {
            Toast.makeText(this, "Celular agregado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el celular", Toast.LENGTH_SHORT).show()
        }
    }

}