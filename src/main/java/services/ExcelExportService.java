package Services;

import models.Tache;
import models.Employe;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelExportService {

    private static final EmployeeService employeService = new EmployeeService();

    /**
     * Exporte les tâches vers un fichier Excel
     * @param taches Liste des tâches à exporter
     * @param filePath Chemin du fichier de destination
     */
    public static void exportTachesToExcel(List<Tache> taches, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tâches");

            // Style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Style pour les cellules
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);

            // Style pour les dates
            CellStyle dateStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/mm/yyyy"));

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Titre", "Description", "Employé", "Deadline", "Priorité", "Statut"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplir les données
            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Tache t : taches) {
                Row row = sheet.createRow(rowNum++);

                // ID
                row.createCell(0).setCellValue(t.getId());

                // Titre
                row.createCell(1).setCellValue(t.getTitre());

                // Description
                row.createCell(2).setCellValue(t.getDescription() != null ? t.getDescription() : "");

                // Employé (nom au lieu de l'ID)
                Employe emp = employeService.getEmployeById(t.getEmployeId());
                String employeName = (emp != null) ? emp.getUsername() : "Employé " + t.getEmployeId();
                row.createCell(3).setCellValue(employeName);

                // Deadline (formatée)
                if (t.getDeadline() != null) {
                    String deadlineStr = t.getDeadline().toLocalDate().format(dateFormatter);
                    row.createCell(4).setCellValue(deadlineStr);
                } else {
                    row.createCell(4).setCellValue("Non définie");
                }

                // Priorité
                row.createCell(5).setCellValue(t.getPriorite());

                // Statut
                row.createCell(6).setCellValue(t.getStatut());
            }

            // Ajuster la largeur des colonnes automatiquement
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écrire dans le fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("✅ Excel exporté avec succès: " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Erreur lors de l'export Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exporte les plannings vers un fichier Excel
     * @param plannings Liste des plannings à exporter
     * @param filePath Chemin du fichier de destination
     */
    public static void exportPlanningsToExcel(List<models.Planning> plannings, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Plannings");

            // Style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Créer l'en-tête
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Employé", "Date", "Heure Début", "Heure Fin", "Type Shift"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Remplir les données
            int rowNum = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (models.Planning p : plannings) {
                Row row = sheet.createRow(rowNum++);

                // ID
                row.createCell(0).setCellValue(p.getId());

                // Employé
                Employe emp = employeService.getEmployeById(p.getEmployeId());
                String employeName = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();
                row.createCell(1).setCellValue(employeName);

                // Date
                String dateStr = p.getDate().toLocalDate().format(dateFormatter);
                row.createCell(2).setCellValue(dateStr);

                // Heure début
                row.createCell(3).setCellValue(p.getHeureDebut().toString().substring(0, 5));

                // Heure fin
                row.createCell(4).setCellValue(p.getHeureFin().toString().substring(0, 5));

                // Type shift
                row.createCell(5).setCellValue(p.getTypeShift());
            }

            // Ajuster la largeur des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écrire dans le fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("✅ Excel plannings exporté: " + filePath);

        } catch (IOException e) {
            System.err.println("❌ Erreur export Excel plannings: " + e.getMessage());
            e.printStackTrace();
        }
    }
}