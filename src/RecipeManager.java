import java.io.*;
import java.util.*;

public class RecipeManager {
    private static RecipeManager instance;

    private RecipeManager() {}

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    public List<Recipe> loadRecipes(String filename) {
        List<Recipe> recipes = new ArrayList<>();
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
        return recipes;
    }
}
