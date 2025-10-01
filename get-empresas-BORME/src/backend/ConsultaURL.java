package backend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class ConsultaURL {

    // Constantes para códigos HTTP comunes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_ACCEPTED = 202;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_METHOD_NOT_ALLOWED = 405;
    public static final int HTTP_INTERNAL_ERROR = 500;
    public static final int HTTP_SERVICE_UNAVAILABLE = 503;

    // Constante para error de red/conexión (no es HTTP)
    public static final int NETWORK_ERROR = -1;

    private String url;
    private HttpClient cliente;
    private String contenido;
    private int codigoEstado;
    private String mensajeError;

    public ConsultaURL() {
        this.cliente = HttpClient.newHttpClient();
    }

    public ConsultaURL(String url) {
        this();
        this.url = url;
    }

    public boolean realizarConsulta() {
        return realizarConsulta(this.url);
    }

    public boolean realizarConsulta(String urlEspecifica) {
        if (urlEspecifica == null || urlEspecifica.trim().isEmpty()) {
            this.mensajeError = generarMensajeError(NETWORK_ERROR, "URL no puede ser nula o vacía");
            this.codigoEstado = NETWORK_ERROR;
            return false;
        }

        try {
            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(urlEspecifica))
                    .GET()
                    .build();

            HttpResponse<String> respuesta = cliente.send(solicitud, HttpResponse.BodyHandlers.ofString());

            this.codigoEstado = respuesta.statusCode();
            this.contenido = respuesta.body();

            if (this.codigoEstado == HTTP_OK) {
                this.mensajeError = null;
                return true;
            } else {
                this.mensajeError = generarMensajeError(this.codigoEstado, null);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            this.mensajeError = generarMensajeError(NETWORK_ERROR, e.getMessage());
            this.codigoEstado = NETWORK_ERROR;
            this.contenido = null;

            // InterruptedException: restaurar estado de interrupción
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
    }

    // Método genérico para construir mensajes de error
    private String generarMensajeError(int codigo, String mensajeAdicional) {
        String mensajeBase;

        switch (codigo) {
            case HTTP_OK:
                mensajeBase = "Éxito";
                break;
            case HTTP_BAD_REQUEST:
                mensajeBase = "Solicitud incorrecta";
                break;
            case HTTP_UNAUTHORIZED:
                mensajeBase = "No autorizado";
                break;
            case HTTP_FORBIDDEN:
                mensajeBase = "Acceso prohibido";
                break;
            case HTTP_NOT_FOUND:
                mensajeBase = "Recurso no encontrado";
                break;
            case HTTP_METHOD_NOT_ALLOWED:
                mensajeBase = "Método no permitido";
                break;
            case HTTP_INTERNAL_ERROR:
                mensajeBase = "Error interno del servidor";
                break;
            case HTTP_SERVICE_UNAVAILABLE:
                mensajeBase = "Servicio no disponible";
                break;
            case NETWORK_ERROR:
                mensajeBase = "Error de conexión";
                break;
            default:
                if (esExitoso()) {
                    mensajeBase = "Éxito (código " + codigo + ")";
                } else if (esErrorCliente()) {
                    mensajeBase = "Error del cliente (código " + codigo + ")";
                } else if (esErrorServidor()) {
                    mensajeBase = "Error del servidor (código " + codigo + ")";
                } else {
                    mensajeBase = "Error desconocido (código " + codigo + ")";
                }
        }

        if (mensajeAdicional != null && !mensajeAdicional.trim().isEmpty()) {
            return mensajeBase + ": " + mensajeAdicional;
        } else {
            return mensajeBase;
        }
    }

    // Getters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getContenido() { return contenido; }
    public int getCodigoEstado() { return codigoEstado; }
    public String getMensajeError() { return mensajeError; }

    // Métodos de utilidad PRIVADOS para uso interno
    private boolean esExitoso() {
        return codigoEstado >= 200 && codigoEstado < 300;
    }

    private boolean esErrorCliente() {
        return codigoEstado >= 400 && codigoEstado < 500;
    }

    private boolean esErrorServidor() {
        return codigoEstado >= 500 && codigoEstado < 600;
    }

    // Método público simplificado para verificar éxito
    public boolean fueExitosa() {
        return codigoEstado == HTTP_OK;
    }

    // Método público para obtener descripción del estado
    public String getDescripcionEstado() {
        return generarMensajeError(codigoEstado, null);
    }
}