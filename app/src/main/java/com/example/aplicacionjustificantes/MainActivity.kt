package com.example.aplicacionjustificantes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var btnSeleccionar: Button
    private lateinit var btnEnviar: Button
    private lateinit var txtArchivo: TextView
    private lateinit var imagePreview: ImageView

    private var archivoUri: Uri? = null
    private var idUsuarioLogueado: Int = 1

    // Variables para recibir lo que escribió el alumno en la Interfaz2A
    private var motivoRecibido: String = ""
    private var fechaRecibida: String = ""

    private val seleccionarArchivoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data: Intent? = result.data
            archivoUri = data?.data

            if (archivoUri != null) {
                txtArchivo.text = getString(R.string.archivo_seleccionado)
                val tipo = contentResolver.getType(archivoUri!!)

                if (tipo != null && tipo.startsWith("image/")) {
                    imagePreview.setImageURI(archivoUri)
                } else {
                    imagePreview.setImageResource(android.R.drawable.ic_menu_save)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // !!! AQUÍ RECUPERAMOS LOS DATOS DE LA PANTALLA ANTERIOR !!!
        motivoRecibido = intent.getStringExtra("EXTRA_MOTIVO") ?: "Sin motivo"
        fechaRecibida = intent.getStringExtra("EXTRA_FECHA") ?: "2026-01-01"

        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnEnviar = findViewById(R.id.btnEnviar)
        txtArchivo = findViewById(R.id.txtArchivo)
        imagePreview = findViewById(R.id.imagePreview)

        btnSeleccionar.setOnClickListener {
            seleccionarArchivo()
        }

        btnEnviar.setOnClickListener {
            if (archivoUri != null) {
                guardarJustificanteEnBaseDatos()
            } else {
                Toast.makeText(this, getString(R.string.selecciona_archivo_primero), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarJustificanteEnBaseDatos() {
        val url = "http://192.168.2.94/justificantes_api/guardar_justificante.php"
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST,
            url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.getString("message")

                    if (status == "success") {
                        Toast.makeText(this@MainActivity, getString(R.string.exito_mensaje, message), Toast.LENGTH_LONG).show()
                        finish() // Cierra esta pantalla y regresa al inicio tras el éxito
                    } else {
                        Toast.makeText(this@MainActivity, getString(R.string.error_servidor, message), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, getString(R.string.error_procesar_respuesta, e.message), Toast.LENGTH_LONG).show()
                }
            },
            { _ ->
                Toast.makeText(this@MainActivity, getString(R.string.error_red), Toast.LENGTH_LONG).show()
            },
        ) {

            // !!! AQUÍ PONEMOS LOS DATOS ENVIADOS DESDE INTERFAZ2A !!!
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_usuario"] = idUsuarioLogueado.toString()
                params["motivo"] = motivoRecibido          // <--- Cambiado de Fijo a Dinámico
                params["fecha_inasistencia"] = fechaRecibida // <--- Cambiado de Fijo a Dinámico
                params["ruta_foto"] = archivoUri.toString()
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun seleccionarArchivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            val tiposPermitidos = arrayOf("image/jpeg", "image/png", "application/pdf")
            putExtra(Intent.EXTRA_MIME_TYPES, tiposPermitidos)
        }
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, getString(R.string.selecciona_justificante)))
    }
}