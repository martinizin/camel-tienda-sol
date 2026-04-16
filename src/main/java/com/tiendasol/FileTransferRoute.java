package com.tiendasol;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class FileTransferRoute extends RouteBuilder {

    private static final String BASE_DIR = ".";

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new FileTransferRoute());
        main.run();
    }

    @Override
    public void configure() throws Exception {

        // Ruta 1: Lee CSV de input, transforma a mayúsculas, escribe en output
        from("file:" + BASE_DIR + "/input?noop=true")
            .filter(header("CamelFileName").endsWith(".csv"))
            .log("Procesando archivo: ${file:name}")
            .log("Fecha/hora de procesamiento: ${date:now:yyyy-MM-dd HH:mm:ss}")
            .convertBodyTo(String.class)
            .process(exchange -> {
                String body = exchange.getIn().getBody(String.class);
                exchange.getIn().setBody(body.toUpperCase());
            })
            .to("file:" + BASE_DIR + "/output")
            .log("Archivo ${file:name} copiado a output con transformación a mayúsculas");

        // Ruta 2: Mueve archivos procesados de output a archived
        from("file:" + BASE_DIR + "/output?noop=true")
            .log("Archivando archivo: ${file:name} a las ${date:now:yyyy-MM-dd HH:mm:ss}")
            .to("file:" + BASE_DIR + "/archived")
            .log("Archivo ${file:name} archivado correctamente");
    }
}
