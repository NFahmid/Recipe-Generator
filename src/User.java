import java.util.Objects;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class User {
    private String username;
    private String hashedPassword;
    private IngredientInventory personalInventory;

    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = hashPassword(password);
        this.personalInventory = IngredientInventory.getInstance();
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    private String hashPassword(String password) {
        StringBuilder result = new StringBuilder();
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                char shifted = (char) (((c - base + 3) % 26) + base);
                result.append(shifted);
            } else if (Character.isDigit(c)) {
                char shifted = (char) (((c - '0' + 3) % 10) + '0');
                result.append(shifted);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public boolean validatePassword(String password) {
        return Objects.equals(this.hashedPassword, hashPassword(password));
    }

    public IngredientInventory getPersonalInventory() {
        return personalInventory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}