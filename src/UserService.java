import java.sql.Connection;
import java.sql.PreparedStatement;

import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    public static boolean register(
            String username,
            String password,
            String fullName,
            String role) {

        String sql = "INSERT INTO users(username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection con = DbUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Hash the password
            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

            ps.setString(1, username);
            ps.setString(2, hashed);
            ps.setString(3, fullName);
            ps.setString(4, role);

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
