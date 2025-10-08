package borme.services;

import borme.parsers.BormePdfParser;
import borme.domain.ConstitucionEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BormeOrchestratorService {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private BormePdfParser pdfParser;

    @Autowired
    private ConstitucionEmpresaService constitucionEmpresaService;

    /**
     * Consulta BD y si no hay datos, los procesa
     */
    public List<ConstitucionEmpresa> procesarBormeCompleto(String fecha) {
        System.out.println("=== ORQUESTANDO PROCESAMIENTO BORME PARA: " + fecha + " ===");

        LocalDate fechaLocalDate = LocalDate.parse(fecha);

        // Verificar si ya existen datos en BD para esta fecha
        if (constitucionEmpresaService.existenConstitucionesParaFecha(fechaLocalDate)) {
            System.out.println("Ya existen constituciones en BD para " + fecha + ". Recuperando desde BD...");
            return constitucionEmpresaService.obtenerConstitucionesPorFecha(fechaLocalDate);
        }

        // No hay datos en BD, procesar desde PDFs
        System.out.println("No hay constituciones en BD para " + fecha + ". Procesando desde PDFs...");

        List<ConstitucionEmpresa> constitucionesExtraidas = new ArrayList<>();

        try {
            // Obtener PDFs del BORME
            List<String> archivosPdf = pdfService.obtenerPdfsDelBorme(fecha);
            System.out.println("PDFs a procesar: " + archivosPdf.size());

            // Procesar cada PDF
            for (String archivoPdf : archivosPdf) {
                System.out.println("Procesando PDF: " + archivoPdf);
                String textoPdf = pdfParser.extraerTextoPdf(archivoPdf);

                if (textoPdf != null) {
                    List<ConstitucionEmpresa> constituciones = pdfParser.parsearConstituciones(textoPdf, extraerNombrePDFDeUrl(archivoPdf), fecha);
                    constitucionesExtraidas.addAll(constituciones);
                }
            }

            // Guardar en BD las constituciones extraídas y devolverlas
            System.out.println("Guardando " + constitucionesExtraidas.size() + " constituciones en BD...");
            return constitucionEmpresaService.guardarConstituciones(constitucionesExtraidas);

        } catch (Exception e) {
            System.err.println("Error en procesamiento: " + e.getMessage());
            throw new RuntimeException("Error procesando BORME: " + e.getMessage(), e);
        }
    }

    /**
     * Solo para la API
     * Devuelve metadatos del procesamiento sin lógica compleja
     */
    public Map<String, Object> obtenerConstitucionesParaAPI(String fecha) {
        Map<String, Object> resultado = new HashMap<>();

        try {

            List<ConstitucionEmpresa> constituciones = procesarBormeCompleto(fecha);

            resultado.put("exito", true);
            resultado.put("fecha", fecha);
            resultado.put("totalConstituciones", constituciones.size());
            resultado.put("constituciones", constituciones);
            resultado.put("mensaje", "Datos obtenidos correctamente");

        } catch (Exception e) {
            System.err.println("Error obteniendo constituciones: " + e.getMessage());
            resultado.put("exito", false);
            resultado.put("fecha", fecha);
            resultado.put("error", e.getMessage());
            resultado.put("totalConstituciones", 0);
        }

        return resultado;
    }


    public String extraerNombrePDFDeUrl(String url) {
        return Paths.get(URI.create(url).getPath()).getFileName().toString();
    }
}