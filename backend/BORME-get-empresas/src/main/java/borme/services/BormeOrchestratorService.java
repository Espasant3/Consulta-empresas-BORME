package borme.services;

import borme.parsers.BormePdfParser;
import borme.domain.ConstitucionEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BormeOrchestratorService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private BormePdfParser pdfParser;

    @Autowired
    private ConstitucionEmpresaService constitucionEmpresaService;

    /**
     * Proceso orquestado completo:
     * 1. Verifica si ya hay datos en BD para la fecha
     * 2. Si hay, los devuelve desde BD
     * 3. Si no, procesa PDFs, extrae constituciones y las guarda en BD
     *
     * @return Lista de constituciones (desde BD o recién procesadas)
     */
    public List<ConstitucionEmpresa> procesarBormeCompleto(String fecha) {
        System.out.println("=== ORQUESTANDO PROCESAMIENTO BORME PARA: " + fecha + " ===");

        LocalDate fechaLocalDate = LocalDate.parse(fecha);

        // 1. Verificar si ya existen datos en BD para esta fecha
        if (constitucionEmpresaService.existenConstitucionesParaFecha(fechaLocalDate)) {
            System.out.println("✅ Ya existen constituciones en BD para " + fecha + ". Recuperando desde BD...");
            return constitucionEmpresaService.obtenerConstitucionesPorFecha(fechaLocalDate);
        }

        // 2. No hay datos en BD, procesar desde PDFs
        System.out.println("📋 No hay constituciones en BD para " + fecha + ". Procesando desde PDFs...");

        List<ConstitucionEmpresa> constitucionesExtraidas = new ArrayList<>();

        try {
            // Obtener PDFs del BORME
            List<String> archivosPdf = pdfService.obtenerPdfsDelBorme(fecha);
            System.out.println("📄 PDFs a procesar: " + archivosPdf.size());

            // Procesar cada PDF
            for (String archivoPdf : archivosPdf) {
                System.out.println("🔍 Procesando PDF: " + archivoPdf);
                String textoPdf = pdfParser.extraerTextoPdf(archivoPdf);

                if (textoPdf != null) {
                    List<ConstitucionEmpresa> constituciones = pdfParser.parsearConstituciones(textoPdf, extraerNombrePDFDeUrl(archivoPdf), fecha);
                    constitucionesExtraidas.addAll(constituciones);
                }
            }

            // 3. Guardar en BD las constituciones extraídas y devolverlas
            System.out.println("💾 Guardando " + constitucionesExtraidas.size() + " constituciones en BD...");
            return constitucionEmpresaService.guardarConstituciones(constitucionesExtraidas);

        } catch (Exception e) {
            System.err.println("💥 Error en procesamiento: " + e.getMessage());
            throw new RuntimeException("Error procesando BORME: " + e.getMessage(), e);
        }
    }

    public String extraerNombrePDFDeUrl(String url) {
        return Paths.get(URI.create(url).getPath()).getFileName().toString();
    }


}