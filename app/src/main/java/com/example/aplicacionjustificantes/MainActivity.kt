package com.example.aplicacionjustificantes

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var btnSeleccionar: Button
    private lateinit var btnEnviar: Button
    private lateinit var txtArchivo: TextView
    private lateinit var imagePreview: ImageView

    private var archivoUri: Uri? = null

    private var idUsuarioLogueado: Int = 1
    private var motivoRecibido: String = ""
    private var fechaRecibida: String = ""
    private var tipoJustificanteRecibido: String = ""

    private var institucionRecibida: String = "No especificado"
    private var cedulaRecibida: String = "No especificado"

    // 🌐 TU NUEVA IP DE RED ACTUALIZADA
    private val IP_SERVIDOR = "192.168.2.155"

    private val seleccionarArchivoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            archivoUri = result.data?.data
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

        idUsuarioLogueado = intent.getIntExtra("ID_USUARIO_LOGUEADO", 1)
        motivoRecibido = intent.getStringExtra("EXTRA_MOTIVO") ?: "Sin motivo"
        fechaRecibida = intent.getStringExtra("EXTRA_FECHA") ?: "2026-01-01"
        tipoJustificanteRecibido = intent.getStringExtra("EXTRA_TIPO") ?: ""
        institucionRecibida = intent.getStringExtra("EXTRA_INSTITUCION") ?: "No especificado"
        cedulaRecibida = intent.getStringExtra("EXTRA_CEDULA") ?: "No especificado"

        btnSeleccionar = findViewById(R.id.btnSeleccionar)
        btnEnviar = findViewById(R.id.btnEnviar)
        txtArchivo = findViewById(R.id.txtArchivo)
        imagePreview = findViewById(R.id.imagePreview)

        if (tipoJustificanteRecibido.contains("Personal", ignoreCase = true)) {
            btnSeleccionar.text = "Subir INE del Padre/Tutor"
            txtArchivo.text = "⚠️ Para asuntos personales es obligatorio adjuntar la credencial INE de tu tutor."
        } else {
            btnSeleccionar.text = "Subir Receta Médica"
            txtArchivo.text = "Por favor, adjunta la foto de tu receta o constancia médica."
        }

        btnSeleccionar.setOnClickListener { seleccionarArchivo() }

        btnEnviar.setOnClickListener {
            if (archivoUri != null) {
                guardarJustificanteEnBaseDatos()
            } else {
                val mensajeError = if (tipoJustificanteRecibido.contains("Personal", ignoreCase = true)) {
                    "Por favor, selecciona la foto de la credencial INE de tu tutor"
                } else {
                    "Por favor, selecciona la foto de tu receta médica"
                }
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertirUriABase64(uri: Uri?): String {
        if (uri == null) return ""
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val opciones = BitmapFactory.Options().apply { inSampleSize = 2 }
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream, null, opciones)
            inputStream?.close()

            if (bitmapOriginal != null) {
                val outputStream = ByteArrayOutputStream()
                bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val bytesComprimidos = outputStream.toByteArray()
                outputStream.close()
                Base64.encodeToString(bytesComprimidos, Base64.DEFAULT)
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun guardarJustificanteEnBaseDatos() {
        // 🔥 CORREGIDO: Ahora usa la variable dinámica con tu IP real activa
        val url = "http://$IP_SERVIDOR/justificantes_api/guardar_justificante.php"
        val queue = Volley.newRequestQueue(this)
        val fotoBase64 = convertirUriABase64(archivoUri)

        if (fotoBase64.isEmpty()) {
            Toast.makeText(this, "Error al procesar la imagen elegida", Toast.LENGTH_SHORT).show()
            return
        }

        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST,
            url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.getString("message")

                    if (status == "success") {
                        Toast.makeText(this@MainActivity, "¡Éxito!: $message", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Respuesta del servidor: $response", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this@MainActivity, "Error de red al conectar con el servidor", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id_usuario"] = idUsuarioLogueado.toString()
                params["motivo"] = motivoRecibido
                params["fecha_inasistencia"] = fechaRecibida
                params["institucion"] = institucionRecibida
                params["cedula_medica"] = cedulaRecibida
                params["ruta_foto"] = fotoBase64
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/x-www-form-urlencoded"
                return headers
            }
        }

        queue.add(stringRequest)
    }

    private fun seleccionarArchivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Selecciona tu justificante"))
    }
}