package com.example.aplicacionjustificantes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PaneldeVisualizacion : AppCompatActivity() {

    // 1. Declaramos la variable para el título dinámico
    private lateinit var tvTituloPanel: TextView
    private lateinit var tvContadorPendiente: TextView
    private lateinit var tvContadorAprobado: TextView
    private lateinit var tvContadorRechazado: TextView
    private lateinit var btnRegresarInterfaz: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.panel)

        // 2. Vinculamos el título del XML
        tvTituloPanel = findViewById(R.id.tvTituloPanel)
        tvContadorPendiente = findViewById(R.id.tvContadorPendiente)
        tvContadorAprobado = findViewById(R.id.tvContadorAprobado)
        tvContadorRechazado = findViewById(R.id.tvContadorRechazado)
        btnRegresarInterfaz = findViewById(R.id.btnRegresarInterfaz)

        // 3. Recuperamos el nombre guardado en la memoria interna (SesionUsuario)
        val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")

        // 4. Inyectamos el nombre de la cuenta registrada en el título
        tvTituloPanel.text = "Panel de visualización de estados (Pendiente, Aprobado, Rechazado) ($nombreUsuario)"

        // Acción para volver a la pantalla Interfaz
        btnRegresarInterfaz.setOnClickListener {
            val intent = Intent(this, Interfaz::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Por ahora se queda en 0 porque el usuario va empezando desde cero
        mostrarDatosEnPanel(pendientes = 0, aprobados = 0, rechazados = 0)
    }

    private fun mostrarDatosEnPanel(pendientes: Int, aprobados: Int, rechazados: Int) {
        tvContadorPendiente.text = pendientes.toString()
        tvContadorAprobado.text = aprobados.toString()
        tvContadorRechazado.text = rechazados.toString()
    }
}