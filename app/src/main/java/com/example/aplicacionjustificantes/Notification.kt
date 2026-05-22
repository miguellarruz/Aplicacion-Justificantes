package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Notification : AppCompatActivity() {

    private lateinit var btnRegresarDesdeNotis: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notis)

        btnRegresarDesdeNotis = findViewById(R.id.btnRegresarDesdeNotis)

        btnRegresarDesdeNotis.setOnClickListener {
            val intent = Intent(this, Interfaz::class.java)
            // Evita crear múltiples instancias de la pantalla principal
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Cierra esta ventana
        }
    }
}