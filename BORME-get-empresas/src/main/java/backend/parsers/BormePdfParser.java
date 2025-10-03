package backend.parsers;

import backend.domain.ConstitucionEmpresa;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public List<ConstitucionEmpresa> parsearConstituciones(String textoPdf) {
        List<ConstitucionEmpresa> constituciones = new ArrayList<>();

        if (textoPdf == null || textoPdf.trim().isEmpty()) {
            return constituciones;
        }

        textoPdf = preprocesarTextoBORME(textoPdf);

        // Primero: dividir en bloques por empresa (cada bloque empieza con "NNNNNN - ")
        Pattern bloquePattern = Pattern.compile("(\\d{6}\\s*-\\s*[^\\n]+(?:\\n(?!\\d{6}\\s*-)[^\\n]*)*)", Pattern.MULTILINE);
        Matcher bloqueMatcher = bloquePattern.matcher(textoPdf);

        while (bloqueMatcher.find()) {
            String bloque = bloqueMatcher.group(1);

            // Verificar si es una constitución
            if (bloque.contains("Constitución.")) {
                try {
                    ConstitucionEmpresa constitucion = parsearBloqueConstitucion(bloque);
                    if (constitucion != null) {
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

    private ConstitucionEmpresa parsearBloqueConstitucion(String bloque) {
        ConstitucionEmpresa constitucion = new ConstitucionEmpresa();

        // Extraer número y nombre (primera línea)
        Pattern nombrePattern = Pattern.compile("(\\d{6})\\s*-\\s*([^\\n]+)");
        Matcher nombreMatcher = nombrePattern.matcher(bloque);
        if (nombreMatcher.find()) {
            constitucion.setNumeroAsiento(nombreMatcher.group(1).trim());
            constitucion.setNombreEmpresa(nombreMatcher.group(2).trim().replaceAll("\\.$", ""));
        }

        // Extraer fecha de constitución
        Pattern fechaPattern = Pattern.compile(
                "Comienzo\\s+de\\s+operaciones\\s*[:：；]\\s*(\\d{1,2}[./]\\d{1,2}[./]\\d{2,4})",
                Pattern.CASE_INSENSITIVE);
        Matcher fechaMatcher = fechaPattern.matcher(bloque);
        if (fechaMatcher.find()) {
            constitucion.setFechaConstitucion(normalizarFecha(fechaMatcher.group(1).trim()));
            //constitucion.setFechaConstitucion(fechaMatcher.group(1).trim());
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

        return constitucion;
    }

    /**
     * Preprocesado del texto del PDF para eliminar elementos innecesarios que puedan afectar al análisis
     * @param texto texto del PDF que queremos limpiar
     *
     */

    private String preprocesarTextoBORME(String texto) {
        if (texto == null) return "";

        // Identificar y eliminar elementos no deseados de las cabeceras
        texto = texto.replaceAll("(?s)(BOLETÍN\\s*OFICIAL\\s*DEL\\s*REGISTRO\\s*MERCANTIL\\s*Núm\\.\\s*\\d+\\s+(?:Lunes|Martes|Miércoles|Jueves|Viernes|Sábado|Domingo)\\s+\\d+\\s+de\\s+(?:enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\\s+de\\s+\\d{4}\\s+Pág\\.\\s+\\d+|Núm\\.\\s*\\d+\\s+(?:Lunes|Martes|Miércoles|Jueves|Viernes|Sábado|Domingo)\\s*\\d+\\s*de\\s*(?:enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\\s*de\\s*\\d{4}\\s*Pág\\.\\s*\\d+)", "");

        // Eliminar elementos web (verticales) con máxima flexibilidad de espacios
        texto = texto.replaceAll("(?s)(cve\\s*:\\s*B\\s*O\\s*R\\s*M\\s*E\\s*-\\s*A\\s*-\\s*\\d{4}\\s*-\\s*\\d{3}\\s*-\\s*\\d{2}\\s*Ver\\s*if*ic\\s*abl\\s*e\\s*e\\s*n\\s*h\\s*t\\s*t\\s*p\\s*s?\\s*:\\/\\/\\s*w\\s*w\\s*w\\s*\\.\\s*b\\s*o\\s*e\\s*\\.\\s*e\\s*s|https?\\s*:\\/\\/\\s*w\\s*w\\s*w\\s*\\.\\s*b\\s*o\\s*e\\s*\\.\\s*e\\s*s\\s+BOLETÍN\\s+OFICIAL\\s+DEL\\s+REGISTRO\\s+MERCANTIL\\s+D\\.\\s*L\\.\\s*:\\s*M-\\d{4}/\\d{4}-\\s*ISSN\\s*:\\s*\\d{4}-\\d{4})", "");

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
    private String normalizarFecha(String fechaRaw) {
        // Limpiar la fecha de espacios y caracteres extraños
        String fecha = fechaRaw.trim().replaceAll("[^\\d./-]", "");

        // Intentar detectar el formato
        if (fecha.matches("\\d{1,2}[./-]\\d{1,2}[./-]\\d{2}")) {
            // Formato dd.mm.aa
            String[] partes = fecha.split("[./-]");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int anio = Integer.parseInt(partes[2]);

            // Asumir que años < 50 son del siglo XXI, > 50 del siglo XX
            anio += (anio < 50) ? 2000 : (anio < 100) ? 1900 : 0;

            return String.format("%04d-%02d-%02d", anio, mes, dia);
        } else if (fecha.matches("\\d{1,2}[./-]\\d{1,2}[./-]\\d{4}")) {
            // Formato dd.mm.aaaa
            String[] partes = fecha.split("[./-]");
            return String.format("%04d-%02d-%02d",
                    Integer.parseInt(partes[2]),
                    Integer.parseInt(partes[1]),
                    Integer.parseInt(partes[0]));
        }

        // Si no se puede normalizar, devolver como está
        return fecha;
    }
}