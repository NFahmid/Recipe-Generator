import java.util.List;

public class StepByStepCookingMode implements CookingMode {
    @Override
    public void startCooking(Recipe recipe) {
        List<String> steps = recipe.getSteps();
        System.out.println("Starting Cooking Mode for " + recipe.getName());
        for (int i = 0; i < steps.size(); i++) {
            System.out.println("Step " + (i + 1) + ": " + steps.get(i));
        }
    }
}
