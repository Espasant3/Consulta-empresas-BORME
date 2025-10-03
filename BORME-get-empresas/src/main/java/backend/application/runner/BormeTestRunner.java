package backend.application.runner;

import backend.domain.ConstitucionEmpresa;
import backend.services.BormeOrchestratorService;
import backend.services.BormeService;
import backend.domain.BormeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class BormeTestRunner implements CommandLineRunner {

    @Autowired
    private BormeService bormeService;

    @Autowired
    private BormeOrchestratorService bormeOrchestratorService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO PRUEBAS BORME ===");

        testConsultaExitosa();
        testFechaInvalida();
        testFechaFutura();
        testConsultaHoy();
        testDescargaPdfs();

        System.out.println("=== PRUEBAS COMPLETADAS ===");
    }

    public void runTests() {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testConsultaExitosa() {
        System.out.println("\n--- Prueba 1: Consulta exitosa ---");
        String fecha = "2024-09-10";

        if (bormeService.validarFecha(fecha)) {
            BormeResponse respuesta = bormeService.consultarBorme(fecha);

            System.out.println("Fecha: " + fecha);
            System.out.println("Éxito: " + respuesta.isExito());
            System.out.println("URL: " + respuesta.getUrlConsultada());
            System.out.println("Código: " + respuesta.getCodigoEstado());

            if (respuesta.isExito()) {
                System.out.println("Contenido obtenido: " + (respuesta.getContenidoHtml() != null ? "SÍ" : "NO"));
                if (respuesta.getContenidoHtml() != null && respuesta.getContenidoHtml().length() > 200) {
                    System.out.println("Preview: " + respuesta.getContenidoHtml().substring(0, 200) + "...");
                }
            } else {
                System.out.println("Error: " + respuesta.getMensajeError());
            }
        } else {
            System.out.println("Fecha no válida: " + fecha);
        }
    }

    private void testFechaInvalida() {
        System.out.println("\n--- Prueba 2: Fecha inválida ---");
        String fecha = "2024-13-45";

        boolean esValida = bormeService.validarFecha(fecha);
        System.out.println("Fecha: " + fecha);
        System.out.println("Válida: " + esValida);
    }

    private void testFechaFutura() {
        System.out.println("\n--- Prueba 3: Fecha futura ---");
        String fechaFutura = LocalDate.now().plusDays(10).format(DATE_FORMATTER);

        boolean esValida = bormeService.validarFecha(fechaFutura);
        System.out.println("Fecha futura: " + fechaFutura);
        System.out.println("Válida: " + esValida);
    }

    private void testConsultaHoy() {
        System.out.println("\n--- Prueba 4: Consulta de hoy ---");

        BormeResponse respuesta = bormeService.consultarBormeHoy();

        System.out.println("Fecha: " + respuesta.getFechaConsulta());
        System.out.println("Éxito: " + respuesta.isExito());
        System.out.println("URL: " + respuesta.getUrlConsultada());

        if (respuesta.isExito()) {
            System.out.println("Contenido obtenido: " + (respuesta.getContenidoHtml() != null ? "SÍ" : "NO"));
        } else {
            System.out.println("Error: " + respuesta.getMensajeError());
        }
    }

    private void testDescargaPdfs() {
        System.out.println("\n--- Prueba 5: Descarga y Procesamiento de PDFs ---");
        String fecha = "2024-09-10";

        try {
            List<ConstitucionEmpresa> constituciones = bormeOrchestratorService.procesarBormeCompleto(fecha);

            System.out.println("=== RESULTADOS ===");
            System.out.println("Total constituciones encontradas: " + constituciones.size());

            for (ConstitucionEmpresa constitucion : constituciones) {
                System.out.println("\n--- Constitución ---");
                System.out.println("Asiento: " + constitucion.getNumeroAsiento());
                System.out.println("Empresa: " + constitucion.getNombreEmpresa());
                System.out.println("Fecha de constitución: " + constitucion.getFechaConstitucion());
                System.out.println("Objeto Social: " + constitucion.getObjetoSocial());
                System.out.println("Domicilio: " + constitucion.getDomicilio());
                System.out.println("Capital: " + constitucion.getCapital());
            }

        } catch (Exception e) {
            System.err.println("Error en prueba de PDFs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}