import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class StepByStepCookingMode implements CookingMode {
    private Scanner scanner;
    private Map<String, Double> usedIngredients;
    private User currentUser;
    private RecipeHistory currentHistory;

    public StepByStepCookingMode(User user) {
        this.scanner = new Scanner(System.in);
        this.usedIngredients = new HashMap<>();
        this.currentUser = user;
    }

    @Override
    public void startCooking(AbstractRecipe recipe) {
        List<String> steps = recipe.getSteps();
        List<Ingredient> ingredients = recipe.getIngredients();
        currentHistory = new RecipeHistory(recipe.getName(), recipe.getServings(), recipe);
        System.out.println("\nStarting Cooking Mode for " + recipe.getName());
        System.out.println("Total steps: " + steps.size());

        if (!checkAndConfirmIngredients(recipe)) {
            System.out.println("\nCooking cancelled.");
            return;
        }

        System.out.println("\nAll ingredients confirmed. Press Enter to start cooking...");
        scanner.nextLine();

        for (int i = 0; i < steps.size(); i++) {
            if (!processStep(recipe, i)) {
                rollbackIngredients();
                return;
            }
        }

        System.out.println("\nCongratulations! You have completed cooking " + recipe.getName() + "!");
        currentUser.addCookingHistory(currentHistory);
        usedIngredients.clear();
    }

    private boolean checkAndConfirmIngredients(AbstractRecipe recipe) {
        IngredientInventory inventory = currentUser.getPersonalInventory();
        List<Ingredient> recipeIngredients = recipe.getIngredients();
        Map<String, Double> insufficientIngredients = new HashMap<>();

        // Check all ingredients first
        for (Ingredient ingredient : recipeIngredients) {
            double availableAmount = inventory.getIngredientAmount(ingredient.getName());
            double requiredAmount = ingredient.getQuantity();

            if (availableAmount < requiredAmount) {
                insufficientIngredients.put(ingredient.getName(), requiredAmount - availableAmount);
            }
        }

        if (!insufficientIngredients.isEmpty()) {
            System.out.println("\nWarning: Some ingredients are insufficient:");
            for (Map.Entry<String, Double> entry : insufficientIngredients.entrySet()) {
                String ingredientName = entry.getKey();
                double missingAmount = entry.getValue();
                double availableAmount = inventory.getIngredientAmount(ingredientName);
                String unit = "";
                for (Ingredient ing : recipeIngredients) {
                    if (ing.getName().equals(ingredientName)) {
                        unit = ing.getUnit();
                        break;
                    }
                }
                System.out.println("- " + ingredientName + ":");
                System.out.println("  Available: " + availableAmount + " " + unit);
                System.out.println("  Missing: " + missingAmount + " " + unit);
            }

            while (true) {
                System.out.println("\nOptions:");
                System.out.println("1. Use available amounts and alternatives for the rest");
                System.out.println("2. Use complete alternatives for insufficient ingredients");
                System.out.println("3. Cancel cooking");
                System.out.print("Choose an option (1-3): ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        for (Map.Entry<String, Double> entry : insufficientIngredients.entrySet()) {
                            String ingredientName = entry.getKey();
                            double availableAmount = inventory.getIngredientAmount(ingredientName);
                            if (availableAmount > 0) {
                                deductIngredient(ingredientName, availableAmount);
                                System.out.println("Using " + availableAmount + " of " + ingredientName + " and alternative for the rest");
                                currentHistory.markIngredientAlternative(ingredientName);
                            }
                        }
                        return true;
                    case "2":
                        for (String ingredientName : insufficientIngredients.keySet()) {
                            System.out.println("Using alternative for " + ingredientName);
                            currentHistory.markIngredientAlternative(ingredientName);
                        }
                        return true;
                    case "3":
                        return false;
                    default:
                        System.out.println("Invalid input. Please enter a number between 1 and 3.");
                }
            }
        }

        // If all ingredients are available, deduct them
        for (Ingredient ingredient : recipeIngredients) {
            deductIngredient(ingredient.getName(), ingredient.getQuantity());
        }

        return true;
    }

    private boolean processStep(AbstractRecipe recipe, int stepIndex) {
        List<String> steps = recipe.getSteps();
        System.out.println("\nStep " + (stepIndex + 1) + ": " + steps.get(stepIndex));

        while (true) {
            System.out.print("\nHave you completed this step? (yes/no/cancel): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes")) {
                return true;
            } else if (response.equals("cancel")) {
                return false;
            } else if (!response.equals("no")) {
                System.out.println("Invalid input. Please enter 'yes', 'no', or 'cancel'.");
            }
        }
    }

    private boolean handleIngredients(AbstractRecipe recipe, int stepIndex) {
        IngredientInventory inventory = currentUser.getPersonalInventory();
        List<Ingredient> recipeIngredients = recipe.getIngredients();

        for (Ingredient ingredient : recipeIngredients) {
            double availableAmount = inventory.getIngredientAmount(ingredient.getName());
            double requiredAmount = ingredient.getQuantity();

            if (availableAmount < requiredAmount) {
                System.out.println("\nWarning: Insufficient " + ingredient.getName());
                System.out.println("Required: " + requiredAmount + " " + ingredient.getUnit());
                System.out.println("Available: " + availableAmount + " " + ingredient.getUnit());

                while (true) {
                    System.out.println("\nOptions:");
                    System.out.println("1. Use available amount and substitute the rest");
                    System.out.println("2. Use a complete alternative ingredient");
                    System.out.println("3. Skip this ingredient");
                    System.out.println("4. Cancel recipe");
                    System.out.print("Choose an option (1-4): ");
                    
                    String choice = scanner.nextLine().trim();

                    switch (choice) {
                        case "1":
                            if (availableAmount > 0) {
                                deductIngredient(ingredient.getName(), availableAmount);
                                System.out.println("Using " + availableAmount + " " + ingredient.getUnit() + " of " + ingredient.getName());
                                System.out.println("Please substitute the remaining " + (requiredAmount - availableAmount) + " " + ingredient.getUnit());
                            }
                            return true;
                        case "2":
                            System.out.println("Proceeding with alternative ingredient. Please use your preferred substitute for " + 
                                             requiredAmount + " " + ingredient.getUnit() + " of " + ingredient.getName());
                            currentHistory.markIngredientAlternative(ingredient.getName());
                            return true;
                        case "3":
                            System.out.println("Skipping " + ingredient.getName() + ". Note that this might affect the final result.");
                            currentHistory.markIngredientSkipped(ingredient.getName());
                            return true;
                        case "4":
                            return false;
                        default:
                            System.out.println("Invalid input. Please enter a number between 1 and 4.");
                    }
                }
            } else {
                deductIngredient(ingredient.getName(), requiredAmount);
            }
        }
        return true;
    }

    private void deductIngredient(String name, double amount) {
        IngredientInventory inventory = currentUser.getPersonalInventory();
        String unit = null;
        for (Ingredient ingredient : currentHistory.getRecipe().getIngredients()) {
            if (ingredient.getName().equalsIgnoreCase(name)) {
                unit = ingredient.getUnit();
                break;
            }
        }
        
        if (!inventory.removeIngredientAmount(name, amount)) {
            System.out.println("Error: Failed to deduct " + amount + " of " + name + " from inventory.");
            return;
        }
        usedIngredients.merge(name, amount, Double::sum);
        currentHistory.addIngredientUsage(name, amount, unit);
    }

    private void rollbackIngredients() {
        IngredientInventory inventory = currentUser.getPersonalInventory();
        for (Map.Entry<String, Double> entry : usedIngredients.entrySet()) {
            inventory.addIngredient(new Ingredient(entry.getKey(), entry.getValue(), ""));
        }
        usedIngredients.clear();
        System.out.println("\nRecipe cancelled. All ingredients have been returned to inventory.");
    }
}
