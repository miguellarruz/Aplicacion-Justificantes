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

            // Capturamos el ID del usuario enviado desde Interfaz
            idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

            // Vincular componentes del XML
            tvTituloPanel = findViewById(R.id.tvTituloPanel)
            tvContadorPendiente = findViewById(R.id.tvContadorPendiente)
            tvContadorAprobado = findViewById(R.id.tvContadorAprobado)
            tvContadorRechazado = findViewById(R.id.tvContadorRechazado)
            btnRegresarInterfaz = findViewById(R.id.btnRegresarInterfaz)

            // Recuperar el nombre guardado en memoria interna
            val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
            val nombreUsuario = sharedPreferences.getString("nombre_cuenta", "Usuario")

            // Inyectamos el texto dinámico en el título
            tvTituloPanel.text = "Panel de visualización de estados (Pendiente, Aprobado, Rechazado) ($nombreUsuario)"

            // Configurar el botón para volver atrás sin romper la cadena del ID
            btnRegresarInterfaz.setOnClickListener {
                val intent = Intent(this, Interfaz::class.java)
                intent.putExtra("ID_USUARIO_LOGUEADO", idUsuarioLogueado)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }

            // Consultar a la base de datos en tiempo real mediante el servidor
            consultarContadoresServidor()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la interfaz: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun consultarContadoresServidor() {
        // 🔑 CORREGIDO: Config.IP_SERVIDOR ya incluye "justificantes_api/" de forma nativa
        val url = "${Config.IP_SERVIDOR}obtener_estado_justificante.php?id_usuario=$idUsuarioLogueado"
        val queue = Volley.newRequestQueue(this)

        // 🛠️ CORREGIDO: Convertido a 'object' para poder inyectar los Headers requeridos por AwardSpace
        val stringRequest = object : StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        val pendientes = jsonResponse.getInt("pendientes")
                        val aprobados = jsonResponse.getInt("aprobados")
                        val rechazados = jsonResponse.getInt("rechazados")

                        // Inyectamos los números calculados por la base de datos
                        mostrarDatosEnPanel(pendientes, aprobados, rechazados)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al leer contadores", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                val msgError = error.message ?: "Filtro de seguridad del hosting o problema de red"
                Toast.makeText(this, "Error de red al cargar el panel de estados: $msgError", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                // 🚀 TRUCO CLAVE: Encabezado obligatorio para saltar el firewall anti-bots del hosting gratuito
                headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                return headers
            }
        }

        queue.add(stringRequest)
    }

    private fun mostrarDatosEnPanel(pendientes: Int, aprobados: Int, rechazados: Int) {
        tvContadorPendiente.text = pendientes.toString()
        tvContadorAprobado.text = aprobados.toString()
        tvContadorRechazado.text = rechazados.toString()
    }
}