package borme.parsers;

import borme.domain.ConstitucionEmpresa;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;

@Component
public class BormePdfParser {

    /**
     * Extrae texto de un PDF
     * @param rutaPdf ruta al PDF a parsear
     */
    public String extraerTextoPdf(String rutaPdf) {
        try (PDDocument document = PDDocument.load(new File(rutaPdf))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            System.err.println("Error leyendo PDF: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parsea constituciones de empresas del texto del PDF
     * @param textoPdf texto del PDF a parsear
     */
    public List<ConstitucionEmpresa> parsearConstituciones(String textoPdf, String nombrePDF, String fechaPDF) {
        List<ConstitucionEmpresa> constituciones = new ArrayList<>();

        if (textoPdf == null || textoPdf.trim().isEmpty()) {
            return constituciones;
        }

        textoPdf = preprocesarTextoBORME(textoPdf);

        // Dividir en bloques por empresa (cada bloque empieza con "NNNNNN - ")
        Pattern bloquePattern = Pattern.compile("(\\d{6}\\s*-\\s*[^\\n]+(?:\\n(?!\\d{6}\\s*-)[^\\n]*)*)", Pattern.MULTILINE);
        Matcher bloqueMatcher = bloquePattern.matcher(textoPdf);

        while (bloqueMatcher.find()) {
            String bloque = bloqueMatcher.group(1);

            // Verificar que "Constitución." aparezca justo después del nombre de la empresa
            Pattern patronConstitucionReal = Pattern.compile(
                    "^\\d{6}\\s*-\\s*[^\\n]*\\n\\s*Constitución\\.",
                    Pattern.MULTILINE
            );

            // Verificar si es una constitución
            if (patronConstitucionReal.matcher(bloque).find()) {
                try {
                    ConstitucionEmpresa constitucion = parsearBloqueConstitucion(bloque, nombrePDF, fechaPDF);
                    if (constitucion != null && constitucion.getNumeroAsiento() != null) {
                        constituciones.add(constitucion);
                        System.out.println("Encontrada constitución: " + constitucion.getNombreEmpresa());
                    }
                } catch (Exception e) {
                    System.err.println("Error parseando bloque: " + e.getMessage());
                }
            }
        }

        System.out.println("Total constituciones parseadas: " + constituciones.size());
        return constituciones;
    }

    /**
     * Parsea la sección concreta dedicada a la parte de constitución
     * @param bloque bloque de una empresa con los datos de la constitución
     *
     */

    private ConstitucionEmpresa parsearBloqueConstitucion(String bloque, String nombrePDF, String fechaPDF) {
        ConstitucionEmpresa constitucion = new ConstitucionEmpresa();

        // Extraer número y nombre (primera línea)
        Pattern nombrePattern = Pattern.compile("(\\d{6})\\s*-\\s*([^\\n]+)");
        Matcher nombreMatcher = nombrePattern.matcher(bloque);
        if (nombreMatcher.find()) {
            constitucion.setNumeroAsiento(nombreMatcher.group(1).trim());
            constitucion.setNombreEmpresa(nombreMatcher.group(2).trim().replaceAll("\\.$", ""));
        }

        // Extraer fecha de constitución - CONVERSIÓN A LocalDate
        Pattern fechaPattern = Pattern.compile(
                "Comienzo\\s+de\\s+operaciones\\s*[:：；]\\s*(\\d{1,2}[./]\\d{1,2}[./]\\d{2,4})",
                Pattern.CASE_INSENSITIVE);
        Matcher fechaMatcher = fechaPattern.matcher(bloque);
        if (fechaMatcher.find()) {
            LocalDate fecha = normalizarFecha(fechaMatcher.group(1).trim());
            if (fecha != null) {
                constitucion.setFechaConstitucion(fecha);
            } else {
                // Si no podemos parsear la fecha, no podemos crear la constitución
                System.err.println("No se pudo parsear fecha para asiento: " + constitucion.getNumeroAsiento());
                return null;
            }
        } else {
            // Si no encontramos fecha en el texto, no podemos crear la constitución
            System.err.println("No se encontró fecha de constitución para asiento: " + constitucion.getNumeroAsiento());
            return null;
        }

        // Extraer objeto social (desde "Objeto social:" hasta "Domicilio:")
        Pattern objetoSocialPattern = Pattern.compile("Objeto social:\\s*([\\s\\S]*?)(?=\\s*Domicilio:)");
        Matcher objetoSocialMatcher = objetoSocialPattern.matcher(bloque);
        if (objetoSocialMatcher.find()) {
            constitucion.setObjetoSocial(objetoSocialMatcher.group(1).trim());
        }

        // Extraer domicilio (desde "Domicilio:" hasta "Capital:")
        Pattern domicilioPattern = Pattern.compile("Domicilio:\\s*([\\s\\S]*?)(?=\\s*Capital:)");
        Matcher domicilioMatcher = domicilioPattern.matcher(bloque);
        if (domicilioMatcher.find()) {
            constitucion.setDomicilio(domicilioMatcher.group(1).trim());
        }

        // Extraer capital
        Pattern capitalPattern = Pattern.compile("Capital:\\s*([\\d\\.,]+)");
        Matcher capitalMatcher = capitalPattern.matcher(bloque);
        if (capitalMatcher.find()) {
            constitucion.setCapital(capitalMatcher.group(1).trim() + " Euros");
        }

        constitucion.setNombreArchivoPDF(nombrePDF);

        constitucion.setFechaPDF(normalizarFecha(fechaPDF));

        return constitucion;
    }

    /**
     * Preprocesado del texto del PDF para eliminar elementos innecesarios que puedan afectar al análisis
     * @param texto texto del PDF que queremos limpiar
     *
     */
    private String preprocesarTextoBORME(String texto) {
        if (texto == null) return "";

        // Normalizar saltos de línea, pero no eliminarlos
        texto = texto.replaceAll("\\r\\n", "\n")
                .replaceAll("\\r", "\n")
                .replaceAll("[ \\t]+", " "); // solo espacios horizontales

        // Eliminar cabeceras completas del Boletín Oficial
        texto = texto.replaceAll(
                "(?i)BOLETÍN\\s+OFICIAL\\s+DEL\\s+REGISTRO\\s+MERCANTIL\\s+Núm\\.\\s*\\d+\\s+" +
                        "(?:Lunes|Martes|Miércoles|Jueves|Viernes|Sábado|Domingo)\\s+\\d+\\s+de\\s+" +
                        "(?:enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\\s+de\\s+\\d{4}\\s+" +
                        "Pág\\.\\s*\\d+",
                ""
        );

        // Eliminar versiones abreviadas de la cabecera (solo fecha y número)
        texto = texto.replaceAll(
                "(?i)Núm\\.\\s*\\d+\\s+" +
                        "(?:Lunes|Martes|Miércoles|Jueves|Viernes|Sábado|Domingo)\\s+\\d+\\s+de\\s+" +
                        "(?:enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\\s+de\\s+\\d{4}\\s+Pág\\.\\s*\\d+",
                ""
        );

        // Eliminar URLs del BOE (ahora innecesario usar \s* entre letras, porque ya normalizamos)
        texto = texto.replaceAll("(?i)https?://www\\.boe\\.es", "");

        // Eliminar bloques con metadatos (DL, ISSN, etc.)
        texto = texto.replaceAll(
                "(?i)BOLETÍN\\s+OFICIAL\\s+DEL\\s+REGISTRO\\s+MERCANTIL\\s+" +
                        "D\\.\\s*L\\.\\s*:\\s*M-\\d{4}/\\d{4}-\\s*ISSN\\s*:\\s*\\d{4}-\\d{4}",
                ""
        );

        // Eliminar referencias CVE completas (incluyendo "Verificable en...")
        texto = texto.replaceAll(
                "(?i)cve\\s*:\\s*BORME-A-\\d{4}-\\d{3}-\\d{2}\\s+Verificable\\s+en\\s+https?://www\\.boe\\.es",
                ""
        );

        // Eliminar CVE si aparece sola (sin URL)
        texto = texto.replaceAll("(?i)cve\\s*:\\s*BORME-A-\\d{4}-\\d{3}-\\d{2}", "");

        // Limpieza del bloque final del PDF
        texto = texto.replaceAll(
                "(?i)https?://www\\.boe\\.es\\s+BOLETÍN\\s+OFICIAL\\s+DEL\\s+REGISTRO\\s+MERCANTIL\\s+D\\.\\s*L\\.\\s*:\\s*M-\\d{4}/\\d{4}\\s*-\\s*ISSN\\s*:\\s*\\d{4}-\\d{4}",
                ""
        );

        // Normalizar saltos de línea preservando estructura
        texto = texto.replaceAll("\\r\\n", "\n");
        texto = texto.replaceAll("\\r", "\n");
        // Mantener un salto de línea después de puntos finales de secciones importantes
        texto = texto.replaceAll("(\\.|\\))\\s+(Constitución|Ceses|Nombramientos|Datos registrales)", "$1\n$2");
        // Eliminar solo saltos de línea excesivos
        texto = texto.replaceAll("\\n{3,}", "\n\n");

        // Eliminar líneas vacías
        texto = texto.replaceAll("(?m)^\\s*$", "");

        // Mantener espacios normales pero eliminar redundantes
        texto = texto.replaceAll("[ \\t]+", " ");

        return texto.trim();
    }


    /**
     * Normaliza la fecha de constitución al formato yyyy-MM-dd
     * @param fechaRaw fecha en formato empleado por el PDF
     */
    private LocalDate normalizarFecha(String fechaRaw) {
        if (fechaRaw == null || fechaRaw.trim().isEmpty()) {
            return null;
        }

        // Limpiar la fecha de espacios y caracteres extraños
        String fecha = fechaRaw.trim().replaceAll("[^\\d./-]", "");

        try {
            // Formato ISO (YYYY-MM-DD)
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return LocalDate.parse(fecha);
            }

            // Intentar detectar el formato
            if (fecha.matches("\\d{1,2}[./-]\\d{1,2}[./-]\\d{2}")) {
                // Formato dd.mm.aa
                String[] partes = fecha.split("[./-]");
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int anho = Integer.parseInt(partes[2]);

                // Asumir que años < 50 son del siglo XXI, > 50 del siglo XX
                anho += (anho < 50) ? 2000 : (anho < 100) ? 1900 : 0;

                return LocalDate.of(anho, mes, dia);
            } else if (fecha.matches("\\d{1,2}[./-]\\d{1,2}[./-]\\d{4}")) {
                // Formato dd.mm.aaaa
                String[] partes = fecha.split("[./-]");
                int dia = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int anho = Integer.parseInt(partes[2]);
                return LocalDate.of(anho, mes, dia);
            }

            // Si no coincide con ningún formato, devolver null
            return null;

        } catch (Exception e) {
            System.err.println("Error parseando fecha: " + fechaRaw + " - " + e.getMessage());
            return null;
        }
    }


}