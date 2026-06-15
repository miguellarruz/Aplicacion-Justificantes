package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class Interfaz2A : AppCompatActivity() {

    private var idUsuarioLogueado: Int = 1
    private var posicionSeleccionada: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContentView(R.layout.interfaz2)


        val vistaRaiz = findViewById<View>(R.id.main)
        if (vistaRaiz != null) {
            ViewCompat.setOnApplyWindowInsetsListener(vistaRaiz) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }


        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)


        val spinnerTipo = findViewById<AutoCompleteTextView>(R.id.spinnerTipo)
        val layoutCamposMedicos = findViewById<LinearLayout>(R.id.layoutCamposMedicos)

        val etMotivo = findViewById<TextInputEditText>(R.id.etMotivo)
        val etFecha = findViewById<TextInputEditText>(R.id.etFecha)
        val etInstitucion = findViewById<TextInputEditText>(R.id.etInstitucion)
        val etCedula = findViewById<TextInputEditText>(R.id.etCedula)
        val etDetalles = findViewById<TextInputEditText>(R.id.etDetalles)
        val btnSiguiente = findViewById<MaterialButton>(R.id.btnSiguiente)


        val opciones = arrayOf("Asunto Médico (Requiere receta)", "Asunto Personal / Familiar")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opciones)
        spinnerTipo.setAdapter(adapter)


        spinnerTipo.setText(opciones[0], false)


        spinnerTipo.setOnItemClickListener { _, _, position, _ ->
            posicionSeleccionada = position
            if (position == 0) {
                layoutCamposMedicos.visibility = View.VISIBLE
            } else {
                layoutCamposMedicos.visibility = View.GONE
            }
        }


        btnSiguiente.setOnClickListener {
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