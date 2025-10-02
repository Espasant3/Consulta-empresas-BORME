package backend.services;

import backend.parsers.BormePdfParser;
import backend.domain.ConstitucionEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BormeOrchestratorService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private BormePdfParser pdfParser;

    /**
     * Proceso completo: consulta BORME, descarga PDFs y extrae constituciones
     */
    public List<ConstitucionEmpresa> procesarBormeCompleto(String fecha, String directorioPdfs) {
        List<ConstitucionEmpresa> todasConstituciones = new ArrayList<>();

        System.out.println("Descargando PDFs para fecha: " + fecha);
        List<String> archivosPdf = pdfService.descargarPdfsDelBorme(fecha, directorioPdfs);

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