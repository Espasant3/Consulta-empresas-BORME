package borme.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class PdfStorageService {

    @Value("${app.storage.pdf.path:pdfs_descargados}")
    private String pdfStoragePath;

    /**
     * Obtiene la ruta física del archivo PDF
     */
    public Path obtenerRutaPdf(String fecha, String nombreArchivo) {
        return Paths.get(pdfStoragePath, fecha, nombreArchivo).normalize();
    }

    /**
     * Verifica si un PDF existe
     */
    public boolean existePdf(String fecha, String nombreArchivo) {
        try {
            Path archivoPath = obtenerRutaPdf(fecha, nombreArchivo);
            // Validación de seguridad: asegurar que está dentro del directorio permitido
            Path basePath = Paths.get(pdfStoragePath).normalize().toAbsolutePath();
            if (!archivoPath.toAbsolutePath().startsWith(basePath)) {
                return false;
            }
            return Files.exists(archivoPath) && Files.isRegularFile(archivoPath);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Carga un PDF como recurso para servir
     */
    public Resource cargarPdfComoRecurso(String fecha, String nombreArchivo) throws IOException {
        Path archivoPath = obtenerRutaPdf(fecha, nombreArchivo);

        // Validación de seguridad
        Path basePath = Paths.get(pdfStoragePath).normalize().toAbsolutePath();
        if (!archivoPath.toAbsolutePath().startsWith(basePath)) {
            throw new SecurityException("Path traversal attempt detected");
        }

        if (!Files.exists(archivoPath)) {
            throw new IOException("PDF no encontrado: " + nombreArchivo);
        }

        Resource resource = new UrlResource(archivoPath.toUri());
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("No se puede leer el PDF: " + nombreArchivo);
        }
    }

    public String getPdfStoragePath() {
        return pdfStoragePath;
    }
}