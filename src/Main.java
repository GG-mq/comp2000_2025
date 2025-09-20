import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Tasks 3–5 combined:
 * - Task 3: Draw a 20x20 grid (cells 35x35, offset 10).
 * - Task 4: Refactor to Grid + Cell classes, each responsible for painting.
 * - Task 5: Highlight cell under mouse in grey.
 */
public class Main extends JFrame {
    public static void main(String[] args) {
        new Main();
    }

    /**
     * Represents one cell of the grid.
     */
    class Cell {
        private int x, y, size;

        public Cell(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        /** Paints this cell (highlighted if mouse is inside). */
        public void paint(Graphics g, Point mouse) {
            // Highlight if mouse is inside this cell
            if (mouse != null &&
                mouse.x >= x && mouse.x < x + size &&
                mouse.y >= y && mouse.y < y + size) {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, size, size);
            }

            g.setColor(Color.BLACK);
            g.drawRect(x, y, size, size);
        }
    }

    /**
     * Represents the whole grid.
     */
    class Grid {
        private Cell[][] cells;
        private int rows, cols;

        public Grid(int rows, int cols, int offset, int cellSize) {
            this.rows = rows;
            this.cols = cols;
            cells = new Cell[rows][cols];

            // Initialize grid of cells
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    int x = offset + c * cellSize;
                    int y = offset + r * cellSize;
                    cells[r][c] = new Cell(x, y, cellSize);
                }
            }
        }

        /** Paints all cells, passing the mouse position down. */
        public void paint(Graphics g, Point mouse) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    cells[r][c].paint(g, mouse);
                }
            }
        }
    }

    /**
     * Canvas = panel that shows the grid.
     */
    class Canvas extends JPanel {
        private Grid grid;

        public Canvas() {
            setPreferredSize(new Dimension(720, 720));
            grid = new Grid(20, 20, 10, 35);

            // Timer to continuously repaint (so hover updates smoothly)
            new Timer(16, e -> repaint()).start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Point mouse = getMousePosition();
            grid.paint(g, mouse);
        }
    }

    /**
     * Window setup
     */
    private Main() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Tasks 3–5: Interactive Grid");
        Canvas canvas = new Canvas();
        this.setContentPane(canvas);
        this.pack();
        this.setVisible(true);
    }
}
