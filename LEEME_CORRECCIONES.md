# Aplicacion Justificantes - correcciones de conexion

## Que se corrigio

- Se mantiene `http://justificateq.atwebpages.com/justificantes_api/` porque el
  historial del proyecto indica que el certificado HTTPS del subdominio no es
  valido.
- Android permite trafico HTTP solamente para `justificateq.atwebpages.com`.
- Todas las peticiones envian los mismos encabezados `Accept` y `User-Agent`.
- Se agregaron timeout, un reintento y desactivacion de cache para las
  peticiones de la API.
- Los errores ahora muestran el codigo HTTP y una parte de la respuesta del
  servidor. Esto permite distinguir un bloqueo 403, una ruta 404 y un error PHP
  500.
- Se retiro `com/example/justificateq/R.kt`. Android genera automaticamente la
  clase `R`; no debe crearse manualmente.
- Se corrigieron los finales de linea del iniciador de Gradle.

## Archivos principales

- `Config.kt`: dominio, construccion de URLs y encabezados comunes.
- `NetworkUtils.kt`: timeout, reintentos y mensajes de error.
- `AndroidManifest.xml`: permisos de Internet y estado de red.
- `network_security_config.xml`: excepcion HTTP limitada al dominio de la API.

## Importante sobre el servidor

Este proyecto contiene la aplicacion Android, pero no contiene los archivos PHP
ni la base de datos del servidor. Para que la app conecte, estas rutas deben
existir y devolver JSON valido:

- `login.php`
- `registrar_usuario.php`
- `guardar_justificante.php`
- `listar_justificantes.php`
- `listar_notificaciones.php`
- `obtener_estado_justificante.php`
- `actualizar_justificante.php`

Si la app muestra `HTTP 403`, el hosting esta bloqueando la solicitud. Si
muestra `HTTP 404`, falta el archivo PHP o la ruta es incorrecta. Si muestra
`HTTP 500`, el problema se encuentra en PHP o en la conexion de PHP con MySQL.
