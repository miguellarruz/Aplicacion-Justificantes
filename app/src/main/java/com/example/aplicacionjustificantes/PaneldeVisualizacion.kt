package com.example.aplicacionjustificantes

import android.content.Context
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

class PaneldeVisualizacion : AppCompatActivity() {

    private lateinit var tvTituloPanel: TextView
    private lateinit var tvContadorPendiente: TextView
    private lateinit var tvContadorAprobado: TextView
    private lateinit var tvContadorRechazado: TextView
    private lateinit var btnRegresarInterfaz: Button

    private var idUsuarioLogueado: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.panel)


            idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)


            tvTituloPanel = findViewById(R.id.tvTituloPanel)
            tvContadorPendiente = findViewById(R.id.tvContadorPendiente)
            tvContadorAprobado = findViewById(R.id.tvContadorAprobado)
            tvContadorRechazado = findViewById(R.id.tvContadorRechazado)
            btnRegresarInterfaz = findViewById(R.id.btnRegresarInterfaz)


            val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
            val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")


            tvTituloPanel.text = "Panel de visualización de estados (Pendiente, Aprobado, Rechazado) ($nombreUsuario)"


            btnRegresarInterfaz.setOnClickListener {
                val intent = Intent(this, Interfaz::class.java)
                intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }


            consultarContadoresServidor()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la interfaz: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun consultarContadoresServidor() {

        val url = Config.endpoint("obtener_estado_justificante.php?id_usuario=$idUsuarioLogueado")
        val queue = Volley.newRequestQueue(this)


        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        val pendientes = jsonResponse.getInt("pendientes")
                        val aprobados = jsonResponse.getInt("aprobados")
                        val rechazados = jsonResponse.getInt("rechazados")


                        mostrarDatosEnPanel(pendientes, aprobados, rechazados)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al leer contadores", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val msgError = NetworkUtils.errorMessage(error)
                Toast.makeText(this, "Error de red al cargar el panel de estados: $msgError", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()

                headers.putAll(Config.headers())
                return headers
            }
        }

        queue.add(NetworkUtils.prepare(stringRequest))
    }

    private fun mostrarDatosEnPanel(pendientes: Int, aprobados: Int, rechazados: Int) {
        tvContadorPendiente.text = pendientes.toString()
        tvContadorAprobado.text = aprobados.toString()
        tvContadorRechazado.text = rechazados.toString()
    }
}
