package com.example.aplicacionjustificantes

import android.os.Bundle
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

    // Declaramos las variables con 'lateinit' porque se inicializarán en el onCreate
    private lateinit var edtNombre: EditText
    private lateinit var edtMatricula: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtVolverLogin: TextView

    // ✅ CORREGIDO: Ahora apunta exactamente a "registrar_usuario.php" que es el nombre real en tu carpeta xampp
    private val URL_REGISTRO = "http://192.168.1.83/justificantes_api/registrar_usuario.php"

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

            // Validaciones locales básicas
            if (nombre.isEmpty() || matricula.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.llena_campos), Toast.LENGTH_SHORT).show()
            } else if (password.length < 4) { // Cambiado a 4 por si usas "1234" en tus pruebas
                Toast.makeText(this, "La contraseña debe tener al menos 4 caracteres", Toast.LENGTH_SHORT).show()
            } else if (!correo.endsWith("@cecyteq.edu.mx")) {
                Toast.makeText(this, "Debes usar tu correo institucional @cecyteq.edu.mx", Toast.LENGTH_LONG).show()
            } else {
                // Si pasa las validaciones, enviamos los datos al servidor XAMPP
                ejecutarRegistro(nombre, matricula, correo, password)
            }
        }

        // Evento para regresar al Login al presionar el texto
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
                        // El PHP guardó todo con éxito en la base de datos
                        Toast.makeText(this@RegistroActivity, "¡Registro exitoso en el servidor!", Toast.LENGTH_LONG).show()
                        finish() // Regresa automáticamente al Login
                    } else {
                        // El PHP regresó un error (por ejemplo, correo o matrícula ya existentes)
                        Toast.makeText(this@RegistroActivity, message, Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegistroActivity, "Error al procesar registro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                // ✅ CORREGIDO: Mensaje actualizado con el nombre correcto del archivo PHP para que sea claro en el celular
                Toast.makeText(this@RegistroActivity, "Error de red en Registro: No se pudo conectar a registrar_usuario.php", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                // ⚠️ Estas claves deben llamarse IGUAL a como las lee tu archivo registrar_usuario.php (ej. $_POST['correo'])
                params["nombre"] = nom
                params["matricula"] = mat
                params["correo"] = corr
                params["contrasena"] = pass // Si en tu PHP usas "password", cámbialo aquí a "password"
                params["rol"] = "Alumno"    // Se envía el rol por defecto tal como está en tu tabla
                return params
            }
        }

        queue.add(stringRequest)
    }
}