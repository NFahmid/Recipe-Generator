import java.util.*;

public class RecipeManager {
    private static RecipeManager instance;
    private RecipeRepository recipeRepository;
    private RecipeValidator recipeValidator;

    private RecipeManager() {
        recipeRepository = RecipeRepository.getInstance();
        recipeValidator = RecipeValidator.getInstance();
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    public void loadRecipes(String filename) {
        recipeRepository.loadRecipes(filename);
    }

    public List<AbstractRecipe> getAllRecipes() {
        return recipeRepository.getAllRecipes();
    }

    public List<AbstractRecipe> getAvailableRecipes() {
        return recipeRepository.getAvailableRecipes();
    }

    public List<AbstractRecipe> getPartiallyAvailableRecipes() {
        return recipeRepository.getPartiallyAvailableRecipes();
    }

    public void refreshAvailability() {
        recipeRepository.refreshAvailability();
    }

    public void addRecipe(String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        recipeRepository.addRecipe(name, servings, ingredients, steps);
    }

    public void createRecipeWithUserInput() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter recipe name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter number of servings: ");
        int servings = Integer.parseInt(scanner.nextLine());
        
        List<Ingredient> ingredients = new ArrayList<>();
        System.out.println("\nEnter ingredients (type 'done' when finished):");
        System.out.println("Format: ingredient_name, quantity, unit (e.g., Flour, 200, g)");
        
        while (true) {
            System.out.print("Ingredient: ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("done")) break;
            
            try {
                String[] parts = input.split(", ");
                if (parts.length != 3) {
                    System.out.println("Invalid format! Please use: ingredient_name, quantity, unit");
                    continue;
                }
                String ingredientName = parts[0];
                double quantity = Double.parseDouble(parts[1]);
                String unit = parts[2];
                ingredients.add(new Ingredient(ingredientName, quantity, unit));
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity! Please enter a valid number.");
            }
        }
        
        List<String> steps = new ArrayList<>();
        System.out.println("\nEnter cooking steps (type 'done' when finished):");
        int stepNumber = 1;
        
        while (true) {
            System.out.print("Step " + stepNumber + ": ");
            String step = scanner.nextLine().trim();
            if (step.equalsIgnoreCase("done")) break;
            steps.add(step);
            stepNumber++;
        }
        
        if (recipeValidator.validateRecipe(name, servings, ingredients, steps)) {
            addRecipe(name, servings, ingredients, steps);
            System.out.println("\nRecipe added successfully!");
        } else {
            String errors = recipeValidator.getValidationErrors(name, servings, ingredients, steps);
            System.out.println("\nFailed to add recipe:\n" + errors);
        }
    }

    public AbstractRecipe getRandomRecipe() {
        return recipeRepository.getRandomRecipe();
    }
}
