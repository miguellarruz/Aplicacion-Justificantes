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
    private var motivoRecibido: String = ""
    private var fechaRecibida: String = ""

    private val seleccionarArchivoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val data: Intent? = result.data
            archivoUri = data?.data

            if (archivoUri != null) {
                txtArchivo.text = "¡Archivo listo para enviar!"
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

        // 📌 RECUPERAMOS LOS DATOS COMPLETOS HEREDADOS DESDE INTERFAZ 2A
        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)
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
                Toast.makeText(this, "Por favor, selecciona una foto primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarJustificanteEnBaseDatos() {
        val url = "http://192.168.1.83/justificantes_api/guardar_justificante.php"
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
                        Toast.makeText(this@MainActivity, "Éxito: $message", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Error del servidor: $message", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Respuesta: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this@MainActivity, "Error de red al conectar con guardar_justificante.php", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                // 📌 Claves idénticas a como las lee tu $_POST['...'] en el archivo PHP
                params["id_usuario"] = idUsuarioLogueado.toString()
                params["motivo"] = motivoRecibido
                params["fecha_inasistencia"] = fechaRecibida
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
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Selecciona tu justificante"))
    }
}