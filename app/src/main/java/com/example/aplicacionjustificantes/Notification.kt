package com.example.aplicacionjustificantes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Notification : AppCompatActivity() {

    private lateinit var btnRegresarDesdeNotis: Button
    private lateinit var tvTituloNotificaciones: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notis)

        // Vincular los componentes del XML
        btnRegresarDesdeNotis = findViewById(R.id.btnRegresarDesdeNotis)
        tvTituloNotificaciones = findViewById(R.id.tvTituloNotificaciones)

        // 1. Ir a la memoria interna y buscar el nombre guardado
        val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)

        // Si por alguna razón no encuentra ningún nombre, pondrá "Usuario" por defecto
        val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")

        // 2. Mostrar el nombre de la cuenta registrada dinámicamente en el TextView
        tvTituloNotificaciones.text = "Sistema de notificaciones para cambios de estado en justificantes ($nombreUsuario)"

        // Configurar el botón para regresar
        btnRegresarDesdeNotis.setOnClickListener {
            val intent = Intent(this, Interfaz::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Cierra esta ventana
        }
    }
}