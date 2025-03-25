import java.util.*;
import java.util.stream.Collectors;

public class RecipeRepository {
    private static RecipeRepository instance;
    private List<AbstractRecipe> recipes;
    private UserManager userManager;
    private RecipeLoader recipeLoader;
    private RecipeValidator recipeValidator;

    private RecipeRepository() {
        recipes = new ArrayList<>();
        userManager = UserManager.getInstance();
        recipeLoader = RecipeLoader.getInstance();
        recipeValidator = RecipeValidator.getInstance();
    }

    public static RecipeRepository getInstance() {
        if (instance == null) {
            instance = new RecipeRepository();
        }
        return instance;
    }

    public void loadRecipes(String filename) {
        this.recipes = recipeLoader.loadRecipes(filename);
        updateRecipeAvailability();
    }

    public void addRecipe(String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        if (!recipeValidator.validateRecipe(name, servings, ingredients, steps)) {
            String errors = recipeValidator.getValidationErrors(name, servings, ingredients, steps);
            System.out.println("Failed to add recipe:\n" + errors);
            return;
        }

        AbstractRecipe newRecipe = createRecipe(name, ingredients, steps, servings);
        recipes.add(newRecipe);
        updateRecipeAvailability();
        recipeLoader.saveRecipes("src/recipes.txt", recipes);
    }

    private AbstractRecipe createRecipe(String name, List<Ingredient> ingredients, List<String> steps, int servings) {
        if (name.toLowerCase().contains("dessert") || name.toLowerCase().contains("cake") || 
            name.toLowerCase().contains("cookie") || name.toLowerCase().contains("pie")) {
            return new DessertRecipe(name, ingredients, steps, servings);
        } else if (name.toLowerCase().contains("drink") || name.toLowerCase().contains("smoothie") || 
                   name.toLowerCase().contains("juice") || name.toLowerCase().contains("tea") || 
                   name.toLowerCase().contains("coffee")) {
            return new BeverageRecipe(name, ingredients, steps, servings);
        } else {
            return new MainDishRecipe(name, ingredients, steps, servings);
        }
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

    public AbstractRecipe getRandomRecipe() {
        if (recipes.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return recipes.get(random.nextInt(recipes.size()));
    }
}