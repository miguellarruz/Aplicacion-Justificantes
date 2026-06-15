package com.example.aplicacionjustificantes

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.aplicacionjustificantes.databinding.ActivityRegistroBinding
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRegistroBinding

    private val URL_REGISTRO = Config.endpoint("registrar_usuario.php")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegistrarReg.setOnClickListener {
            if (validarCampos()) {
                ejecutarRegistro()
            }
        }


        binding.txtLoginReg.setOnClickListener {
            finish()
        }
    }


    private fun validarCampos(): Boolean {
        var esValido = true

        val nombre = binding.edtNombreReg.text.toString().trim()
        val matricula = binding.edtMatriculaReg.text.toString().trim()
        val correo = binding.edtCorreoReg.text.toString().trim()
        val password = binding.edtPasswordReg.text.toString().trim()


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

        binding.btnRegistrarReg.isEnabled = false

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            URL_REGISTRO,
            { response ->
                binding.btnRegistrarReg.isEnabled = true
                try {

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


        VolleySingleton.getInstance(this).addToRequestQueue(NetworkUtils.prepare(stringRequest))
    }
}
