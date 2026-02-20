package utiles;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

public class DBConnectionTest {

    @Test
    public void testGetConnection() {
        Connection conn = DBConnection.getConnection();
        assertNotNull(conn, "La connexion à la base de données ne doit pas être nulle !");
        try {
            assertFalse(conn.isClosed(), "La connexion ne doit pas être fermée !");
        } catch (Exception e) {
            fail("Erreur lors de la vérification de la connexion : " + e.getMessage());
        }
    }
}
