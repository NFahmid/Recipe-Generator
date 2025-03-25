import java.util.*;

public abstract class AbstractRecipe {
    protected String name;
    protected int servings;
    protected List<Ingredient> ingredients;
    protected List<String> steps;
    protected RecipeStatus status;
    protected double matchPercentage;

    public enum RecipeStatus {
        FULLY_AVAILABLE,
        PARTIALLY_AVAILABLE,
        NOT_AVAILABLE
    }

    public AbstractRecipe(String name, List<Ingredient> ingredients, List<String> steps, int servings) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.status = RecipeStatus.NOT_AVAILABLE;
        this.matchPercentage = 0.0;
    }

    public AbstractRecipe adjustServings(int desiredServings) {
        if (desiredServings <= 0) return this;
        
        List<Ingredient> adjustedIngredients = new ArrayList<>();
        double scaleFactor = (double) desiredServings / this.servings;
        
        for (Ingredient ingredient : this.ingredients) {
            double adjustedQuantity = ingredient.getQuantity() * scaleFactor;
            adjustedIngredients.add(new Ingredient(ingredient.getName(), adjustedQuantity, ingredient.getUnit()));
        }
        
        return createAdjustedRecipe(this.name + " (Adjusted for " + desiredServings + " servings)",
                         adjustedIngredients,
                         new ArrayList<>(this.steps),
                         desiredServings);
    }

    protected abstract AbstractRecipe createAdjustedRecipe(String name, List<Ingredient> ingredients, List<String> steps, int servings);

    public String getName() { return name; }
    public int getServings() { return servings; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<String> getSteps() { return steps; }
    public RecipeStatus getStatus() { return status; }
    public double getMatchPercentage() { return matchPercentage; }

    public void updateAvailabilityStatus(IngredientInventory inventory) {
        int availableCount = 0;
        double totalMatchPercentage = 0.0;

        for (Ingredient recipeIngredient : ingredients) {
            String recipeIngredientName = recipeIngredient.getName();
            double requiredAmount = recipeIngredient.getQuantity();
            double availableAmount = inventory.getIngredientAmount(recipeIngredientName);

            if (availableAmount > 0) {
                if (availableAmount >= requiredAmount) {
                    availableCount++;
                    totalMatchPercentage += 100.0;
                } else {
                    double matchPercent = (availableAmount / requiredAmount) * 100.0;
                    totalMatchPercentage += matchPercent;
                }
            }
        }

        matchPercentage = totalMatchPercentage / ingredients.size();

        if (matchPercentage >= 99.99) {
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
        sb.append(ConsoleColors.BLUE_BOLD).append("Recipe: ").append(ConsoleColors.RESET).append(name)
          .append("\n").append(ConsoleColors.BLUE_BOLD).append("Servings: ").append(ConsoleColors.RESET).append(servings)
          .append("\n").append(ConsoleColors.BLUE_BOLD).append("Status: ").append(ConsoleColors.RESET);

        switch (status) {
            case FULLY_AVAILABLE:
                sb.append(ConsoleColors.GREEN).append(status);
                break;
            case PARTIALLY_AVAILABLE:
                sb.append(ConsoleColors.YELLOW).append(status);
                break;
            case NOT_AVAILABLE:
                sb.append(ConsoleColors.RED).append(status);
                break;
        }

        sb.append(ConsoleColors.RESET)
          .append(" (").append(String.format("%.1f", matchPercentage)).append("% match)")
          .append("\n").append(ConsoleColors.BLUE_BOLD).append("Ingredients:").append(ConsoleColors.RESET).append("\n");
        
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