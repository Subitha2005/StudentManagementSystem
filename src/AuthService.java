import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    public static boolean authenticate(String username, String password) {

        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbHash = rs.getString("password_hash");
                return BCrypt.checkpw(password, dbHash);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
