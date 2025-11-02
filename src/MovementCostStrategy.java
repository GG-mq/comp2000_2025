@FunctionalInterface
public interface MovementCostStrategy {
    double calculateCost(Cell cell, double rain, double temp);
}
