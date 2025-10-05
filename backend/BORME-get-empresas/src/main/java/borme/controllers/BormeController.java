package borme.controllers;

import borme.services.BormeService;
import borme.domain.BormeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/borme")
@CrossOrigin(origins = "*") // Para el frontend Svelte
public class BormeController {

    @Autowired
    private BormeService bormeService;

    /**
     * Consulta el BORME para una fecha específica
     * GET /api/borme/2024-09-10
     */
    @GetMapping("/{fecha}")
    public ResponseEntity<Map<String, Object>> consultarBorme(@PathVariable String fecha) {
        Map<String, Object> response = new HashMap<>();

        // Validar fecha
        if (!bormeService.validarFecha(fecha)) {
            response.put("error", "Fecha no válida");
            response.put("fecha", fecha);
            return ResponseEntity.badRequest().body(response);
        }

        // Realizar consulta
        BormeResponse respuesta = bormeService.consultarBorme(fecha);

        response.put("fecha", fecha);
        response.put("exito", respuesta.isExito());
        response.put("url", respuesta.getUrlConsultada());
        response.put("codigoEstado", respuesta.getCodigoEstado());

        if (respuesta.isExito()) {
            response.put("contenido", respuesta.getContenidoHtml());
            response.put("mensaje", "Consulta realizada correctamente");
        } else {
            response.put("error", respuesta.getMensajeError());
            response.put("contenido", null);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Consulta el BORME para la fecha de hoy
     * GET /api/borme/hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<Map<String, Object>> consultarBormeHoy() {
        BormeResponse respuesta = bormeService.consultarBormeHoy();

        Map<String, Object> response = new HashMap<>();
        response.put("fecha", respuesta.getFechaConsulta());
        response.put("exito", respuesta.isExito());
        response.put("url", respuesta.getUrlConsultada());

        if (respuesta.isExito()) {
            response.put("contenido", respuesta.getContenidoHtml());
        } else {
            response.put("error", respuesta.getMensajeError());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Valida una fecha para consultar el BORME
     * GET /api/borme/validar/2024-09-10
     */
    @GetMapping("/validar/{fecha}")
    public ResponseEntity<Map<String, Object>> validarFecha(@PathVariable String fecha) {
        boolean esValida = bormeService.validarFecha(fecha);

        Map<String, Object> response = new HashMap<>();
        response.put("fecha", fecha);
        response.put("valida", esValida);

        if (!esValida) {
            response.put("mensaje", "La fecha debe estar entre 1990-01-01 y hoy, en formato YYYY-MM-DD");
        }

        return ResponseEntity.ok(response);
    }
}