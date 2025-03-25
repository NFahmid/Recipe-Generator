import java.io.*;
import java.util.*;

public class IngredientInventory {
    private static IngredientInventory instance;
    private Map<String, Ingredient> availableIngredients;

    private IngredientInventory() {
        availableIngredients = new HashMap<>();
    }

    public static IngredientInventory getInstance() {
        if (instance == null) {
            instance = new IngredientInventory();
        }
        return instance;
    }

    public void addIngredient(Ingredient ingredient) {
        String lowercaseName = ingredient.getName().toLowerCase();
        Ingredient existingIngredient = availableIngredients.get(lowercaseName);
        
        if (existingIngredient != null) {
            double newQuantity = existingIngredient.getQuantity() + ingredient.getQuantity();
            availableIngredients.put(lowercaseName, new Ingredient(existingIngredient.getName(), newQuantity, existingIngredient.getUnit()));
        } else {
            availableIngredients.put(lowercaseName, ingredient);
        }
        
        saveIngredientsToFile();
    }

    public void removeIngredient(String name) {
        availableIngredients.remove(name.toLowerCase());
        saveIngredientsToFile();
    }

    public boolean removeIngredientAmount(String name, double amount) {
        Ingredient ingredient = availableIngredients.get(name.toLowerCase());
        if (ingredient == null || ingredient.getQuantity() < amount) {
            return false;
        }
        double newQuantity = ingredient.getQuantity() - amount;
        if (newQuantity > 0) {
            availableIngredients.put(name.toLowerCase(), new Ingredient(ingredient.getName(), newQuantity, ingredient.getUnit()));
        } else {
            availableIngredients.remove(name.toLowerCase());
        }
        saveIngredientsToFile();
        return true;
    }

    public boolean hasIngredient(String name, double quantity) {
        String lowercaseName = name.toLowerCase().trim();
        Ingredient available = availableIngredients.get(lowercaseName);
        return available != null && available.getQuantity() >= quantity;
    }

    public Map<String, Ingredient> getAvailableIngredients() {
        return new HashMap<>(availableIngredients);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ConsoleColors.BLUE_BOLD).append("Available Ingredients:").append(ConsoleColors.RESET).append("\n");
        
        for (Ingredient ingredient : availableIngredients.values()) {
            sb.append(ConsoleColors.CYAN).append("- ").append(ConsoleColors.RESET)
              .append(ingredient.toString()).append("\n");
        }
        return sb.toString();
    }

    public double getIngredientAmount(String name) {
        String lowercaseName = name.toLowerCase().trim();
        Ingredient ingredient = availableIngredients.get(lowercaseName);
        return ingredient != null ? ingredient.getQuantity() : 0.0;
    }

    private void saveIngredientsToFile() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            String filename = "src/user_ingredients/" + currentUser.getUsername() + "_ingredients.txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                for (Ingredient ing : availableIngredients.values()) {
                    writer.println(ing.getName() + ", " + ing.getQuantity() + ", " + ing.getUnit());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadIngredientsFromFile(String filename) {
        availableIngredients.clear(); // Clear existing ingredients before loading
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(", ");
                if (parts.length == 3) {
                    String name = parts[0].trim();
                    double quantity = Double.parseDouble(parts[1]);
                    String unit = parts[2].trim();
                    Ingredient ingredient = new Ingredient(name, quantity, unit);
                    String lowercaseName = name.toLowerCase();
                    availableIngredients.put(lowercaseName, ingredient);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}