package com.example.aplicacionjustificantes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    // Declaramos las variables con 'lateinit' porque se inicializarán en el onCreate
    private lateinit var edtNombre: EditText
    private lateinit var edtMatricula: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var txtVolverLogin: TextView

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

            // Validaciones
            if (nombre.isEmpty() || matricula.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.llena_campos), Toast.LENGTH_SHORT).show()
            } else if (password.length < 6) {
                Toast.makeText(this, getString(R.string.error_password_corta), Toast.LENGTH_SHORT).show()
            } else {
                // String templates de Kotlin ($nombre) para armar textos fácilmente
                Toast.makeText(this, getString(R.string.registro_exitoso, nombre), Toast.LENGTH_LONG).show()

                // Finaliza la actividad y regresa al Login
                finish()
            }
        }

        // Evento para regresar al Login al presionar el texto
        txtVolverLogin.setOnClickListener {
            finish()
        }
    }
}
