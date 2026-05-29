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

    // 🌐 TU NUEVA IP DE RED ACTUALIZADA
    private val IP_SERVIDOR = "192.168.2.155"

    // ✅ CONFIGURADO: Apunta de forma dinámica a tu servidor XAMPP actual
    private val URL_LOGIN = "http://$IP_SERVIDOR/justificantes_api/login.php"

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

            // ACCESO DIRECTO LOCAL PARA ENFERMERÍA (Por si el servidor falla o vas empezando)
            if (correo.equals("enfermeria@cecyteq.edu.mx", ignoreCase = true) && contrasena == "1234") {
                Toast.makeText(this, "Bienvenido Personal de Enfermería (Modo Local)", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, EnfermeriaActivity::class.java)
                startActivity(intent)
                finish()
            } else if (correo.endsWith("@cecyteq.edu.mx")) {
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

                        // Validamos el rol para decidir a dónde mandar al usuario desde la Base de Datos
                        if (rol == "Enfermeria" || rol == "Administrador") {
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
                // 🔥 CORREGIDO: Mensaje con la IP dinámica real de tu red actual
                Toast.makeText(this@PrimeraVistaEder, "Error de red en Login: No se pudo conectar a login.php (IP: $IP_SERVIDOR)", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["correo"] = correoUsuario
                params["contrasena"] = contrasenaUsuario
                return params
            }
        }

        queue.add(stringRequest)
    }
}