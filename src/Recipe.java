import java.util.*;

public class Recipe {
    private String name;
    private int servings;
    private List<Ingredient> ingredients;
    private List<String> steps;

    public Recipe(String name, List<Ingredient> ingredients, List<String> steps, int servings) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
    }

    public String getName() { return name; }
    public int getServings() { return servings; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }

    @Override
    public String toString() {
        return "Recipe: " + name + "\nServings: " + servings + "\nIngredients: " + ingredients + "\nSteps: " + steps;
    }
}
