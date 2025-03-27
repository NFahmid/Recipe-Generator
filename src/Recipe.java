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
        sb.append(ConsoleColors.BLUE_BOLD).append("Color Legend:").append(ConsoleColors.RESET).append("\n")
          .append(ConsoleColors.GREEN).append("■").append(ConsoleColors.RESET).append(" Available, ")
          .append(ConsoleColors.YELLOW).append("■").append(ConsoleColors.RESET).append(" Partially Available, ")
          .append(ConsoleColors.RED).append("■").append(ConsoleColors.RESET).append(" Not Available\n\n")
          .append(ConsoleColors.BLUE_BOLD).append("Recipe: ").append(ConsoleColors.RESET).append(name)
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
        
        IngredientInventory inventory = IngredientInventory.getInstance();
        for (Ingredient ingredient : ingredients) {
            double availableAmount = inventory.getIngredientAmount(ingredient.getName());
            double requiredAmount = ingredient.getQuantity();
            
            sb.append("- ");
            String colorCode;
            if (availableAmount >= requiredAmount) {
                colorCode = ConsoleColors.GREEN;
            } else if (availableAmount > 0) {
                colorCode = ConsoleColors.YELLOW;
            } else {
                colorCode = ConsoleColors.RED;
            }
            
            sb.append(colorCode)
              .append(ingredient.getName())
              .append(" (")
              .append(ingredient.getQuantity())
              .append(" ")
              .append(ingredient.getUnit())
              .append(")")
              .append(ConsoleColors.RESET)
              .append("\n");
        }
        
        sb.append("Steps:\n");
        for (int i = 0; i < steps.size(); i++) {
            sb.append(i + 1).append(". ").append(steps.get(i)).append("\n");
        }
        
        return sb.toString();
    }
}
