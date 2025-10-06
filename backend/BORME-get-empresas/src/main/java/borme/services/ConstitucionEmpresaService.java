package borme.services;

import borme.domain.ConstitucionEmpresa;
import borme.domain.ConstitucionEmpresaId;
import borme.repository.ConstitucionEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class ConstitucionEmpresaService {

    @Autowired
    private ConstitucionEmpresaRepository repository;

    // Tus métodos existentes de consulta
    public List<ConstitucionEmpresa> findAll() {
        return repository.findAll();
    }

    public List<ConstitucionEmpresa> findByFechaConstitucion(LocalDate fecha) {
        return repository.findByFechaConstitucion(fecha);
    }

    public List<ConstitucionEmpresa> findByNombreContaining(String nombre) {
        return repository.findByNombreEmpresaContainingIgnoreCase(nombre);
    }

    public Optional<ConstitucionEmpresa> findById(String numeroAsiento, LocalDate fechaConstitucion) {
        return repository.findByNumeroAsientoAndFechaConstitucion(numeroAsiento, fechaConstitucion);
    }

    public Map<String, Object> getEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmpresas", repository.count());
        stats.put("fechaMasReciente", repository.findMaxFechaConstitucion());
        stats.put("fechaMasAntigua", repository.findMinFechaConstitucion());
        return stats;
    }

    // Mantener tus métodos existentes que usa BormeOrchestratorService
    public boolean existenConstitucionesParaFecha(LocalDate fecha) {
        return repository.existsByFechaConstitucion(fecha);
    }

    public List<ConstitucionEmpresa> obtenerConstitucionesPorFecha(LocalDate fecha) {
        return repository.findByFechaConstitucion(fecha);
    }

    public List<ConstitucionEmpresa> guardarConstituciones(List<ConstitucionEmpresa> constituciones) {
        return repository.saveAll(constituciones);
    }
}