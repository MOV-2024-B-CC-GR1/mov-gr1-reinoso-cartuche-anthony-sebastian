package com.example.ccgr12024b_asrc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EsqliteHelperEntrenador(
    contexto: Context?
): SQLiteOpenHelper(
    contexto,
    "moviles",
    null,
    1
){
    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaEntrenador =
            """
                CREATE TABLE ENTRENADOR(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    descripcion VARCHAR(50)
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaEntrenador)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int){}

    fun crearEntrenador(nombre: String, descripcion: String): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", nombre)
        valoresGuardar.put("descripcion", descripcion)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "ENTRENADOR", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }

    fun eliminarEntrenador(id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        // where .. ID=? and nombre=? [?,?]
        val parametrosConsultaDelete = arrayOf(id.toString())
        val resultadoEliminar = baseDatosEscritura
            .delete(
                "ENTRENADOR", // tabla
                "id=?", // consulta
                parametrosConsultaDelete // parametros
            )
        baseDatosEscritura.close()
        return if (resultadoEliminar.toInt() == -1) false else true
    }
    fun actualizarEntrenador(nombre: String, descripcion: String, id: Int): Boolean {
        val baseDatosEscritura = writableDatabase
        val valoresActualizar = ContentValues()
        valoresActualizar.put("nombre", nombre)
        valoresActualizar.put("descripcion", descripcion)
        // where
        val parametrosConsultaActualizar = arrayOf(id.toString())
        val resultadoActualizar = baseDatosEscritura
            .update(
                "ENTRENADOR", // tabla
                valoresActualizar, // valores
                "id=?", // id=?
                parametrosConsultaActualizar // [1]
            )
        baseDatosEscritura.close()
        return if (resultadoActualizar.toInt() == -1) false else true
    }

    fun consultarEntrenadorPorId(id: Int): BEntrenador? {
        val baseDatosLectura = readableDatabase
        val scriptConsultaLectura = """
            SELECT * FROM ENTRENADOR WHERE ID = ?
        """.trimIndent()
        val parametrosConsultaLectura = arrayOf(id.toString())
        val resultadoConsultaLectura = baseDatosLectura
            .rawQuery(
                scriptConsultaLectura,
                parametrosConsultaLectura
            )
        val existeAlMenosUno = resultadoConsultaLectura.moveToFirst()
        if(existeAlMenosUno){
            val arregloRespuesta = arrayListOf<BEntrenador>()
            do{
                val entrenador = BEntrenador(
                    resultadoConsultaLectura.getInt(0), // 0 = id
                    resultadoConsultaLectura.getString(1), // 1 = nombre
                    resultadoConsultaLectura.getString(2) // 2 = descripcion
                )
                arregloRespuesta.add(entrenador)
            }while(resultadoConsultaLectura.moveToNext())
            return arregloRespuesta[0] // En otros casos devolvemos el arrreglo completo
        }else{
            return null
        }
    }

}