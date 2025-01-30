package com.example.ccgr12024b_asrc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class GGoogleMaps : AppCompatActivity() {

    private lateinit var mapa: GoogleMap
    var permisos = false
    var nombrePermisoFine = android.Manifest.permission.ACCESS_FINE_LOCATION
    var nombrePermisoCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ggoogle_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar el botón de aceptar
        val btnAceptar = findViewById<Button>(R.id.btnAceptar)
        btnAceptar.setOnClickListener {
            // Cierra la actividad actual y regresa a la actividad anterior
            finish()
        }
        solicitarPermisos()
        inicializarLogicaMapa()
    }

    fun tengoPermisos():Boolean{
        val contexto = applicationContext
        val permisoFine = ContextCompat.checkSelfPermission(contexto, nombrePermisoFine)
        val permisoCoarse = ContextCompat.checkSelfPermission(contexto, nombrePermisoCoarse)
        val tienePermisos = permisoFine == PackageManager.PERMISSION_GRANTED &&
                permisoCoarse == PackageManager.PERMISSION_GRANTED
        permisos = tienePermisos
        return permisos
    }

    fun solicitarPermisos(){
        if (!tengoPermisos()){
            ActivityCompat.requestPermissions(
                this, arrayOf( nombrePermisoFine, nombrePermisoCoarse), 1
            )
        }
    }

    fun inicializarLogicaMapa(){
        val fragmentoMapa = supportFragmentManager.
        findFragmentById(R.id.map) as SupportMapFragment
        fragmentoMapa.getMapAsync{ googleMap ->
            with(googleMap){
                mapa = googleMap
                establecerConfiguracionMapa()
                moverFabricante()
            }
        }
    }

    fun moverFabricante(){
        val fabricante = LatLng(37.46602439096917, 127.02287503691754)
        val titulo = "Fabricante"
        val marcadorFabricante = anadirMarcador(fabricante, titulo)
        marcadorFabricante.tag = titulo
        moverCamaraConZoom(fabricante)
    }

    @SuppressLint("MissingPermission")

    fun establecerConfiguracionMapa(){
        with(mapa){
            if(tengoPermisos()){
                mapa.isMyLocationEnabled = true
                uiSettings.isMyLocationButtonEnabled = true
            }
            uiSettings.isZoomControlsEnabled = true
        }
    }
    fun  moverCamaraConZoom( latLang: LatLng, zoom: Float = 17f){
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(latLang, zoom))
    }
    fun anadirMarcador(latLang: LatLng, title:String): Marker{
        return mapa.addMarker(MarkerOptions().position(latLang).title(title))!!
    }


}