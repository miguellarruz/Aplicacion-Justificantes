package com.example.aplicacionjustificantes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PrimeraVistaEder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.eder_vista)

       
        val edtCorreo = findViewById<EditText>(R.id.edtCorreoEder)
        val edtPassword = findViewById<EditText>(R.id.edtPasswordEder)
        val btnIngresar = findViewById<Button>(R.id.btnIngresarEder)
        val txtRegistrarse = findViewById<TextView>(R.id.txtRegistrarseEder)

      
        btnIngresar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString().trim()

           
            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            
            if (correo.endsWith("@cecyteq.edu.mx")) {

                Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()

                // MANDA DIRECTO A LA PANTALLA INTERFAZ (Tu menú principal con las 3 opciones)
                val intent = Intent(this, Interfaz::class.java)
                startActivity(intent)

                finish() // Cierra el Login para que no se puedan regresar al picarle "atrás" en el cel

            } else {
                // Bloqueo total si usan correos ajenos a la escuela
                edtCorreo.error = "Solo se permiten correos con dominio @cecyteq.edu.mx"
                Toast.makeText(this, "Error: Debes usar tu correo institucional", Toast.LENGTH_LONG).show()
            }
        }


        txtRegistrarse.setOnClickListener {
            // Te manda a la actividad de registro (Si tu clase se llama diferente, cambia "RegistroActivity")
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}