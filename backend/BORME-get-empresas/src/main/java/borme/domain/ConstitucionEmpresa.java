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

    /**
     * Genera la URL del PDF a través del endpoint del API
     * Esto es consistente sin importar el entorno
     */
    public String getUrlPDF() {
        if (nombreArchivoPDF != null && fechaPDF != null) {
            return "/api/pdfs/" + fechaPDF + "/" + nombreArchivoPDF;
        }
        return null;
    }

    /**
     * URL completa si se necesita absoluta
     */
    public String getUrlPDF(String baseUrl) {
        String relativeUrl = getUrlPDF();
        return relativeUrl != null ? baseUrl + relativeUrl : null;
    }

    @Override
    public String toString() {
        return String.format("Asiento %s: %s - %s", numeroAsiento, nombreEmpresa, objetoSocial);
    }
}