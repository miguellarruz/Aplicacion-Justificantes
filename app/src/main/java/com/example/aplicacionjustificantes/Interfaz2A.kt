package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Interfaz2A : AppCompatActivity() {

    // 📌 ID del usuario que viaja a través de la aplicación
    private var idUsuarioLogueado: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.interfaz2)

        // 📌 Recuperamos el ID real asignado en el Login
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        // Inicialización explícita de vistas usando el nuevo diseño estilizado
        val spinnerTipo = findViewById<Spinner>(R.id.spinnerTipo)
        val layoutCamposMedicos = findViewById<LinearLayout>(R.id.layoutCamposMedicos)
        val etMotivo = findViewById<EditText>(R.id.etMotivo)
        val etInstitucion = findViewById<EditText>(R.id.etInstitucion)
        val etCedula = findViewById<EditText>(R.id.etCedula)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etDetalles = findViewById<EditText>(R.id.etDetalles)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)

        // Ajuste dinámico de barras de estado directamente sobre el contenedor del spinner de forma segura
        ViewCompat.setOnApplyWindowInsetsListener(spinnerTipo) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar las opciones del Spinner
        val opciones = arrayOf("Asunto Médico (Requiere receta)", "Asunto Personal / Familiar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spinnerTipo.adapter = adapter

        // Listener para ocultar o mostrar campos según la selección
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    layoutCamposMedicos.visibility = View.VISIBLE
                } else {
                    layoutCamposMedicos.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSiguiente.setOnClickListener {
            val tipoJustificante = spinnerTipo.selectedItem.toString()
            val posicionSpinner = spinnerTipo.selectedItemPosition
            val motivoText = etMotivo.text.toString().trim()
            val fechaText = etFecha.text.toString().trim()
            val detallesText = etDetalles.text.toString().trim()

            var institucionText = "N/A"
            var cedulaText = "N/A"

            if (motivoText.isEmpty() || fechaText.isEmpty() || detallesText.isEmpty()) {
                Toast.makeText(this, "Por favor completa los campos requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (posicionSpinner == 0) {
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
                putExtra("EXTRA_TIPO_POS", posicionSpinner)
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