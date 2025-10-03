package backend.http;

import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class URLFetcher {

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

    // Constante para error de red/conexión
    public static final int NETWORK_ERROR = -1;

    private String url;
    private HttpClient cliente;
    private String contenido;
    private int codigoEstado;
    private String mensajeError;

    public URLFetcher() {
        this.cliente = HttpClient.newHttpClient();
    }

    public URLFetcher(String url) {
        this();
        this.url = url;
    }

    public boolean realizarConsulta() {
        return realizarConsulta(this.url);
    }

    /**
     *
     * @param urlEspecifica
     * @return
     */
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

    /**
     * Descargar un archivo binario (PDF, etc.) y lo guarda en disco
     * @param url
     * @param filePath
     * @return
     */
    public boolean downloadBinaryFile(String url, String filePath) {
        try {
            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<byte[]> respuesta = HttpClient.newHttpClient().send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            if (respuesta.statusCode() == HTTP_OK) {
                Files.write(Path.of(filePath), respuesta.body(), StandardOpenOption.CREATE);
                return true;
            }
            return false;

        } catch (IOException | InterruptedException e) {
            System.err.println("Error descargando archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene contenido binario en memoria (para procesamiento directo)
     *
     * @param url
     */
    public byte[] fetchBinaryContent(String url) {
        try {
            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<byte[]> respuesta = HttpClient.newHttpClient().send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofByteArray()
            );

            return respuesta.statusCode() == HTTP_OK ? respuesta.body() : null;

        } catch (IOException | InterruptedException e) {
            System.err.println("Error obteniendo contenido binario: " + e.getMessage());
            return null;
        }
    }


    /**
     * Construccion generica de mensajes de error en función del código recibido
     * @param codigo
     * @param mensajeAdicional
     * */
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


    private boolean esExitoso() {
        return codigoEstado >= 200 && codigoEstado < 300;
    }

    private boolean esErrorCliente() {
        return codigoEstado >= 400 && codigoEstado < 500;
    }

    private boolean esErrorServidor() {
        return codigoEstado >= 500 && codigoEstado < 600;
    }

    public boolean fueExitosa() {
        return codigoEstado == HTTP_OK;
    }

    public String getDescripcionEstado() {
        return generarMensajeError(codigoEstado, null);
    }

}