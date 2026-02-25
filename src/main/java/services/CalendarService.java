package services;

import models.CalendarEvent;
import models.Planning;
import models.Employe;
import utils.MyDataBase; // Corrected import to match your package

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalendarService {

    private EmployeeService employeeService;

    public CalendarService() {
        this.employeeService = new EmployeeService();
    }

    public List<CalendarEvent> getEventsForMonth(int year, int month) {
        List<CalendarEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM planning WHERE YEAR(date) = ? AND MONTH(date) = ?";

        // Get the singleton connection from MyDataBase
        Connection conn = MyDataBase.getInstance().getCnx();

        // Note: We do NOT put 'conn' in the try-with-resources brackets
        // because we don't want Java to close the shared singleton connection.
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Planning p = new Planning();
                    p.setId(rs.getInt("id"));
                    p.setEmployeId(rs.getInt("employe_id"));
                    p.setDate(rs.getDate("date"));
                    p.setHeureDebut(rs.getTime("heure_debut"));
                    p.setHeureFin(rs.getTime("heure_fin"));
                    p.setTypeShift(rs.getString("type_shift"));

                    // Récupérer le nom de l'employé
                    Employe emp = employeeService.getEmployeById(p.getEmployeId());
                    String employeNom = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();

                    // Créer l'événement calendrier
                    CalendarEvent event = new CalendarEvent(p, employeNom);
                    events.add(event);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }

    public List<CalendarEvent> getEventsForDay(LocalDate date) {
        List<CalendarEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM planning WHERE date = ?";

        // Get the singleton connection from MyDataBase
        Connection conn = MyDataBase.getInstance().getCnx();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Planning p = new Planning();
                    p.setId(rs.getInt("id"));
                    p.setEmployeId(rs.getInt("employe_id"));
                    p.setDate(rs.getDate("date"));
                    p.setHeureDebut(rs.getTime("heure_debut"));
                    p.setHeureFin(rs.getTime("heure_fin"));
                    p.setTypeShift(rs.getString("type_shift"));

                    Employe emp = employeeService.getEmployeById(p.getEmployeId());
                    String employeNom = (emp != null) ? emp.getUsername() : "Employé " + p.getEmployeId();

                    CalendarEvent event = new CalendarEvent(p, employeNom);
                    events.add(event);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return events;
    }
}