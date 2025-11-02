import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.ArrayList;
public class Grid {
    public static final double CELL_SIZE = 0;
    Cell[][] cells = new Cell[20][20];
    private Random random = new Random();
    private Map<Integer, Map<Integer, Map<String, Double>>> weatherCache = new HashMap<>();
    public Grid() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                CellType cellType = generateCellType();
                cells[i][j] = new Cell(colToLabel(i), j, 10 + Cell.size * i, 10 + Cell.size * j, cellType);
            }
        }
    }
    private CellType generateCellType() {
        int type = random.nextInt(4);
        switch (type) {
            case 0: return new GrassCell();
            case 1: return new WaterCell();
            case 2: return new SandCell();
            case 3: return new RockCell();
            default: return new GrassCell();
        }
    }
    private char colToLabel(int col) {
        return (char) (col + 'A');
    }
    private int labelToCol(char col) {
        return col - 'A';
    }
    public void paint(Graphics g, Point mousePos) {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.paint(g, mousePos);
                Map<String, Double> weather = getWeatherAt(cell.row, cell.col - 'A');
                double rain = weather.getOrDefault("rain", 0.0);
                double temp = weather.getOrDefault("temp", 0.5);
                if (rain > 0.5) {
                    g.setColor(new Color(0, 100, 255, Math.min(128, (int)(100 * rain))));
                    int rainDrops = Math.min(5, (int)(rain * 4));
                    for (int i = 0; i < rainDrops; i++) {
                        int x = cell.x + 5 + (i * 7);
                        int y = cell.y + (i * 3);
                        g.fillOval(x, y, 2, 2);
                    }
                }
                if (temp > 0.7) {
                    g.setColor(new Color(255, 255, 0, 50));
                    g.fillRect(cell.x, cell.y, cell.width, cell.height);
                } else if (temp < 0.3) {
                    g.setColor(new Color(200, 220, 255, 50));
                    g.fillRect(cell.x, cell.y, cell.width, cell.height);
                }
            }
        }
    }
    public Optional<Cell> cellAtColRow(int c, int r) {
        if (c >= 0 && c < cells.length && r >= 0 && r < cells[c].length) {
            return Optional.of(cells[c][r]);
        }
        return Optional.empty();
    }
    public Optional<Cell> cellAtColRow(char c, int r) {
        return cellAtColRow(labelToCol(c), r);
    }
    public Optional<Cell> cellAtPoint(Point p) {
        if (p == null) return Optional.empty();
        int c = (p.x - 10) / Cell.size;
        int r = (p.y - 10) / Cell.size;
        return cellAtColRow(c, r);
    }
    public List<Cell> getAdjacentCells(Cell cell) {
        List<Cell> adj = new ArrayList<>();
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] d : dirs) {
            int nc = cell.col - 'A' + d[0];
            int nr = cell.row + d[1];
            cellAtColRow(nc, nr).ifPresent(adj::add);
        }
        return adj;
    }

    public void applyWeather(List<WeatherRecord> records, WeatherEventManager eventManager) {
        records.forEach(rec -> {
            int c = 10 + (int)(rec.x / 10.0);
            int r = 10 - (int)(rec.y / 10.0);
            if (r >= 0 && r < 20 && c >= 0 && c < 20) {
                weatherCache.computeIfAbsent(r, k -> new HashMap<>())
                    .computeIfAbsent(c, k -> new HashMap<>())
                    .put(rec.attribute, rec.value);
            }
        });
        for (int r = 0; r < 20; r++) {
            for (int c = 0; c < 20; c++) {
                Map<String, Double> weather = getWeatherAt(r, c);
                if (!weather.isEmpty()) {
                    Cell baseCell = cells[r][c];
                    while (baseCell instanceof CellDecorator) {
                        baseCell = ((CellDecorator) baseCell).decoratedCell;
                    }
                    Cell decorated = baseCell;
                    double rain = weather.getOrDefault("rain", 0.0);
                    double temp = weather.getOrDefault("temp", 0.5);
                    if (rain > 0.5) decorated = new FloodDecorator(decorated);
                    if (temp > 0.7) decorated = new HeatWaveDecorator(decorated);
                    else if (temp < 0.3) decorated = new ChillDecorator(decorated);
                    cells[r][c] = decorated;
                    double windX = Math.abs(weather.getOrDefault("windx", 0.0));
                    double windY = Math.abs(weather.getOrDefault("windy", 0.0));
                    eventManager.updateStorm(rain + windX + windY > 1.4);
                }
            }
        }
    }
    public Map<String, Double> getWeatherAt(int r, int c) {
        return weatherCache.getOrDefault(r, new HashMap<>()).getOrDefault(c, new HashMap<>());
    }
    public Actor getActorAt(Cell cell) { return null; }
}
