# TiendaSol - File Transfer con Apache Camel

## Descripción del proyecto

TiendaSol es una cadena minorista que opera con dos sistemas antiguos: un **sistema de ventas** que exporta transacciones diarias en archivos CSV y un **sistema de inventario** que necesita recibir esa información para actualizar existencias. Hasta ahora, los empleados copiaban manualmente el archivo `ventas.csv` de una carpeta a otra todos los días.

Este proyecto automatiza ese proceso aplicando el **patrón de integración File Transfer** (transferencia de archivos) mediante [Apache Camel](https://camel.apache.org/). Camel actúa como intermediario: vigila una carpeta de entrada, procesa automáticamente los archivos CSV que aparezcan, y los distribuye a las carpetas correspondientes.

## ¿Qué hace el proyecto?

El flujo completo que ejecuta Camel es:

1. **Lectura**: Detecta automáticamente archivos `.csv` nuevos en la carpeta `input/`.
2. **Transformación**: Convierte todo el contenido del archivo a **mayúsculas** (simulando una normalización de datos).
3. **Escritura**: Deposita el archivo transformado en la carpeta `output/` (donde lo consumiría el sistema de inventario).
4. **Archivado**: Una segunda ruta copia los archivos ya procesados de `output/` a `archived/` como respaldo.

Todo esto ocurre de forma continua: Camel queda corriendo y procesa cada archivo nuevo que aparezca en `input/` sin intervención manual.

## Explicación del código

El proyecto tiene una única clase Java: `FileTransferRoute.java`, que define dos rutas de Camel.

### Ruta 1 — Procesamiento (`input → output`)

```java
from("file:./input?noop=true")                          // Vigila la carpeta input/
    .filter(header("CamelFileName").endsWith(".csv"))    // Solo procesa archivos .csv
    .log("Procesando archivo: ${file:name}")             // Registra en consola
    .log("Fecha/hora: ${date:now:yyyy-MM-dd HH:mm:ss}") // Log con timestamp
    .convertBodyTo(String.class)                         // Lee el contenido como texto
    .process(exchange -> {                               // Transforma a mayúsculas
        String body = exchange.getIn().getBody(String.class);
        exchange.getIn().setBody(body.toUpperCase());
    })
    .to("file:./output");                                // Escribe el resultado en output/
```

- `noop=true`: lee los archivos sin moverlos ni borrarlos del origen.
- `.filter(...)`: garantiza que solo se procesen archivos con extensión `.csv`.
- `.process(...)`: usa un `Processor` de Java para aplicar `toUpperCase()` al contenido completo.

### Ruta 2 — Archivado (`output → archived`)

```java
from("file:./output?noop=true")                             // Vigila la carpeta output/
    .log("Archivando: ${file:name} a las ${date:now:...}")  // Log con fecha/hora
    .to("file:./archived");                                 // Copia a archived/
```

Opera de forma independiente: cuando detecta un archivo nuevo en `output/`, lo copia a `archived/` como respaldo histórico.

### Punto de entrada

```java
public static void main(String[] args) throws Exception {
    Main main = new Main();
    main.configure().addRoutesBuilder(new FileTransferRoute());
    main.run();  // Inicia Camel y queda escuchando indefinidamente
}
```

`Camel Main` mantiene el proceso vivo, escuchando cambios en las carpetas configuradas.

## Stack tecnológico

| Componente | Versión |
|---|---|
| Java | 17+ |
| Apache Camel | 4.14.0 |
| Maven | 3.8+ (wrapper incluido) |
| SLF4J | 2.0.9 (logging) |

## Estructura del proyecto

```
tienda-sol/
├── input/       → Archivos CSV de entrada (sistema de ventas)
├── output/      → Archivos transformados a mayúsculas (sistema de inventario)
├── archived/    → Respaldo de archivos ya procesados
├── logs/        → Carpeta para registros
├── src/main/java/com/tiendasol/
│   └── FileTransferRoute.java   → Única clase con las 2 rutas de Camel
├── pom.xml      → Dependencias Maven (camel-core, camel-main, camel-file)
└── mvnw.cmd     → Maven wrapper para ejecutar sin instalar Maven
```

## Ejecución

```powershell
# En PowerShell (Windows)
.\mvnw.cmd compile exec:java '-Dexec.mainClass=com.tiendasol.FileTransferRoute'
```

```cmd
# En CMD (Windows)
mvnw.cmd compile exec:java -Dexec.mainClass="com.tiendasol.FileTransferRoute"
```

Una vez iniciado, Camel queda escuchando. Para probarlo, colocá un archivo `.csv` en `input/` y revisá que aparezca transformado en `output/` y copiado en `archived/`.

## Detener

Presioná `Ctrl+C` en la terminal para detener Camel.

## Patrón de integración: File Transfer

Este proyecto implementa el patrón **File Transfer**, uno de los tres patrones clásicos de integración de sistemas. El sistema productor (ventas) escribe un archivo en una ubicación compartida y el sistema consumidor (inventario) lo lee desde ahí. Apache Camel automatiza la vigilancia, transformación y distribución de esos archivos.
