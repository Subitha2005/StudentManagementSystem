public class UserSession {
    private static String username;

    public static void login(String user) {
        username = user;
    }

    public static void logout() {
        username = null;
    }

    public static String getUsername() {
        return username;
    }
}
