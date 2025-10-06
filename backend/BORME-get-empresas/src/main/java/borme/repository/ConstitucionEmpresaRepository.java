package borme.repository;

import borme.domain.ConstitucionEmpresa;
import borme.domain.ConstitucionEmpresaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConstitucionEmpresaRepository extends JpaRepository<ConstitucionEmpresa, ConstitucionEmpresaId> {

    // Métodos que ya tienes
    List<ConstitucionEmpresa> findByNombreEmpresaContaining(String nombre);

    @Query("SELECT c FROM ConstitucionEmpresa c WHERE c.fechaConstitucion = :date")
    List<ConstitucionEmpresa> findByFechaConstitucion(LocalDate date);

    @Query("SELECT c FROM ConstitucionEmpresa c WHERE c.capital >= :capitalMinimo")
    List<ConstitucionEmpresa> findByCapitalMinimo(@Param("capitalMinimo") String capitalMinimo);

    List<ConstitucionEmpresa> findByFechaConstitucionBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Búsqueda case-insensitive
    List<ConstitucionEmpresa> findByNombreEmpresaContainingIgnoreCase(String nombre);

    // Búsqueda exacta por nombre
    List<ConstitucionEmpresa> findByNombreEmpresa(String nombre);

    // Búsqueda por parte del objeto social
    List<ConstitucionEmpresa> findByObjetoSocialContaining(String objetoSocial);

    // Contar empresas por fecha
    @Query("SELECT COUNT(c) FROM ConstitucionEmpresa c WHERE c.fechaConstitucion = :fecha")
    Long countByFechaConstitucion(@Param("fecha") LocalDate fecha);

    // Buscar usando la clave primaria compuesta
    Optional<ConstitucionEmpresa> findByNumeroAsientoAndFechaConstitucion(String numeroAsiento, LocalDate fechaConstitucion);
}