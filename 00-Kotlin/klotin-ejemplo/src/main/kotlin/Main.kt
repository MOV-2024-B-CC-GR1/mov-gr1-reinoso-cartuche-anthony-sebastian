package org.example

import java.util.*

fun main() {
    println("Hello World!")

    //INMUTABLES (No se RE-ASIGNA "=")
    val inmutable: String = "Adrian";
    //inmutable = "vicente" //Error!

    //MUTABLES
    var mutable: String = "Vicente"
    mutable = "Adrian" //OK

    //VAL > VAR

    //Duck Typing
    val ejemploVariable = "Adrian Euguez"
    ejemploVariable.trim()
    val edadEjemplo: Int = 12
    //ejemploVariable = edadEjemplo // Error!
    //Variables Primitivas
    val nombreProfesor: String = "Adrian Euguez"
    val sueldo: Double = 1.2
    val estadoCivil: Char = 'C'
    val mayorEdad:Boolean = true
    // Clases en Java
    val fechaNacimiento: Date = Date()

    //When (Switch)
    val estadoCivilWhen = "C"
    when (estadoCivilWhen) {
        ("C") ->{
            println("Casado")
        }
        "S" -> {
            println("Soltero")
        }
        else -> {
            println("No sabemos")
        }
    }

    val esSoltero = (estadoCivilWhen == "S")
    val coqueteo = if (esSoltero) "Si" else "No" //if else chiquito

    imprimirNombre("Adrian")


    calcularSueldo(10.00) // solo parametro requerido
    calcularSueldo(10.00,15.00,20.00) //parametro requerido y sobreescribir parametros
    //Named parameters
    //calcularSueldo(sueldo,tasa,bonoEspecial)
    calcularSueldo(10.00, bonoEspecial = 20.00) //usando el parametro de boonoEspecial en 2da posicion
                                                      // gracias a los parametros nombrados
    calcularSueldo(bonoEspecial = 20.00, sueldo = 10.00, tasa = 14.00)
    // usando el parametro bonoEspecial en 1ra posicion
    // usando el parametro sueldo en 2da posicion
    // usando el parametro tasa en 3era posicion
    // gracias a los parametros mencionados

    //CLASES USO:
    val sumaA = Suma(1,1)
    val sumaB = Suma(null,1)
    val sumaC = Suma(1,null)
    val sumaD = Suma(null,null)
    sumaA.sumar()
    sumaB.sumar()
    sumaC.sumar()
    sumaD.sumar()
    println(Suma.pi)
    println(Suma.elevarAlCuadrado(2))
    println(Suma.historialSumas)

}



// Creacion de funciones

fun imprimirNombre(nombre:String): Unit{

    fun otraFuncionAdentro(){
        println("Otra funcion adentro")
    }

    println("Nombre: $nombre") // Uso sin llaves
    println("Nombre: ${nombre}") // Uso con llaves opcional
    println("Nombre: ${nombre+nombre}") // Uso con llaves (concatenadas)
    println("Nombre: ${nombre.toString()}") // Uso con llaves (funcion)
    println("Nombre: $nombre.toString()") // INCORRECETO!
    //(no pueden usar sin llaves)
    otraFuncionAdentro()

}

fun calcularSueldo(
    sueldo:Double, // Requerido
    tasa: Double = 12.00, // Opcional (defecto)
    bonoEspecial:Double? = null // Opcional(nullable)
    //Variable? -> "?" Es Nullable (osea que puede en algun aumento ser nulo)
):Double{
    // Int -> Int? (nullable)
    // String -> String? (nullable)
    // Date -> Date? (nullable)
    if (bonoEspecial == null) {
        return sueldo * (100/tasa)
    }else{
        return sueldo * (100/tasa) * bonoEspecial
    }
}


abstract class NumerosJava{
    protected val numeroUno:Int
    private val numeroDos: Int

    constructor(
        uno:Int,
        dos:Int,
    ){
        this.numeroUno = uno
        this.numeroDos = dos
        println("Inicializando")
    }
}

abstract class Numeros(
    protected val numeroUno: Int?,
    protected val numeroDos: Int?,
    parametroNoUsadoNoPropiedadDeLaClase:Int? = null

){
    init{
        this.numeroUno
        this.numeroDos
        println("Inicializando")
    }
}

class suma( //Constructor primario
    unoParametro: Int?, //Parametro
    dosParametro: Int? //Parametro
 ): Numeros( //Clase papa, Numeros (extendiendo)
     unoParametro,
     dosParametro
 );{
     public val soyPublicoExplicito: String = "Publicas"
    val soyPublicoImplicite: String = "Publico Implicito"
    int{ //bloque constructor primario
        this.numeroUno
        this.numeroDos
        numeroUno
        numeroDos
        this.soyPublicoImplicite
        soyPublicoExplicito
    }
 }

constructor(
    uno: Int?,
    dos: Int,
): this(
        if (uno == null) 0 else uno,
        dos
    ){
        //bloque de codigo de constructor secundario
    }

constructor(
    uno: Int,
    dos: Int?,
    ): this(
    uno,
    if (dos == null) 0 else dos,

)

constructor(
    uno: Int?,
    dos: Int?,
    ): this(
    if (uno == null) 0 else uno,
    if (dos == null) 0 else dos
)

fun sumar(): Int{
    val total = numeroUno + numeroDos
    agregarHistorial(total)
    return total
}

companion object{
    val  pi = 3.14
    fun elevarAlCuadrado(num:Int):Int{ return num * num}
    val historialSumas = arrayListOf<Int>()

    fun agregarHistorial(valorTotalSuma: Int){
        historialSumas.add(valorTotalSuma)
    }
}




