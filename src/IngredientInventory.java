import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        availableIngredients.put(ingredient.getName().toLowerCase(), ingredient);
    }

    public void removeIngredient(String name) {
        availableIngredients.remove(name.toLowerCase());
    }

    public boolean hasIngredient(String name, double quantity) {
        Ingredient available = availableIngredients.get(name.toLowerCase());
        return available != null && available.getQuantity() >= quantity;
    }

    public Map<String, Ingredient> getAvailableIngredients() {
        return new HashMap<>(availableIngredients);
    }

    public void loadIngredientsFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(", ");
                if (parts.length == 3) {
                    String name = parts[0];
                    double quantity = Double.parseDouble(parts[1]);
                    String unit = parts[2];
                    addIngredient(new Ingredient(name, quantity, unit));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}