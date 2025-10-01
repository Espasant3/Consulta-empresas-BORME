package backend.clients;

import backend.http.URLFetcher;
import backend.domain.BormeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BormeClient {
    private static final String BASE_URL = "https://www.boe.es/borme/dias/";
    private static final DateTimeFormatter URL_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final URLFetcher URLFetcher;

    @Autowired
    public BormeClient(URLFetcher URLFetcher) {
        this.URLFetcher = URLFetcher;
    }

    public BormeResponse consultarPorFecha(String fecha) {
        try {
            LocalDate fechaDate = LocalDate.parse(fecha, INPUT_FORMATTER);
            String url = construirUrl(fechaDate);

            boolean exito = URLFetcher.realizarConsulta(url);

            return new BormeResponse(
                    exito,
                    fecha,
                    url,
                    URLFetcher.getContenido(),
                    URLFetcher.getCodigoEstado(),
                    URLFetcher.getMensajeError()
            );

        } catch (Exception e) {
            return new BormeResponse(
                    false,
                    fecha,
                    null,
                    null,
                    URLFetcher.NETWORK_ERROR,
                    "Error procesando fecha: " + e.getMessage()
            );
        }
    }

    public BormeResponse consultarPorFecha(LocalDate fecha) {
        String fechaStr = fecha.format(INPUT_FORMATTER);
        return consultarPorFecha(fechaStr);
    }

    private String construirUrl(LocalDate fecha) {
        String fechaUrl = fecha.format(URL_FORMATTER);
        return BASE_URL + fechaUrl + "/";
    }
}