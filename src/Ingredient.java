public class Ingredient {
    private String name;
    private double quantity;
    private String unit;

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    @Override
    public String toString() {
        return ConsoleColors.PURPLE + name + ConsoleColors.RESET + " (" + 
               ConsoleColors.CYAN + quantity + " " + unit + ConsoleColors.RESET + ")";
    }
}
