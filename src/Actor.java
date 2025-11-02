import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;
import java.util.Optional;
public abstract class Actor implements java.util.function.Consumer<Boolean> {
    Color color;
    Cell loc;
    List<Polygon> display;
    protected String actorType;
    protected MovementStrategy strategy;
    protected double windPushX, windPushY;
    protected float visibility = 1.0f;
    protected boolean isSelected = false;
    public Actor(Cell location, String actorType) {
        this.loc = location;
        this.actorType = actorType;
        this.strategy = (actor, adj, grid, wx, wy) -> Optional.empty();
    }
    public void paint(Graphics g) {
        for (Polygon p : display) {
            g.setColor(new Color(0, 0, 0, (int)(30 * visibility)));
            g.translate(2, 2);
            g.fillPolygon(p);
            g.translate(-2, -2);
        }
        for (Polygon p : display) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(255 * visibility)));
            g.fillPolygon(p);
            g.setColor(Color.BLACK);
            g.drawPolygon(p);
        }
        drawDetails(g);
    }
    protected void drawDetails(Graphics g) {}
    public String getActorType() { return actorType; }
    public Cell getLocation() { return loc; }
    public void moveTo(Cell newLoc) { loc = newLoc; updatePolygons(); }
    public void setStrategy(MovementStrategy strat) { this.strategy = strat; }
    public Optional<Cell> chooseMove(List<Cell> adj, Grid grid, double wx, double wy) {
        return strategy.chooseMove(this, adj, grid, wx, wy);
    }
    protected void updatePolygons() {};
    @Override
    public void accept(Boolean isStorm) {
        visibility = isStorm ? 0.3f : 1.0f;
    }
    public void setSelected(boolean selected) { isSelected = selected; }
}
