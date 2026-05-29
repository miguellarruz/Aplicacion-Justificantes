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
    private lateinit var txtInstruccionFoto: TextView

    private var archivoUri: Uri? = null

    private var idUsuarioLogueado: Int = 1
    private var motivoRecibido: String = ""
    private var fechaRecibida: String = ""
    private var tipoJustificanteRecibido: String = ""

    private var institucionRecibida: String = "No especificado"
    private var cedulaRecibida: String = "No especificado"

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
            txtArchivo.text = "Por favor, adjunta la foto de tu receta o constancia médica médica."
        }

        btnSeleccionar.setOnClickListener {
            seleccionarArchivo()
        }

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

    // 📌 OPTIMIZADO: Abre la imagen, reduce su escala y la comprime en calidad para que no sature Volley
    private fun convertirUriABase64(uri: Uri?): String {
        if (uri == null) return ""
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            // Decodificar la imagen reduciendo sus dimensiones si es muy enorme (escala)
            val opciones = BitmapFactory.Options().apply {
                inSampleSize = 2 // Reduce el tamaño a la mitad (píxeles) optimizando la RAM
            }
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream, null, opciones)
            inputStream?.close()

            if (bitmapOriginal != null) {
                val outputStream = ByteArrayOutputStream()
                // Comprimir al 70% de calidad en formato JPEG (mantiene legibilidad de la receta y reduce el peso un 90%)
                bitmapOriginal.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val bytesComprimidos = outputStream.toByteArray()
                outputStream.close()

                Base64.encodeToString(bytesComprimidos, Base64.DEFAULT)
            } else ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun guardarJustificanteEnBaseDatos() {
        val url = "http://192.168.1.83/justificantes_api/guardar_justificante.php"
        val queue = Volley.newRequestQueue(this)

        // Ahora obtendremos una cadena Base64 ultra ligera y totalmente válida
        val fotoBase64 = convertirUriABase64(archivoUri)

        if (fotoBase64.isEmpty()) {
            Toast.makeText(this, "Error al procesar la imagen elegida", Toast.LENGTH_SHORT).show()
            return
        }

        // 📌 MODIFICADO: Se fuerza la ruta de envío explícita com.android.volley.Request.Method.POST
        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST,
            url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.getString("message")

                    if (status == "success") {
                        Toast.makeText(this@MainActivity, "¡Éxito!: $message", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Error del servidor: $message", Toast.LENGTH_SHORT).show()
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
                params["id_usuario"] = idUsuarioLogueado.toString()
                params["motivo"] = motivoRecibido
                params["fecha_inasistencia"] = fechaRecibida
                params["institucion"] = institucionRecibida
                params["cedula_medica"] = cedulaRecibida
                params["ruta_foto"] = fotoBase64
                return params
            }
        }

        queue.add(stringRequest)
    }

    private fun seleccionarArchivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            val tiposPermitidos = arrayOf("image/jpeg", "image/png")
            putExtra(Intent.EXTRA_MIME_TYPES, tiposPermitidos)
        }
        seleccionarArchivoLauncher.launch(Intent.createChooser(intent, "Selecciona tu justificante"))
    }
}