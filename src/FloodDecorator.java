import java.awt.Color;
public class FloodDecorator extends CellDecorator {
    public FloodDecorator(Cell cell) {
        super(cell, v -> cell.cellType instanceof WaterCell ? v * 3.0 : v * 2.0);
    }
    @Override
    public Color getTint() { return new Color(0, 0, 255, 128); }
}
