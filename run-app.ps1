# Script para ejecutar la aplicación Camel File Transfer
# Uso: .\run-app.ps1

# Configurar Java 25
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot"

# Ejecutar la aplicación
.\mvnw.cmd compile 'exec:java' '-Dexec.mainClass=com.tiendasol.FileTransferRoute'

Write-Host "Aplicación detenida. Presiona Enter para continuar..."
Read-Host