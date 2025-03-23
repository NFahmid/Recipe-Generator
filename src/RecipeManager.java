import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeManager {
    private static RecipeManager instance;
    private List<Recipe> recipes;
    private IngredientInventory inventory;

    private RecipeManager() {
        recipes = new ArrayList<>();
        inventory = IngredientInventory.getInstance();
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    public void loadRecipes(String filename) {
        this.recipes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String name = "";
            int servings = 0;
            List<Ingredient> ingredients = new ArrayList<>();
            List<String> steps = new ArrayList<>();
            boolean isIngredientSection = false, isStepSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Recipe:")) {
                    name = line.substring(8).trim();
                } else if (line.startsWith("Servings:")) {
                    servings = Integer.parseInt(line.substring(9).trim());
                } else if (line.equals("Ingredients:")) {
                    isIngredientSection = true;
                    isStepSection = false;
                } else if (line.equals("Steps:")) {
                    isIngredientSection = false;
                    isStepSection = true;
                } else if (line.equals("---")) {
                    recipes.add(new Recipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings));
                    ingredients.clear();
                    steps.clear();
                } else if (isIngredientSection) {
                    String[] parts = line.substring(2).split(", ");
                    ingredients.add(new Ingredient(parts[0], Double.parseDouble(parts[1]), parts[2]));
                } else if (isStepSection) {
                    steps.add(line.substring(3));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateRecipeAvailability();
    }

    private void updateRecipeAvailability() {
        for (Recipe recipe : recipes) {
            recipe.updateAvailabilityStatus(inventory);
        }
    }

    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }

    public List<Recipe> getAvailableRecipes() {
        return recipes.stream()
                .filter(r -> r.getStatus() == Recipe.RecipeStatus.FULLY_AVAILABLE)
                .collect(Collectors.toList());
    }

    public List<Recipe> getPartiallyAvailableRecipes() {
        return recipes.stream()
                .filter(r -> r.getStatus() == Recipe.RecipeStatus.PARTIALLY_AVAILABLE)
                .collect(Collectors.toList());
    }

    public void refreshAvailability() {
        updateRecipeAvailability();
    }
}
