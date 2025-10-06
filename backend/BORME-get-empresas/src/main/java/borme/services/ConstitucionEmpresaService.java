package borme.services;

import borme.domain.ConstitucionEmpresa;
import borme.domain.ConstitucionEmpresaId;
import borme.repository.ConstitucionEmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConstitucionEmpresaService {

    @Autowired
    private ConstitucionEmpresaRepository repository;

    // ========== MÉTODOS EXISTENTES ==========

    public List<ConstitucionEmpresa> buscarPorNombre(String nombre) {
        return repository.findByNombreEmpresaContaining(nombre);
    }

    public ConstitucionEmpresa guardar(ConstitucionEmpresa empresa) {
        return repository.save(empresa);
    }

    // ========== NUEVOS MÉTODOS PARA GESTIÓN COMPLETA ==========

    /**
     * Verificar si ya existen constituciones para una fecha
     */
    public boolean existenConstitucionesParaFecha(LocalDate fecha) {
        return repository.findByFechaConstitucion(fecha).size() > 0;
    }

    /**
     * Obtener constituciones por fecha
     */
    public List<ConstitucionEmpresa> obtenerConstitucionesPorFecha(LocalDate fecha) {
        return repository.findByFechaConstitucion(fecha);
    }

    /**
     * Guardar múltiples constituciones con manejo de duplicados
     * Devuelve la lista de constituciones que se guardaron exitosamente
     */
    public List<ConstitucionEmpresa> guardarConstituciones(List<ConstitucionEmpresa> constituciones) {
        List<ConstitucionEmpresa> guardadasExitosamente = new ArrayList<>();
        int duplicadas = 0;
        int errores = 0;

        for (ConstitucionEmpresa constitucion : constituciones) {
            try {
                if (guardarConstitucionSiNoExiste(constitucion)) {
                    guardadasExitosamente.add(constitucion);
                } else {
                    duplicadas++;
                    System.out.println("⚠️  Duplicada: " + constitucion.getNumeroAsiento());
                }
            } catch (Exception e) {
                errores++;
                System.err.println("❌ Error guardando " + constitucion.getNumeroAsiento() + ": " + e.getMessage());
            }
        }

        // Solo log las estadísticas
        if (duplicadas > 0 || errores > 0) {
            System.out.println("📊 Resumen guardado: " + guardadasExitosamente.size() + " exitosas, " +
                    duplicadas + " duplicadas, " + errores + " errores");
        }

        return guardadasExitosamente;
    }

    /**
     * Guardar una constitución solo si no existe (evitar duplicados)
     */
    private boolean guardarConstitucionSiNoExiste(ConstitucionEmpresa constitucion) {
        if (constitucion.getNumeroAsiento() == null || constitucion.getFechaConstitucion() == null) {
            throw new IllegalArgumentException("Constitución inválida: falta número de asiento o fecha");
        }

        // Verificar si ya existe por clave primaria compuesta
        boolean existe = repository.existsById(
                new ConstitucionEmpresaId(
                        constitucion.getNumeroAsiento(),
                        constitucion.getFechaConstitucion()
                )
        );

        if (!existe) {
            repository.save(constitucion);
            return true;
        }
        return false;
    }

    /**
     * Buscar por rango de fechas
     */
    public List<ConstitucionEmpresa> buscarPorRangoFechas(LocalDate desde, LocalDate hasta) {
        return repository.findByFechaConstitucionBetween(desde, hasta);
    }

    /**
     * Buscar por capital mínimo
     */
    public List<ConstitucionEmpresa> buscarPorCapitalMinimo(String capitalMinimo) {
        return repository.findByCapitalMinimo(capitalMinimo);
    }

    /**
     * Buscar por clave primaria
     */
    public Optional<ConstitucionEmpresa> buscarPorClavePrimaria(String numeroAsiento, LocalDate fecha) {
        return repository.findById(new ConstitucionEmpresaId(numeroAsiento, fecha));
    }

    /**
     * Obtener todas las constituciones
     */
    public List<ConstitucionEmpresa> obtenerTodas() {
        return repository.findAll();
    }

    /**
     * Contar total de constituciones
     */
    public long contarTotal() {
        return repository.count();
    }

    /**
     * Eliminar una constitución
     */
    public void eliminar(String numeroAsiento, LocalDate fecha) {
        repository.deleteById(new ConstitucionEmpresaId(numeroAsiento, fecha));
    }

    /**
     * Verificar si existe una constitución
     */
    public boolean existe(String numeroAsiento, LocalDate fecha) {
        return repository.existsById(new ConstitucionEmpresaId(numeroAsiento, fecha));
    }

}