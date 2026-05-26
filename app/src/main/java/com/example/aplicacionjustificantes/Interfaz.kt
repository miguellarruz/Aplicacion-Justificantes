package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Interfaz : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interfaz)

        // Buscamos los botones (Asegúrate de que el ID en tu XML sea btnIrAutenticacion o cámbialo al que uses)
        val btnNuevaSolicitud = findViewById<Button>(R.id.btnIrAutenticacion)
        val btnNotificaciones = findViewById<Button>(R.id.btnIrNotificaciones)
        val btnVisualizacion = findViewById<Button>(R.id.btnIrVisualizacion)

        // AHORA MANDA AL MAIN ACTIVITY (Formulario de Justificante)
        btnNuevaSolicitud.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, Notification::class.java)
            startActivity(intent)
        }

        btnVisualizacion.setOnClickListener {
            val intent = Intent(this, PaneldeVisualizacion::class.java)
            startActivity(intent)
        }
    }
}