package backend.parsers;

import backend.domain.ConstitucionEmpresa;
import org.springframework.stereotype.Component;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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
     */
    public List<ConstitucionEmpresa> parsearConstituciones(String textoPdf) {
        List<ConstitucionEmpresa> constituciones = new ArrayList<>();

        // Patrón para detectar constituciones (ajusta según el formato real del BORME)
        Pattern pattern = Pattern.compile(
                "(\\d+)\\s*-\\s*([^\\n]+)\\nConstitución[^\\n]*\\n([^\\n]*)\\nDomicilio:[^\\n]*\\nCapital:[^\\n]*",
                Pattern.MULTILINE
        );

        Matcher matcher = pattern.matcher(textoPdf);
        while (matcher.find()) {
            ConstitucionEmpresa constitucion = new ConstitucionEmpresa();
            constitucion.setNumeroAsiento(matcher.group(1));
            constitucion.setNombreEmpresa(matcher.group(2).trim());
            constitucion.setObjetoSocial(matcher.group(3).trim());
            constituciones.add(constitucion);
        }

        return constituciones;
    }
}