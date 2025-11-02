import java.awt.Color;
import java.awt.Graphics;
public class FoodItem extends Item implements Consumable {
    protected final String edibleByActorType;
    public FoodItem(String name, Color color, int x, int y, String edibleByActorType) {
        super(name, color, x, y);
        this.edibleByActorType = edibleByActorType;
    }
    @Override
    public boolean canBeEatenBy(String actorType) {
        return edibleByActorType != null && edibleByActorType.equals(actorType);
    }
    @Override
    protected void drawDetails(Graphics g) {}
}


