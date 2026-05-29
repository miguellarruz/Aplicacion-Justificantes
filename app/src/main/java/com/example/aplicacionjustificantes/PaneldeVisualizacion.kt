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
            // 📌 Vinculamos con tu diseño XML que se llama "panel.xml"
            setContentView(R.layout.panel)

            // Capturamos el ID del usuario enviado desde Interfaz
            idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)

            // Vincular componentes con los IDs exactos de tu XML
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

            // 📌 MODIFICADO: Ahora en lugar de poner ceros fijos, va a consultar a la base de datos en tiempo real
            consultarContadoresServidor()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la interfaz: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // 📌 NUEVA FUNCIÓN: Se conecta con tu PHP mandando el ID del alumno logueado por método POST
    private fun consultarContadoresServidor() {
        val url = "http://192.168.56.1/justificantes_api/obtener_estado_justificante.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(Method.POST, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")

                    if (status == "success") {
                        val pendientes = jsonResponse.getInt("pendientes")
                        val aprobados = jsonResponse.getInt("aprobados")
                        val rechazados = jsonResponse.getInt("rechazados")

                        // Inyectamos los números que calculó el PHP en HeidiSQL
                        mostrarDatosEnPanel(pendientes, aprobados, rechazados)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al leer contadores", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de red al cargar el panel de estados", Toast.LENGTH_SHORT).show()
            }
        ) {
            // Mandamos el ID del usuario actual para que el PHP sepa de quién contar las solicitudes
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_usuario"] = idUsuarioLogueado.toString()
                return params
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