import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Map;

public class RecipeApp {
    private static UserManager userManager;
    private static RecipeManager recipeManager;
    private static Scanner scanner;

    private static void displayAuthMenu() {
        System.out.println("\n=== Recipe Manager Login ===");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.print("\nEnter your choice (1-3): ");
    }

    private static void displayMenu() {
        System.out.println("\n=== Recipe Manager Menu ===");
        System.out.println("1. Add New Recipe");
        System.out.println("2. View All Recipes");
        System.out.println("3. View Available Recipes");
        System.out.println("4. View Partially Available Recipes");
        System.out.println("5. Manage Ingredients");
        System.out.println("6. Logout");
        System.out.print("\nEnter your choice (1-6): ");
    }

    private static void displayIngredientMenu() {
        System.out.println("\n=== Ingredient Management ===");
        System.out.println("1. View All Ingredients");
        System.out.println("2. Add New Ingredient");
        System.out.println("3. Remove Ingredient");
        System.out.println("4. Back to Main Menu");
        System.out.print("\nEnter your choice (1-4): ");
    }

    private static void viewAllIngredients(IngredientInventory inventory) {
        System.out.println("\nAvailable Ingredients:\n");
        Map<String, Ingredient> ingredients = inventory.getAvailableIngredients();
        if (ingredients.isEmpty()) {
            System.out.println("No ingredients available.");
            return;
        }
        for (Ingredient ingredient : ingredients.values()) {
            System.out.printf("%s: %.2f %s%n", 
                ingredient.getName(), 
                ingredient.getQuantity(), 
                ingredient.getUnit());
        }
    }

    private static void addNewIngredient(IngredientInventory inventory, Scanner scanner) {
        System.out.println("\nAdd New Ingredient");
        System.out.println("Format: ingredient_name, quantity, unit (e.g., Flour, 200, g)");
        
        System.out.print("Enter ingredient details: ");
        String input = scanner.nextLine().trim();
        
        try {
            String[] parts = input.split(", ");
            if (parts.length != 3) {
                System.out.println("Invalid format! Please use: ingredient_name, quantity, unit");
                return;
            }
            String name = parts[0];
            double quantity = Double.parseDouble(parts[1]);
            String unit = parts[2];
            
            inventory.addIngredient(new Ingredient(name, quantity, unit));
            System.out.println("Ingredient added successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity! Please enter a valid number.");
        }
    }

    private static void removeIngredient(IngredientInventory inventory, Scanner scanner) {
        System.out.println("\nRemove Ingredient");
        System.out.print("Enter ingredient name to remove: ");
        String name = scanner.nextLine().trim();
        
        inventory.removeIngredient(name);
        System.out.println("Ingredient removed successfully!");
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

    private static boolean handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (userManager.login(username, password)) {
            System.out.println("\nLogin successful! Welcome, " + username + "!");
            return true;
        } else {
            System.out.println("\nInvalid username or password.");
            return false;
        }
    }

    private static boolean handleSignup() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        if (userManager.registerUser(username, password)) {
            System.out.println("\nRegistration successful! Please login.");
            return true;
        } else {
            System.out.println("\nUsername already exists. Please choose a different username.");
            return false;
        }
    }

    public static void main(String[] args) {
        userManager = UserManager.getInstance();
        recipeManager = RecipeManager.getInstance();
        scanner = new Scanner(System.in);

        recipeManager.loadRecipes("src/recipes.txt");

        while (true) {
            displayAuthMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (handleLogin()) {
                        while (true) {
                            displayMenu();
                            String menuChoice = scanner.nextLine().trim();

                            switch (menuChoice) {
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
                                    while (true) {
                                        displayIngredientMenu();
                                        String ingredientChoice = scanner.nextLine().trim();
                                        
                                        switch (ingredientChoice) {
                                            case "1":
                                                viewAllIngredients(userManager.getCurrentUser().getPersonalInventory());
                                                break;
                                            case "2":
                                                addNewIngredient(userManager.getCurrentUser().getPersonalInventory(), scanner);
                                                break;
                                            case "3":
                                                removeIngredient(userManager.getCurrentUser().getPersonalInventory(), scanner);
                                                break;
                                            case "4":
                                                break;
                                            default:
                                                System.out.println("\nInvalid choice! Please try again.");
                                                continue;
                                        }
                                        
                                        if (ingredientChoice.equals("4")) break;
                                        System.out.println("\nPress Enter to continue...");
                                        scanner.nextLine();
                                    }
                                    break;
                                case "6":
                                    userManager.logout();
                                    System.out.println("\nLogged out successfully!");
                                    break;
                                default:
                                    System.out.println("\nInvalid choice! Please try again.");
                            }

                            if (menuChoice.equals("6")) break;
                            System.out.println("\nPress Enter to continue...");
                            scanner.nextLine();
                        }
                    }
                    break;
                case "2":
                    handleSignup();
                    break;
                case "3":
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
