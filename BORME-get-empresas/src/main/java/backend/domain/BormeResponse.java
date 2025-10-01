package backend.domain;

public class BormeResponse {
    private final boolean exito;
    private final String fechaConsulta;
    private final String urlConsultada;
    private final String contenidoHtml;
    private final int codigoEstado;
    private final String mensajeError;

    public BormeResponse(boolean exito, String fechaConsulta, String urlConsultada,
                         String contenidoHtml, int codigoEstado, String mensajeError) {
        this.exito = exito;
        this.fechaConsulta = fechaConsulta;
        this.urlConsultada = urlConsultada;
        this.contenidoHtml = contenidoHtml;
        this.codigoEstado = codigoEstado;
        this.mensajeError = mensajeError;
    }

    // Getters
    public boolean isExito() { return exito; }
    public String getFechaConsulta() { return fechaConsulta; }
    public String getUrlConsultada() { return urlConsultada; }
    public String getContenidoHtml() { return contenidoHtml; }
    public int getCodigoEstado() { return codigoEstado; }
    public String getMensajeError() { return mensajeError; }

    @Override
    public String toString() {
        return String.format("BormeResponse{fecha=%s, exito=%s, estado=%d, error=%s}",
                fechaConsulta, exito, codigoEstado, mensajeError);
    }
}