import java.util.*;

public class Recipe {
    private String name;
    private int servings;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private RecipeStatus status;
    private double matchPercentage;

    public enum RecipeStatus {
        FULLY_AVAILABLE,
        PARTIALLY_AVAILABLE,
        NOT_AVAILABLE
    }

    public Recipe(String name, List<Ingredient> ingredients, List<String> steps, int servings) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.status = RecipeStatus.NOT_AVAILABLE;
        this.matchPercentage = 0.0;
    }

    public Recipe adjustServings(int desiredServings) {
        if (desiredServings <= 0) return this;
        
        List<Ingredient> adjustedIngredients = new ArrayList<>();
        double scaleFactor = (double) desiredServings / this.servings;
        
        for (Ingredient ingredient : this.ingredients) {
            double adjustedQuantity = ingredient.getQuantity() * scaleFactor;
            adjustedIngredients.add(new Ingredient(ingredient.getName(), adjustedQuantity, ingredient.getUnit()));
        }
        
        return new Recipe(this.name + " (Adjusted for " + desiredServings + " servings)",
                         adjustedIngredients,
                         new ArrayList<>(this.steps),
                         desiredServings);
    }

    public String getName() { return name; }
    public int getServings() { return servings; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
    public RecipeStatus getStatus() { return status; }
    public double getMatchPercentage() { return matchPercentage; }

    public void updateAvailabilityStatus(IngredientInventory inventory) {
        int availableCount = 0;
        for (Ingredient ingredient : ingredients) {
            if (inventory.hasIngredient(ingredient.getName(), ingredient.getQuantity())) {
                availableCount++;
            }
        }

        matchPercentage = (double) availableCount / ingredients.size() * 100;
        
        if (matchPercentage == 100) {
            status = RecipeStatus.FULLY_AVAILABLE;
        } else if (matchPercentage > 0) {
            status = RecipeStatus.PARTIALLY_AVAILABLE;
        } else {
            status = RecipeStatus.NOT_AVAILABLE;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Recipe: ").append(name)
          .append("\nServings: ").append(servings)
          .append("\nStatus: ").append(status)
          .append(" (").append(String.format("%.1f", matchPercentage)).append("% match)")
          .append("\nIngredients:\n");
        
        for (Ingredient ingredient : ingredients) {
            sb.append("- ").append(ingredient.toString()).append("\n");
        }
        
        sb.append("Steps:\n");
        for (int i = 0; i < steps.size(); i++) {
            sb.append(i + 1).append(". ").append(steps.get(i)).append("\n");
        }
        
        return sb.toString();
    }
}
