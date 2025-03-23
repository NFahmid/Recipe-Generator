public class RecipeApp {
    public static void main(String[] args) {
        RecipeManager recipeManager = RecipeManager.getInstance();
        List<Recipe> recipes = recipeManager.loadRecipes("recipes.txt");

        // Example: Display all recipes
        for (Recipe recipe : recipes) {
            System.out.println(recipe);
        }
    }
}
