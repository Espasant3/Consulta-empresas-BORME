package borme.controllers;

import borme.services.PdfStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pdfs")
@CrossOrigin(origins = "*")
public class PdfController {

    private static final Logger logger = LoggerFactory.getLogger(PdfController.class);

    @Autowired
    private PdfStorageService pdfStorageService;

    /**
     * Servir un PDF específico
     * GET /api/pdfs/2024-09-10/BORME-A-2024-174-02.pdf
     */
    @GetMapping("/{fecha}/{nombreArchivo:.+}")
    public ResponseEntity<Resource> servirPdf(
            @PathVariable String fecha,
            @PathVariable String nombreArchivo) {

        try {
            logger.info("Solicitando PDF: {}/{}", fecha, nombreArchivo);

            Resource pdfResource = pdfStorageService.cargarPdfComoRecurso(fecha, nombreArchivo);

            // Determinar content type
            MediaType mediaType = MediaType.APPLICATION_PDF;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + nombreArchivo + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600") // Cache de 1 hora
                    .body(pdfResource);

        } catch (Exception e) {
            logger.error("Error sirviendo PDF {}/{}: {}", fecha, nombreArchivo, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verificar existencia de PDF
     * GET /api/pdfs/2024-09-10/BORME-A-2024-174-02.pdf/existe
     */
    @GetMapping("/{fecha}/{nombreArchivo:.+}/existe")
    public ResponseEntity<Map<String, Object>> verificarPdf(
            @PathVariable String fecha,
            @PathVariable String nombreArchivo) {

        Map<String, Object> respuesta = new HashMap<>();
        boolean existe = pdfStorageService.existePdf(fecha, nombreArchivo);

        respuesta.put("fecha", fecha);
        respuesta.put("nombreArchivo", nombreArchivo);
        respuesta.put("existe", existe);

        if (existe) {
            respuesta.put("url", "/api/pdfs/" + fecha + "/" + nombreArchivo);
        }

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint de salud para el servicio de PDFs
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OK");
        status.put("storagePath", pdfStorageService.getPdfStoragePath());
        return ResponseEntity.ok(status);
    }
}