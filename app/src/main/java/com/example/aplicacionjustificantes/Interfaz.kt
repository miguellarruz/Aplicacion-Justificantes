package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aplicacionjustificantes.databinding.InterfazBinding

/**
 * Main dashboard activity.
 * Suggestion: Rename to HomeActivity or DashboardActivity for clarity.
 */
class Interfaz : AppCompatActivity() {

    private lateinit var binding: InterfazBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Use View Binding for better safety and performance
        binding = InterfazBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {
        // Navigation listeners
        binding.btnIrAutenticacion.setOnClickListener {
            startActivity(Intent(this, Interfaz2A::class.java))
        }

        binding.btnIrNotificaciones.setOnClickListener {
            startActivity(Intent(this, Notification::class.java))
        }

        binding.btnIrVisualizacion.setOnClickListener {
            startActivity(Intent(this, PaneldeVisualizacion::class.java))
        }

        // Example data - In a real app, consider using a RecyclerView with an Adapter
        agregarJustificanteALaLista("Justificante Médico - 26/05/2026", "Cita en el IMSS por malestar general")
    }

    /**
     * Adds a summary item to the list container.
     * Suggestion: Replace this manual inflation with a RecyclerView for scalability.
     */
    private fun agregarJustificanteALaLista(titulo: String, motivo: String) {
        val vistaJustificante = layoutInflater.inflate(R.layout.item_justificante, binding.contenedorLista, false)

        val txtTitulo = vistaJustificante.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaJustificante.findViewById<TextView>(R.id.txtMotivoJustificante)

        txtTitulo.text = titulo
        txtMotivo.text = getString(R.string.motivo_format, motivo)

        binding.contenedorLista.addView(vistaJustificante)
    }
}
