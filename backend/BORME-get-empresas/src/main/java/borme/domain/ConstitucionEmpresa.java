package borme.domain;

import javax.persistence.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    // Nuevas columnas - solo metadatos
    @Column(name = "fecha_pdf")
    private LocalDate fechaPDF;

    @Column(name = "nombre_archivo_pdf")
    private String nombreArchivoPDF;

    // Getters y Setters existentes
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

    public LocalDate getFechaPDF() { return fechaPDF; }
    public void setFechaPDF(LocalDate fechaPDF) { this.fechaPDF = fechaPDF; }

    public String getNombreArchivoPDF() { return nombreArchivoPDF; }
    public void setNombreArchivoPDF(String nombreArchivoPDF) { this.nombreArchivoPDF = nombreArchivoPDF; }

    public Path getRutaCompletaPDF(String directorioBase) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String rutaFecha = fechaPDF.format(formatter);

        return Paths.get(directorioBase)
                .resolve(rutaFecha)
                .resolve(nombreArchivoPDF)
                .normalize();
    }

    @Override
    public String toString() {
        return String.format("Asiento %s: %s - %s", numeroAsiento, nombreEmpresa, objetoSocial);
    }
}