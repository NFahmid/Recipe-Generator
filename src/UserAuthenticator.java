import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class UserAuthenticator {
    private static UserAuthenticator instance;

    private UserAuthenticator() {}

    public static UserAuthenticator getInstance() {
        if (instance == null) {
            instance = new UserAuthenticator();
        }
        return instance;
    }

    public boolean validateRegistration(String username, String password) {
        return username != null &&
                !username.trim().isEmpty() &&
                password != null &&
                !password.trim().isEmpty();
    }

    public boolean authenticateUser(String username, String password, Map<String, User> users) {
        User user = users.get(username);
        String hashedInputPassword = hashPassword(password);
        return user != null && user.getHashedPassword().equals(hashedInputPassword);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}