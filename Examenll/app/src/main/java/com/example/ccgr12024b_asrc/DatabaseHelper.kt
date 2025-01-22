package com.example.ccgr12024b_asrc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (
    context: Context
) : SQLiteOpenHelper(context, "celularesDB", null, 2)
    {
        override fun onCreate(db: SQLiteDatabase?) {
            // Crear tabla Celular
            db?.execSQL(
                """
                    CREATE TABLE Celular (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        marca TEXT NOT NULL,
                        modelo TEXT NOT NULL,
                        precio REAL NOT NULL,
                        fechaLanzamiento TEXT NOT NULL,
                        disponible INTEGER NOT NULL
                    )
                    """
            )

            // Crear tabla Aplicativo
            db?.execSQL(
                """
            CREATE TABLE Aplicativo (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                version TEXT NOT NULL,
                tamanoMB REAL NOT NULL,
                disponible INTEGER NOT NULL,
                categoria TEXT NOT NULL,
                celularId INTEGER NOT NULL,
                FOREIGN KEY (celularId) REFERENCES Celular (id)
            )
            """
            )
            // Insertar datos iniciales
            insertarDatosEjemplo(db!!)
        }


        override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
            db?.execSQL("DROP TABLE IF EXISTS Aplicativo")
            db?.execSQL("DROP TABLE IF EXISTS Celular")
            onCreate(db)
        }

        private fun insertarDatosEjemplo(db: SQLiteDatabase) {
            db.execSQL(
                """
            INSERT INTO Celular (marca, modelo, precio, fechaLanzamiento, disponible)
            VALUES
            ('Samsung', 'Android Galaxy S21', 899.99, '2022-03-15', 1),
            ('Apple', 'iPhone 14', 1199.99, '2023-02-20', 1),
            ('Xiaomi', 'Android Redmi Note 10', 499.99, '2021-08-30', 1),
            ('Huawei', 'Android P40 Pro', 699.99, '2022-06-10', 1)
            """
            )

            db.execSQL(
                """
            INSERT INTO Aplicativo (nombre, version, tamanoMB, disponible, categoria, celularId)
            VALUES
            ('WhatsApp', '2.23.11.77', 120.5, 1, 'Comunicación', 1),
            ('Instagram', '292.0', 80.3, 1, 'Redes Sociales', 1),
            ('Spotify', '8.8.44', 150.0, 1, 'Entretenimiento', 1),
            ('Safari', '16.3', 75.0, 1, 'Navegador', 2),
            ('GarageBand', '2.3.12', 170.0, 1, 'Música', 2),
            ('iMovie', '10.3.5', 250.0, 1, 'Video', 2),
            ('TikTok', '29.6.4', 200.0, 1, 'Redes Sociales', 3),
            ('Telegram', '9.5.2', 100.0, 1, 'Comunicación', 3),
            ('Zoom', '5.15.0', 90.0, 1, 'Video Conferencia', 3),
            ('Petal Maps', '3.1.2', 150.0, 1, 'Navegación', 4),
            ('Facebook', '397.1', 120.0, 1, 'Redes Sociales', 4),
            ('Adobe Reader', '22.5.0', 90.5, 1, 'Productividad', 4)
            """
            )
        }

        fun agregarCelular(marca: String, modelo: String, precio: Double, fechaLanzamiento: String, disponible: Boolean): Long {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("marca", marca)
                put("modelo", modelo)
                put("precio", precio)
                put("fechaLanzamiento", fechaLanzamiento)
                put("disponible", if (disponible) 1 else 0)
            }

            // Insertar el nuevo registro
            val resultado = db.insert("Celular", null, values)
            db.close()
            return resultado // Devuelve el ID del registro insertado o -1 si hubo un error
        }

        fun obtenerTodosLosCelulares(): List<Celular> {
            val lista = mutableListOf<Celular>()
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM Celular", null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"))
                    val modelo = cursor.getString(cursor.getColumnIndexOrThrow("modelo"))
                    val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
                    val fechaLanzamiento = cursor.getString(cursor.getColumnIndexOrThrow("fechaLanzamiento"))
                    val disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1

                    lista.add(Celular(id, marca, modelo, precio, fechaLanzamiento, disponible))
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return lista
        }

        fun agregarAplicativo(nombre: String, categoria: String, celularId: Int): Long {
            val db = writableDatabase
            val valores = ContentValues().apply {
                put("nombre", nombre)
                put("categoria", categoria)
                put("celular_id", celularId)
            }
            return db.insert("Aplicativo", null, valores)
        }


    }