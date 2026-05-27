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

    private lateinit var spinnerTipo: Spinner
    private lateinit var layoutCamposMedicos: LinearLayout
    private lateinit var etMotivo: EditText
    private lateinit var etInstitucion: EditText
    private lateinit var etCedula: EditText
    private lateinit var etFecha: EditText
    private lateinit var etDetalles: EditText
    private lateinit var btnSiguiente: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.interfaz2)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerTipo = findViewById(R.id.spinnerTipo)
        layoutCamposMedicos = findViewById(R.id.layoutCamposMedicos)
        etMotivo = findViewById(R.id.etMotivo)
        etInstitucion = findViewById(R.id.etInstitucion)
        etCedula = findViewById(R.id.etCedula)
        etFecha = findViewById(R.id.etFecha)
        etDetalles = findViewById(R.id.etDetalles)
        btnSiguiente = findViewById(R.id.btnSiguiente)

        // Configurar las opciones del Spinner
        val opciones = arrayOf("Asunto Médico (Requiere receta)", "Asunto Personal / Familiar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        spinnerTipo.adapter = adapter

        // Listener para ocultar o mostrar campos según la selección
        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Seleccionó Médico -> Mostrar campos
                    layoutCamposMedicos.visibility = View.VISIBLE
                } else {
                    // Seleccionó Personal -> Ocultar campos médicos por completo
                    layoutCamposMedicos.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnSiguiente.setOnClickListener {
            val tipoJustificante = spinnerTipo.selectedItem.toString()
            val motivoText = etMotivo.text.toString().trim()
            val fechaText = etFecha.text.toString().trim()
            val detallesText = etDetalles.text.toString().trim()

            var institucionText = "N/A"
            var cedulaText = "N/A"

            // Validaciones según el tipo de motivo
            if (motivoText.isEmpty() || fechaText.isEmpty() || detallesText.isEmpty()) {
                Toast.makeText(this, "Por favor completa los campos requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (spinnerTipo.selectedItemPosition == 0) { // Si es Médico
                institucionText = etInstitucion.text.toString().trim()
                cedulaText = etCedula.text.toString().trim()

                if (institucionText.isEmpty() || cedulaText.isEmpty()) {
                    Toast.makeText(this, "Por favor llena los datos de la receta médica", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Si pasa las validaciones, mandamos todo al MainActivity
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("EXTRA_TIPO", tipoJustificante)
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