import java.io.*;
import java.util.*;

public class UserDataManager {
    private static UserDataManager instance;
    private Map<String, User> users;
    private User currentUser;
    private static final String USER_INGREDIENTS_DIR = "src/user_ingredients/";

    private UserDataManager() {
        users = new HashMap<>();
        new File(USER_INGREDIENTS_DIR).mkdirs();
        loadUsers();
    }

    public static UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public void loadUserIngredients() {
        if (currentUser == null) return;
        String filename = USER_INGREDIENTS_DIR + currentUser.getUsername() + "_ingredients.txt";
        currentUser.getPersonalInventory().loadIngredientsFromFile(filename);
    }

    public void saveUserIngredients() {
        if (currentUser == null) return;
        String filename = USER_INGREDIENTS_DIR + currentUser.getUsername() + "_ingredients.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Ingredient ingredient : currentUser.getPersonalInventory().getAvailableIngredients().values()) {
                writer.println(ingredient.getName() + ", " + ingredient.getQuantity() + ", " + ingredient.getUnit());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createUserIngredientFile(String username) {
        String ingredientFilePath = USER_INGREDIENTS_DIR + username + "_ingredients.txt";
        try {
            new File(ingredientFilePath).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/users.txt"))) {
            for (User user : users.values()) {
                writer.println(user.getUsername() + "," + user.getHashedPassword());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    User user = new User(parts[0]);
                    user.setHashedPassword(parts[1]);
                    users.put(parts[0], user);
                }
            }
        } catch (IOException e) {
            // File might not exist yet, which is fine for first run
        }
    }
}