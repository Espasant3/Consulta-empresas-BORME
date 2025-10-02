package backend.services;

import backend.http.URLFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfService {

    @Autowired
    private URLFetcher urlFetcher;

    @Autowired
    private BormeService bormeService;

    /**
     * Extrae URLs de PDFs del HTML del BORME
     */
    public List<String> extraerUrlsPdfDelBorme(String fecha) {
        List<String> urlsPdf = new ArrayList<>();

        // Consultar el BORME para la fecha
        var respuesta = bormeService.consultarBorme(fecha);
        if (!respuesta.isExito()) {
            return urlsPdf;
        }

        // Buscar patrones de URLs de PDF en el HTML
        String html = respuesta.getContenidoHtml();
        Pattern pattern = Pattern.compile("href=\"([^\"]*\\.pdf)\"", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {
            String urlPdf = matcher.group(1);
            // Convertir URL relativa a absoluta si es necesario
            if (urlPdf.startsWith("/")) {
                urlPdf = "https://www.boe.es" + urlPdf;
            }
            urlsPdf.add(urlPdf);
        }

        return urlsPdf;
    }

    /**
     * Descarga todos los PDFs de una fecha específica
     */
    public List<String> descargarPdfsDelBorme(String fecha, String directorioDestino) {
        List<String> archivosDescargados = new ArrayList<>();
        List<String> urlsPdf = extraerUrlsPdfDelBorme(fecha);

        // Crear directorio si no existe
        try {
            Files.createDirectories(Paths.get(directorioDestino));
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
            return archivosDescargados;
        }

        for (String urlPdf : urlsPdf) {
            String nombreArchivo = extraerNombreArchivoDeUrl(urlPdf);
            String rutaCompleta = Paths.get(directorioDestino, nombreArchivo).toString();

            if (urlFetcher.downloadBinaryFile(urlPdf, rutaCompleta)) {
                archivosDescargados.add(rutaCompleta);
                System.out.println("PDF descargado: " + rutaCompleta);
            }
        }

        return archivosDescargados;
    }

    private String extraerNombreArchivoDeUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}