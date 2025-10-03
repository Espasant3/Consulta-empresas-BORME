package backend.services;

import backend.parsers.BormePdfParser;
import backend.domain.ConstitucionEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class BormeOrchestratorService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private BormePdfParser pdfParser;

    /**
     * Proceso completo: consulta BORME, descarga PDFs (todos o los que falten) y extrae constituciones
     */
    public List<ConstitucionEmpresa> procesarBormeCompleto(String fecha) {
        List<ConstitucionEmpresa> todasConstituciones = new ArrayList<>();

        // Pedir a PdfService que me dé los PDFs de esa fecha (PdfService se encarga de extraer URLs, comprobar qué hay en disco y descargar lo que falte)
        List<String> archivosPdf = pdfService.obtenerPdfsDelBorme(fecha);

        // Procesar PDFs con el parser
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

