import java.io.*;
import java.util.*;
import java.nio.file.*;

public class UserManager {
    private static volatile UserManager instance;
    private Map<String, User> users;
    private User currentUser;
    private static final String USERS_FILE = "src/users.txt";
    private static final String USER_INGREDIENTS_DIR = "src/user_ingredients/";

    private UserManager() {
        users = new HashMap<>();
        new File(USER_INGREDIENTS_DIR).mkdirs();
        loadUsers();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null) {
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false;
        }
        User newUser = new User(username, password);
        users.put(username, newUser);
        saveUsers();
        
        // Create empty ingredient file for new user
        String filename = USER_INGREDIENTS_DIR + username + "_ingredients.txt";
        try {
            new File(filename).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.validatePassword(password)) {
            currentUser = user;
            loadUserIngredients();
            return true;
        }
        return false;
    }

    public void logout() {
        if (currentUser != null) {
            saveUserIngredients();
            currentUser = null;
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                return;
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 2) {
                        String username = parts[0];
                        String hashedPassword = parts[1];
                        User user = new User(username, "");
                        user.setHashedPassword(hashedPassword);
                        users.put(username, user);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.println(user.getUsername() + "," + user.getHashedPassword());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserIngredients() {
        if (currentUser == null) return;
        String filename = USER_INGREDIENTS_DIR + currentUser.getUsername() + "_ingredients.txt";
        currentUser.getPersonalInventory().loadIngredientsFromFile(filename);
    }

    private void saveUserIngredients() {
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
}