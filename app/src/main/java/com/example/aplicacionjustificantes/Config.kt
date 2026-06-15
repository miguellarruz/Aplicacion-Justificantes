package com.example.aplicacionjustificantes

object Config {
    // AwardSpace no presenta un certificado HTTPS valido para este subdominio.
    // AndroidManifest y network_security_config permiten HTTP solo para este host.
    const val IP_SERVIDOR = "http://justificateq.atwebpages.com/justificantes_api/"
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

    fun endpoint(archivoPhp: String): String =
        IP_SERVIDOR + archivoPhp.trimStart('/')

    fun headers(): MutableMap<String, String> =
        mutableMapOf(
            "Accept" to "application/json",
            "User-Agent" to USER_AGENT
        )
}
