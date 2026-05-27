package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EnfermeriaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enfermeria_panel)

        val btnAprobar = findViewById<Button>(R.id.btnAprobarEnfermera)
        val btnRechazar = findViewById<Button>(R.id.btnRechazarEnfermera)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionEnfermera)
        val tvStatus = findViewById<TextView>(R.id.tvNombreAlumnoRevision)

        btnAprobar.setOnClickListener {
            tvStatus.text = "Justificante: ✅ APROBADO"
            Toast.makeText(this, "Estado actualizado: Aprobado", Toast.LENGTH_SHORT).show()
            // Aquí en un futuro se hará el UPDATE al MySQL puerto 3307
        }

        btnRechazar.setOnClickListener {
            tvStatus.text = "Justificante: ❌ RECHAZADO"
            Toast.makeText(this, "Estado actualizado: Rechazado", Toast.LENGTH_SHORT).show()
        }

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, PrimeraVistaEder::class.java)
            startActivity(intent)
            finish()
        }
    }
}