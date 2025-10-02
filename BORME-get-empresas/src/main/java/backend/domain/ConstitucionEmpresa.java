package backend.domain;

public class ConstitucionEmpresa {
    private String numeroAsiento;
    private String nombreEmpresa;
    private String objetoSocial;
    private String domicilio;
    private String capital;
    private String fechaConstitucion;

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

    public String getFechaConstitucion() { return fechaConstitucion; }
    public void setFechaConstitucion(String fechaConstitucion) { this.fechaConstitucion = fechaConstitucion; }

    @Override
    public String toString() {
        return String.format("Asiento %s: %s - %s", numeroAsiento, nombreEmpresa, objetoSocial);
    }
}