import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
public class Cell extends Rectangle {
    static int size = 35;
    char col;
    int row;
    CellType cellType;
    public Cell(char inCol, int inRow, int x, int y, CellType cellType) {
        super(x, y, size, size);
        col = inCol;
        row = inRow;
        this.cellType = cellType;
    }
    public void paint(Graphics g, Point mousePos) {
        boolean isHighlighted = contains(mousePos);
        cellType.paint(g, x, y, size, isHighlighted);
    }
    public boolean contains(Point p) {
        return p != null && super.contains(p);
    }
    public CellType getCellType() {
        return cellType;
    }
    public double getMovementCost(double weatherValue) {
        return cellType instanceof WaterCell && weatherValue > 0.5 ? 3.0 : 1.0;
    }
    public void setCellType(CellType type) {
        this.cellType = type;
    }
}
