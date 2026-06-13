package com.example.aplicacionjustificantes

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class Notification : AppCompatActivity() {

    private lateinit var contenedorNotificaciones: LinearLayout
    private lateinit var txtNotisVacias: TextView
    private var idUsuarioLogueado: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notis)

        // Recuperamos el ID del alumno logueado
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        // Enlazamos componentes del XML
        contenedorNotificaciones = findViewById(R.id.contenedorLista)
        txtNotisVacias = findViewById(R.id.txtListaVacia)

        cargarNotificaciones()
    }

    private fun cargarNotificaciones() {
        contenedorNotificaciones.removeAllViews()

        // ✅ CORREGIDO: Usando el objeto global Config
        val url = "${Config.IP_SERVIDOR}/justificantes_api/listar_notificaciones.php?id_usuario=$idUsuarioLogueado"

        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        val jsonArray = jsonResponse.getJSONArray("datos")

                        for (i in 0 until jsonArray.length()) {
                            val objeto = jsonArray.getJSONObject(i)
                            val estatus = objeto.getString("estatus")
                            val motivo = objeto.getString("motivo")

                            // Inflamos una tarjeta para la notificación
                            agregarNotificacionALaLista(estatus, motivo)
                        }
                    } else {
                        actualizarVisibilidadNotis()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    actualizarVisibilidadNotis()
                }
            },
            { error ->
                Toast.makeText(this, "Error al conectar con las notificaciones", Toast.LENGTH_SHORT).show()
                actualizarVisibilidadNotis()
            }
        )

        queue.add(stringRequest)
    }

    private fun agregarNotificacionALaLista(estatus: String, motivo: String) {
        val vistaNoti = layoutInflater.inflate(R.layout.item_justificante, null)

        val txtTitulo = vistaNoti.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaNoti.findViewById<TextView>(R.id.txtMotivoJustificante)

        // Personalizamos el diseño según la respuesta de Enfermería
        if (estatus.equals("Aceptado", ignoreCase = true) || estatus.equals("Aprobado", ignoreCase = true)) {
            txtTitulo.text = "SOLICITUD APROBADA"
            txtTitulo.setTextColor(android.graphics.Color.parseColor("#2E7D32")) // Verde
        } else {
            txtTitulo.text = "SOLICITUD RECHAZADA"
            txtTitulo.setTextColor(android.graphics.Color.parseColor("#C62828")) // Rojo
        }

        txtMotivo.text = "Tu justificante por \"$motivo\" ha sido cambiado a estatus: $estatus."

        contenedorNotificaciones.addView(vistaNoti)
        actualizarVisibilidadNotis()
    }

    private fun actualizarVisibilidadNotis() {
        if (contenedorNotificaciones.childCount == 0) {
            txtNotisVacias.visibility = View.VISIBLE
            txtNotisVacias.text = "🔔 No tienes nuevas notificaciones de tus justificantes."
            contenedorNotificaciones.visibility = View.GONE
        } else {
            txtNotisVacias.visibility = View.GONE
            contenedorNotificaciones.visibility = View.VISIBLE
        }
    }
}