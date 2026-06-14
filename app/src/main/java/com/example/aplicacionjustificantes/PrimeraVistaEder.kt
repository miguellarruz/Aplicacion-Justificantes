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
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class PrimeraVistaEder : AppCompatActivity() {

    private lateinit var edtCorreo: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnIngresar: Button
    private lateinit var txtRegistrarse: TextView

    // 🔑 CORREGIDO: Config.IP_SERVIDOR ya incluye "justificantes_api/"
    private val URL_LOGIN = "${Config.IP_SERVIDOR}login.php"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eder_vista)

        // Inicializar los componentes usando los IDs exactos de tu XML
        edtCorreo = findViewById(R.id.edtCorreoEder)
        edtPassword = findViewById(R.id.edtPasswordEder)
        btnIngresar = findViewById(R.id.btnIngresarEder)
        txtRegistrarse = findViewById(R.id.txtRegistrarseEder)

        // Acción al presionar el botón de Ingresar
        btnIngresar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val contrasena = edtPassword.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ACCESO DIRECTO LOCAL PARA ENFERMERÍA (Sincronizado con contraseña '123456')
            if (correo.equals("enfermeria@cecyteq.edu.mx", ignoreCase = true) && contrasena == "123456") {
                Toast.makeText(this, "Bienvenido Personal de Enfermería (Modo Local)", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EnfermeriaActivity::class.java)
                startActivity(intent)
                finish()
            } else if (correo.endsWith("@cecyteq.edu.mx") || correo.endsWith("@cecyte.edu.mx")) {
                // Si es un alumno o usuario de la escuela general, hace la consulta al servidor MySQL
                ejecutarLogin(correo, contrasena)
            } else {
                edtCorreo.error = "Solo se permiten correos con dominio @cecyteq.edu.mx"
                Toast.makeText(this, "Error: Debes usar tu correo institucional", Toast.LENGTH_LONG).show()
            }
        }

        // Acción para ir a la pantalla de Registro si no tiene cuenta
        txtRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun ejecutarLogin(correoUsuario: String, contrasenaUsuario: String) {
        val queue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            URL_LOGIN,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val status = jsonResponse.getString("status")
                    val message = jsonResponse.getString("message")

                    if (status == "success") {
                        Toast.makeText(this@PrimeraVistaEder, message, Toast.LENGTH_SHORT).show()

                        // Guardamos el nombre en SharedPreferences para tus paneles dinámicos
                        try {
                            val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            val nombreExtraido = correoUsuario.substringBefore("@")
                            editor.putString("nombre_cuenta", nombreExtraido)
                            editor.apply()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        // Extraemos el rol que nos devuelve el archivo PHP
                        val rol = jsonResponse.getString("rol")
                        val idUsuario = jsonResponse.getInt("id_usuario")

                        // 🔑 CORREGIDO: Compara ignorando mayúsculas/minúsculas para capturar 'enfermera' de AwardSpace
                        if (rol.equals("enfermera", ignoreCase = true) || rol.equals("Enfermeria", ignoreCase = true) || rol.equals("Administrador", ignoreCase = true)) {
                            val intent = Intent(this@PrimeraVistaEder, EnfermeriaActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@PrimeraVistaEder, Interfaz::class.java)
                            intent.putExtra("ID_USUARIO_LOGUEADO", idUsuario)
                            startActivity(intent)
                        }
                        finish()

                    } else {
                        Toast.makeText(this@PrimeraVistaEder, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PrimeraVistaEder, "Error al procesar los datos: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                val msgError = error.message ?: "Filtro de seguridad del servidor o credenciales no válidas"
                Toast.makeText(this@PrimeraVistaEder, "Error de red en Login: $msgError", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["correo"] = correoUsuario
                params["contrasena"] = contrasenaUsuario
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                // 🚀 TRUCO CLAVE: Cabecera obligatoria para saltar la validación anti-bots de AwardSpace
                headers["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                return headers
            }
        }

        queue.add(stringRequest)
    }
}