package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
        enableEdgeToEdge()
        setContentView(R.layout.interfaz)

        val vistaRaiz = findViewById<LinearLayout>(R.id.main)
        if (vistaRaiz != null) {
            ViewCompat.setOnApplyWindowInsetsListener(vistaRaiz) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

        val btnNuevaSolicitud = findViewById<Button>(R.id.btnNuevaSolicitud)
        val btnNotificaciones = findViewById<Button>(R.id.btnIrNotificaciones)
        val btnVisualizacion = findViewById<Button>(R.id.btnIrVisualizacion)
        val btnCerrarSesion = findViewById<Button>(R.id.btnCerrarSesion)

        contenedorLista = findViewById(R.id.contenedorLista)
        txtListaVacia = findViewById(R.id.txtListaVacia)

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

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, PrimeraVistaEder::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarJustificantesDesdeBaseDatos()
    }

    private fun cargarJustificantesDesdeBaseDatos() {
        contenedorLista.removeAllViews()

        val url = "${Config.IP_SERVIDOR}/justificantes_api/listar_justificantes.php?id_usuario=$idUsuarioLogueado"

        val queue = Volley.newRequestQueue(this)

        // Convertimos a un objeto StringRequest explícito para poder sobreescribir sus métodos
        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        // 📌 CORREGIDO: Tu PHP retorna la clave "data", no "datos"
                        val jsonArray = jsonResponse.getJSONArray("data")

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
        ) {
            // 🔥 AGREGADO: Cabecera indispensable para saltarse el filtro de ngrok
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["ngrok-skip-browser-warning"] = "true"
                return headers
            }
        }

        queue.add(stringRequest)
    }

    private fun agregarJustificanteALaLista(titulo: String, motivo: String) {
        val vistaJustificante = layoutInflater.inflate(R.layout.item_justificante, null)

        val txtTitulo = vistaJustificante.findViewById<TextView>(R.id.txtTituloJustificante)
        val txtMotivo = vistaJustificante.findViewById<TextView>(R.id.txtMotivoJustificante)

        txtTitulo.text = titulo
        txtMotivo.text = "Motivo: $motivo"

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