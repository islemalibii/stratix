package services;

import models.Service;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFService {

    public static void exporterServices(List<Service> services, String cheminFichier) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Rapport des Services");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(50, 730);
        contentStream.showText("Généré le : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        contentStream.endText();

        int y = 700;
        contentStream.setLineWidth(0.5f);
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);

        contentStream.beginText();
        contentStream.newLineAtOffset(50, y);
        contentStream.showText("Titre");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(200, y);
        contentStream.showText("Catégorie");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(320, y);
        contentStream.showText("Budget");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(400, y);
        contentStream.showText("Début");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(480, y);
        contentStream.showText("Fin");
        contentStream.endText();

        contentStream.moveTo(50, y - 5);
        contentStream.lineTo(550, y - 5);
        contentStream.stroke();

        y -= 20;
        contentStream.setFont(PDType1Font.HELVETICA, 10);

        for (Service s : services) {
            if (y < 50) {
                contentStream.close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                y = 750;
            }

            String titre = s.getTitre().length() > 20 ? s.getTitre().substring(0, 17) + "..." : s.getTitre();
            String categorie = s.getCategorie() != null ? s.getCategorie().getNom() : "N/A";
            String budget = String.format("%,.0f DT", s.getBudget());
            String debut = s.getDateDebut();
            String fin = s.getDateFin();

            contentStream.beginText();
            contentStream.newLineAtOffset(50, y);
            contentStream.showText(titre);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(200, y);
            contentStream.showText(categorie);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(320, y);
            contentStream.showText(budget);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(400, y);
            contentStream.showText(debut);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(480, y);
            contentStream.showText(fin);
            contentStream.endText();

            y -= 20;
        }

        contentStream.close();
        document.save(new File(cheminFichier));
        document.close();
    }
}