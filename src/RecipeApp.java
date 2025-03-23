import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class RecipeApp {
    private static void displayMenu() {
        System.out.println("\n=== Recipe Manager Menu ===");
        System.out.println("1. Add New Recipe");
        System.out.println("2. View All Recipes");
        System.out.println("3. View Available Recipes");
        System.out.println("4. View Partially Available Recipes");
        System.out.println("5. Exit");
        System.out.print("\nEnter your choice (1-5): ");
    }

    private static void viewAllRecipes(RecipeManager recipeManager) {
        System.out.println("\nAll Recipes:\n");
        List<Recipe> recipes = recipeManager.getAllRecipes();
        if (recipes.isEmpty()) {
            System.out.println("No recipes found.");
            return;
        }
        for (Recipe recipe : recipes) {
            System.out.println(recipe);
            System.out.println();
        }
    }

    private static void viewAvailableRecipes(RecipeManager recipeManager) {
        System.out.println("\nFully Available Recipes:\n");
        List<Recipe> availableRecipes = recipeManager.getAvailableRecipes();
        if (availableRecipes.isEmpty()) {
            System.out.println("No fully available recipes found.");
            return;
        }
        int count = 1;
        for (Recipe recipe : availableRecipes) {
            System.out.println(count + ". " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            count++;
        }
    }

    private static void viewPartiallyAvailableRecipes(RecipeManager recipeManager) {
        System.out.println("\nPartially Available Recipes:\n");
        List<Recipe> partialRecipes = recipeManager.getPartiallyAvailableRecipes();
        if (partialRecipes.isEmpty()) {
            System.out.println("No partially available recipes found.");
            return;
        }
        int count = 1;
        for (Recipe recipe : partialRecipes) {
            System.out.println(count + ". " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            count++;
        }
    }

    public static void main(String[] args) {
        RecipeManager recipeManager = RecipeManager.getInstance();
        IngredientInventory inventory = IngredientInventory.getInstance();
        Scanner scanner = new Scanner(System.in);

        // Load available ingredients
        inventory.loadIngredientsFromFile("src/ingredients.txt");

        // Load recipes
        recipeManager.loadRecipes("src/recipes.txt");

        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    recipeManager.createRecipeWithUserInput();
                    break;
                case "2":
                    viewAllRecipes(recipeManager);
                    break;
                case "3":
                    viewAvailableRecipes(recipeManager);
                    break;
                case "4":
                    viewPartiallyAvailableRecipes(recipeManager);
                    break;
                case "5":
                    System.out.println("\nThank you for using Recipe Manager!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("\nInvalid choice! Please try again.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
}
