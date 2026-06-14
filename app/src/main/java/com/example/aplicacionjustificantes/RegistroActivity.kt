package com.example.aplicacionjustificantes

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RegistroActivity : AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtMatricula: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtVolverLogin: TextView

    // 🔑 CORREGIDO: Config.IP_SERVIDOR ya incluye "justificantes_api/", solo añadimos el archivo PHP
    private val URL_REGISTRO = "${Config.IP_SERVIDOR}registrar_usuario.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        edtNombre = findViewById(R.id.edtNombreReg)
        edtMatricula = findViewById(R.id.edtMatriculaReg)
        edtCorreo = findViewById(R.id.edtCorreoReg)
        edtPassword = findViewById(R.id.edtPasswordReg)
        btnRegistrar = findViewById(R.id.btnRegistrarReg)
        txtVolverLogin = findViewById(R.id.txtLoginReg)

        btnRegistrar.setOnClickListener {
            val nombre = edtNombre.text.toString().trim()
            val matricula = edtMatricula.text.toString().trim()
            val correo = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (nombre.isEmpty() || matricula.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.llena_campos), Toast.LENGTH_SHORT).show()
            } else if (password.length < 4) {
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
            } else if (!correo.endsWith("@cecyteq.edu.mx") && !correo.endsWith("@cecyte.edu.mx")) {
                Toast.makeText(this, "Debes usar tu correo institucional válido", Toast.LENGTH_LONG).show()
            } else {
                ejecutarRegistro(nombre, matricula, correo, password)
            }
        }

        txtVolverLogin.setOnClickListener {
            finish()
        }
    }

    private fun ejecutarRegistro(nom: String, mat: String, corr: String, pass: String) {
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            URL_REGISTRO,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.getString("message")

                    if (status == "success") {
                        Toast.makeText(this@RegistroActivity, "¡Registro exitoso en el servidor!", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this@RegistroActivity, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroActivity, "Respuesta del servidor: $response", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val msgError = error.message ?: "Código de respuesta inesperado"
                Toast.makeText(this@RegistroActivity, "Error de red en Registro: $msgError", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["nombre"] = nom
                params["matricula"] = mat
                params["correo"] = corr
                // ⚠️ Sincronizado: Enviamos 'contrasena' por POST, tu registrar_usuario.php lo recibe así.
                params["contrasena"] = pass
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                // 🚀 TRUCO CLAVE: Engañamos a AwardSpace simulando ser un navegador Chrome para saltar el filtro anti-bots
                headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                return headers
            }
        }

        queue.add(stringRequest)
    }
}