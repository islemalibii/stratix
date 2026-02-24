package Services;

import models.EventFeedback;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEventFeedback {
    Connection cnx ;
    public ServiceEventFeedback() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    public void add(EventFeedback f) {

        String sql = "INSERT INTO event_feedback(evenement_id, rating, commentaire, date_feedback) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, f.getEvenementId());
            ps.setInt(2, f.getRating());
            ps.setString(3, f.getCommentaire());
            ps.setDate(4, Date.valueOf(f.getDateFeedback()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public List<EventFeedback> getByEvent(int eventId) {

        List<EventFeedback> list = new ArrayList<>();

        String sql = "SELECT * FROM event_feedback WHERE evenement_id=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EventFeedback f = new EventFeedback();
                f.setId(rs.getInt("id"));
                f.setEvenementId(rs.getInt("evenement_id"));
                f.setRating(rs.getInt("rating"));
                f.setCommentaire(rs.getString("commentaire"));
                f.setDateFeedback(rs.getDate("date_feedback").toLocalDate());

                list.add(f);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public boolean exists(int eventId, String comment) {
        String sql = "SELECT count(*) FROM event_feedback WHERE evenement_id = ? AND TRIM(commentaire) = TRIM(?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setString(2, comment.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
