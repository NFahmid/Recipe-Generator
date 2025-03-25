import java.util.*;

public class RecipeHistory {
    private String recipeName;
    private int servings;
    private Date cookingDate;
    private Map<String, String> ingredientUsage;
    private Recipe recipe;

    public RecipeHistory(String recipeName, int servings, Recipe recipe) {
        this.recipeName = recipeName;
        this.servings = servings;
        this.cookingDate = new Date();
        this.ingredientUsage = new HashMap<>();
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void addIngredientUsage(String ingredientName, double amount, String unit) {
        if (unit != null && !unit.isEmpty()) {
            ingredientUsage.put(ingredientName, amount + " " + unit);
        } else {
            ingredientUsage.put(ingredientName, String.valueOf(amount));
        }
    }

    public void markIngredientSkipped(String ingredientName) {
        ingredientUsage.put(ingredientName, "Skipped");
    }

    public void markIngredientAlternative(String ingredientName) {
        ingredientUsage.put(ingredientName, "Used Alternative");
    }

    public String getRecipeName() {
        return recipeName;
    }

    public int getServings() {
        return servings;
    }

    public Date getCookingDate() {
        return cookingDate;
    }

    public Map<String, String> getIngredientUsage() {
        return ingredientUsage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nRecipe: ").append(recipeName)
          .append("\nDate: ").append(cookingDate)
          .append("\nServings: ").append(servings)
          .append("\nIngredients Used:\n");

        for (Map.Entry<String, String> entry : ingredientUsage.entrySet()) {
            sb.append("- ").append(entry.getKey()).append(": ")
              .append(entry.getValue()).append("\n");
        }

        return sb.toString();
    }
}