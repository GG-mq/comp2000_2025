import java.awt.Color;
public class HeatWaveDecorator extends CellDecorator {
    public HeatWaveDecorator(Cell cell) {
        super(cell, v -> v * 0.5);
    }
    @Override
    public Color getTint() { return new Color(255, 255, 0, 128); }
}
