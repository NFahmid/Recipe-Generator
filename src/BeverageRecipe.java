public class BeverageRecipe extends AbstractRecipe {
    public BeverageRecipe(String name, java.util.List<Ingredient> ingredients, java.util.List<String> steps, int servings) {
        super(name, ingredients, steps, servings);
    }

    @Override
    protected AbstractRecipe createAdjustedRecipe(String name, java.util.List<Ingredient> ingredients, java.util.List<String> steps, int servings) {
        return new BeverageRecipe(name, ingredients, steps, servings);
    }
}