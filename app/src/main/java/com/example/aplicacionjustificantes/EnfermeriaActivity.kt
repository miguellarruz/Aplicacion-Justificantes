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

        // 📌 Al abrir la pantalla, busca el justificante pendiente en HeidiSQL
        cargarJustificantePendiente()

        btnAprobar.setOnClickListener {
            if (idJustificanteActual != -1) {
                actualizarEstatusEnServidor("Aprobado")
            } else {
                Toast.makeText(this, "No hay ningún justificante seleccionado", Toast.LENGTH_SHORT).show()
            }
        }

        btnRechazar.setOnClickListener {
            if (idJustificanteActual != -1) {
                actualizarEstatusEnServidor("Rechazado")
            } else {
                Toast.makeText(this, "No hay ningún justificante seleccionado", Toast.LENGTH_SHORT).show()
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

                        tvStatus.text = "Revisando a: $alumno\nMotivo: $motivo"
                    } else {
                        idJustificanteActual = -1
                        tvStatus.text = "No hay justificantes pendientes por revisar"
                    }
                } catch (e: Exception) {
                    tvStatus.text = "No hay justificantes pendientes"
                }
            },
            {
                Toast.makeText(this, "Error de red al conectar con Enfermería", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }

    private fun actualizarEstatusEnServidor(nuevoEstatus: String) {
        val url = "http://192.168.1.83/justificantes_api/actualizar_justificante.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    if (status == "success") {
                        Toast.makeText(this, "Justificante $nuevoEstatus con éxito", Toast.LENGTH_SHORT).show()
                        // Recargamos la pantalla para ver si hay otro alumno en la fila
                        cargarJustificantePendiente()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error de respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_justificante"] = idJustificanteActual.toString()
                params["estatus"] = nuevoEstatus
                return params
            }
        }
        queue.add(stringRequest)
    }
}