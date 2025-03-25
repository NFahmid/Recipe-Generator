import java.util.*;

public class RecipeValidator {
    private static RecipeValidator instance;

    private RecipeValidator() {}

    public static RecipeValidator getInstance() {
        if (instance == null) {
            instance = new RecipeValidator();
        }
        return instance;
    }

    public boolean validateRecipe(String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        return validateName(name) && 
               validateServings(servings) && 
               validateIngredients(ingredients) && 
               validateSteps(steps);
    }

    private boolean validateName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private boolean validateServings(int servings) {
        return servings > 0;
    }

    private boolean validateIngredients(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return false;
        }

        for (Ingredient ingredient : ingredients) {
            if (!validateIngredient(ingredient)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateIngredient(Ingredient ingredient) {
        return ingredient != null && 
               ingredient.getName() != null && 
               !ingredient.getName().trim().isEmpty() && 
               ingredient.getQuantity() > 0 && 
               ingredient.getUnit() != null && 
               !ingredient.getUnit().trim().isEmpty();
    }

    private boolean validateSteps(List<String> steps) {
        if (steps == null || steps.isEmpty()) {
            return false;
        }

        for (String step : steps) {
            if (step == null || step.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public String getValidationErrors(String name, int servings, List<Ingredient> ingredients, List<String> steps) {
        StringBuilder errors = new StringBuilder();

        if (!validateName(name)) {
            errors.append("Recipe name cannot be empty.\n");
        }

        if (!validateServings(servings)) {
            errors.append("Number of servings must be greater than 0.\n");
        }

        if (ingredients == null || ingredients.isEmpty()) {
            errors.append("Recipe must have at least one ingredient.\n");
        } else {
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);
                if (!validateIngredient(ingredient)) {
                    errors.append(String.format("Invalid ingredient at position %d: Must have name, positive quantity, and unit.\n", i + 1));
                }
            }
        }

        if (steps == null || steps.isEmpty()) {
            errors.append("Recipe must have at least one step.\n");
        } else {
            for (int i = 0; i < steps.size(); i++) {
                String step = steps.get(i);
                if (step == null || step.trim().isEmpty()) {
                    errors.append(String.format("Step %d cannot be empty.\n", i + 1));
                }
            }
        }

        return errors.length() > 0 ? errors.toString() : null;
    }
}