package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class EnfermeriaActivity : AppCompatActivity() {

    private lateinit var tvSinSolicitudesEnfermera: TextView
    private lateinit var layoutTarjetaRevision: LinearLayout
    private lateinit var tvNombreAlumnoRevision: TextView
    private lateinit var tvDetalleJustificante: TextView

    private var idJustificanteActual: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enfermeria_panel)

        // Vincular todos los componentes del XML
        tvSinSolicitudesEnfermera = findViewById(R.id.tvSinSolicitudesEnfermera)
        layoutTarjetaRevision = findViewById(R.id.layoutTarjetaRevision)
        tvNombreAlumnoRevision = findViewById(R.id.tvNombreAlumnoRevision)
        tvDetalleJustificante = findViewById(R.id.tvDetalleJustificante)

        val btnAprobar = findViewById<Button>(R.id.btnAprobarEnfermera)
        val btnRechazar = findViewById<Button>(R.id.btnRechazarEnfermera)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionEnfermera)

        // 📌 Buscamos el justificante pendiente en HeidiSQL al iniciar
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

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        idJustificanteActual = jsonResponse.getInt("id_justificante")
                        val alumno = jsonResponse.getString("nombre_alumno")
                        val motivo = jsonResponse.getString("motivo")

                        // 1. Inyectamos los datos reales en los TextViews del XML
                        tvNombreAlumnoRevision.text = "Alumno: $alumno"
                        tvDetalleJustificante.text = "Motivo: $motivo"

                        // 2. 📌 MÁGIA: Escondemos el mensaje de "No hay" y mostramos la tarjeta blanca con los botones
                        tvSinSolicitudesEnfermera.visibility = View.GONE
                        layoutTarjetaRevision.visibility = View.VISIBLE

                    } else {
                        // Si el servidor responde que está vacío, regresamos al estado inicial
                        mostrarPantallaVacia()
                    }
                } catch (e: Exception) {
                    // Si ocurre un error de lectura, mostramos el mensaje de error crudo en el texto
                    idJustificanteActual = -1
                    tvSinSolicitudesEnfermera.text = "Error al procesar los datos del servidor."
                    tvSinSolicitudesEnfermera.visibility = View.VISIBLE
                    layoutTarjetaRevision.visibility = View.GONE
                }
            },
            { error ->
                Toast.makeText(this, "Error de red al conectar con Enfermería", Toast.LENGTH_SHORT).show()
            }
        ) {
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
                        // Recargamos la pantalla para ver si hay otro alumno en espera
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

    private fun mostrarPantallaVacia() {
        idJustificanteActual = -1
        tvSinSolicitudesEnfermera.text = "No hay solicitudes de justificantes pendientes por revisar."
        tvSinSolicitudesEnfermera.visibility = View.VISIBLE
        layoutTarjetaRevision.visibility = View.GONE
    }
}