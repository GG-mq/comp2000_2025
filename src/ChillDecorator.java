import java.awt.Color;
public class ChillDecorator extends CellDecorator {
    public ChillDecorator(Cell cell) {
        super(cell, v -> v * 1.5);
    }
    @Override
    public Color getTint() { return new Color(255, 255, 255, 128); }
}
