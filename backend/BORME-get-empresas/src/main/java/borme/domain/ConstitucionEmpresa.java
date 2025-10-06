package borme.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@IdClass(ConstitucionEmpresaId.class)
@Table(name = "borme_constitucion_empresa")
public class ConstitucionEmpresa {
    @Id
    @Column(length = 100)
    private String numeroAsiento;

    @Id
    @Column(name = "fecha_constitucion")
    private LocalDate fechaConstitucion;

    @Column(columnDefinition = "TEXT")
    private String nombreEmpresa;

    @Column(columnDefinition = "TEXT")
    private String objetoSocial;

    @Column(columnDefinition = "TEXT")
    private String domicilio;

    @Column(columnDefinition = "TEXT")
    private String capital;

    // Getters y Setters
    public String getNumeroAsiento() { return numeroAsiento; }
    public void setNumeroAsiento(String numeroAsiento) { this.numeroAsiento = numeroAsiento; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }

    public String getObjetoSocial() { return objetoSocial; }
    public void setObjetoSocial(String objetoSocial) { this.objetoSocial = objetoSocial; }

    public String getDomicilio() { return domicilio; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    public String getCapital() { return capital; }
    public void setCapital(String capital) { this.capital = capital; }

    public LocalDate getFechaConstitucion() { return fechaConstitucion; }
    public void setFechaConstitucion(LocalDate fechaConstitucion) { this.fechaConstitucion = fechaConstitucion; }

    @Override
    public String toString() {
        return String.format("Asiento %s: %s - %s", numeroAsiento, nombreEmpresa, objetoSocial);
    }
}