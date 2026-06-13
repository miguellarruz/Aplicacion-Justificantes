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

    // 🌐 TU ENLACE SEGURO DE NGROK ACTUALIZADO
    private val IP_SERVIDOR = "https://wriggle-luster-renderer.ngrok-free.dev"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enfermeria_panel)

        // Vincular componentes del XML nuevos y antiguos
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
        // ✅ CORREGIDO: Se quitó "http://" y se cambió a método GET para que el PHP entre directo al Bloque B (Enfermería)
        val url = "$IP_SERVIDOR/justificantes_api/obtener_estado_justificante.php"
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
                        val fechaInasistencia = jsonResponse.getString("fecha_inasistencia")
                        val institucion = jsonResponse.getString("institucion")
                        val cedula = jsonResponse.getString("cedula_medica")
                        val fotoBase64 = jsonResponse.getString("foto_base64")

                        // 1. Mostrar textos estructurados
                        tvNombreAlumnoRevision.text = "Alumno: $alumno"
                        tvDetalleJustificante.text = "Motivo: $motivo\nFecha Inasistencia: $fechaInasistencia"
                        tvDatosExtraRevision.text = "Institución médica/Lugar: $institucion\nCédula Profesional: $cedula"

                        // 2. DECODIFICAR FOTO BASE64 A IMAGEN EN VIVO
                        if (fotoBase64.isNotEmpty()) {
                            try {
                                val decodedString = Base64.decode(fotoBase64, Base64.DEFAULT)
                                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                ivEvidenciaEnfermera.setImageBitmap(decodedByte)
                            } catch (e: Exception) {
                                ivEvidenciaEnfermera.setImageResource(android.R.drawable.ic_menu_gallery)
                            }
                        } else {
                            ivEvidenciaEnfermera.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
                        }

                        // 3. Ajustar visibilidades
                        tvSinSolicitudesEnfermera.visibility = View.GONE
                        layoutTarjetaRevision.visibility = View.VISIBLE

                    } else {
                        mostrarPantallaVacia()
                    }
                } catch (e: Exception) {
                    idJustificanteActual = -1
                    tvSinSolicitudesEnfermera.text = "Error al procesar los datos."
                    tvSinSolicitudesEnfermera.visibility = View.VISIBLE
                    layoutTarjetaRevision.visibility = View.GONE
                }
            },
            {
                Toast.makeText(this, "Error de red al conectar con Enfermería", Toast.LENGTH_SHORT).show()
            }
        )
        queue.add(stringRequest)
    }

    private fun actualizarEstatusEnServidor(nuevoEstatus: String) {
        // ✅ CORREGIDO: Se quitó el "http://" inicial para evitar el choque de protocolos
        val url = "$IP_SERVIDOR/justificantes_api/actualizar_justificante.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    if (status == "success") {
                        Toast.makeText(this, "Justificante $nuevoEstatus con éxito", Toast.LENGTH_SHORT).show()
                        cargarJustificantePendiente()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show()
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