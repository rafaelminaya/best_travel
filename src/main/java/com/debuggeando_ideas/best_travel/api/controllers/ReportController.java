package com.debuggeando_ideas.best_travel.api.controllers;

import com.debuggeando_ideas.best_travel.infraestructure.abstract_services.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping(path = "report")
@Tag(name = "Report")
// Anotación de OpenApi-Swagger para modificar el subtítulo de este controlador en la interfaz gráfica.
public class ReportController {

    private final ReportService reportService;

    // El tipo a retornar será un tipo "Resource" del package "org.springframework.core.io.Resource"
    @GetMapping
    public ResponseEntity<Resource> get() {
        // creamos unos headers, ya que este archivo no lo leeremo por postman sino que lo descargaremos desde un navegador.
        var headers = new HttpHeaders();

        // especificamos el "content type" del header"
        headers.setContentType(FORCE_DOWNLOAD);
        // especificamos el "header" como tal
        headers.set(HttpHeaders.CONTENT_DISPOSITION, FORCE_DOWNLOAD_HEADER_VALUE);
        // usamos al método del servicio
        var fileInBytes = this.reportService.readFile();
        // creamos el "ByteArrayResource" el cual es una implementación de la interfaz "Resource" a retornar
        ByteArrayResource response = new ByteArrayResource(fileInBytes);

        // personalizaremos el response http
        return ResponseEntity.ok()
                .headers(headers) // header del response
                .contentLength(fileInBytes.length) // longitud del response que será la longitud de bytes
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // "content type" indicando que será un stream de bytes
                .body(response); // body del response
    }

    private static final MediaType FORCE_DOWNLOAD = new MediaType("application", "force-download");
    // adjuntamos el nombre del archivo
    private static final String FORCE_DOWNLOAD_HEADER_VALUE = "attachment; filename=report.xlsx";


}
