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
        if (password == null) return null;
        
        StringBuilder hashedPassword = new StringBuilder();
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                hashedPassword.append((char) (((c - base + 3) % 26) + base));
            } else if (Character.isDigit(c)) {
                hashedPassword.append((char) (((c - '0' + 3) % 10) + '0'));
            } else {
                hashedPassword.append(c);
            }
        }
        return hashedPassword.toString();
    }
}