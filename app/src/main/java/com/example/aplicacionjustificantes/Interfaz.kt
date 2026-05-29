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

class Interfaz : AppCompatActivity() {

    private lateinit var contenedorLista: LinearLayout
    private lateinit var txtListaVacia: TextView

    private var idUsuarioLogueado: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interfaz)

        // Recuperamos el ID real que viene desde el inicio de sesión
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        val btnNuevaSolicitud = findViewById<Button>(R.id.btnNuevaSolicitud)
        val btnNotificaciones = findViewById<Button>(R.id.btnIrNotificaciones)
        val btnVisualizacion = findViewById<Button>(R.id.btnIrVisualizacion)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        contenedorLista = findViewById(R.id.contenedorLista)
        txtListaVacia = findViewById(R.id.txtListaVacia)

        // Abre el formulario de llenado (Interfaz2A ya corregida)
        btnNuevaSolicitud.setOnClickListener {
            val intent = Intent(this, Interfaz2A::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }

        btnNotificaciones.setOnClickListener {
            val intent = Intent(this, Notification::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }

        btnVisualizacion.setOnClickListener {
            val intent = Intent(this, PaneldeVisualizacion::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }

        // Cierre de sesión seguro redirigiendo a tu Login exacto
        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, PrimeraVistaEder::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // Cada que el usuario regresa a esta pantalla, refresca la lista automáticamente
    override fun onResume() {
        super.onResume()
        cargarJustificantesDesdeBaseDatos()
    }

    private fun cargarJustificantesDesdeBaseDatos() {
        // Limpiamos la lista anterior para que no se dupliquen las tarjetas visuales
        contenedorLista.removeAllViews()

        val url = "http://10.0.2.2/justificantes_api/listar_justificantes.php?id_usuario=$idUsuarioLogueado"

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

                            agregarJustificanteALaLista("Estatus: $estatus", motivo)
                        }
                    } else {
                        actualizarVisibilidadHistorial()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    actualizarVisibilidadHistorial()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión con el servidor principal", Toast.LENGTH_SHORT).show()
                actualizarVisibilidadHistorial()
            }
        )

        queue.add(stringRequest)
    }

    private fun agregarJustificanteALaLista(titulo: String, motivo: String) {
        val vistaJustificante = layoutInflater.inflate(R.layout.item_justificante, null)

        val txtTitulo = vistaJustificante.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaJustificante.findViewById<TextView>(R.id.txtMotivoJustificante)

        txtTitulo.text = titulo
        txtMotivo.text = "Motivo: $motivo"

        // Te manda al panel de supervisión detallada si presionas la tarjeta
        vistaJustificante.setOnClickListener {
            val intent = Intent(this, PaneldeVisualizacion::class.java)
            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
            startActivity(intent)
        }

        contenedorLista.addView(vistaJustificante)
        actualizarVisibilidadHistorial()
    }

    private fun actualizarVisibilidadHistorial() {
        if (contenedorLista.childCount == 0) {
            txtListaVacia.visibility = View.VISIBLE
            contenedorLista.visibility = View.GONE
        } else {
            txtListaVacia.visibility = View.GONE
            contenedorLista.visibility = View.VISIBLE
        }
    }
}