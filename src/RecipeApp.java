import java.util.List;

public class RecipeApp {
    public static void main(String[] args) {
        RecipeManager recipeManager = RecipeManager.getInstance();
        IngredientInventory inventory = IngredientInventory.getInstance();

        // Load available ingredients
        inventory.loadIngredientsFromFile("src/ingredients.txt");

        // Load recipes
        recipeManager.loadRecipes("src/recipes.txt");

        // Display all recipes with their availability status
        System.out.println("All Recipes:\n");
        for (Recipe recipe : recipeManager.getAllRecipes()) {
            System.out.println(recipe);
            System.out.println();
        }

        // Display fully available recipes
        System.out.println("\nFully Available Recipes:\n");
        int count = 1;
        for (Recipe recipe : recipeManager.getAvailableRecipes()) {
            System.out.println(count + "  " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            count++;
        }

        // Display partially available recipes
        System.out.println("\nPartially Available Recipes:\n");
        for (Recipe recipe : recipeManager.getPartiallyAvailableRecipes()) {
            int availableCount = 1;
            System.out.println(availableCount + "  " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            availableCount++;
        }
    }
}
