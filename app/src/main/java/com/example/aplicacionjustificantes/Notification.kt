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
        // Asegúrate de que tu XML de notificaciones se llame "notis" o cámbialo por el nombre correcto
        setContentView(R.layout.notis)

        // Recuperamos el ID del alumno logueado
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        // Enlazamos componentes de tu XML
        contenedorNotificaciones = findViewById(R.id.contenedorLista) // El LinearLayout vertical dentro del ScrollView
        txtNotisVacias = findViewById(R.id.txtListaVacia) // El TextView que dice que no hay elementos

        cargarNotificaciones()
    }

    private fun cargarNotificaciones() {
        contenedorNotificaciones.removeAllViews()

        //  Usando tu IP local corregida 192.168.1.83
        val url = "http://192.168.56.1/justificantes_api/listar_notificaciones.php?id_usuario=$idUsuarioLogueado"

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
        // Reutilizamos tu diseño de item_justificante para mostrar el resultado
        val vistaNoti = layoutInflater.inflate(R.layout.item_justificante, null)

        val txtTitulo = vistaNoti.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaNoti.findViewById<TextView>(R.id.txtMotivoJustificante)

        // Personalizamos el diseño según la respuesta del administrador
        if (estatus.equals("Aceptado", ignoreCase = true)) {
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
            // Si tienes un mensaje personalizado de "No tienes notificaciones", puedes cambiar el texto aquí:
            txtNotisVacias.text = "🔔 No tienes nuevas notificaciones de tus justificantes."
            contenedorNotificaciones.visibility = View.GONE
        } else {
            txtNotisVacias.visibility = View.GONE
            contenedorNotificaciones.visibility = View.VISIBLE
        }
    }
}