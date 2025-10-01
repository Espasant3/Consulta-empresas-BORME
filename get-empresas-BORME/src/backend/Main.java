package backend;


public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }

        // Crear instancia de ConsultaBoe
        ConsultaURL consulta = new ConsultaURL();

        // Consulta 1
        String url1 = "https://www.boe.es/borme/dias/2024/09/10/";
        if (consulta.realizarConsulta(url1)) {
            System.out.println("Consulta exitosa!");
            System.out.println("Contenido: " + consulta.getContenido());
            System.out.println("Contenido: " + consulta.getContenido().substring(0, 100) + "...");
        } else {
            System.out.println("Error: " + consulta.getMensajeError());
        }

        // Consulta 2 con setter
        consulta.setUrl("https://www.boe.es/borme/dias/2024/09/11/");

        if (consulta.realizarConsulta()) {
            System.out.println("Éxito: " + consulta.getContenido().substring(0, 100));
        } else {
            // El mensaje de error ya está construido de forma genérica
            System.err.println("Error: " + consulta.getMensajeError());
            System.err.println("Descripción: " + consulta.getDescripcionEstado());
        }
    }
}