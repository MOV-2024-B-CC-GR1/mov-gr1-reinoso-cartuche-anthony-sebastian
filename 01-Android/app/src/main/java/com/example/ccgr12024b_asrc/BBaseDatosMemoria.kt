package com.example.ccgr12024b_asrc

class BBaseDatosMemoria {

    companion object{
        val arrgeloBEntrenador = arrayListOf<BEntrenador>()
        init {
            arrgeloBEntrenador.add(BEntrenador(1,"Adrian", "a@a.com"))
            arrgeloBEntrenador.add(BEntrenador(2,"Vicente", "b@b.com"))
            arrgeloBEntrenador.add(BEntrenador(3,"Carolina", "c@c.com"))
        }
    }

}