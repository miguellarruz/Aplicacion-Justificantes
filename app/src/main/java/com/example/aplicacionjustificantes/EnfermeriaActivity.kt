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

        // 📌 MODIFICADO: Cambiado a StringRequest de tipo POST para acoplarse al PHP unificado
        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        idJustificanteActual = jsonResponse.getInt("id_justificante")
                        val alumno = jsonResponse.getString("nombre_alumno")
                        val motivo = jsonResponse.getString("motivo")

                        // 📌 Coloca de forma dinámica el nombre del alumno y motivo real de la BD
                        tvStatus.text = "Revisando a: $alumno\nMotivo: $motivo"
                    } else {
                        idJustificanteActual = -1
                        val msg = jsonResponse.optString("message", "No hay justificantes pendientes por revisar")
                        tvStatus.text = msg
                    }
                } catch (e: Exception) {
                    // 📌 DIAGNÓSTICO: Si el PHP responde algo raro, aquí verás el texto crudo en lugar de una pantalla en blanco
                    tvStatus.text = "Error de respuesta. Respuesta cruda:\n$response"
                }
            },
            { error ->
                Toast.makeText(this, "Error de red al conectar con Enfermería", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Mandamos los parámetros vacíos porque para Enfermería queremos todos los registros pendientes de forma general
            override fun getParams(): MutableMap<String, String> {
                return HashMap()
            }
        }
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
                        // Recargamos la pantalla de inmediato para ver al siguiente alumno
                        cargarJustificantePendiente()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error de respuesta al actualizar", Toast.LENGTH_SHORT).show()
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