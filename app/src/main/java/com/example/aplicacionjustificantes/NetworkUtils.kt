package com.example.aplicacionjustificantes

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.VolleyError
import java.nio.charset.StandardCharsets

object NetworkUtils {
    private const val TIMEOUT_MS = 20_000

    fun <T> prepare(request: Request<T>): Request<T> {
        request.retryPolicy = DefaultRetryPolicy(
            TIMEOUT_MS,
            1,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        request.setShouldCache(false)
        return request
    }

    fun errorMessage(error: VolleyError): String {
        val response = error.networkResponse
        if (response != null) {
            val body = try {
                String(response.data ?: byteArrayOf(), StandardCharsets.UTF_8)
                    .replace(Regex("\\s+"), " ")
                    .take(180)
            } catch (_: Exception) {
                ""
            }

            return if (body.isBlank()) {
                "Servidor respondio HTTP ${response.statusCode}"
            } else {
                "HTTP ${response.statusCode}: $body"
            }
        }

        return when {
            error.message?.isNotBlank() == true -> error.message!!
            error.cause?.message?.isNotBlank() == true -> error.cause!!.message!!
            else -> "No se pudo contactar al servidor. Revisa Internet y que la API siga activa."
        }
    }
}
