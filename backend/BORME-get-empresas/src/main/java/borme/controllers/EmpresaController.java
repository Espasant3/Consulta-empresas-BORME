package borme.controllers;

import borme.services.BormeOrchestratorService;
import borme.services.ConstitucionEmpresaService;
import borme.domain.ConstitucionEmpresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    @Autowired
    private ConstitucionEmpresaService empresaService;

    @Autowired
    private BormeOrchestratorService orchestratorService;

    /**
     * Obtener constituciones para una fecha específica
     * Si no existen en BD, las descarga y procesa automáticamente
     * GET /api/empresas/fecha/2024-09-10
     */
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<Map<String, Object>> getEmpresasPorFecha(@PathVariable String fecha) {
        Map<String, Object> resultado = orchestratorService.obtenerConstitucionesParaAPI(fecha);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Buscar constituciones por nombre de empresa
     * GET /api/empresas/buscar?nombre=tech
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ConstitucionEmpresa>> buscarEmpresas(@RequestParam String nombre) {
        List<ConstitucionEmpresa> empresas = empresaService.findByNombreContaining(nombre);
        return ResponseEntity.ok(empresas);
    }

    /**
     * Obtener todas las constituciones (solo las que ya están en BD)
     * GET /api/empresas
     */
    @GetMapping
    public ResponseEntity<List<ConstitucionEmpresa>> getAllEmpresas() {
        List<ConstitucionEmpresa> empresas = empresaService.findAll();
        return ResponseEntity.ok(empresas);
    }

    /**
     * Obtener una empresa específica
     * GET /api/empresas/389847/2024-09-10
     */
    @GetMapping("/{numeroAsiento}/{fechaConstitucion}")
    public ResponseEntity<ConstitucionEmpresa> getEmpresa(
            @PathVariable String numeroAsiento,
            @PathVariable String fechaConstitucion) {

        LocalDate fecha = LocalDate.parse(fechaConstitucion);
        Optional<ConstitucionEmpresa> empresa = empresaService.findById(numeroAsiento, fecha);

        return empresa.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtener estadísticas
     * GET /api/empresas/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        Map<String, Object> estadisticas = empresaService.getEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
}