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

        var respuesta = bormeService.consultarBorme(fecha);
        if (!respuesta.isExito()) {
            return urlsPdf; // lista vacía
        }

        String html = respuesta.getContenidoHtml();

        // Capturar cualquier texto que hay ENTRE el título de SECCIÓN PRIMERA y el título de SECCIÓN SEGUNDA (o hasta el final si no hay segunda)
        Pattern seccionPrimeraPattern = Pattern.compile(
                "<h[1-6][^>]*>\\s*SECCI[ÓO]N\\s+PRIMERA.*?</h[1-6]>(.*?)(?=<h[1-6][^>]*>\\s*SECCI[ÓO]N\\s+SEGUNDA|\\z)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher seccionPrimeraMatcher = seccionPrimeraPattern.matcher(html);

        if (!seccionPrimeraMatcher.find()) {
            return urlsPdf; // Como no hay sección primera se devuelve una lista vacía
        }

        String bloqueSeccionPrimera = seccionPrimeraMatcher.group(1);

        // Asegurar que estamos desde "Actos inscritos" hacia adelante dentro de la sección primera
        Pattern actosInscritosPattern = Pattern.compile(
                "<h[1-6][^>]*>\\s*Actos\\s+inscritos\\s*</h[1-6]>(.*)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher actosMatcher = actosInscritosPattern.matcher(bloqueSeccionPrimera);

        String contenidoParaBuscar;
        if (actosMatcher.find()) {
            contenidoParaBuscar = actosMatcher.group(1);
        } else {
            // Si no aparece el h de "Actos inscritos", buscamos PDFs en toda la sección primera
            contenidoParaBuscar = bloqueSeccionPrimera;
        }

        // Extraer TODOS los PDFs dentro del contenido seleccionado
        Pattern pdfPattern = Pattern.compile("href\\s*=\\s*\"([^\"]*\\.pdf)\"", Pattern.CASE_INSENSITIVE);
        Matcher pdfMatcher = pdfPattern.matcher(contenidoParaBuscar);

        while (pdfMatcher.find()) {
            String urlPdf = pdfMatcher.group(1);
            if (urlPdf.startsWith("/")) {
                urlPdf = "https://www.boe.es" + urlPdf;
            }
            urlsPdf.add(urlPdf);
        }

        return urlsPdf; // Si finalmente la sección existe pero está vacía, la lista será vacía al no haber PDFs
    }


    /**
     * Descarga todos los PDFs de una fecha específica
     */
    public List<String> descargarPdfsDelBorme(String fecha) {
        List<String> archivosDescargados = new ArrayList<>();
        List<String> urlsPdf = extraerUrlsPdfDelBorme(fecha);

        // Directorio base fijo
        String directorioBase = "pdfs_descargados";

        // Subdirectorio por fecha
        Path directorioFecha = Paths.get(directorioBase, fecha);

        // Crear directorio solo si no existe
        try {
            if (!Files.exists(directorioFecha)) {
                Files.createDirectories(directorioFecha);
                System.out.println("Directorio creado: " + directorioFecha);
            }
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
            return archivosDescargados;
        }

        // Descargar cada PDF en el directorio correspondiente
        for (String urlPdf : urlsPdf) {
            String nombreArchivo = extraerNombreArchivoDeUrl(urlPdf);
            Path rutaCompleta = directorioFecha.resolve(nombreArchivo);

            if (urlFetcher.downloadBinaryFile(urlPdf, rutaCompleta.toString())) {
                archivosDescargados.add(rutaCompleta.toString());
                System.out.println("PDF descargado: " + rutaCompleta);
            }
        }

        return archivosDescargados;
    }


    private String extraerNombreArchivoDeUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}