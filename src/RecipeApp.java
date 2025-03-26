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
        System.out.println(ConsoleColors.CYAN + "1." + ConsoleColors.RESET + " Login");
        System.out.println(ConsoleColors.CYAN + "2." + ConsoleColors.RESET + " Sign Up");
        System.out.println(ConsoleColors.CYAN + "3." + ConsoleColors.RESET + " Exit");
        System.out.print("\nEnter your choice (1-3): ");
    }

    private static void displayMenu() {
        System.out.println("\n=== Recipe Manager Menu ===");
        System.out.println(ConsoleColors.CYAN + "1." + ConsoleColors.RESET + " Add New Recipe");
        System.out.println(ConsoleColors.CYAN + "2." + ConsoleColors.RESET + " View All Recipes");
        System.out.println(ConsoleColors.CYAN + "3." + ConsoleColors.RESET + " Get Random Recipe");
        System.out.println(ConsoleColors.CYAN + "4." + ConsoleColors.RESET + " Manage Ingredients");
        System.out.println(ConsoleColors.CYAN + "5." + ConsoleColors.RESET + " View Cooking History");
        System.out.println(ConsoleColors.CYAN + "6." + ConsoleColors.RESET + " Logout");
        System.out.print("\nEnter your choice (1-6): ");
    }

    private static void displayIngredientMenu() {
        System.out.println("\n=== Ingredient Management ===");
        System.out.println(ConsoleColors.CYAN + "1." + ConsoleColors.RESET + " View All Ingredients");
        System.out.println(ConsoleColors.CYAN + "2." + ConsoleColors.RESET + " Add New Ingredient");
        System.out.println(ConsoleColors.CYAN + "3." + ConsoleColors.RESET + " Remove Ingredient");
        System.out.println(ConsoleColors.CYAN + "4." + ConsoleColors.RESET + " Back to Main Menu");
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

    private static void viewCookingHistory(User user) {
        List<RecipeHistory> history = user.getCookingHistory();
        if (history.isEmpty()) {
            System.out.println("\nNo cooking history found.");
            return;
        }

        System.out.println("\n=== Your Cooking History ===");
        for (RecipeHistory entry : history) {
            System.out.println(entry);
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void handleRecipeSelection(List<AbstractRecipe> recipes) {
        if (recipes.isEmpty()) return;

        System.out.println("\nWould you like to cook one of these recipes? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
            System.out.print("Enter the recipe number to cook (1-" + recipes.size() + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice > 0 && choice <= recipes.size()) {
                    AbstractRecipe selectedRecipe = recipes.get(choice - 1);
                    StepByStepCookingMode cookingMode = new StepByStepCookingMode(userManager.getCurrentUser());
                    cookingMode.startCooking(selectedRecipe);
                    recipeManager.refreshAvailability();
                } else {
                    System.out.println("Invalid recipe number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static void viewAllRecipes(RecipeManager recipeManager) {
        recipeManager.refreshAvailability();

        System.out.println("\nAll Recipes:\n");
        List<AbstractRecipe> recipeList = recipeManager.getAllRecipes();
        if (recipeList.isEmpty()) {
            System.out.println("No recipes found.");
            return;
        }

        int count = 1;
        for (AbstractRecipe recipe : recipeList) {
            System.out.println(count + ". " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            System.out.println(recipe);
            System.out.println();
            count++;
        }

        System.out.print("\nWould you like to adjust the serving size for all recipes? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y")) {
            System.out.print("Enter desired number of servings: ");
            try {
                int desiredServings = Integer.parseInt(scanner.nextLine().trim());
                if (desiredServings > 0) {
                    System.out.println("\nAdjusted Recipes:\n");
                    for (AbstractRecipe recipe : recipeList) {
                        AbstractRecipe adjustedRecipe = recipe.adjustServings(desiredServings);
                        System.out.println(adjustedRecipe);
                        System.out.println();
                    }
                } else {
                    System.out.println("Please enter a valid number of servings greater than 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static void viewAvailableRecipes(RecipeManager recipeManager) {
        System.out.println("\nFully Available Recipes:\n");
        List<AbstractRecipe> availableRecipes = recipeManager.getAvailableRecipes();
        if (availableRecipes.isEmpty()) {
            System.out.println("No fully available recipes found.");
            return;
        }
        int count = 1;
        for (AbstractRecipe recipe : availableRecipes) {
            System.out.println(count + ". " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            count++;
        }
        handleRecipeSelection(availableRecipes);
    }

    private static void viewPartiallyAvailableRecipes(RecipeManager recipeManager) {
        System.out.println("\nPartially Available Recipes:\n");
        List<AbstractRecipe> partialRecipes = recipeManager.getPartiallyAvailableRecipes();
        if (partialRecipes.isEmpty()) {
            System.out.println("No partially available recipes found.");
            return;
        }
        int count = 1;
        for (AbstractRecipe recipe : partialRecipes) {
            System.out.println(count + ". " + recipe.getName() + " - " + recipe.getMatchPercentage() + "% match");
            count++;
        }
        handleRecipeSelection(partialRecipes);
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

    private static void handleRandomRecipe() {
        AbstractRecipe randomRecipe = recipeManager.getRandomRecipe();
        if (randomRecipe != null) {
            System.out.println("\nHere's a random recipe for you:\n");
            System.out.println(randomRecipe);
            List<AbstractRecipe> singleRecipe = new ArrayList<>();
            singleRecipe.add(randomRecipe);
            handleRecipeSelection(singleRecipe);
        } else {
            System.out.println("\nNo recipes available.");
        }
    }

    private static void handleIngredientManagement() {
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
                    return;
                default:
                    System.out.println("\nInvalid choice! Please try again.");
                    continue;
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void handleMainMenu() {
        while (true) {
            displayMenu();
            String menuChoice = scanner.nextLine().trim();

            switch (menuChoice) {
                case "1":
                    recipeManager.createRecipeWithUserInput();
                    break;
                case "2":
                    viewAllRecipes(recipeManager);
                    handleRecipeSelection(recipeManager.getAllRecipes());
                    break;
                case "3":
                    handleRandomRecipe();
                    break;
                case "4":
                    handleIngredientManagement();
                    break;
                case "5":
                    viewCookingHistory(userManager.getCurrentUser());
                    break;
                case "6":
                    userManager.logout();
                    System.out.println("\nLogged out successfully!");
                    return;
                default:
                    System.out.println("\nInvalid choice! Please try again.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void handleAuthMenu() {
        while (true) {
            displayAuthMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (handleLogin()) {
                        handleMainMenu();
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

    public static void main(String[] args) {
        userManager = UserManager.getInstance();
        recipeManager = RecipeManager.getInstance();
        scanner = new Scanner(System.in);

        recipeManager.loadRecipes("src/recipes.txt");
        handleAuthMenu();
    }
}
