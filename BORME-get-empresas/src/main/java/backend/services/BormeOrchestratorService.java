package backend.services;

import backend.parsers.BormePdfParser;
import backend.domain.ConstitucionEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.ArrayList;
import java.util.List;

@Service
public class BormeOrchestratorService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private BormePdfParser pdfParser;

    private static final String DIRECTORIO_BASE = "pdfs_descargados";

    /**
     * Proceso completo: consulta BORME, descarga PDFs y extrae constituciones
     */
    public List<ConstitucionEmpresa> procesarBormeCompleto(String fecha) {
        List<ConstitucionEmpresa> todasConstituciones = new ArrayList<>();

        // Ruta del directorio de esa fecha
        Path directorioFecha = Paths.get(DIRECTORIO_BASE, fecha);

        List<String> archivosPdf = new ArrayList<>();

        // Comprobar si ya existe el directorio correspondiente y contiene PDFs
        if (Files.exists(directorioFecha) && Files.isDirectory(directorioFecha)) {
            try (Stream<Path> stream = Files.list(directorioFecha)) {
                archivosPdf = stream
                        .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                        .map(Path::toString)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                System.err.println("Error leyendo directorio existente: " + e.getMessage());
            }
        }

        // Si no hay PDFs, descargar
        if (archivosPdf.isEmpty()) {
            System.out.println("No se encontraron PDFs locales. Descargando para fecha: " + fecha);
            archivosPdf = pdfService.descargarPdfsDelBorme(fecha);
        } else {
            System.out.println("Usando PDFs ya descargados para fecha: " + fecha);
        }

        // Procesar PDFs
        for (String archivoPdf : archivosPdf) {
            System.out.println("Procesando PDF: " + archivoPdf);
            String textoPdf = pdfParser.extraerTextoPdf(archivoPdf);

            if (textoPdf != null) {
                List<ConstitucionEmpresa> constituciones = pdfParser.parsearConstituciones(textoPdf);
                todasConstituciones.addAll(constituciones);
            }
        }

        return todasConstituciones;
    }
}
