import java.util.List;
import java.util.Optional;
@FunctionalInterface
public interface MovementStrategy {
    Optional<Cell> chooseMove(Actor actor, List<Cell> adjacent, Grid grid, double windX, double windY);
}
