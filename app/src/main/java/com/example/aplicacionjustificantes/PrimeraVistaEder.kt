package com.example.aplicacionjustificantes

import android.content.Context
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

            // 1. FILTRO ESPECIAL: Si es el correo del personal de Enfermería
            if (correo.equals("enfermeria@cecyteq.edu.mx", ignoreCase = true) && password == "1234") {
                Toast.makeText(this, "Bienvenido Personal de Enfermería", Toast.LENGTH_SHORT).show()

                // Redirige al panel exclusivo de la enfermera
                val intent = Intent(this, EnfermeriaActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            // 2. Filtro para Alumnos normales
            if (correo.endsWith("@cecyteq.edu.mx")) {
                Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()

                // Guardamos el correo o un nombre simulado en la memoria de la sesión
                val sharedPreferences = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                // Extrae la parte antes del @ para usarlo de nombre provisional
                val nombreExtraido = correo.substringBefore("@")
                editor.putString("nombre_cuenta", nombreExtraido)
                editor.apply()

                // Manda a la pantalla Interfaz del Alumno
                val intent = Intent(this, Interfaz::class.java)
                startActivity(intent)
                finish()

            } else {
                edtCorreo.error = "Solo se permiten correos con dominio @cecyteq.edu.mx"
                Toast.makeText(this, "Error: Debes usar tu correo institucional", Toast.LENGTH_LONG).show()
            }
        }

        txtRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }
}