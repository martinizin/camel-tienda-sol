# TiendaSol - File Transfer con Apache Camel

Proyecto que usa Apache Camel para procesar archivos CSV: lee desde `input/`, transforma el contenido a mayúsculas, lo escribe en `output/` y lo archiva en `archived/`.

## Requisitos

- Java 17+
- Maven 3.8+ (o usar el wrapper `mvnw` incluido)

## Ejecución

```bash
# Compilar y ejecutar
./mvnw compile exec:java -Dexec.mainClass="com.tiendasol.FileTransferRoute"
```

En Windows:

```cmd
mvnw.cmd compile exec:java -Dexec.mainClass="com.tiendasol.FileTransferRoute"
```

## Estructura

```
input/       → Colocá archivos .csv acá para que sean procesados
output/      → Archivos transformados a mayúsculas
archived/    → Copia de archivos ya procesados
```

## Detener

Presioná `Ctrl+C` en la terminal para detener Camel.
