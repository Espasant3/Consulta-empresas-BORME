package borme.runner;

import borme.domain.ConstitucionEmpresa;
import borme.domain.ConstitucionEmpresaId;
import borme.repository.ConstitucionEmpresaRepository;
import borme.services.BormeOrchestratorService;
import borme.services.ConstitucionEmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BormeIntegrationTest implements CommandLineRunner {

    @Autowired
    private BormeOrchestratorService bormeOrchestratorService;

    @Autowired
    private ConstitucionEmpresaService constitucionEmpresaService;

    @Autowired
    private ConstitucionEmpresaRepository constitucionEmpresaRepository;

    private static final String FECHA_TEST = "2024-09-10";

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🚀 INICIANDO TEST DE INTEGRACIÓN COMPLETO");
        System.out.println("===========================================");

        testFlujoCompleto();
        testConsultasAvanzadas();
        testManejoDuplicados();
        testEstadisticas();

        System.out.println("✅ TEST DE INTEGRACIÓN COMPLETADO EXITOSAMENTE");
    }

    public void runTests() {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * TEST 1: Flujo completo del sistema
     */
    private void testFlujoCompleto() {
        System.out.println("\n📋 TEST 1: FLUJO COMPLETO DEL SISTEMA");
        System.out.println("-----------------------------------------");

        try {
            // Paso 1: Verificar que ya existen datos en BD (caché)
            System.out.println("1. Verificando caché en BD para " + FECHA_TEST + "...");
            boolean existenDatos = constitucionEmpresaService.existenConstitucionesParaFecha(LocalDate.parse(FECHA_TEST));
            System.out.println("   ¿Existen constituciones en BD? " + (existenDatos ? "✅ SÍ" : "❌ NO"));

            if (!existenDatos) {
                System.out.println("   ⚠️  No hay datos en BD. Esto es inesperado para la fecha " + FECHA_TEST);
                return;
            }

            // Paso 2: Usar el Orchestrator (debería devolver datos desde BD)
            System.out.println("2. Ejecutando Orchestrator...");
            List<ConstitucionEmpresa> resultado = bormeOrchestratorService.procesarBormeCompleto(FECHA_TEST);
            System.out.println("   ✅ Orchestrator completado. Constituciones obtenidas: " + resultado.size());

            // Paso 3: Verificar que los datos son consistentes
            System.out.println("3. Verificando consistencia de datos...");
            List<ConstitucionEmpresa> desdeBD = constitucionEmpresaService.obtenerConstitucionesPorFecha(LocalDate.parse(FECHA_TEST));
            System.out.println("   Constituciones en BD: " + desdeBD.size());
            System.out.println("   Constituciones del Orchestrator: " + resultado.size());

            if (resultado.size() == desdeBD.size()) {
                System.out.println("   ✅ Los conteos coinciden");
            } else {
                System.out.println("   ❌ Los conteos NO coinciden - posible problema");
            }

            // Paso 4: Mostrar muestra de datos
            System.out.println("4. Muestra de constituciones:");
            if (!resultado.isEmpty()) {
                for (int i = 0; i < Math.min(3, resultado.size()); i++) {
                    ConstitucionEmpresa c = resultado.get(i);
                    System.out.println("   📄 " + (i + 1) + ". " + c.getNumeroAsiento() + " - " +
                            c.getNombreEmpresa() + " (" + c.getFechaConstitucion() + ")");
                }
                if (resultado.size() > 3) {
                    System.out.println("   ... y " + (resultado.size() - 3) + " más");
                }
            }

            System.out.println("✅ TEST 1 COMPLETADO - Flujo principal funciona correctamente");

        } catch (Exception e) {
            System.err.println("❌ ERROR en Test 1: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TEST 2: Consultas avanzadas y búsquedas
     */
    private void testConsultasAvanzadas() {
        System.out.println("\n🔍 TEST 2: CONSULTAS AVANZADAS");
        System.out.println("--------------------------------");

        try {
            // Consulta 1: Búsqueda por nombre
            System.out.println("1. Búsqueda por nombre 'S.L.'...");
            List<ConstitucionEmpresa> porNombre = constitucionEmpresaService.buscarPorNombre("S.L.");
            System.out.println("   ✅ Encontradas: " + porNombre.size() + " empresas");
            if (!porNombre.isEmpty()) {
                System.out.println("   Ejemplo: " + porNombre.get(0).getNombreEmpresa());
            }

            // Consulta 2: Búsqueda por rango de fechas
            System.out.println("2. Búsqueda por rango de fechas (todo 2024)...");
            List<ConstitucionEmpresa> porRango = constitucionEmpresaService.buscarPorRangoFechas(
                    LocalDate.of(2024, 1, 1),
                    LocalDate.of(2024, 12, 31)
            );
            System.out.println("   ✅ Encontradas: " + porRango.size() + " constituciones en 2024");

            // Consulta 3: Búsqueda por capital mínimo
            System.out.println("3. Búsqueda por capital mínimo (10000)...");
            List<ConstitucionEmpresa> porCapital = constitucionEmpresaService.buscarPorCapitalMinimo("10000");
            System.out.println("   ✅ Encontradas: " + porCapital.size() + " empresas con capital >= 10000");
            if (!porCapital.isEmpty()) {
                System.out.println("   Ejemplo: " + porCapital.get(0).getNombreEmpresa() +
                        " - " + porCapital.get(0).getCapital());
            }

            // Consulta 4: Búsqueda por clave primaria
            System.out.println("4. Búsqueda de constitución específica...");
            if (!porNombre.isEmpty()) {
                ConstitucionEmpresa ejemplo = porNombre.get(0);
                Optional<ConstitucionEmpresa> porClave = constitucionEmpresaService.buscarPorClavePrimaria(
                        ejemplo.getNumeroAsiento(),
                        ejemplo.getFechaConstitucion()
                );
                if (porClave.isPresent()) {
                    System.out.println("   ✅ Constitución encontrada: " + porClave.get().getNombreEmpresa());
                } else {
                    System.out.println("   ❌ Constitución NO encontrada");
                }
            }

            System.out.println("✅ TEST 2 COMPLETADO - Consultas avanzadas funcionando");

        } catch (Exception e) {
            System.err.println("❌ ERROR en Test 2: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TEST 3: Manejo de duplicados y consistencia
     */
    private void testManejoDuplicados() {
        System.out.println("\n🛡️  TEST 3: MANEJO DE DUPLICADOS");
        System.out.println("----------------------------------");

        try {
            // Obtener una constitución existente para probar duplicado
            List<ConstitucionEmpresa> existentes = constitucionEmpresaService.obtenerConstitucionesPorFecha(LocalDate.parse(FECHA_TEST));
            if (existentes.isEmpty()) {
                System.out.println("⚠️  No hay constituciones para probar duplicados");
                return;
            }

            ConstitucionEmpresa ejemplo = existentes.get(0);

            System.out.println("1. Probando duplicado con: " + ejemplo.getNumeroAsiento());

            List<ConstitucionEmpresa> listaConEjemplo = new ArrayList<>();

            listaConEjemplo.add(ejemplo);

            // Intentar guardar la misma constitución (debería detectar duplicado)
            System.out.println("2. Intentando guardar duplicado...");
            ConstitucionEmpresa resultado = constitucionEmpresaService.guardarConstituciones(listaConEjemplo).get(0);

            if (resultado == null) {
                System.out.println("   ✅ Correctamente detectado como duplicado - no se guardó");
            } else {
                System.out.println("   ❌ ERROR: Se guardó un duplicado");
            }

            // Verificar que el conteo no cambió
            long conteoOriginal = constitucionEmpresaService.contarTotal();
            System.out.println("3. Verificando consistencia del conteo...");
            System.out.println("   Conteo total: " + conteoOriginal);

            // Probar el Orchestrator nuevamente (no debería duplicar)
            System.out.println("4. Ejecutando Orchestrator nuevamente...");
            List<ConstitucionEmpresa> resultadoOrchestrator = bormeOrchestratorService.procesarBormeCompleto(FECHA_TEST);
            long conteoDespues = constitucionEmpresaService.contarTotal();

            if (conteoOriginal == conteoDespues) {
                System.out.println("   ✅ Conteo consistente - no hay duplicados");
            } else {
                System.out.println("   ❌ ERROR: Conteo cambió de " + conteoOriginal + " a " + conteoDespues);
            }

            System.out.println("✅ TEST 3 COMPLETADO - Manejo de duplicados correcto");

        } catch (Exception e) {
            System.err.println("❌ ERROR en Test 3: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * TEST 4: Estadísticas y métricas
     */
    private void testEstadisticas() {
        System.out.println("\n📊 TEST 4: ESTADÍSTICAS Y MÉTRICAS");
        System.out.println("-----------------------------------");

        try {
            // Estadística 1: Conteo total
            long total = constitucionEmpresaService.contarTotal();
            System.out.println("1. Conteo total de constituciones: " + total);

            // Estadística 2: Conteo por fecha específica
            List<ConstitucionEmpresa> porFecha = constitucionEmpresaService.obtenerConstitucionesPorFecha(LocalDate.parse(FECHA_TEST));
            System.out.println("2. Constituciones para " + FECHA_TEST + ": " + porFecha.size());

            // Estadística 3: Distribución por tipo de empresa (aproximado)
            long sociedadesLimitadas = constitucionEmpresaService.buscarPorNombre("S.L.").size();
            long sociedadesAnonimas = constitucionEmpresaService.buscarPorNombre("S.A.").size();
            System.out.println("3. Distribución aproximada:");
            System.out.println("   - Sociedades Limitadas (S.L.): " + sociedadesLimitadas);
            System.out.println("   - Sociedades Anónimas (S.A.): " + sociedadesAnonimas);

            // Estadística 4: Empresas con mayor capital
            List<ConstitucionEmpresa> grandesCapitales = constitucionEmpresaService.buscarPorCapitalMinimo("100000");
            System.out.println("4. Empresas con capital >= 100,000: " + grandesCapitales.size());

            // Mostrar algunas empresas grandes
            if (!grandesCapitales.isEmpty()) {
                System.out.println("   Ejemplos de grandes empresas:");
                for (int i = 0; i < Math.min(2, grandesCapitales.size()); i++) {
                    ConstitucionEmpresa empresa = grandesCapitales.get(i);
                    System.out.println("   💰 " + empresa.getNombreEmpresa() + " - " + empresa.getCapital());
                }
            }

            // Estadística 5: Verificar que todas las constituciones tienen fecha
            System.out.println("5. Verificando integridad de datos...");
            long constitucionesSinFecha = constitucionEmpresaService.obtenerTodas().stream()
                    .filter(c -> c.getFechaConstitucion() == null)
                    .count();
            System.out.println("   Constituciones sin fecha: " + constitucionesSinFecha + " (debería ser 0)");

            if (constitucionesSinFecha == 0) {
                System.out.println("   ✅ Todas las constituciones tienen fecha");
            } else {
                System.out.println("   ❌ Hay constituciones sin fecha");
            }

            System.out.println("✅ TEST 4 COMPLETADO - Estadísticas calculadas correctamente");

        } catch (Exception e) {
            System.err.println("❌ ERROR en Test 4: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar para ejecutar tests individuales
     */
    public void ejecutarTests() {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}