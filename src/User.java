import java.util.Objects;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String hashedPassword;
    private IngredientInventory personalInventory;
    private List<RecipeHistory> cookingHistory;

    public User(String username, String password) {
        this.username = username;
        this.hashedPassword = hashPassword(password);
        this.personalInventory = IngredientInventory.getInstance();
        this.cookingHistory = new ArrayList<>();
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

    public boolean validatePassword(String password) {
        String hashedInput = hashPassword(password);
        return Objects.equals(this.hashedPassword, hashedInput);
    }

    public IngredientInventory getPersonalInventory() {
        return personalInventory;
    }

    public void addCookingHistory(RecipeHistory history) {
        cookingHistory.add(history);
    }

    public List<RecipeHistory> getCookingHistory() {
        return cookingHistory;
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