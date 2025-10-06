package borme.services;

import borme.http.URLFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PdfService {

    @Autowired
    private URLFetcher urlFetcher;

    @Autowired
    private BormeService bormeService;

    @Value("${app.storage.pdf.path:pdfs_descargados}")
    private String DIRECTORIO_BASE;

    // O si prefieres mantener el control directo:
    private Path getDirectorioBase() {
        return Paths.get(DIRECTORIO_BASE);
    }

    /**
     * Extrae URLs de PDFs del HTML del BORME para una fecha dada
     * @param fecha
     */
    public List<String> extraerUrlsPdfDelBorme(String fecha) {
        List<String> urlsPdf = new ArrayList<>();

        var respuesta = bormeService.consultarBorme(fecha);
        if (!respuesta.isExito()) {
            return urlsPdf; // lista vacía
        }

        String html = respuesta.getContenidoHtml();

        // Capturar bloque de SECCIÓN PRIMERA que comprende desde SECCION PRIMERA hasta SECCION SEGUNDA o final de página
        Pattern seccionPrimeraPattern = Pattern.compile(
                "<h[1-6][^>]*>\\s*SECCI[ÓO]N\\s+PRIMERA.*?</h[1-6]>(.*?)(?=<h[1-6][^>]*>\\s*SECCI[ÓO]N\\s+SEGUNDA|\\z)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher seccionPrimeraMatcher = seccionPrimeraPattern.matcher(html);

        if (!seccionPrimeraMatcher.find()) {
            return urlsPdf; // Como no hay sección primera se devuelve una lista vacía
        }

        String bloqueSeccionPrimera = seccionPrimeraMatcher.group(1);

        // Desde "Actos inscritos" si existe
        Pattern actosInscritosPattern = Pattern.compile(
                "<h[1-6][^>]*>\\s*Actos\\s+inscritos\\s*</h[1-6]>(.*)",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );
        Matcher actosMatcher = actosInscritosPattern.matcher(bloqueSeccionPrimera);

        String contenidoParaBuscar = actosMatcher.find()
                ? actosMatcher.group(1)
                : bloqueSeccionPrimera;

        // Extraer TODAS las URLs de los PDFs dentro del contenido seleccionado
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
     * Devuelve todos los PDFs de una fecha, descargando los que falten
     * @param fecha
     */
    public List<String> obtenerPdfsDelBorme(String fecha) {

        // URLs esperadas
        List<String> urlsPdf = extraerUrlsPdfDelBorme(fecha);

        // PDFs ya descargados anteriormente
        List<String> pdfsDisponibles = listarPdfsLocales(fecha);

        Set<String> nombresLocales = pdfsDisponibles.stream()
                .map(p -> Paths.get(p).getFileName().toString())
                .collect(Collectors.toSet());

        // Filtrar los que faltan
        List<String> urlsFaltantes = urlsPdf.stream()
                .filter(url -> !nombresLocales.contains(extraerNombreArchivoDeUrl(url)))
                .collect(Collectors.toList());

        // Descargar los que falten
        if (!urlsFaltantes.isEmpty()) {
            System.out.println("Faltan " + urlsFaltantes.size() + " PDFs. Descargando...");
            List<String> nuevos = descargarPdfs(fecha, urlsFaltantes);
            pdfsDisponibles.addAll(nuevos);
        } else {
            System.out.println("Todos los PDFs ya están descargados para fecha: " + fecha);
        }

        return pdfsDisponibles;
    }

    /**
     * Lista los PDFs ya descargados en disco para una fecha
     * @param fecha
     */
    public List<String> listarPdfsLocales(String fecha) {
        List<String> archivosLocales = new ArrayList<>();
        Path directorioFecha = getDirectorioBase().resolve(fecha);

        if (Files.exists(directorioFecha) && Files.isDirectory(directorioFecha)) {
            try (Stream<Path> stream = Files.list(directorioFecha)) {
                archivosLocales = stream
                        .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                        .map(Path::toString)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                System.err.println("Error leyendo directorio existente: " + e.getMessage());
            }
        }

        return archivosLocales;
    }

    /**
     * Descarga una lista de URLs de PDFs en el directorio de la fecha
     * @param fecha
     * @param urlsPdf
     */
    private List<String> descargarPdfs(String fecha, List<String> urlsPdf) {
        List<String> archivosDescargados = new ArrayList<>();

        Path directorioFecha = getDirectorioBase().resolve(fecha);

        try {
            if (!Files.exists(directorioFecha)) {
                Files.createDirectories(directorioFecha);
                System.out.println("Directorio creado: " + directorioFecha.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creando directorio: " + e.getMessage());
            return archivosDescargados;
        }

        for (String urlPdf : urlsPdf) {
            String nombreArchivo = extraerNombreArchivoDeUrl(urlPdf);
            Path rutaCompleta = directorioFecha.resolve(nombreArchivo);

            if (Files.exists(rutaCompleta)) { // Situación deseablemente nunca posible
                // Ya existe, no se descarga de nuevo
                archivosDescargados.add(rutaCompleta.toString());
                System.out.println("PDF ya existente: " + rutaCompleta);
                continue;
            }

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
