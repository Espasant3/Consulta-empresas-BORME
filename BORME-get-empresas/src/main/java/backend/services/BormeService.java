package backend.services;

import backend.clients.BormeClient;
import backend.domain.BormeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class BormeService {

    private final BormeClient bormeClient;

    @Autowired
    public BormeService(BormeClient bormeClient) {
        this.bormeClient = bormeClient;
    }

    public BormeResponse consultarBorme(String fecha) {
        return bormeClient.consultarPorFecha(fecha);
    }

    public BormeResponse consultarBormeHoy() {
        return bormeClient.consultarPorFecha(LocalDate.now());
    }

    public boolean validarFecha(String fecha) {
        try {
            LocalDate fechaDate = LocalDate.parse(fecha);
            LocalDate hoy = LocalDate.now();

            if (fechaDate.isAfter(hoy)) {
                return false;
            }

            LocalDate fechaMinima = LocalDate.of(1990, 1, 1);
            return !fechaDate.isBefore(fechaMinima);

        } catch (Exception e) {
            return false;
        }
    }
}