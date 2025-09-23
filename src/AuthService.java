public class AuthService {
    // for demo, a default admin: username=admin, password=admin123
    public static boolean authenticate(String user, String pass) {
        // plaintext for demo
        return user.equals("admin") && pass.equals("admin123");
    }
}
