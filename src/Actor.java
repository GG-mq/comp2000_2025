import java.awt.Color;
import java.awt.Graphics;

public abstract class Actor {
  Color color;
  Cell loc;

  public Actor(Cell inLoc, Color inColor) {
    this.loc = inLoc;
    this.color = inColor;
  }

  public void paint(Graphics g) {
    g.setColor(color);
    g.fillRect(loc.x + 5, loc.y + 5, loc.width - 10, loc.height - 10);

    g.setColor(Color.GRAY);
    g.drawRect(loc.x + 5, loc.y + 5, loc.width - 10, loc.height - 10);
  }
}
