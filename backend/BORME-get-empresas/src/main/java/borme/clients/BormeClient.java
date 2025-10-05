package borme.clients;

import borme.http.URLFetcher;
import borme.domain.BormeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BormeClient {

    // URL del BORME preparada para ser accedida
    private static final String BASE_URL = "https://www.boe.es/borme/dias/";

    // Formato de fecha que, junto a la URL del BORME, permitirá acceder a la pagina web del día indicado
    private static final DateTimeFormatter URL_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    // Formato de fecha que va a aceptar la aplicación  según la especificacion
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final URLFetcher urlFetcher;

    @Autowired
    public BormeClient(URLFetcher urlFetcher) {
        this.urlFetcher = urlFetcher;
    }

    public BormeResponse consultarPorFecha(String fecha) {
        try {
            LocalDate fechaDate = LocalDate.parse(fecha, INPUT_FORMATTER);
            String url = construirUrl(fechaDate);

            boolean exito = urlFetcher.realizarConsulta(url);

            return new BormeResponse(
                    exito,
                    fecha,
                    url,
                    urlFetcher.getContenido(),
                    urlFetcher.getCodigoEstado(),
                    urlFetcher.getMensajeError()
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
