package com.example.aplicativodeentidades

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (
    context: Context
) : SQLiteOpenHelper(
    context, "CelularesDB", null, 1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Tabla de Celular
        db?.execSQL(
            """
                CREATE TABLE Celular (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    marca TEXT NOT NULL,
                    modelo TEXT NOT NULL,
                    precio REAL NOT NULL,
                    fechaFabricacion TEXT NOT NULL,
                    esSmartphone INTEGER NOT NULL
                )
                """
        )

        // Tabla de Aplicativo
        db?.execSQL(
            """
                CREATE TABLE Aplicativo (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    version TEXT NOT NULL,
                    pesoMb REAL NOT NULL,
                    esGratuito INTEGER NOT NULL,
                    categoria TEXT NOT NULL,
                    celularId INTEGER NOT NULL,
                    FOREIGN KEY (celularId) REFERENCES Celular(id)
                )
                """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Celular")
        db?.execSQL("DROP TABLE IF EXISTS Aplicativo")
        onCreate(db)
    }

}