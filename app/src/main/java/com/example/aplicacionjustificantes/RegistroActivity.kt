package com.example.aplicacionjustificantes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.aplicacionjustificantes.databinding.ActivityRegistroBinding
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {

    // Usamos View Binding para una vinculación segura y eficiente con el XML
    private lateinit var binding: ActivityRegistroBinding

    // 🔑 La URL base se toma de Config para mantener el orden
    private val URL_REGISTRO = Config.endpoint("registrar_usuario.php")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener del botón de registro
        binding.btnRegistrarReg.setOnClickListener {
            if (validarCampos()) {
                ejecutarRegistro()
            }
        }

        // Volver al login
        binding.txtLoginReg.setOnClickListener {
            finish()
        }
    }

    /**
     * Valida que los datos ingresados sean correctos antes de enviarlos al servidor.
     * Utiliza TextInputLayout para mostrar errores de forma visualmente atractiva.
     */
    private fun validarCampos(): Boolean {
        var esValido = true

        val nombre = binding.edtNombreReg.text.toString().trim()
        val matricula = binding.edtMatriculaReg.text.toString().trim()
        val correo = binding.edtCorreoReg.text.toString().trim()
        val password = binding.edtPasswordReg.text.toString().trim()

        // Limpiar errores previos
        binding.tilNombreReg.error = null
        binding.tilMatriculaReg.error = null
        binding.tilCorreoReg.error = null
        binding.tilPasswordReg.error = null

        if (nombre.isEmpty()) {
            binding.tilNombreReg.error = getString(R.string.llena_campos)
            esValido = false
        }

        if (matricula.isEmpty()) {
            binding.tilMatriculaReg.error = getString(R.string.llena_campos)
            esValido = false
        }

        if (correo.isEmpty()) {
            binding.tilCorreoReg.error = getString(R.string.llena_campos)
            esValido = false
        } else if (!correo.endsWith("@cecyteq.edu.mx") && !correo.endsWith("@cecyte.edu.mx")) {
            binding.tilCorreoReg.error = "Debes usar tu correo institucional (@cecyteq.edu.mx)"
            esValido = false
        }

        if (password.length < 6) {
            // Se usa el string de error_password_corta definido en strings.xml (6 caracteres)
            binding.tilPasswordReg.error = getString(R.string.error_password_corta)
            esValido = false
        }

        return esValido
    }

    private fun ejecutarRegistro() {
        // Deshabilitamos el botón para evitar registros duplicados por clics accidentales
        binding.btnRegistrarReg.isEnabled = false

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            URL_REGISTRO,
            { response ->
                binding.btnRegistrarReg.isEnabled = true
                try {
                    // Limpiamos la respuesta por si el servidor devuelve espacios en blanco
                    val jsonResponse = JSONObject(response.trim())
                    val status = jsonResponse.optString("status")
                    val message = jsonResponse.optString("message", "Sin mensaje del servidor")

                    if (status == "success") {
                        val nombre = binding.edtNombreReg.text.toString()
                        Toast.makeText(this, getString(R.string.registro_exitoso, nombre), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    // Si falla el parseo JSON, mostramos la respuesta cruda para diagnóstico
                    Toast.makeText(this, "Respuesta inesperada: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                binding.btnRegistrarReg.isEnabled = true
                val msgError = NetworkUtils.errorMessage(error)
                Toast.makeText(this, "Error de red: $msgError", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                return mutableMapOf(
                    "nombre" to binding.edtNombreReg.text.toString().trim(),
                    "matricula" to binding.edtMatriculaReg.text.toString().trim(),
                    "correo" to binding.edtCorreoReg.text.toString().trim(),
                    "contrasena" to binding.edtPasswordReg.text.toString().trim()
                )
            }

            override fun getHeaders(): MutableMap<String, String> {
                return Config.headers()
            }
        }

        // Usamos el Singleton de Volley para manejar la cola de peticiones globalmente
        VolleySingleton.getInstance(this).addToRequestQueue(NetworkUtils.prepare(stringRequest))
    }
}
