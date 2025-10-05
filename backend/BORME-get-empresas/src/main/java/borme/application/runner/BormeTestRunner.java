package borme.application.runner;

import borme.domain.ConstitucionEmpresa;
import borme.repository.ConstitucionEmpresaRepository;
import borme.services.BormeOrchestratorService;
import borme.services.BormeService;
import borme.domain.BormeResponse;
import borme.services.ConstitucionEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.LocalDate;



import borme.domain.ConstitucionEmpresaId;
import java.util.Optional;


import org.springframework.jdbc.core.JdbcTemplate;


@Component
public class BormeTestRunner implements CommandLineRunner {

    @Autowired
    private BormeService bormeService;

    @Autowired
    private BormeOrchestratorService bormeOrchestratorService;

    @Autowired
    private ConstitucionEmpresaRepository constitucionEmpresaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO PRUEBAS BORME ===");

        testConsultaExitosa();
        testFechaInvalida();
        testFechaFutura();
        testConsultaHoy();
        testDescargaPdfs();

        System.out.println("=== INICIANDO PRUEBAS JPA Y BASE DE DATOS ===");

        testJpaGuardarConstitucion();
        testJpaBuscarPorClavePrimaria();
        testJpaBuscarPorNombre();        // <- Usa findByNombreEmpresaContaining
        testJpaBuscarPorFecha();         // <- Usa findByFechaConstitucionContaining
        testJpaBuscarPorCapital();       // <- Usa findByCapitalMinimo
        testJpaBuscarPorRangoFechas();   // <- Usa findByFechaConstitucionBetween
        testJpaActualizarRegistro();
        testJpaEliminarRegistro();
        testJpaConsultarTodos();

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

    // ========== PRUEBAS JPA ==========

    private void testJpaGuardarConstitucion() {
        System.out.println("\n--- Prueba JPA 1: Guardar Constitución ---");

        ConstitucionEmpresa empresa = new ConstitucionEmpresa();
        empresa.setNumeroAsiento("TEST-001");
        empresa.setFechaConstitucion(LocalDate.parse("2024-01-15"));
        empresa.setNombreEmpresa("Empresa de Pruebas S.L.");
        empresa.setObjetoSocial("Desarrollo de software y consultoría IT");
        empresa.setDomicilio("Calle Prueba 123, Madrid");
        empresa.setCapital("30000.00 EUR");

        try {
            ConstitucionEmpresa guardada = constitucionEmpresaRepository.save(empresa);
            System.out.println("✓ Constitución guardada: " + guardada);
            System.out.println("  - Asiento: " + guardada.getNumeroAsiento());
            System.out.println("  - Fecha: " + guardada.getFechaConstitucion());
            System.out.println("  - Empresa: " + guardada.getNombreEmpresa());
        } catch (Exception e) {
            System.err.println("✗ Error guardando: " + e.getMessage());
        }
    }

    private void testJpaBuscarPorClavePrimaria() {
        System.out.println("\n--- Prueba JPA 2: Buscar por Clave Primaria ---");

        try {
            ConstitucionEmpresaId id = new ConstitucionEmpresaId("TEST-001", LocalDate.parse("2024-01-15"));
            Optional<ConstitucionEmpresa> encontrada = constitucionEmpresaRepository.findById(id);

            if (encontrada.isPresent()) {
                System.out.println("✓ Constitución encontrada:");
                System.out.println("  - Empresa: " + encontrada.get().getNombreEmpresa());
                System.out.println("  - Objeto Social: " + encontrada.get().getObjetoSocial());
                System.out.println("  - Capital: " + encontrada.get().getCapital());
            } else {
                System.out.println("✗ No se encontró la constitución");
            }
        } catch (Exception e) {
            System.err.println("✗ Error buscando: " + e.getMessage());
        }
    }

    private void testJpaBuscarPorNombre() {
        System.out.println("\n--- Prueba JPA 3: Buscar por Nombre ---");

        try {
            List<ConstitucionEmpresa> resultados = constitucionEmpresaRepository
                    .findByNombreEmpresaContainingIgnoreCase("Prueba");

            System.out.println("✓ Encontradas " + resultados.size() + " empresas:");
            for (ConstitucionEmpresa emp : resultados) {
                System.out.println("  - " + emp.getNombreEmpresa() + " (Asiento: " + emp.getNumeroAsiento() + ")");
            }
        } catch (Exception e) {
            System.err.println("✗ Error buscando por nombre: " + e.getMessage());
        }
    }

    private void testJpaActualizarRegistro() {
        System.out.println("\n--- Prueba JPA 4: Actualizar Registro ---");

        try {
            ConstitucionEmpresaId id = new ConstitucionEmpresaId("TEST-001", LocalDate.parse("2024-01-15"));
            Optional<ConstitucionEmpresa> encontrada = constitucionEmpresaRepository.findById(id);

            if (encontrada.isPresent()) {
                ConstitucionEmpresa empresa = encontrada.get();
                empresa.setCapital("50000.00 EUR"); // Actualizar capital
                empresa.setDomicilio("Calle Actualizada 456, Barcelona");

                ConstitucionEmpresa actualizada = constitucionEmpresaRepository.save(empresa);
                System.out.println("✓ Constitución actualizada:");
                System.out.println("  - Nuevo capital: " + actualizada.getCapital());
                System.out.println("  - Nuevo domicilio: " + actualizada.getDomicilio());
            }
        } catch (Exception e) {
            System.err.println("✗ Error actualizando: " + e.getMessage());
        }
    }

    private void testJpaEliminarRegistro() {
        System.out.println("\n--- Prueba JPA 5: Eliminar Registro ---");

        try {
            // Primero creamos uno para eliminar
            ConstitucionEmpresa temporal = new ConstitucionEmpresa();
            temporal.setNumeroAsiento("TEMP-DELETE");
            temporal.setFechaConstitucion(LocalDate.parse("2024-01-01"));
            temporal.setNombreEmpresa("Empresa Temporal");
            temporal.setObjetoSocial("Temporal");
            temporal.setDomicilio("Temp");
            temporal.setCapital("1000");

            constitucionEmpresaRepository.save(temporal);
            System.out.println("✓ Registro temporal creado");

            // Ahora lo eliminamos
            ConstitucionEmpresaId id = new ConstitucionEmpresaId("TEMP-DELETE", LocalDate.parse("2024-01-01"));
            constitucionEmpresaRepository.deleteById(id);
            System.out.println("✓ Registro temporal eliminado");

        } catch (Exception e) {
            System.err.println("✗ Error en eliminación: " + e.getMessage());
        }
    }

    private void testJpaConsultarTodos() {
        System.out.println("\n--- Prueba JPA 6: Consultar Todos los Registros ---");

        try {
            List<ConstitucionEmpresa> todas = constitucionEmpresaRepository.findAll();
            System.out.println("✓ Total de constituciones en BD: " + todas.size());

            for (ConstitucionEmpresa emp : todas) {
                System.out.println("  - " + emp.getNumeroAsiento() + ": " + emp.getNombreEmpresa());
            }
        } catch (Exception e) {
            System.err.println("✗ Error consultando todos: " + e.getMessage());
        }
    }


    private void testJpaBuscarPorFecha() {
        System.out.println("\n--- Prueba JPA 4: Buscar por Fecha ---");

        try {
            // Usa findByFechaConstitucionContaining (que tienes con @Query)
            List<ConstitucionEmpresa> resultados = constitucionEmpresaRepository
                    .findByFechaConstitucionContaining(LocalDate.parse("2024-01-15"));

            System.out.println("✓ Encontradas " + resultados.size() + " empresas para la fecha:");
            for (ConstitucionEmpresa emp : resultados) {
                System.out.println("  - " + emp.getNombreEmpresa() + " (Fecha: " + emp.getFechaConstitucion() + ")");
            }
        } catch (Exception e) {
            System.err.println("✗ Error buscando por fecha: " + e.getMessage());
        }
    }

    private void testJpaBuscarPorCapital() {
        System.out.println("\n--- Prueba JPA 5: Buscar por Capital Mínimo ---");

        try {
            // Usa findByCapitalMinimo (que tienes con @Query)
            List<ConstitucionEmpresa> resultados = constitucionEmpresaRepository
                    .findByCapitalMinimo("10000");

            System.out.println("✓ Encontradas " + resultados.size() + " empresas con capital >= 10000:");
            for (ConstitucionEmpresa emp : resultados) {
                System.out.println("  - " + emp.getNombreEmpresa() + " (Capital: " + emp.getCapital() + ")");
            }
        } catch (Exception e) {
            System.err.println("✗ Error buscando por capital: " + e.getMessage());
        }
    }

    private void testJpaBuscarPorRangoFechas() {
        System.out.println("\n--- Prueba JPA 6: Buscar por Rango de Fechas ---");

        try {
            // Usa findByFechaConstitucionBetween (que tienes)
            List<ConstitucionEmpresa> resultados = constitucionEmpresaRepository
                    .findByFechaConstitucionBetween(LocalDate.parse("2024-01-01"), LocalDate.parse("2024-12-31"));

            System.out.println("✓ Encontradas " + resultados.size() + " empresas en el rango de fechas:");
            for (ConstitucionEmpresa emp : resultados) {
                System.out.println("  - " + emp.getNombreEmpresa() + " (Fecha: " + emp.getFechaConstitucion() + ")");
            }
        } catch (Exception e) {
            System.err.println("✗ Error buscando por rango de fechas: " + e.getMessage());
        }
    }

}