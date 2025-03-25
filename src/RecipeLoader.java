import java.io.*;
import java.util.*;

public class RecipeLoader {
    private static RecipeLoader instance;

    private RecipeLoader() {}

    public static RecipeLoader getInstance() {
        if (instance == null) {
            instance = new RecipeLoader();
        }
        return instance;
    }

    public List<AbstractRecipe> loadRecipes(String filename) {
        List<AbstractRecipe> recipes = new ArrayList<>();
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
                    recipes.add(createRecipe(name, servings, ingredients, steps));
                    ingredients.clear();
                    steps.clear();
                } else if (isIngredientSection) {
                    String[] parts = line.substring(2).split(", ");
                    ingredients.add(new Ingredient(parts[0], Double.parseDouble(parts[1]), parts[2]));
                } else if (isStepSection) {
                    steps.add(line.substring(2));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public void saveRecipes(String filename, List<AbstractRecipe> recipes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (AbstractRecipe recipe : recipes) {
                writer.println("Recipe: " + recipe.getName());
                writer.println("Servings: " + recipe.getServings());
                writer.println("Ingredients:");
                for (Ingredient ingredient : recipe.getIngredients()) {
                    writer.println("- " + ingredient.getName() + ", " + ingredient.getQuantity() + ", " + ingredient.getUnit());
                }
                writer.println("Steps:");
                for (String step : recipe.getSteps()) {
                    writer.println("- " + step);
                }
                writer.println("---");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AbstractRecipe createRecipe(String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        if (name.toLowerCase().contains("dessert") || name.toLowerCase().contains("cake") || 
            name.toLowerCase().contains("cookie") || name.toLowerCase().contains("pie")) {
            return new DessertRecipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings);
        } else if (name.toLowerCase().contains("drink") || name.toLowerCase().contains("smoothie") || 
                   name.toLowerCase().contains("juice") || name.toLowerCase().contains("tea") || 
                   name.toLowerCase().contains("coffee")) {
            return new BeverageRecipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings);
        } else {
            return new MainDishRecipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings);
        }
    }
}