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

    public User(String username) {
        this.username = username;
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