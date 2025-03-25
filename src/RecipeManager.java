import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RecipeManager {
    private static RecipeManager instance;
    private List<AbstractRecipe> recipes;
    private UserManager userManager;

    private RecipeManager() {
        recipes = new ArrayList<>();
        userManager = UserManager.getInstance();
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
                    // Create appropriate recipe type based on name or ingredients
                    AbstractRecipe recipe;
                    if (name.toLowerCase().contains("dessert") || name.toLowerCase().contains("cake") || 
                        name.toLowerCase().contains("cookie") || name.toLowerCase().contains("pie")) {
                        recipe = new DessertRecipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings);
                    } else if (name.toLowerCase().contains("drink") || name.toLowerCase().contains("smoothie") || 
                             name.toLowerCase().contains("juice") || name.toLowerCase().contains("tea") || 
                             name.toLowerCase().contains("coffee")) {
                        recipe = new BeverageRecipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings);
                    } else {
                        recipe = new MainDishRecipe(name, new ArrayList<>(ingredients), new ArrayList<>(steps), servings);
                    }
                    recipes.add(recipe);
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
        updateRecipeAvailability();
    }

    private void updateRecipeAvailability() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) return;
        
        for (AbstractRecipe recipe : recipes) {
            recipe.updateAvailabilityStatus(currentUser.getPersonalInventory());
        }
    }

    public List<AbstractRecipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }

    public List<AbstractRecipe> getAvailableRecipes() {
        return recipes.stream()
                .filter(r -> r.getStatus() == AbstractRecipe.RecipeStatus.FULLY_AVAILABLE)
                .collect(Collectors.toList());
    }

    public List<AbstractRecipe> getPartiallyAvailableRecipes() {
        return recipes.stream()
                .filter(r -> r.getStatus() == AbstractRecipe.RecipeStatus.PARTIALLY_AVAILABLE)
                .collect(Collectors.toList());
    }

    public void refreshAvailability() {
        updateRecipeAvailability();
    }

    public void addRecipe(String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        AbstractRecipe newRecipe;
        if (name.toLowerCase().contains("dessert") || name.toLowerCase().contains("cake") || 
            name.toLowerCase().contains("cookie") || name.toLowerCase().contains("pie")) {
            newRecipe = new DessertRecipe(name, ingredients, steps, servings);
        } else if (name.toLowerCase().contains("drink") || name.toLowerCase().contains("smoothie") || 
                   name.toLowerCase().contains("juice") || name.toLowerCase().contains("tea") || 
                   name.toLowerCase().contains("coffee")) {
            newRecipe = new BeverageRecipe(name, ingredients, steps, servings);
        } else {
            newRecipe = new MainDishRecipe(name, ingredients, steps, servings);
        }
        recipes.add(newRecipe);
        updateRecipeAvailability();
        saveRecipes("src/recipes.txt");
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
        
        addRecipe(name, servings, ingredients, steps);
        System.out.println("\nRecipe added successfully!");
    }

    public AbstractRecipe getRandomRecipe() {
        if (recipes.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return recipes.get(random.nextInt(recipes.size()));
    }

    private void saveRecipes(String filename) {
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
}
