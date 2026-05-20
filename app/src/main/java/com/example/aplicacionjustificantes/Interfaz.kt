package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class Interfaz : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interfaz)

        
        val btnAutenticacion = findViewById<Button>(R.id.btnIrAutenticacion)
        val btnNotificaciones = findViewById<Button>(R.id.btnIrNotificaciones)
        val btnVisualizacion = findViewById<Button>(R.id.btnIrVisualizacion)

       
        btnAutenticacion.setOnClickListener {
            val intent = Intent(this, PrimeraVistaEder::class.java)
            startActivity(intent)
        }

        
        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, Notificaciones::class.java)
            startActivity(intent)
        }

        
        btnVisualizacion.setOnClickListener {
            val intent = Intent(this, PaneldeVisualizacion::class.java)
            startActivity(intent)
        }
    }
}