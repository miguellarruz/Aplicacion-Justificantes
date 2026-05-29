package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class EnfermeriaActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private var idJustificanteActual: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enfermeria_panel)

        val btnAprobar = findViewById<Button>(R.id.btnAprobarEnfermera)
        val btnRechazar = findViewById<Button>(R.id.btnRechazarEnfermera)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionEnfermera)
        tvStatus = findViewById(R.id.tvNombreAlumnoRevision)

        // 📌 Al abrir la pantalla, jalamos de inmediato el justificante desde XAMPP
        cargarJustificantePendiente()

        btnAprobar.setOnClickListener {
            if (idJustificanteActual != -1) {
                tvStatus.text = "Justificante: ✅ APROBADO"
                Toast.makeText(this, "Estado actualizado a Aprobado", Toast.LENGTH_SHORT).show()
                // Nota: Aquí podrás implementar el cambio de estatus en BD más adelante
            }
        }

        btnRechazar.setOnClickListener {
            if (idJustificanteActual != -1) {
                tvStatus.text = "Justificante: ❌ RECHAZADO"
                Toast.makeText(this, "Estado actualizado a Rechazado", Toast.LENGTH_SHORT).show()
            }
        }

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, PrimeraVistaEder::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun cargarJustificantePendiente() {
        val url = "http://192.168.1.83/justificantes_api/obtener_estado_justificante.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        idJustificanteActual = jsonResponse.getInt("id_justificante")
                        val alumno = jsonResponse.getString("nombre_alumno")
                        val motivo = jsonResponse.getString("motivo")

                        // Ponemos el nombre del alumno real que obtuvimos de HeidiSQL
                        tvStatus.text = "Revisando a: $alumno\nMotivo: $motivo"
                    } else {
                        tvStatus.text = "No hay justificantes pendientes"
                    }
                } catch (e: Exception) {
                    tvStatus.text = "Error al procesar datos"
                }
            },
            {
                Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }
}