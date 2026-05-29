package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Interfaz2A : AppCompatActivity() {

    // 📌 ID del usuario que viaja a través de la aplicación
    private var idUsuarioLogueado: Int = 1

    // Guardamos la posición seleccionada de forma manual (0 para Médico, 1 para Personal)
    private var posicionSeleccionada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.interfaz2)

        // 📌 Recuperamos el ID real asignado en el Login
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        // 🛠️ CORREGIDO: Buscamos como AutoCompleteTextView para acoplarse al XML arreglado
        val spinnerTipo = findViewById<AutoCompleteTextView>(R.id.spinnerTipo)
        val layoutCamposMedicos = findViewById<LinearLayout>(R.id.layoutCamposMedicos)
        val etMotivo = findViewById<EditText>(R.id.etMotivo)
        val etInstitucion = findViewById<EditText>(R.id.etInstitucion)
        val etCedula = findViewById<EditText>(R.id.etCedula)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etDetalles = findViewById<EditText>(R.id.etDetalles)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)

        ViewCompat.setOnApplyWindowInsetsListener(spinnerTipo) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🎨 Adaptador optimizado para Material Design (Texto fuerte y visible)
        val opciones = arrayOf("Asunto Médico (Requiere receta)", "Asunto Personal / Familiar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)
        spinnerTipo.setAdapter(adapter)

        // Por defecto, dejamos seleccionado el primer elemento ("Asunto Médico") para que se vea lleno
        spinnerTipo.setText(opciones[0], false)

        // 🔄 CORREGIDO: Listener moderno para ocultar/mostrar los campos médicos sin crasheos
        spinnerTipo.setOnItemClickListener { _, _, position, _ ->
            posicionSeleccionada = position
            if (position == 0) {
                layoutCamposMedicos.visibility = View.VISIBLE
            } else {
                layoutCamposMedicos.visibility = View.GONE
            }
        }

        btnSiguiente.setOnClickListener {
            // 🔄 CORREGIDO: Extraemos el texto directamente de la caja expuesta
            val tipoJustificante = spinnerTipo.text.toString()
            val motivoText = etMotivo.text.toString().trim()
            val fechaText = etFecha.text.toString().trim()
            val detallesText = etDetalles.text.toString().trim()

            var institucionText = "N/A"
            var cedulaText = "N/A"

            if (motivoText.isEmpty() || fechaText.isEmpty() || detallesText.isEmpty()) {
                Toast.makeText(this, "Por favor completa los campos requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (posicionSeleccionada == 0) {
                institucionText = etInstitucion.text.toString().trim()
                cedulaText = etCedula.text.toString().trim()

                if (institucionText.isEmpty() || cedulaText.isEmpty()) {
                    Toast.makeText(this, "Por favor llena los datos de la receta médica", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Enviamos el paquete completo de datos hacia el MainActivity (que maneja la foto)
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
                putExtra("EXTRA_TIPO", tipoJustificante)
                putExtra("EXTRA_TIPO_POS", posicionSeleccionada)
                putExtra("EXTRA_MOTIVO", motivoText)
                putExtra("EXTRA_INSTITUCION", institucionText)
                putExtra("EXTRA_CEDULA", cedulaText)
                putExtra("EXTRA_FECHA", fechaText)
                putExtra("EXTRA_DETALLES", detallesText)
            }
            startActivity(intent)
        }
    }
}