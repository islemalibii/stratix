package service.export;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import models.produit;
import models.ressource;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportPDFService {

    // ==================== MÉTHODES POUR LES PRODUITS ====================

    public static void exporterProduitsVersPDF(List<produit> produits, String cheminFichier) throws IOException {
        PdfWriter writer = new PdfWriter(cheminFichier);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Titre
        Paragraph title = new Paragraph("Catalogue des Produits")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Date de génération
        String dateGen = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        document.add(new Paragraph("Généré le : " + dateGen)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(new Paragraph("\n"));

        // Statistiques
        document.add(new Paragraph("Résumé :")
                .setFontSize(14)
                .setBold());
        document.add(new Paragraph("Total produits : " + produits.size()));

        double valeurTotale = produits.stream()
                .mapToDouble(p -> p.getPrix() * p.getStock_actuel())
                .sum();
        document.add(new Paragraph("Valeur totale du stock : " + String.format("%.2f DT", valeurTotale)));

        long produitsPerimes = produits.stream()
                .filter(produit::estPerime)
                .count();
        document.add(new Paragraph("Produits périmés : " + produitsPerimes));

        document.add(new Paragraph("\n"));

        // Tableau des produits
        float[] columnWidths = {1, 3, 2, 1, 1, 1, 1, 1};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));

        // En-têtes
        String[] headers = {"ID", "Nom", "Catégorie", "Prix", "Stock", "Min", "Péremption", "Statut"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }

        // Données
        for (produit p : produits) {
            table.addCell(String.valueOf(p.getId()));
            table.addCell(p.getNom() != null ? p.getNom() : "-");
            table.addCell(p.getCategorie() != null ? p.getCategorie() : "-");
            table.addCell(String.format("%.2f DT", p.getPrix()));
            table.addCell(String.valueOf(p.getStock_actuel()));
            table.addCell(String.valueOf(p.getStock_min()));
            table.addCell(p.getDate_peremption() != null ? p.getDate_peremption() : "-");

            String statut = p.estPerime() ? "PÉRIMÉ" :
                    (p.estBientotPerime(30) ? "Bientôt périmé" : "OK");
            table.addCell(statut);
        }

        document.add(table);
        document.close();
    }

    // ==================== MÉTHODES POUR LES RESSOURCES ====================

    public static void exporterRessourcesVersPDF(List<ressource> ressources, String cheminFichier) throws IOException {
        PdfWriter writer = new PdfWriter(cheminFichier);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Titre
        Paragraph title = new Paragraph("Inventaire des Ressources")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Date de génération
        String dateGen = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        document.add(new Paragraph("Généré le : " + dateGen)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(new Paragraph("\n"));

        // Statistiques
        document.add(new Paragraph("Résumé :")
                .setFontSize(14)
                .setBold());
        document.add(new Paragraph("Total ressources : " + ressources.size()));

        int quantiteTotale = ressources.stream()
                .mapToInt(ressource::getQuatite)
                .sum();
        document.add(new Paragraph("Quantité totale : " + quantiteTotale));

        long typesUniques = ressources.stream()
                .map(ressource::getType_ressource)
                .distinct()
                .count();
        document.add(new Paragraph("Types de ressources : " + typesUniques));

        document.add(new Paragraph("\n"));

        // Tableau des ressources
        float[] columnWidths = {1, 3, 2, 1, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));

        // En-têtes
        String[] headers = {"ID", "Nom", "Type", "Quantité", "Fournisseur"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }

        // Données
        for (ressource r : ressources) {
            table.addCell(String.valueOf(r.getid()));
            table.addCell(r.getNom() != null ? r.getNom() : "-");
            table.addCell(r.getType_ressource() != null ? r.getType_ressource() : "-");
            table.addCell(String.valueOf(r.getQuatite()));
            table.addCell(r.getFournisseur() != null ? r.getFournisseur() : "-");
        }

        document.add(table);
        document.close();
    }

    // ==================== MÉTHODE AVEC STATISTIQUES DÉTAILLÉES ====================

    public static void exporterRessourcesAvecDetailsPDF(List<ressource> ressources, String cheminFichier) throws IOException {
        PdfWriter writer = new PdfWriter(cheminFichier);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Titre
        Paragraph title = new Paragraph("Rapport détaillé des Ressources")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20)
                .setBold();
        document.add(title);

        // Date
        String dateGen = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        document.add(new Paragraph("Généré le : " + dateGen)
                .setTextAlignment(TextAlignment.RIGHT));

        document.add(new Paragraph("\n"));

        // Statistiques globales
        document.add(new Paragraph("STATISTIQUES GLOBALES")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        document.add(new Paragraph("Total des ressources : " + ressources.size()));

        int quantiteTotale = ressources.stream()
                .mapToInt(ressource::getQuatite)
                .sum();
        document.add(new Paragraph("Quantité totale en stock : " + quantiteTotale));

        // Statistiques par type
        document.add(new Paragraph("\nRÉPARTITION PAR TYPE :")
                .setFontSize(14)
                .setBold());

        java.util.Map<String, Integer> quantiteParType = new java.util.HashMap<>();
        java.util.Map<String, Integer> countParType = new java.util.HashMap<>();

        for (ressource r : ressources) {
            String type = r.getType_ressource() != null ? r.getType_ressource() : "Non spécifié";
            quantiteParType.put(type, quantiteParType.getOrDefault(type, 0) + r.getQuatite());
            countParType.put(type, countParType.getOrDefault(type, 0) + 1);
        }

        for (String type : quantiteParType.keySet()) {
            document.add(new Paragraph("• " + type + " : " + countParType.get(type) + " ressources, "
                    + quantiteParType.get(type) + " unités"));
        }

        document.add(new Paragraph("\n"));

        // Tableau détaillé
        document.add(new Paragraph("LISTE DÉTAILLÉE DES RESSOURCES")
                .setFontSize(14)
                .setBold());

        float[] columnWidths = {1, 3, 2, 1, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));

        String[] headers = {"ID", "Nom", "Type", "Quantité", "Fournisseur"};
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }

        for (ressource r : ressources) {
            table.addCell(String.valueOf(r.getid()));
            table.addCell(r.getNom() != null ? r.getNom() : "-");
            table.addCell(r.getType_ressource() != null ? r.getType_ressource() : "-");

            Cell quantiteCell = new Cell().add(new Paragraph(String.valueOf(r.getQuatite())));
            if (r.getQuatite() <= 5) {
                quantiteCell.setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.RED);
                quantiteCell.setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE);
            }
            table.addCell(quantiteCell);

            table.addCell(r.getFournisseur() != null ? r.getFournisseur() : "-");
        }

        document.add(table);
        document.close();
    }
}