package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaneldeVisualizacion : AppCompatActivity() {

    private lateinit var tvContadorPendiente: TextView
    private lateinit var tvContadorAprobado: TextView
    private lateinit var tvContadorRechazado: TextView
    private lateinit var btnRegresarInterfaz: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.panel)

        tvContadorPendiente = findViewById(R.id.tvContadorPendiente)
        tvContadorAprobado = findViewById(R.id.tvContadorAprobado)
        tvContadorRechazado = findViewById(R.id.tvContadorRechazado)
        btnRegresarInterfaz = findViewById(R.id.btnRegresarInterfaz)

        // Acción para volver a la pantalla Interfaz
        btnRegresarInterfaz.setOnClickListener {
            val intent = Intent(this, Interfaz::class.java)
            startActivity(intent)
            finish() // Cierra esta pantalla para no acumular actividades en segundo plano
        }

        mostrarDatosEnPanel(pendientes = 5, aprobados = 14, rechazados = 2)
    }

    private fun mostrarDatosEnPanel(pendientes: Int, aprobados: Int, rechazados: Int) {
        tvContadorPendiente.text = pendientes.toString()
        tvContadorAprobado.text = aprobados.toString()
        tvContadorRechazado.text = rechazados.toString()
    }
}