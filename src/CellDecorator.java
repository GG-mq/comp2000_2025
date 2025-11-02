import java.awt.Graphics;
import java.awt.Point;
import java.awt.Color;
import java.util.function.DoubleUnaryOperator;
public abstract class CellDecorator extends Cell {
    protected Cell decoratedCell;
    protected DoubleUnaryOperator costModifier;
    public CellDecorator(Cell cell, DoubleUnaryOperator costModifier) {
        super(cell.col, cell.row, cell.x, cell.y, cell.cellType);
        this.decoratedCell = cell;
        this.costModifier = costModifier;
    }
    @Override
    public void paint(Graphics g, Point mousePos) {
        decoratedCell.paint(g, mousePos);
        g.setColor(getTint());
        g.fillRect(x, y, width, height);
    }
    public double getMovementCost(double weatherValue) {
        return costModifier.applyAsDouble(decoratedCell.getMovementCost(weatherValue));
    }
    public abstract Color getTint();
}
