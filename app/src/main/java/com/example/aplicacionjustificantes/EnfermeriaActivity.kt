package com.example.aplicacionjustificantes

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
    private lateinit var tvDatosExtraRevision: TextView
    private lateinit var ivEvidenciaEnfermera: ImageView

    private var idJustificanteActual: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enfermeria_panel)

        tvSinSolicitudesEnfermera = findViewById(R.id.tvSinSolicitudesEnfermera)
        layoutTarjetaRevision = findViewById(R.id.layoutTarjetaRevision)
        tvNombreAlumnoRevision = findViewById(R.id.tvNombreAlumnoRevision)
        tvDetalleJustificante = findViewById(R.id.tvDetalleJustificante)
        tvDatosExtraRevision = findViewById(R.id.tvDatosExtraRevision)
        ivEvidenciaEnfermera = findViewById(R.id.ivEvidenciaEnfermera)

        val btnAprobar = findViewById<Button>(R.id.btnAprobarEnfermera)
        val btnRechazar = findViewById<Button>(R.id.btnRechazarEnfermera)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesionEnfermera)

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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun cargarJustificantePendiente() {
        // 🔑 CORREGIDO: Config.IP_SERVIDOR ya incluye "justificantes_api/"
        val url = Config.endpoint("obtener_estado_justificante.php")
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        idJustificanteActual = jsonResponse.getInt("id_justificante")
                        val alumno = jsonResponse.getString("nombre_alumno")
                        val motivo = jsonResponse.getString("motivo")
                        val fechaInasistencia = jsonResponse.getString("fecha_inasistencia")
                        val institucion = jsonResponse.getString("institucion")
                        val cedula = jsonResponse.getString("cedula_medica")

                        val fotoBase64 = jsonResponse.optString("foto_base64", "")

                        tvNombreAlumnoRevision.text = "Alumno: $alumno"
                        tvDetalleJustificante.text = "Motivo: $motivo\nFecha Inasistencia: $fechaInasistencia"
                        tvDatosExtraRevision.text = "Institución médica/Lugar: $institucion\nCédula Profesional: $cedula"

                        if (fotoBase64.isNotEmpty() && fotoBase64.length > 50) {
                            try {
                                val decodedString = Base64.decode(fotoBase64, Base64.DEFAULT)
                                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                if (decodedByte != null) {
                                    ivEvidenciaEnfermera.setImageBitmap(decodedByte)
                                } else {
                                    ivEvidenciaEnfermera.setImageResource(android.R.drawable.ic_menu_gallery)
                                }
                            } catch (e: Exception) {
                                ivEvidenciaEnfermera.setImageResource(android.R.drawable.ic_menu_gallery)
                            }
                        } else {
                            ivEvidenciaEnfermera.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                        }

                        tvSinSolicitudesEnfermera.visibility = View.GONE
                        layoutTarjetaRevision.visibility = View.VISIBLE

                    } else {
                        mostrarPantallaVacia()
                    }
                } catch (e: Exception) {
                    mostrarPantallaVacia()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error de red al conectar con Enfermeria: ${NetworkUtils.errorMessage(error)}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                // 🚀 TRUCO CLAVE: Encabezado para evadir el firewall de AwardSpace
                headers.putAll(Config.headers())
                return headers
            }
        }
        queue.add(NetworkUtils.prepare(stringRequest))
    }

    private fun actualizarEstatusEnServidor(nuevoEstatus: String) {
        // 🔑 CORREGIDO: Config.IP_SERVIDOR ya incluye "justificantes_api/"
        val url = Config.endpoint("actualizar_justificante.php")
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.optString("message", "")

                    if (status == "success") {
                        Toast.makeText(this, "¡Éxito!: Justificante $nuevoEstatus", Toast.LENGTH_SHORT).show()
                        cargarJustificantePendiente()
                    } else {
                        Toast.makeText(this, "Error del Servidor: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Respuesta inesperada: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Fallo de red: ${NetworkUtils.errorMessage(error)}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_justificante"] = idJustificanteActual.toString()
                params["estatus"] = nuevoEstatus
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                // 🚀 TRUCO CLAVE: Sincronizado para AwardSpace
                headers.putAll(Config.headers())
                return headers
            }
        }
        queue.add(NetworkUtils.prepare(stringRequest))
    }

    private fun mostrarPantallaVacia() {
        idJustificanteActual = -1
        tvSinSolicitudesEnfermera.text = "No hay solicitudes de justificantes pendientes por revisar."
        tvSinSolicitudesEnfermera.visibility = View.VISIBLE
        layoutTarjetaRevision.visibility = View.GONE
    }
}
