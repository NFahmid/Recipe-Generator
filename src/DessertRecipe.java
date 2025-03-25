public class DessertRecipe extends AbstractRecipe {
    public DessertRecipe(String name, java.util.List<Ingredient> ingredients, java.util.List<String> steps, int servings) {
        super(name, ingredients, steps, servings);
    }

    @Override
    protected AbstractRecipe createAdjustedRecipe(String name, java.util.List<Ingredient> ingredients, java.util.List<String> steps, int servings) {
        return new DessertRecipe(name, ingredients, steps, servings);
    }
}